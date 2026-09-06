#!/bin/node

/**
 * Packs the generated map data into the compact binary the app reads.
 *
 * The app used to ship SVG, which spends about four megabytes per projection
 * spelling coordinates out in decimal ASCII inside XML, and needed the map
 * parsed as a document at startup. Nothing needs a document: the app wants a
 * list of places and the outlines that belong to them, which is what this
 * writes.
 *
 * Input is mapshaper's projected GeoJSON, where each feature carries:
 *   CODE   the place it belongs to, e.g. "FRA" or "FRA_CE"
 *   LEVEL  0 for a country outline, 1 for one of its regions
 *
 * Output format. All integers are LEB128 varints and coordinates are in grid
 * steps, on a canvas MAP_WIDTH user units across:
 *
 *   "BMAP" 0x02
 *   precision                         uvarint, grid steps per user unit
 *   width, height                     uvarint, in grid steps
 *   shapeCount                        uvarint
 *   per shape:
 *     codeLength, code                uvarint + UTF-8 bytes
 *     flags                           1 byte, bit 0 = is a region
 *     ringCount                       uvarint
 *     per ring:
 *       pointCount                    uvarint
 *       flags                         1 byte, bit 0 = even-odd fill
 *       pointCount pairs of dx, dy    zigzag varint, from the previous point,
 *                                     starting from the origin on each ring
 */

import { readFileSync, writeFileSync } from 'fs'

/** Canvas width in user units. Stroke widths in the app are relative to it. */
const MAP_WIDTH = 800

/** Blank edge left around the map, as mapshaper's own SVG output used to. */
const MARGIN = 1

/**
 * Grid steps per user unit. Coordinates are snapped to this, so it sets how
 * fine the map can be; it is written into the header rather than agreed by
 * convention, so raising it only means regenerating the assets.
 */
const PRECISION = 20

function uvarint(bytes, value) {
  while (value >= 0x80) {
    bytes.push((value & 0x7f) | 0x80)
    value >>>= 7
  }
  bytes.push(value)
}

function svarint(bytes, value) {
  uvarint(bytes, value < 0 ? -value * 2 - 1 : value * 2)
}

/** Every polygon of a feature, outer rings and holes alike. */
function ringsOf(geometry) {
  if (!geometry) return []
  if (geometry.type === 'Polygon') return [geometry.coordinates]
  if (geometry.type === 'MultiPolygon') return geometry.coordinates
  return []
}

/**
 * Groups features by the place they belong to. A country outline and its
 * regions arrive as separate features; the code is what ties them together.
 */
function collect(features) {
  const shapes = []
  const byCode = new Map()

  for (const feature of features) {
    const code = feature.properties?.CODE
    if (!code) continue
    const polygons = ringsOf(feature.geometry)
    if (polygons.length === 0) continue

    let shape = byCode.get(code)
    if (!shape) {
      shape = { code, isState: feature.properties.LEVEL === 1, rings: [], seen: new Set() }
      byCode.set(code, shape)
      shapes.push(shape)
    }
    // A polygon with holes only reads correctly under an even-odd fill, which
    // is the rule mapshaper's SVG output used to pick for the same reason.
    const evenOdd = polygons.some((polygon) => polygon.length > 1)

    for (const polygon of polygons) {
      for (const ring of polygon) {
        if (ring.length >= 3) shape.rings.push({ ring, evenOdd })
      }
    }
  }
  return shapes
}

/** Twice the signed area of a closed ring of `x, y` pairs. */
function shoelace(points) {
  let sum = 0
  const count = points.length / 2
  for (let i = 0, j = count - 1; i < count; j = i++) {
    sum += points[j * 2] * points[i * 2 + 1] - points[i * 2] * points[j * 2 + 1]
  }
  return sum
}

/**
 * Snaps every ring to the output grid and throws away what that makes
 * redundant: points that land on the one before them, and rings left too small
 * to enclose anything. The simplification upstream works to about this
 * precision, so a good share of the raw points say nothing once rounded.
 */
function quantize(shapes, canvas) {
  const kept = []
  for (const shape of shapes) {
    const seen = new Set()
    const rings = []
    for (const { ring, evenOdd } of shape.rings) {
      const points = []
      let px = null
      let py = null
      for (const [rx, ry] of ring) {
        const x = canvas.x(rx)
        const y = canvas.y(ry)
        if (x === px && y === py) continue
        points.push(x, y)
        px = x
        py = y
      }
      if (points.length < 6) continue
      // Coordinates are integers now, so any ring that still encloses area
      // encloses at least half a grid square. One that comes out at exactly
      // zero is a sliver the rounding flattened, and it would draw nothing.
      if (shoelace(points) === 0) continue
      // Antarctica arrives twice over, once per admin level, because its source
      // carries no country code and both levels land on ATA. A ring counted
      // twice cancels itself out in the even-odd test the app hit tests with,
      // which would leave the shape impossible to tap.
      const key = points.join(',')
      if (seen.has(key)) continue
      seen.add(key)
      rings.push({ points, evenOdd })
    }
    if (rings.length > 0) kept.push({ code: shape.code, isState: shape.isState, rings })
  }
  // By code, which puts a country next to its own regions. They are neighbours
  // on the map, so their coordinates are similar, and the compressor in the APK
  // gets noticeably more out of the file for it.
  return kept.sort((a, b) => (a.code < b.code ? -1 : a.code > b.code ? 1 : 0))
}

/** Projected metres in, user units on a MAP_WIDTH canvas out, y flipped. */
function fitToCanvas(shapes) {
  let minX = Infinity
  let minY = Infinity
  let maxX = -Infinity
  let maxY = -Infinity
  for (const shape of shapes) {
    for (const { ring } of shape.rings) {
      for (const [x, y] of ring) {
        if (x < minX) minX = x
        if (x > maxX) maxX = x
        if (y < minY) minY = y
        if (y > maxY) maxY = y
      }
    }
  }
  const scale = (MAP_WIDTH - 2 * MARGIN) / (maxX - minX)
  return {
    width: MAP_WIDTH,
    height: 2 * MARGIN + (maxY - minY) * scale,
    x: (v) => Math.round((MARGIN + (v - minX) * scale) * PRECISION),
    y: (v) => Math.round((MARGIN + (maxY - v) * scale) * PRECISION),
  }
}

function pack(path) {
  const geojson = JSON.parse(readFileSync(path, 'utf8'))
  const collected = collect(geojson.features ?? [])
  const canvas = fitToCanvas(collected)
  const shapes = quantize(collected, canvas)

  const bytes = [0x42, 0x4d, 0x41, 0x50, 0x02] // "BMAP" v2
  uvarint(bytes, PRECISION)
  uvarint(bytes, Math.round(canvas.width * PRECISION))
  uvarint(bytes, Math.round(canvas.height * PRECISION))
  uvarint(bytes, shapes.length)

  let rings = 0
  let points = 0
  for (const shape of shapes) {
    const code = Buffer.from(shape.code, 'utf8')
    uvarint(bytes, code.length)
    for (const byte of code) bytes.push(byte)
    bytes.push(shape.isState ? 1 : 0)
    uvarint(bytes, shape.rings.length)

    for (const { points: ring, evenOdd } of shape.rings) {
      uvarint(bytes, ring.length / 2)
      bytes.push(evenOdd ? 1 : 0)
      let px = 0
      let py = 0
      for (let i = 0; i < ring.length; i += 2) {
        svarint(bytes, ring[i] - px)
        svarint(bytes, ring[i + 1] - py)
        px = ring[i]
        py = ring[i + 1]
      }
      rings++
      points += ring.length / 2
    }
  }

  const out = path.replace(/^.*\//, './app/src/main/assets/').replace(/\.geojson$/, '.bmap')
  writeFileSync(out, Buffer.from(bytes))
  console.log(
    `${out}: ${shapes.length} shapes, ${rings} rings, ${points} points, ` +
      `${canvas.width}x${canvas.height.toFixed(1)}, ${bytes.length} bytes`,
  )
}

const inputs = process.argv.slice(2)
if (inputs.length === 0) {
  console.error('usage: node packmap.js <map>.geojson ...')
  process.exit(1)
}
inputs.forEach(pack)

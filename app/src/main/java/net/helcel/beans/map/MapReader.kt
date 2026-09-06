package net.helcel.beans.map

import android.graphics.Path
import android.graphics.RectF
import java.io.InputStream

private const val VERSION = 2

/**
 * Reads the packed map assets produced by `packmap.js`.
 *
 * The maps used to ship as SVG, which spends around four megabytes per
 * projection spelling coordinates out in decimal ASCII inside XML. The packed
 * form says the same thing in a fraction of that, and reading it is a straight
 * scan over a byte array instead of an XML parse with a float scanner on top.
 *
 * The format is documented in `packmap.js`; the two have to agree.
 */
object MapReader {

    fun read(input: InputStream): MapWorld {
        val reader = ByteReader(input.readBytes())
        reader.readHeader()
        val width = reader.unsigned() * reader.scale
        val height = reader.unsigned() * reader.scale
        val count = reader.unsigned()
        val shapes = ArrayList<MapShape>(count)
        repeat(count) { shapes.add(reader.shape()) }
        return MapWorld(width, height, shapes)
    }
}

private class ByteReader(private val data: ByteArray) {

    private var pos = 0

    /** One grid step in user units, read from the header. */
    var scale = 1f
        private set

    fun readHeader() {
        require(
            data.size > 5 &&
                data[0] == 'B'.code.toByte() && data[1] == 'M'.code.toByte() &&
                data[2] == 'A'.code.toByte() && data[3] == 'P'.code.toByte()
        ) { "not a packed map" }
        require(data[4].toInt() == VERSION) { "unsupported packed map version ${data[4]}" }
        pos = 5
        scale = 1f / unsigned()
    }

    fun unsigned(): Int {
        var result = 0
        var shift = 0
        while (true) {
            val byte = data[pos++].toInt()
            result = result or ((byte and 0x7F) shl shift)
            if (byte and 0x80 == 0) return result
            shift += 7
        }
    }

    /** Zigzag, because deltas run both ways and are almost always tiny. */
    fun signed(): Int {
        val value = unsigned()
        return if (value and 1 == 1) -((value ushr 1) + 1) else value ushr 1
    }

    fun flag(): Boolean = data[pos++].toInt() != 0

    fun text(): String {
        val length = unsigned()
        val value = String(data, pos, length, Charsets.UTF_8)
        pos += length
        return value
    }

    fun shape(): MapShape {
        val code = text()
        val isState = flag()
        val ringCount = unsigned()

        val rings = ArrayList<FloatArray>(ringCount)
        var nonZero: Path? = null
        var evenOdd: Path? = null
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE

        repeat(ringCount) {
            val points = unsigned()
            val useEvenOdd = flag()
            val ring = FloatArray(points * 2)
            // Each point is a delta from the one before it, the first from the
            // origin, so the whole ring decodes in one pass.
            var x = 0
            var y = 0
            for (i in 0 until points) {
                x += signed()
                y += signed()
                val fx = x * scale
                val fy = y * scale
                ring[i * 2] = fx
                ring[i * 2 + 1] = fy
                if (fx < minX) minX = fx
                if (fx > maxX) maxX = fx
                if (fy < minY) minY = fy
                if (fy > maxY) maxY = fy
            }
            rings.add(ring)

            val path = if (useEvenOdd) {
                evenOdd ?: Path().also { it.fillType = Path.FillType.EVEN_ODD; evenOdd = it }
            } else {
                nonZero ?: Path().also { nonZero = it }
            }
            path.moveTo(ring[0], ring[1])
            var i = 2
            while (i < ring.size) {
                path.lineTo(ring[i], ring[i + 1])
                i += 2
            }
            path.close()
        }

        return MapShape(
            code, isState, rings,
            RectF(minX, minY, maxX, maxY),
            nonZero, evenOdd,
        )
    }
}

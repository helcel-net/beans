package net.helcel.beans.map

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import kotlin.math.min
import kotlin.math.roundToInt

/** Draws a [MapWorld] onto a canvas through a pan/zoom transform. */
class MapRenderer(private val map: MapWorld) {

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val inverse = Matrix()
    private val values = FloatArray(9)
    private val visible = RectF()

    /** False when the border is too faint to be worth a draw call at all. */
    private var strokeVisible = true

    /**
     * Paints the map into [canvas], mapping user units through [transform].
     * Shapes outside the canvas are skipped, which is what makes drawing cheap
     * once the map is zoomed in.
     */
    fun draw(canvas: Canvas, transform: Matrix, style: MapStyle) {
        // SRC rather than blended: the bitmap is reused between renders.
        canvas.drawColor(style.background, PorterDuff.Mode.SRC)
        if (!transform.invert(inverse)) return
        visible.set(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        inverse.mapRect(visible)

        // Widths are given in device pixels, so undo the zoom the canvas is
        // about to apply to them, then let them grow slowly with it.
        transform.getValues(values)
        val scale = values[Matrix.MSCALE_X]
        val perPixel = 1f / scale
        val fitted = min(canvas.width / map.width, canvas.height / map.height)
        val zoom = (scale / fitted).coerceAtLeast(1f)
        val regionPx = strokeWidthAt(zoom, REGION_STROKE_STOPS)
        val countryPx = strokeWidthAt(zoom, COUNTRY_STROKE_STOPS)

        canvas.save()
        canvas.concat(transform)
        setStroke(style.background, if (style.regional) regionPx else countryPx, perPixel)
        for ((code, color) in style.fills) {
            val shape = map.byCode[code] ?: continue
            if (!RectF.intersects(shape.bounds, visible)) continue
            fill.color = color
            drawShape(canvas, shape.nonZero, true)
            drawShape(canvas, shape.evenOdd, true)
        }

        // With regions on, the country outlines come back on top as the thicker
        // borders between them.
        if (style.regional) {
            setStroke(style.background, countryPx, perPixel)
            for (shape in map.countries) {
                if (!RectF.intersects(shape.bounds, visible)) continue
                drawShape(canvas, shape.nonZero, false)
                drawShape(canvas, shape.evenOdd, false)
            }
        }
        canvas.restore()
    }

    /**
     * Sets a border width given in device pixels.
     *
     * Anything thinner than a pixel cannot be drawn as such: the rasteriser
     * turns it into a one pixel hairline at full strength, which is why every
     * border below that width used to come out the same weight no matter how
     * far out the map was. Below a pixel the width is carried as opacity
     * instead, so a quarter-pixel border reads as a quarter as dark.
     */
    private fun setStroke(color: Int, devicePx: Float, perPixel: Float) {
        strokeVisible = devicePx > 0.004f
        if (!strokeVisible) return
        stroke.color = color
        if (devicePx < 1f) {
            stroke.strokeWidth = perPixel
            stroke.alpha = (devicePx * 255f).roundToInt().coerceIn(0, 255)
        } else {
            stroke.strokeWidth = perPixel * devicePx
            stroke.alpha = 255
        }
    }

    private fun drawShape(canvas: Canvas, path: Path?, filled: Boolean) {
        if (path == null) return
        if (filled) canvas.drawPath(path, fill)
        if (strokeVisible) canvas.drawPath(path, stroke)
    }
}

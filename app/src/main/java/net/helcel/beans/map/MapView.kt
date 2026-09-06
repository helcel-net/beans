package net.helcel.beans.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import androidx.core.graphics.createBitmap
import kotlin.math.min

/** A shape a tap landed on or near, with how far away it was in user units. */
class MapPick(val code: String, val distance: Float)

/** How far past the fitted map a pinch may zoom in. */
private const val MAX_ZOOM = 64f

/** How much a double tap zooms in by. */
private const val DOUBLE_TAP_ZOOM = 3f

/** Time a gesture has to settle before the map is redrawn sharp again. */
private const val RENDER_DELAY_MS = 80L

/** How long the map may stay stretched while a gesture keeps going. */
private const val MAX_STALE_MS = 500L

/**
 * Most candidates a single tap will offer. A wide radius over a crowded part of
 * the map can reach dozens of places, which is more of a list than anyone wants
 * to read; the nearest few are what the tap was plausibly aiming at.
 */
private const val MAX_CANDIDATES = 16

/**
 * The map itself: pan, pinch and tap over a [MapWorld].
 *
 * Drawing every country as vectors costs far too much to do on each frame, so a
 * render lands in an off-screen bitmap on a worker thread and gestures just move
 * that bitmap around. Once the gesture settles the map is drawn again at the new
 * zoom, which is what keeps it sharp all the way in.
 */
@SuppressLint("ViewConstructor")
class MapView(context: Context) : View(context) {

    var world: MapWorld? = null
        set(value) {
            if (field === value) return
            field = value
            renderer = value?.let(::MapRenderer)
            fitToView()
            requestRender(0L)
        }

    @Volatile
    var style: MapStyle? = null
        set(value) {
            if (field === value) return
            field = value
            requestRender(0L)
        }

    /** How far from a tap, in dp, other shapes still count as candidates. */
    var touchRadiusDp: Float = 4f

    /** Called on a tap with every nearby shape, nearest first. */
    var onPick: ((List<MapPick>) -> Unit)? = null

    @Volatile
    private var renderer: MapRenderer? = null

    private val scroller = OverScroller(context)

    private val transform = Matrix()
    private val inverse = Matrix()
    private val values = FloatArray(9)
    private val point = FloatArray(2)
    private var minScale = 1f
    private var maxScale = 1f

    private val blitPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val blit = Matrix()

    private val lock = Any()
    private var front: Bitmap? = null
    private var back: Bitmap? = null
    private val frontMatrix = Matrix()
    private val frontValues = FloatArray(9)
    private val pendingMatrix = Matrix()
    private var pendingWidth = 0
    private var pendingHeight = 0
    @Volatile private var lastRenderAt = 0L

    private val renderThread = HandlerThread("map-render").apply { start() }
    private val renderHandler = Handler(renderThread.looper)
    private val renderTask = Runnable { render() }

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scale = currentScale()
                val target = (scale * detector.scaleFactor).coerceIn(minScale, maxScale)
                transform.postScale(target / scale, target / scale, detector.focusX, detector.focusY)
                clamp()
                invalidate()
                return true
            }
        },
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                scroller.forceFinished(true)
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float,
            ): Boolean {
                val map = world ?: return false
                transform.getValues(values)
                val scale = values[Matrix.MSCALE_X]
                val x = values[Matrix.MTRANS_X].toInt()
                val y = values[Matrix.MTRANS_Y].toInt()
                val w = map.width * scale
                val h = map.height * scale
                scroller.forceFinished(true)
                scroller.fling(
                    x, y, velocityX.toInt(), velocityY.toInt(),
                    if (w > width) (width - w).toInt() else x, if (w > width) 0 else x,
                    if (h > height) (height - h).toInt() else y, if (h > height) 0 else y,
                )
                postInvalidateOnAnimation()
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
            ): Boolean {
                transform.postTranslate(-distanceX, -distanceY)
                clamp()
                invalidate()
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                pick(e.x, e.y)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val scale = currentScale()
                val target = if (scale >= maxScale * 0.99f) {
                    minScale
                } else {
                    min(scale * DOUBLE_TAP_ZOOM, maxScale)
                }
                transform.postScale(target / scale, target / scale, e.x, e.y)
                clamp()
                invalidate()
                return true
            }
        },
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (!scroller.computeScrollOffset()) return
        transform.getValues(values)
        values[Matrix.MTRANS_X] = scroller.currX.toFloat()
        values[Matrix.MTRANS_Y] = scroller.currY.toFloat()
        transform.setValues(values)
        postInvalidateOnAnimation()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fitToView()
        requestRender(0L)
    }

    override fun onDraw(canvas: Canvas) {
        val current = style
        canvas.drawColor(current?.background ?: 0)

        var stale = true
        synchronized(lock) {
            val bitmap = front
            if (bitmap != null && !bitmap.isRecycled && frontMatrix.invert(blit)) {
                blit.postConcat(transform)
                canvas.drawBitmap(bitmap, blit, blitPaint)
                transform.getValues(values)
                stale = !values.contentEquals(frontValues)
            }
        }
        // Scheduling only ever happens from here or from a setter, both on the
        // main thread, so a render never starts while its bitmap is on screen.
        if (stale) {
            val overdue = SystemClock.uptimeMillis() - lastRenderAt > MAX_STALE_MS
            requestRender(if (overdue) 0L else RENDER_DELAY_MS)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scroller.forceFinished(true)
        renderHandler.removeCallbacks(renderTask)
        renderThread.quitSafely()
        // Dropped rather than recycled: a render may still be part way through
        // one of them, and the collector is happy to take them from here.
        synchronized(lock) {
            front = null
            back = null
        }
    }

    /** Centres the map in the view and works out how far it may be zoomed. */
    private fun fitToView() {
        val map = world ?: return
        if (width == 0 || height == 0 || map.width <= 0f || map.height <= 0f) return
        val scale = min(width / map.width, height / map.height)
        minScale = scale
        maxScale = scale * MAX_ZOOM
        transform.setScale(scale, scale)
        transform.postTranslate(
            (width - map.width * scale) / 2f,
            (height - map.height * scale) / 2f,
        )
    }

    private fun currentScale(): Float {
        transform.getValues(values)
        return values[Matrix.MSCALE_X]
    }

    /** Keeps the map from being dragged away from the viewport. */
    private fun clamp() {
        val map = world ?: return
        transform.getValues(values)
        val scale = values[Matrix.MSCALE_X]
        val w = map.width * scale
        val h = map.height * scale
        values[Matrix.MTRANS_X] = if (w <= width) {
            (width - w) / 2f
        } else {
            values[Matrix.MTRANS_X].coerceIn(width - w, 0f)
        }
        values[Matrix.MTRANS_Y] = if (h <= height) {
            (height - h) / 2f
        } else {
            values[Matrix.MTRANS_Y].coerceIn(height - h, 0f)
        }
        transform.setValues(values)
    }

    private fun requestRender(delay: Long) {
        if (width == 0 || height == 0 || renderer == null || style == null) return
        synchronized(lock) {
            pendingMatrix.set(transform)
            pendingWidth = width
            pendingHeight = height
        }
        renderHandler.removeCallbacks(renderTask)
        renderHandler.postDelayed(renderTask, delay)
    }

    private fun render() {
        val matrix = Matrix()
        val w: Int
        val h: Int
        synchronized(lock) {
            matrix.set(pendingMatrix)
            w = pendingWidth
            h = pendingHeight
        }
        if (w <= 0 || h <= 0) return
        val current = renderer ?: return
        val currentStyle = style ?: return

        var target = back
        if (target == null || target.width != w || target.height != h) {
            target?.recycle()
            target = createBitmap(w, h)
            back = target
        }
        current.draw(Canvas(target), matrix, currentStyle)

        synchronized(lock) {
            back = front
            front = target
            frontMatrix.set(matrix)
            matrix.getValues(frontValues)
        }
        lastRenderAt = SystemClock.uptimeMillis()
        postInvalidate()
    }

    /** Reports every shape within the tap radius of ([x], [y]), nearest first. */
    private fun pick(x: Float, y: Float) {
        val map = world ?: return
        val current = style ?: return
        val callback = onPick ?: return
        if (!transform.invert(inverse)) return

        point[0] = x
        point[1] = y
        inverse.mapPoints(point)
        val radius = touchRadiusDp * resources.displayMetrics.density / currentScale()

        val picks = ArrayList<MapPick>()
        for (code in current.fills.keys) {
            val shape = map.byCode[code] ?: continue
            val bounds = shape.bounds
            if (point[0] < bounds.left - radius || point[0] > bounds.right + radius) continue
            if (point[1] < bounds.top - radius || point[1] > bounds.bottom + radius) continue
            val distance = shape.distanceTo(point[0], point[1])
            if (distance <= radius) picks.add(MapPick(code, distance))
        }
        if (picks.isEmpty()) return
        picks.sortBy { it.distance }
        callback(if (picks.size > MAX_CANDIDATES) picks.subList(0, MAX_CANDIDATES) else picks)
    }
}

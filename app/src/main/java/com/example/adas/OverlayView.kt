package com.example.adas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var detections: List<Detection> = emptyList()
    private var status: String = "No object"
    private var distance: Float? = null
    private var ttc: Float? = null

    private val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        style = Paint.Style.FILL
        setShadowLayer(5f, 0f, 0f, Color.BLACK)
    }

    private val warningPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    
    private val warningTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        style = Paint.Style.FILL
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    fun setResults(detections: List<Detection>, status: String, distance: Float?, ttc: Float?) {
        this.detections = detections
        this.status = status
        this.distance = distance
        this.ttc = ttc
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw detections
        for (detection in detections) {
            when (detection.label) {
                "person" -> boxPaint.color = Color.RED
                "bicycle", "motorbike" -> boxPaint.color = Color.BLUE
                else -> boxPaint.color = Color.MAGENTA // car, bus, truck
            }
            
            val rect = RectF(
                detection.x1.toFloat(),
                detection.y1.toFloat(),
                detection.x2.toFloat(),
                detection.y2.toFloat()
            )
            canvas.drawRect(rect, boxPaint)
        }

        // Draw HUD
        var y = 60f
        canvas.drawText("Status: $status", 20f, y, textPaint)
        y += 50f
        
        if (distance != null) {
            canvas.drawText("Dist: %.2f m".format(distance), 20f, y, textPaint)
            y += 50f
        }
        
        if (ttc != null) {
            canvas.drawText("TTC: %.2f s".format(ttc), 20f, y, textPaint)
        }

        // Draw Warning
        if (status == "VERY CLOSE - BRAKE") {
            val warningRect = RectF(width - 300f, 20f, width - 20f, 120f)
            canvas.drawRect(warningRect, warningPaint)
            canvas.drawText("BRAKE", width - 260f, 90f, warningTextPaint)
        }
    }
}

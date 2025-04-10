package com.techsavvy.showcaseme.ui.qr.shapes

import android.graphics.Path
import androidx.annotation.FloatRange
import com.github.alexzhirkevich.customqrgenerator.style.Neighbors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape

data class Hexagon(
    @FloatRange(from = 0.0) val width: Float = 1f,
    override val size: Int = 7
) : QrVectorFrameShape {
    override fun Path.shape(size: Float, neighbors: Neighbors) {
        val width = (size / this@Hexagon.size.coerceAtLeast(1)) * width.coerceAtLeast(0f)

        // Logic for drawing a hexagon shape
        val halfSize = size / 2f
        val innerWidth = size - width * 2f

        moveTo(width, halfSize)
        lineTo(halfSize, width)
        lineTo(size - width, width)
        lineTo(size - width, halfSize)
        lineTo(size - width, size - width)
        lineTo(halfSize, size - width)
        lineTo(width, size - width)
        lineTo(width, halfSize)
    }
}
package com.techsavvy.showcaseme.ui.qr.shapes

import android.graphics.Path
import androidx.annotation.FloatRange
import com.github.alexzhirkevich.customqrgenerator.style.Neighbors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape


data class Octagon(
    @FloatRange(from = 0.0) val width: Float = 1f,
    override val size: Int = 7
) : QrVectorFrameShape {
    override fun Path.shape(size: Float, neighbors: Neighbors) {
        val width = (size / this@Octagon.size.coerceAtLeast(1)) * width.coerceAtLeast(0f)

        // Logic for drawing an octagon shape
        val halfSize = size / 2f
        val innerWidth = size - width * 2f

        moveTo(width, 0f)
        lineTo(innerWidth, 0f)
        lineTo(size, width)
        lineTo(size, innerWidth)
        lineTo(size - width, size)
        lineTo(width, size)
        lineTo(0f, innerWidth)
        lineTo(0f, width)
        lineTo(width, 0f)
    }
}
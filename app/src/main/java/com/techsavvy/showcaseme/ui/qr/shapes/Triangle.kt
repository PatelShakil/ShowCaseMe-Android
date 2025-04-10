package com.techsavvy.showcaseme.ui.qr.shapes

import android.graphics.Path
import androidx.annotation.FloatRange
import com.github.alexzhirkevich.customqrgenerator.style.Neighbors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape


data class Triangle(
    @FloatRange(from = 0.0) val width: Float = 1f,
    override val size: Int = 7
) : QrVectorFrameShape {
    override fun Path.shape(size: Float, neighbors: Neighbors) {
        val width = (size / this@Triangle.size.coerceAtLeast(1)) * width.coerceAtLeast(0f)
        moveTo(size / 2f, 0f)
        lineTo(size - width, size)
        lineTo(0f, size)
        lineTo(size / 2f, 0f)
    }
}

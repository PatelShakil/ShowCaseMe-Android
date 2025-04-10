package com.techsavvy.showcaseme.ui.qr.shapes

import android.graphics.Path
import androidx.annotation.FloatRange
import com.github.alexzhirkevich.customqrgenerator.style.Neighbors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape

data class Heart(
    @FloatRange(from = 0.0) val width: Float = 1f,
    override val size: Int = 7
) : QrVectorFrameShape {
    override fun Path.shape(size: Float, neighbors: Neighbors) {
        val width = (size / this@Heart.size.coerceAtLeast(1)) * width.coerceAtLeast(0f)

        // Logic for drawing a heart shape
        moveTo(size / 2f, size / 5f)
        cubicTo(
            5 * size / 14, 0f,
            0f, size / 15,
            size / 28, 2 * size / 5
        )
        cubicTo(
            size / 14, 2 * size / 3,
            3 * size / 7, 5 * size / 6,
            size / 2, size
        )
        cubicTo(
            4 * size / 7, 5 * size / 6,
            13 * size / 14, 2 * size / 3,
            27 * size / 28, 2 * size / 5
        )
        cubicTo(
            size, size / 15,
            9 * size / 14, 0f,
            size / 2, size / 5
        )
    }
}
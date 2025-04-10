package com.techsavvy.showcaseme.widgets

import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.encoder.QrCodeMatrix
import com.github.alexzhirkevich.customqrgenerator.style.QrShape
import com.github.alexzhirkevich.customqrgenerator.style.RandomBased
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.techsavvy.showcaseme.common.URLS
import com.techsavvy.showcaseme.ui.qr.GenerateQRViewModel
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
@Composable
fun QRCodeViewHome(modifier : Modifier = Modifier,url : String) {
    val qrFinal = QrCodeDrawable(QrData.Url(URLS.WEB_URL + url))
    if(qrFinal.isVisible ) {
        Box(contentAlignment = Alignment.Center,
            modifier = modifier) {
            Image(
                qrFinal.toBitmap(1024,1024).asImageBitmap(),
                ""
            )
        }
    }else{
        CircularProgressIndicator()
    }
}
data class Heart(
    @FloatRange(from = 1.0, to = 2.0)
    val padding: Float = 1.1f,
    override val seed: Long = 233
)
    : QrShape, RandomBased {

    override val shapeSizeIncrease: Float =
        1 + (padding * sqrt(2.0) - 1).toFloat()

    override fun apply(matrix: QrCodeMatrix): QrCodeMatrix = with(matrix) {
        val padding = padding.coerceIn(1f, 2f)
        val added = (((size * padding * sqrt(2.0)) - size) / 2).roundToInt()
        val newSize = size + 2 * added
        val newMatrix = QrCodeMatrix(newSize)

        val center = newSize / 2f

        val random = Random(seed)

        for (i in 0 until newSize) {
            for (j in 0 until newSize) {
                if (isHeartPixel(i, j, center, added, newSize)) {
                    newMatrix[i, j] = if (random.nextBoolean()) QrCodeMatrix.PixelType.DarkPixel
                    else QrCodeMatrix.PixelType.LightPixel
                }
            }
        }

        for (i in 0 until size) {
            for (j in 0 until size) {
                newMatrix[added + i, added + j] = this[i, j]
            }
        }
        return newMatrix
    }

    override fun pixelInShape(i: Int, j: Int, modifiedByteMatrix: QrCodeMatrix): Boolean =
        isHeartPixel(i, j, modifiedByteMatrix.size / 2f, 0, modifiedByteMatrix.size)

    private fun isHeartPixel(i: Int, j: Int, center: Float, added: Int, newSize: Int): Boolean {
        val x = i - center
        val y = j - center

        return ((x * x + y * y <= center * center) &&
                ((y > -0.6 * x && y > 0.6 * x) || (y > 1.6 * x && y > -1.6 * x)))
    }
}

data class Triangle(
    @FloatRange(from = 1.0, to = 2.0)
    val padding: Float = 1.1f,
    override val seed: Long = 233
)
    : QrShape, RandomBased {

    override val shapeSizeIncrease: Float =
        1 + (padding * sqrt(3.0) - 1).toFloat()

    override fun apply(matrix: QrCodeMatrix): QrCodeMatrix {
        val padding = padding.coerceIn(1f, 2f)
        val added = (((matrix.size * padding * sqrt(3.0)) - matrix.size) / 2).roundToInt()
        val newSize = matrix.size + 2 * added
        val newMatrix = QrCodeMatrix(newSize)

        val center = newSize / 2f

        for (i in 0 until newSize) {
            for (j in 0 until newSize) {
                if (isTrianglePixel(i, j, center, added)) {
                    newMatrix[i, j] = if (isWithinQRBounds(i, j, matrix.size, added)) {
                        matrix[i - added, j - added]
                    } else {
                        QrCodeMatrix.PixelType.DarkPixel
                    }
                }
            }
        }
        return newMatrix
    }

    override fun pixelInShape(i: Int, j: Int, modifiedByteMatrix: QrCodeMatrix): Boolean =
        isTrianglePixel(i, j, modifiedByteMatrix.size / 2f, 0)

    private fun isTrianglePixel(i: Int, j: Int, center: Float, added: Int): Boolean {
        val x = i - center
        val y = j - center * sqrt(3f) / 3

        val line1 = y >= -x * sqrt(3f)
        val line2 = y >= x * sqrt(3f)

        return (line1 && line2 && y <= center * sqrt(3f))
    }

    private fun isWithinQRBounds(i: Int, j: Int, size: Int, added: Int): Boolean {
        return i - added in 0 until size && j - added in 0 until size
    }
}


@Composable
fun QRContainer(modifier : Modifier,viewModel : GenerateQRViewModel) {
    Image(
        viewModel.qrDrawable.value.toBitmap(1024, 1024).asImageBitmap(),
        "",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .clip(if(viewModel.curQRShape == QrShape.Circle()) CircleShape else RectangleShape)
            .border(viewModel.curOutlineSize,viewModel.qrOutlineColor,if(viewModel.curQRShape == QrShape.Circle()) CircleShape else RectangleShape)
//            .border(0.dp,viewModel.qrOutlineColor,if(viewModel.curQRShape == QrShape.Circle()) CircleShape else RectangleShape)
            .padding(if(viewModel.curQRShape == QrShape.Default) 5.dp else 0.dp)
            .padding(viewModel.curOutlineSize - 1.dp)
    )
}
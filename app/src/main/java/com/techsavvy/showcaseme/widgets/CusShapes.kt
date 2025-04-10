package com.techsavvy.showcaseme.widgets

import android.graphics.PathMeasure
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun TriangleShapeBox(
    modifier: Modifier = Modifier,
    color: Color = Color.Blue,
    content : @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(300.dp) // Modify size as needed
            .background(color = Color.Transparent)
            .clip(
                TriangleS()
            )
            .background(color = color),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val trianglePath = Path().apply {
                trianglePath(size)
            }

            // Draw the triangle shape
            drawPath(
                path = trianglePath,
                color = color,
                style = Stroke(width = 4f)
            )

            // Draw dots inside the triangle shape
            val dotRadius = 4f
            val bounds = trianglePath.getBounds()
            val dotSpacing = 10f // Adjust spacing between dots as needed
            for (x in bounds.left.toInt()..bounds.right.toInt() step dotSpacing.toInt()) {
                for (y in bounds.top.toInt()..bounds.bottom.toInt() step dotSpacing.toInt()) {
                    val point = Offset(x.toFloat(), y.toFloat())
                    if (isPointInPath(point, trianglePath)) {
                        drawCircle(
                            color = Color.Black,
                            radius = dotRadius,
                            center = point
                        )
                    }
                }
            }
        }
        content()
    }
}

class TriangleS : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            trianglePath(size = size)
            close()
        }
        return Outline.Generic(path)
    }
}

fun Path.trianglePath(size: Size): Path {
    val width: Float = size.width
    val height: Float = size.height

    moveTo(width / 2, 0f)
    lineTo(0f, height)
    lineTo(width, height)
    close()

    return this
}

@Composable
fun HeartShapeBox(
    modifier: Modifier = Modifier,
    color: Color = Color.Red,
    content : @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(300.dp) // Modify size as needed
            .background(color = Color.Transparent)
            .clip(
                HeartS()
            )
            .background(color = color),
        contentAlignment = Alignment.Center
    ){ Canvas(modifier = Modifier.matchParentSize()) {
        Path().apply {
            heartPath(size)
        }
        drawHeartWithBackground(color,2000)
    }
        content()
    }
}

fun DrawScope.drawHeartWithBackground(color: Color, numberOfDots: Int) {
    val heartPath = Path().apply {
        heartPath(size)
    }

    // Draw the heart shape
    drawPath(
        path = heartPath,
        color = color,
        style = Stroke(width = 4f)
    )

    // Draw random dots inside the heart shape for the background
    repeat(numberOfDots) {
        val randomX = (0..size.width.toInt()).random().toFloat()
        val randomY = (0..size.height.toInt()).random().toFloat()

        val point = Offset(randomX, randomY)
        if (isPointInPath(point, heartPath)) {
            drawCircle(
                color = Color.Black,
                radius = 2f,
                center = point
            )
        }
    }
}
private fun isPointInPath(point: Offset, path: Path): Boolean {
    val bounds = path.getBounds()
    if (!bounds.contains(point)) return false

    val pathMeasure = PathMeasure(path.asAndroidPath(), false)
    var inside = false

    val stepSize = 1 // Adjust the step size for precision
    val pathLength = pathMeasure.length.toInt()

    for (i in 0 until pathLength step stepSize) {
        val pos = FloatArray(2)
        val tan = FloatArray(2)

        pathMeasure.getPosTan(i.toFloat(), pos, tan)

        val x = pos[0]
        val y = pos[1]

        val x1 = pos[0] + stepSize
        val y1 = pos[1] + stepSize

        if (((y1 >= point.y) != (y >= point.y)) &&
            (point.x <= (x - x1) * (point.y - y1) / (y - y1) + x)
        ) {
            inside = !inside
        }
    }

    return inside
}


class HeartS: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            heartPath(size = size)
            close()
        }
        return Outline.Generic(path)
    }
}

fun Path.heartPath(size: Size): Path {
    //the logic is taken from StackOverFlow [answer](https://stackoverflow.com/a/41251829/5348665)and converted into extension function

    val width: Float = size.width
    val height: Float = size.height

    // Starting point
    moveTo(width / 2, height / 5)

    // Upper left path
    cubicTo(
        5 * width / 14, 0f,
        0f, height / 15,
        width / 28, 2 * height / 5
    )

    // Lower left path
    cubicTo(
        width / 14, 2 * height / 3,
        3 * width / 7, 5 * height / 6,
        width / 2, height
    )

    // Lower right path
    cubicTo(
        4 * width / 7, 5 * height / 6,
        13 * width / 14, 2 * height / 3,
        27 * width / 28, 2 * height / 5
    )

    // Upper right path
    cubicTo(
        width, height / 15,
        9 * width / 14, 0f,
        width / 2, height / 5
    )
    return this
}
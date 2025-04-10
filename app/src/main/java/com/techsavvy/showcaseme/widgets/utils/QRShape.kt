package com.techsavvy.showcaseme.widgets.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorShapeModifier

data class QRShape(
    val id : Int,
    val name : String,
    val shape : QrVectorShapeModifier,
    val image : @Composable (modifier : Modifier) -> Unit
)

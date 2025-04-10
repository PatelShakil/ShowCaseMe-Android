package com.techsavvy.showcaseme.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast
import com.techsavvy.showcaseme.widgets.utils.shimmerEffect

@Composable
fun LoadingWithContent(
    isLoading: Boolean,
    errorMsg : String = "",
    loadingContent: @Composable ()->Unit,
    realContent: @Composable ()->Unit,

) {
    val toast = LocalSmartToast.current
    if (errorMsg == "") {
        if (isLoading) {
            loadingContent()
        } else {
            realContent()
        }
    }else{
toast.show(errorMsg)
    }

}

@Composable
fun LoadingBox(modifier : Modifier = Modifier) {
    Card(modifier = modifier,
        colors = CardDefaults.cardColors(Color.Transparent),
        shape = RoundedCornerShape(15.dp)
    ) {
        Box(modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .shimmerEffect())
    }
}

@Composable
fun LoadingText(modifier : Modifier = Modifier) {
    Card(modifier = modifier,
        colors = CardDefaults.cardColors(Color.Transparent),
        shape = RoundedCornerShape(15.dp)
    ) {
        Box(modifier = modifier
            .height(30.dp)
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(15.dp))
            .shimmerEffect())
    }
}
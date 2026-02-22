package com.techsavvy.showcaseme.ui.qr

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.style.BitmapScale
import com.github.alexzhirkevich.customqrgenerator.style.QrShape
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.asBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.asFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.asPixelShape
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.common.URLS
import com.techsavvy.showcaseme.data.models.api_response.JwtVerifyResponse
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import com.techsavvy.showcaseme.ui.qr.shapes.Heart
import com.techsavvy.showcaseme.utils.Helpers
import com.techsavvy.showcaseme.widgets.HeartS
import com.techsavvy.showcaseme.widgets.utils.QRShape
import com.techsavvy.showcaseme.widgets.utils.STORAGE_PATH
import com.techsavvy.showcaseme.widgets.utils.convertLongToDate
import com.techsavvy.showcaseme.widgets.utils.requestPermissionsIfNecessary
import com.techsavvy.showcaseme.widgets.utils.saveFile
import com.techsavvy.showcaseme.widgets.utils.shareImageWithCaption
import com.techsavvy.showcaseme.widgets.utils.viewImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateQRViewModel @Inject constructor(
    private val repo : AuthRepo,
    private val helper : Helpers
) : ViewModel(){
    val url = mutableStateOf(URLS.WEB_URL) //your own URL -- BASE_URL / SLUG https://savvyqr.techsavvysolution.in

    var defOptions = createQrVectorOptions {
        colors {
            dark = QrVectorColor.Solid(Color.Black.toArgb())
            frame = QrVectorColor.Solid(Color.Black.toArgb())
            ball = QrVectorColor.Solid(Color.Black.toArgb())
        }
    }

    private val _user = mutableStateOf<Resource<Response<JwtVerifyResponse>>?>(null)
    val user = _user

    init{
        getUserDetails()
    }


    fun onShare(dest:String = "",context: Context,userName : String,bitmap : Bitmap)= viewModelScope.launch{
        val filename = "QR_" + convertLongToDate(System.currentTimeMillis(),"hh_mm_a_dd_MMM_yyyy_")
        if(requestPermissionsIfNecessary(context as Activity)){
            if(saveFile(filename,bitmap,context)){
                Toast.makeText(context, "QR Saved to Gallery", Toast.LENGTH_SHORT).show()
                shareImageWithCaption(dest,Uri.parse(STORAGE_PATH + "/$filename.png"),"${url.value} \n\nClick to view full Social site of ${userName}\n\nDownload ShowCaseMe Application\nhttps://patelshakil.tech/showcaseme.html",context)
            }

        }else{
            requestPermissionsIfNecessary(context)
        }

    }

    fun onSave(context : Activity,bitmap : Bitmap)=viewModelScope.launch {
        val filename = "ShowCaseMe_QR_" + convertLongToDate(System.currentTimeMillis(),"hh_mm_a_dd_MMM_yyyy")
        if(requestPermissionsIfNecessary(context)){
            if(saveFile(filename,bitmap,context)){
                Toast.makeText(context, "QR Saved to Gallery", Toast.LENGTH_SHORT).show()
                viewImage(context,filename)
            }
        }else{
            requestPermissionsIfNecessary(context)
        }
    }

    fun getUserDetails()=viewModelScope.launch {
        user.value = Resource.Loading
        user.value = repo.jwtVerify(helper.getString("token"))
    }


    var qrDrawable = mutableStateOf(QrCodeDrawable(QrData.Url(url.value),defOptions))
    var qrLogo : Drawable? = null
    var qrPixelStartColor  = Color(0xFF000000)
    var qrPixelEndColor : Color = Color(0xFF000000)
    var qrBallColor : Color = Color(0xFF000000)
    var qrFrameColor : Color = Color(0xFF000000)
    var qrOutlineColor : Color = Color(0xFF000000)
    var curPixelShape : QRShape = generatePixelShapeList()[0]
    var curBallShape : QRShape = generateBallShapeList()[0]
    var curFrameShape : QRShape = generateFrameShapeList()[0]
    var curQRShape : QrShape = generateQRShape(1)
    var curOutlineSize : Dp = 5.dp

    fun generateQRCode()=viewModelScope.launch{
            qrDrawable.value = QrCodeDrawable(QrData.Url(url.value),createQrVectorOptions {
                if(qrLogo != null) {
                    logo {
                        drawable = qrLogo
                        size = .25f
                        padding = QrVectorLogoPadding.Natural(.1f)
                        shape = QrVectorLogoShape.Circle
                        scale = BitmapScale.CenterCrop
                    }
                }
                colors {
                    dark = QrVectorColor.LinearGradient(
                        colors = listOf(.25f to qrPixelStartColor.toArgb(),
                            .75f to qrPixelEndColor.toArgb(),
                            .75f to qrPixelStartColor.toArgb(),
                            .25f to qrPixelEndColor.toArgb()),
                        orientation = QrVectorColor.LinearGradient.Orientation.Vertical

                    )

                    ball = QrVectorColor.Solid(qrBallColor.toArgb())

                    frame = QrVectorColor.Solid(qrFrameColor.toArgb())
                }
                shapes {
                    darkPixel = curPixelShape.shape.asPixelShape()
                    ball = curBallShape.shape.asBallShape()
                    frame = curFrameShape.shape.asFrameShape()
                }
                codeShape = curQRShape
            })
    }

    fun resetQROptions()=viewModelScope.launch {
        qrLogo = null
        qrPixelStartColor = Color(0xFF000000)
        qrPixelEndColor = Color(0xFF000000)
        qrBallColor = Color(0xFF000000)
        qrFrameColor = Color(0xFF000000)
        curPixelShape = generatePixelShapeList()[0]
        curBallShape = generateBallShapeList()[0]
        curFrameShape = generateFrameShapeList()[0]
        curQRShape = generateQRShape(1)
        generateQRCode()
    }

    fun generateQRShape(value : Int): QrShape {
        return when(value){
            1 -> QrShape.Default
            2 -> QrShape.Circle()
            else->{
                QrShape.Default
            }
        }
    }

    fun generatePixelShapeList() = listOf(
        QRShape(1, "Square", QrVectorPixelShape.Rect()) {
            SquareBox()
        },
        QRShape(2, "Circle", QrVectorPixelShape.Circle(1f)) {
            CircleBox()
        },
        QRShape(3, "Rhombus", QrVectorPixelShape.Rhombus()) {
            RhombusBox()
        },
        QRShape(4, "Heart", Heart()) {
            HeartBox()
        },
        QRShape(5, "Round Corners", QrVectorPixelShape.RoundCorners(.25f)) {
            RoundCorners()
        },
        QRShape(6, "Star", QrVectorPixelShape.Star) {
            StarBox()

        },
        QRShape(7, "Round Corners Vertical", QrVectorPixelShape.RoundCornersVertical()) {},
        QRShape(8, "Round Corners Horizontal", QrVectorPixelShape.RoundCornersHorizontal()) {},

        )
    fun generateBallShapeList() = listOf(
        QRShape(1,"Square", QrVectorBallShape.Rect()){
            SquareBox()
        }
        , QRShape(2,"Circle", QrVectorBallShape.Circle()){
            CircleBox()
        }
        , QRShape(3,"Rhombus", QrVectorBallShape.Rhombus()){
                                                           RhombusBox()
        }
        , QRShape(4,"Heart", Heart()){
                                     HeartBox()
        }
        , QRShape(5,"Round Corners", QrVectorBallShape.RoundCorners(.25f)){
            RoundCorners()
        }

    )
    fun generateFrameShapeList() = listOf(
        QRShape(1,"Square",QrVectorFrameShape.Rect()){
            SquareBox()
        },
        QRShape(2,"Circle",QrVectorFrameShape.Circle()){
            CircleBox()
        },
        QRShape(3,"Rhombus", QrVectorFrameShape.AsPixelShape(QrVectorPixelShape.Rhombus())){
                                                                                           RhombusBox()
        }  ,
        QRShape(4,"Heart", QrVectorFrameShape.AsPixelShape(Heart().asPixelShape())){
            HeartBox()
        }    ,
        QRShape(5,"Round Corners",QrVectorFrameShape.RoundCorners(.25f)){
            RoundCorners()
        }


        )
    fun generateQRShapeList() = listOf(
        QrShape.Default,
        QrShape.Circle()
    )

    @Composable
    fun RoundCorners() {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.Black)
        )
    }

    @Composable
    fun SquareBox() {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(RectangleShape)
                .background(Color.Black)
        )
    }
    @Composable
    fun CircleBox() {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
    }

    @Composable
    fun StarBox() {
        Box(
            modifier = Modifier.size(15.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()
                val halfWidth = size.width / 2
                val halfHeight = size.height / 2
                path.moveTo(halfWidth, 0f)
                path.lineTo(halfWidth + halfWidth / 4, halfHeight)
                path.lineTo(size.width, halfHeight)
                path.lineTo(halfWidth + halfWidth / 3, halfHeight + halfHeight / 3)
                path.lineTo(halfWidth + halfWidth / 2, size.height)
                path.lineTo(halfWidth, halfHeight + halfHeight / 2)
                path.lineTo(halfWidth - halfWidth / 2, size.height)
                path.lineTo(halfWidth - halfWidth / 3, halfHeight + halfHeight / 3)
                path.lineTo(0f, halfHeight)
                path.lineTo(halfWidth - halfWidth / 4, halfHeight)
                path.close()

                drawPath(
                    path = path,
                    color = Color.Black
                )
            }
        }
    }

    @Composable
    fun HeartBox() {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(HeartS())
                .background(Color.Black)
        )
    }

    @Composable
    fun RhombusBox() {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CutCornerShape(10.dp))
                .background(Color.Black)
        )
    }


}
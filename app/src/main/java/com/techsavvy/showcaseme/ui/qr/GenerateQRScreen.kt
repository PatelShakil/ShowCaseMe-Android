package com.techsavvy.showcaseme.ui.qr

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.NavController
import com.github.alexzhirkevich.customqrgenerator.style.QrShape
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.techsavvy.showcaseme.widgets.CusColorPicker
import com.techsavvy.showcaseme.widgets.LoadingBox
import com.techsavvy.showcaseme.widgets.LoadingWithContent
import com.techsavvy.showcaseme.widgets.QRContainer
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.URLS
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.ui.theme.WHITE
import com.techsavvy.showcaseme.ui.theme.YellowColor
import com.techsavvy.showcaseme.widgets.utils.isInternetAvailable
import com.techsavvy.showcaseme.widgets.utils.loadBitmapFromLocalStorage
import com.techsavvy.showcaseme.widgets.utils.rememberGetContentContractLauncher
import com.techsavvy.showcaseme.widgets.utils.shimmerEffect
import kotlinx.coroutines.launch
import org.json.JSONObject


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenerateQRScreen(navController: NavController,viewModel: GenerateQRViewModel) {

        val context = LocalContext.current
    var logoImg by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFFF0F0F0)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                viewModel.user.value.let {
                    when (it) {
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .align(Center)
                                        .shimmerEffect()
                                )
                            }
                        }

                        is Resource.Success -> {
                            val user = it.result.data?.user
                            viewModel.url.value = URLS.WEB_URL + user?.username
                            val scope = rememberCoroutineScope()
/*
                            viewModel.subscription.collectAsState().value.let {
                                when (it) {
                                    is Resource.Loading -> {
                                        Dialog(onDismissRequest = {}) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                            ) {
                                                Dialog(onDismissRequest = { }) {
                                                    Card {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 20.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            LoadingBox(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(horizontal = 15.dp)
                                                                    .height(5.dp)
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(
                                                                    bottom = 20.dp,
                                                                    top = 5.dp
                                                                ),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                "Loading...",
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    is Resource.Success -> {
                                        if (it.result != null) {
                                            if (it.result.status && System.currentTimeMillis() >= it.result.endTimestamp) {
                                                ConfirmationDialog(isPremium = true,
                                                    title = "Want to customise your QR code ?\n" +
                                                            "Buy premium membership plan",
                                                    onDismiss = {
                                                        navController.popBackStack()
                                                    },
                                                    onSkip = {
                                                        navController.navigate(AppScreen.QRShare.route)
                                                    },
                                                    onConfirm = {
                                                        val orderRequest = JSONObject()
                                                        orderRequest.put(
                                                            "amount",
                                                            amount
                                                        ) // amount in the smallest currency unit
                                                        orderRequest.put(
                                                            "currency",
                                                            "INR"
                                                        )
                                                        orderRequest.put(
                                                            "name",
                                                            "SmugLinks"
                                                        )
                                                        orderRequest.put(
                                                            "description",
                                                            "Subscription Charges"
                                                        )
                                                        orderRequest.put(
                                                            "image",
                                                            "https://smuglinks.com/api/v1/assets/static/logo.jpg"
                                                        )
                                                        orderRequest.put(
                                                            "theme.color",
                                                            "#02af55"
                                                        )
                                                        orderRequest.put(
                                                            "receipt",
                                                            "order_" + generateRandomString(
                                                                9
                                                            )
                                                        )
                                                        orderRequest.put(
                                                            "uid",
                                                            user.uid
                                                        )

                                                        scope.launch {
                                                            Checkout.preload(context)

                                                            val prefill = JSONObject()
                                                            prefill.put(
                                                                "email",
                                                                user.email
                                                            )
                                                            prefill.put(
                                                                "contact",
                                                                user.phone
                                                            )

                                                            orderRequest.put(
                                                                "prefill",
                                                                prefill
                                                            )
                                                            if(id.isNotEmpty() && amount.isNotEmpty() ) {
                                                                co.open(
                                                                    context as Activity,
                                                                    orderRequest
                                                                )
                                                            }
                                                        }
                                                    })
                                            }
                                            if (!it.result.status) {
                                                Dialog(
                                                    onDismissRequest = {
                                                        navController.popBackStack()
                                                    }
                                                ) {
                                                    Card(
                                                        colors = CardDefaults.cardColors(
                                                            MaterialTheme.colorScheme.error
                                                        ),
                                                        modifier = Modifier.padding(
                                                            horizontal = 30.dp,
                                                            vertical = 10.dp
                                                        )
                                                    ) {
                                                        Text(
                                                            "You're subscription might be suspended. Try to contact developers",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            modifier = Modifier.padding(
                                                                vertical = 5.dp,
                                                                horizontal = 8.dp
                                                            ),
                                                            color = Color.White,
                                                            textAlign = TextAlign.Center
                                                        )

                                                    }
                                                }
                                            }
                                        }
                                    }

                                    is Resource.Failure -> {
                                        when (it.errorCode) {
                                            404, 406 -> {
                                                ConfirmationDialog(isPremium = true,
                                                    title = "Want to customise your QR code ?\n" +
                                                            "Buy premium membership plan",
                                                    onDismiss = {
                                                        navController.popBackStack()
                                                    },
                                                    onSkip = {
                                                        navController.navigate(AppScreen.QRShare.route)
                                                    },
                                                    onConfirm = {
                                                        val orderRequest = JSONObject()
                                                        orderRequest.put(
                                                            "amount",
                                                            amount
                                                        ) // amount in the smallest currency unit
                                                        orderRequest.put(
                                                            "currency",
                                                            "INR"
                                                        )
                                                        orderRequest.put(
                                                            "name",
                                                            "SmugLinks"
                                                        )
                                                        orderRequest.put(
                                                            "description",
                                                            "Subscription Charges"
                                                        )
                                                        orderRequest.put(
                                                            "image",
                                                            "https://smuglinks.com/api/v1/assets/static/logo.jpg"
                                                        )
                                                        orderRequest.put(
                                                            "theme.color",
                                                            "#02af55"
                                                        )
                                                        orderRequest.put(
                                                            "receipt",
                                                            "order_" + generateRandomString(
                                                                9
                                                            )
                                                        )
                                                        orderRequest.put(
                                                            "uid",
                                                            user.uid
                                                        )

                                                        scope.launch {
                                                            Checkout.preload(context)
                                                            val prefill = JSONObject()
                                                            prefill.put(
                                                                "email",
                                                                user.email
                                                            )
                                                            prefill.put(
                                                                "contact",
                                                                user.phone
                                                            )

                                                            orderRequest.put(
                                                                "prefill",
                                                                prefill
                                                            )
                                                            if(id.isNotEmpty() && amount.isNotEmpty()) {
                                                                co.open(
                                                                    context as Activity,
                                                                    orderRequest
                                                                )
                                                            }
                                                        }

                                                    })
                                            }

                                            else -> {
                                                Dialog(onDismissRequest = { navController.popBackStack() }) {
                                                    OnApiFailed(errorMsg = it.errorMsgBody)
                                                }
                                            }
                                        }

                                    }

                                    else -> {

                                    }
                                }
                            }
*/
                            LoadingWithContent(
                                isLoading = !viewModel.qrDrawable.value.isVisible,
                                loadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(250.dp)
                                            .clip(if (viewModel.curQRShape == QrShape.Default) RectangleShape else CircleShape)
                                            .shimmerEffect()
                                    )
                                }) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    QRContainer(
                                        modifier = Modifier
                                            .align(Center)
                                            .size(250.dp), viewModel = viewModel
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(CenterEnd)
                                            .padding(5.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                navController.navigate(Screens.QRShare.route)
                                            },
                                            modifier = Modifier.padding(5.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Share,
                                                "",
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.resetQROptions()

                                            },
                                            modifier = Modifier.padding(5.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                "",
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                "@" + user?.username,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        is Resource.Failure -> {}
                        else -> {}
                    }

                }


            }

            if (isInternetAvailable(context)) {
                Box(modifier = Modifier.fillMaxWidth()) {

//                var pixelStartColor by remember { mutableStateOf(Color(0xFF000000)) }
//                var ballColor by remember { mutableStateOf(Color(0xFF000000)) }
//                var frameColor by remember { mutableStateOf(Color(0xFF000000)) }
//                var pixelEndColor by remember { mutableStateOf(Color(0xFF000000)) }
                    var pixelStartController = rememberColorPickerController()
                    pixelStartController.setWheelColor(viewModel.qrPixelStartColor)
//                viewModel.qrPixelStartColor.value = pixelStartColor

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val getlogoImg = rememberGetContentContractLauncher(onResult = {
                                    if (it != null) {
                                        logoImg = it.toString()
                                        loadBitmapFromLocalStorage(context, logoImg, {
                                            viewModel.qrLogo = it.toDrawable(context.resources)
                                            viewModel.generateQRCode()
                                        }) {

                                        }
                                    }
                                })
                                if (viewModel.qrLogo != null) {
                                    Image(
                                        viewModel.qrLogo?.toBitmap(1024, 1024)!!.asImageBitmap(),
                                        "",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Button(
                                    {
                                        if (viewModel.qrLogo == null) {
                                            getlogoImg.launch("image/*")
                                        } else {
                                            viewModel.qrLogo = null
                                            viewModel.generateQRCode()
                                        }
                                    }
                                ) {
                                    Text(
                                        if (viewModel.qrLogo == null) "Choose Logo" else "Delete Logo",
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color.Transparent)
                        ) {
                            Text(
                                "Dots Shape",
                                modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                for (it in viewModel.generatePixelShapeList()) {
                                    Card(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(2.dp)
                                            .clickable {
                                                viewModel.curPixelShape = it
                                                viewModel.generateQRCode()
                                            },
                                        colors = CardDefaults.cardColors(
                                            if (viewModel.curPixelShape.id == it.id) YellowColor else WHITE,
                                        ),

                                        border = BorderStroke(
                                            .5.dp,
                                            Black
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(60.dp),
                                            horizontalAlignment = CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            it.image(Modifier)
                                            Text(
                                                it.name,
//                                                fontFamily = SF_FONT_BOLD.toFontFamily(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                softWrap = true,
                                                modifier = Modifier.padding(3.dp),
                                                maxLines = 3,
                                                lineHeight = TextUnit(10f, TextUnitType.Sp),
                                                textAlign = TextAlign.Center,
                                                color = Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color.Transparent)
                        ) {
                            Text(
                                "EyeFrame Shape",
                                modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                viewModel.generateFrameShapeList().forEach {
                                    Card(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(2.dp)
                                            .clickable {
                                                viewModel.curFrameShape = it
                                                viewModel.generateQRCode()
                                            },
                                        colors = CardDefaults.cardColors(
                                            if (viewModel.curFrameShape.id == it.id) YellowColor else WHITE,
                                        ),
                                        border = BorderStroke(
                                            .5.dp,
                                            Black
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(60.dp),
                                            horizontalAlignment = CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            it.image(Modifier)
                                            Text(
                                                it.name,
//                                                fontFamily = SF_FONT_BOLD.toFontFamily(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                softWrap = true,
                                                modifier = Modifier.padding(3.dp),
                                                maxLines = 3,
                                                lineHeight = TextUnit(10f, TextUnitType.Sp),
                                                textAlign = TextAlign.Center,
                                                color = Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color.Transparent)
                        ) {
                            Text(
                                "EyeBall Shape",
                                modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                viewModel.generateBallShapeList().forEach {
                                    Card(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(2.dp)
                                            .clickable {
                                                viewModel.curBallShape = it
                                                viewModel.generateQRCode()
                                            },
                                        colors = CardDefaults.cardColors(
                                            if (viewModel.curBallShape.id == it.id) YellowColor else WHITE,
                                        ),
                                        border = BorderStroke(
                                            .5.dp,
                                            Black
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(60.dp),
                                            horizontalAlignment = CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            it.image(Modifier)
                                            Text(
                                                it.name,
//                                                fontFamily = SF_FONT_BOLD.toFontFamily(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                softWrap = true,
                                                modifier = Modifier.padding(3.dp),
                                                maxLines = 3,
                                                lineHeight = TextUnit(10f, TextUnitType.Sp),
                                                textAlign = TextAlign.Center,
                                                color = Black

                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color.Transparent)
                        ) {
                            Text(
                                "QR Shape",
                                modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                viewModel.generateQRShapeList().forEach {
                                    Card(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(2.dp)
                                            .clickable {
                                                viewModel.curQRShape = it as QrShape
                                                viewModel.generateQRCode()
                                            },
                                        colors = CardDefaults.cardColors(
                                            if (viewModel.curQRShape == it) YellowColor else WHITE,
                                        ),
                                        border = BorderStroke(
                                            .5.dp,
                                            Black
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(60.dp),
                                            horizontalAlignment = CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if (it is QrShape.Circle) {
                                                viewModel.generatePixelShapeList()[1].image(Modifier)
                                            } else {
                                                viewModel.generatePixelShapeList()[0].image(Modifier)
                                            }
                                            Text(
                                                if (it is QrShape.Circle) "Round" else "Square",
//                                                fontFamily = SF_FONT_BOLD.toFontFamily(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                softWrap = true,
                                                modifier = Modifier.padding(3.dp),
                                                maxLines = 3,
                                                lineHeight = TextUnit(10f, TextUnitType.Sp),
                                                textAlign = TextAlign.Center,
                                                color = Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Card(
                            modifier = Modifier
                                .padding(0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color.Transparent),
                            border = BorderStroke(.5.dp, Gray)
                        ) {
                            Text(
                                "QR Border Size",
                                modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .clickable {
                                            viewModel.curOutlineSize = 3.dp
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = viewModel.curOutlineSize == 3.dp,
                                        onClick = {
                                            viewModel.curOutlineSize = 3.dp
                                        },
                                        modifier = Modifier.padding(end = 2.dp),
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = viewModel.qrOutlineColor
                                        )
                                    )
                                    Text(text = "Small")
                                }
                                Row(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .clickable {
                                            viewModel.curOutlineSize = 5.dp
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = viewModel.curOutlineSize == 5.dp,
                                        onClick = {
                                            viewModel.curOutlineSize = 5.dp
                                        },
                                        modifier = Modifier.padding(end = 2.dp),
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = viewModel.qrOutlineColor
                                        )
                                    )
                                    Text(text = "Medium")
                                }
                                Row(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .clickable {
                                            viewModel.curOutlineSize = 7.dp
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = viewModel.curOutlineSize == 7.dp,
                                        onClick = {
                                            viewModel.curOutlineSize = 7.dp
                                        },
                                        modifier = Modifier.padding(end = 2.dp),
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = viewModel.qrOutlineColor,

                                            )
                                    )
                                    Text(text = "Thick")
                                }

                            }
                        }
                        Spacer(Modifier.height(5.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            CusColorPicker("Start Color",
                                curColor = viewModel.qrPixelStartColor,
                                onColorChange = {
                                    viewModel.qrPixelStartColor = it
                                    viewModel.generateQRCode()
                                }
                            )

                            CusColorPicker("End Color",
                                curColor = viewModel.qrPixelEndColor,
                                onColorChange = {
                                    viewModel.qrPixelEndColor = it
                                    viewModel.generateQRCode()
                                })
                            CusColorPicker("Ball Color",
                                curColor = viewModel.qrBallColor,
                                onColorChange = {
                                    viewModel.qrBallColor = it
                                    viewModel.generateQRCode()
                                })
                            CusColorPicker(
                                "Border Color",
                                curColor = viewModel.qrOutlineColor,
                                onColorChange = {
                                    viewModel.qrOutlineColor = it
                                    viewModel.generateQRCode()
                                })
                            CusColorPicker(
                                "EyeFrame Color",
                                curColor = viewModel.qrFrameColor,
                                onColorChange = {
                                    viewModel.qrFrameColor = it
                                    viewModel.generateQRCode()
                                })
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Center
                ) {
                    Text("Please turn on the internet...")
                }
            }
        }
}






package com.techsavvy.showcaseme.ui.qr

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.Color.WHITE
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.widgets.LoadingBox
import com.techsavvy.showcaseme.widgets.QRContainer
import com.techsavvy.showcaseme.widgets.ScrollableCapturable
import com.techsavvy.showcaseme.widgets.rememberCaptureController


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QRShareScreen(navController: NavController,viewModel: GenerateQRViewModel) {

    var isShareDialog by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            viewModel.user.value.let {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val context = LocalContext.current
                        var bitmap by remember {
                            mutableStateOf(
                                viewModel.qrDrawable.value.toBitmap(
                                    2048,
                                    2048
                                )
                            )
                        }
                        var isSave by remember { mutableStateOf(true) }
                        val view = ComposeView(context).apply {
                            setContent {
                                QRViewShare(viewModel = viewModel)
                            }
                        }

                        val controller = rememberCaptureController()
                        ScrollableCapturable(
                            controller = controller,
                            onCaptured = { bmp, throwable ->
                                if (bmp != null) {
                                    bitmap = bmp
                                    if (isSave) {
                                        viewModel.onSave(context as Activity, bitmap)
                                    } else {
                                        isShareDialog = true
                                        viewModel.onShare("", context, it.result.data?.user?.username.toString(), bitmap)
                                    }
                                }
                                if (throwable != null) {
                                    Toast.makeText(
                                        context,
                                        throwable.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.padding(bottom = 90.dp)) {
                            AndroidView(factory = { view })
                        }
//                        if (isShareDialog) {
//                            ShareQRCode(viewModel, it.result.username, bitmap) {
//                                isShareDialog = false
//                            }
//                        }

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(
                                onClick = {
                                    isSave = false
                                    controller.capture()
                                },
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    "",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("QR", color = Color.Black)
                            }
                            Button(
                                onClick = {
                                    isSave = true
                                    controller.capture()
                                },
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    "",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("QR", color = Color.Black)
                            }
                            Button(
                                onClick = {
                                    val clip = ClipData.newPlainText(
                                        "ShowCaseMe",
                                        viewModel.url.value
                                    )
                                    (getSystemService(
                                        context,
                                        ClipboardManager::class.java
                                    ) as ClipboardManager?)?.setPrimaryClip(clip)
                                    Toast.makeText(
                                        context,
                                        "Link copied successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.padding(bottom = 50.dp),
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    "",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Link", color = Color.Black)
                            }


                        }
                    }

                    is Resource.Failure -> {}
                    else -> {}
                }
            }


        }
}

@Composable
fun ShareQRCode(
    viewModel: GenerateQRViewModel,
    userName: String,
    bitmap: Bitmap,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Text("Share QR Code to...",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp,top = 20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ShareComponent(
                    img = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Instagram_logo_2022.svg/2048px-Instagram_logo_2022.svg.png",
                    label = "Instagram"
                ) {
                    viewModel.onShare("ig", context, userName, bitmap)

                }
                ShareComponent(
                    img = "https://upload.wikimedia.org/wikipedia/commons/6/6c/Facebook_Logo_2023.png",
                    label = "Facebook"
                ) {
                    viewModel.onShare("fb", context, userName, bitmap)

                }
                ShareComponent(
                    img = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/WhatsApp.svg/512px-WhatsApp.svg.png",
                    label = "Whatsapp"
                ) {
                    viewModel.onShare("wp", context, userName, bitmap)


                }
                ShareComponent(
                    img = "https://t4.ftcdn.net/jpg/05/62/21/11/360_F_562211118_ITosCsVOmLDnxmOAaHnlhyCqvMsb2QKr.jpg",
                    label = "More"
                ) {
                    viewModel.onShare("", context, userName, bitmap)

                }

            }
        }


    }
}


@Composable
fun ShareComponent(img: String, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(Color.White),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = img, contentDescription = "",
                modifier = Modifier
                    .size(60.dp),
                contentScale = ContentScale.Crop
            )
            Text(label, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun QRViewShare(viewModel: GenerateQRViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(Color.White),
        border = BorderStroke(.5.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewModel.user.value.let {
                when (it) {
                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Loading...",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LoadingBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .padding(horizontal = 20.dp)
                            )
                        }
                    }

                    is Resource.Success -> {
                        QRContainer(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(top = 10.dp, bottom = 5.dp), viewModel = viewModel
                        )
                        if (it.result != null) {
                            Text(
                                "@" + it.result.data?.user?.username,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                viewModel.url.value,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 9.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 3.dp),
                                textAlign = TextAlign.Center,
                                lineHeight = TextUnit(10f, TextUnitType.Sp),
                            )
                            Spacer(Modifier.height(5.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Powered by ",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "@ShowCaseMe",
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }

                    is Resource.Failure -> {}
                    else -> {}
                }
            }

        }
    }
}

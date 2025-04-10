package com.techsavvy.showcaseme.widgets.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.techsavvy.showcaseme.common.Resource
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


const val STORAGE_PATH = "/storage/emulated/0/Pictures"


fun encodeImage(bitmap: Bitmap): String {
    val width = bitmap.width
    val height = bitmap.height
    val pbitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
    val byteArrayOutputStream = ByteArrayOutputStream()
    pbitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    val bytes = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T > jsonToClass(json : String):T{
    return Json.decodeFromString<T>(json)
}

suspend fun handleError(response: HttpResponse): Resource.Failure {
    Log.d("Response",response.toString())
    return Resource.Failure(
        errorCode = response.status.value,
        message = response.bodyAsText()
    )
}

suspend fun getBitmapFromUrl(url : String, context : Context):Bitmap{
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false) // Disable hardware bitmaps.
        .build()
    val result = (loader.execute(request) as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}

fun loadBitmapFromLocalStorage(context: Context, filePath: String, onSuccess: (Bitmap) -> Unit, onError: () -> Unit) {
    val imageLoader = ImageLoader.Builder(context)
        .allowHardware(false) // Disable hardware bitmaps
        .build()

    val request = ImageRequest.Builder(context)
        .data(filePath)
        .target(
            onSuccess = { result ->
                onSuccess(result.toBitmap()) // Handle successful image loading
            },
            onError = {
                onError() // Handle error
            }
        )
        .build()

    imageLoader.enqueue(request)
}


fun generateRandomString(length: Int): String {
    val allowedChars = ('1'..'9') + ('a'..'z') + ('A'..'Z') + '_'
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}



@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun getPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }
}
fun requestPermissionsIfNecessary(activity: Activity): Boolean {
    val permissions = getPermissions()
    ActivityCompat.requestPermissions(activity, permissions, 1)



    return ContextCompat.checkSelfPermission(activity,permissions[0]) == PackageManager.PERMISSION_GRANTED &&
            if (permissions.size > 1) ContextCompat.checkSelfPermission(activity,permissions[1]) == PackageManager.PERMISSION_GRANTED
    else true
}



fun saveFile(filename: String, bitmap: Bitmap,context: Context): Boolean {
    val file = File("$STORAGE_PATH/$filename.png")
    if(!file.exists())
        file.createNewFile()
    return saveBitmapToFile(bitmap,file,context)
}

fun saveBitmapToFile(bitmap: Bitmap, file: File,context: Context): Boolean {
    return try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        scanFile(file.absolutePath,context)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
private fun scanFile(path: String,context: Context) {
    MediaScannerConnection.scanFile(
        context,
        arrayOf<String>(path), null
    ) { path, uri -> Log.i("TAG", "Finished scanning $path") }
}
fun saveBitmapToPictures(bitmap: Bitmap, filename: String, context: Context): Boolean {
    val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png") // Adjust MIME type if needed
    }

    return try {
        val imageUri = context.contentResolver.insert(imageCollection, imageDetails)
        if (imageUri != null) {
            context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            true
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun convertLongToDate(timestamp: Long, format: String): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}

fun viewImage(activity: Activity, filename: String) {
    val file = File(
        "$STORAGE_PATH/$filename.png"
    )

    if (file.exists()) {
        val uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            file
        )
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no PDF viewer app is installed
        }
    }
}
fun shareImageWithCaption(dest : String,imageUri: Uri, caption: String, context: Context) {

    when(dest){
        "fb" ->{
            val intent: Intent? =
                context.packageManager.getLaunchIntentForPackage("com.facebook.katana")
            if (intent != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("image/png")
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    imageUri
                )
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.setPackage("com.facebook.katana")
                context.startActivity(shareIntent)
            } else {
                Toast.makeText(context, "Facebook require..!!", Toast.LENGTH_SHORT).show()
            }

        }
        "ig" ->{
            val intent = context.packageManager.getLaunchIntentForPackage("com.instagram.android");
            if (intent != null)
            {
                val shareIntent = Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setPackage("com.instagram.android");
                context.startActivity(shareIntent);

            }
            else
            {
                Toast.makeText(context, "Instagram require..!!", Toast.LENGTH_SHORT).show();

            }

        }
        "wp" ->{
            try {
                val oShareIntent = Intent()
                oShareIntent.setPackage("com.whatsapp")
                oShareIntent.putExtra(Intent.EXTRA_TEXT, caption)
                oShareIntent.putExtra(Intent.EXTRA_STREAM,imageUri)
                oShareIntent.setType("image/png")
                oShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(oShareIntent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, "WhatsApp require..!!", Toast.LENGTH_SHORT).show()
            }

        }
        else ->{
            val shareIntent = Intent()
            shareIntent.setAction(Intent.ACTION_SEND)
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"SmugLinks")
            shareIntent.putExtra(Intent.EXTRA_TEXT, caption)
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                imageUri
            )
            shareIntent.setType("image/png")
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(Intent.createChooser(shareIntent, "Send QR Code"))
            }catch (e: Exception) {
                Toast.makeText(context,e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun Modifier.shimmerEffect(colorList : List<Color> = listOf(
    Color(0xFFC8E6C9),
    Color(0xFFF5F5F5),
    Color(0xFFC8E6C9)
)): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(500)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = colorList,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}
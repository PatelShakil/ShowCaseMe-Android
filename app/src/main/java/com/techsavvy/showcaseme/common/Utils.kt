package com.techsavvy.showcaseme.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File
import java.io.FileOutputStream


// Kept for source compatibility — real values live in Brand (Brand.kt).
// WEB_URL is the authenticated dashboard; use Brand.PUBLIC_URL for QR codes / public share links.
object URLS{
    const val API_URL = Brand.API_URL
    const val WEB_URL = Brand.DASHBOARD_URL
}

fun isValidUsername(input: String): Boolean {
    if(input.isEmpty()) return false
    val usernameRegex = "^[a-z0-9_]{0,}$".toRegex()
    return usernameRegex.matches(input)
}

fun drawableToFile(context: Context, drawable: Drawable, fileName: String = "temp_image.png"): File {
    // Step 1: Convert Drawable to Bitmap
    val bitmap = (drawable as? BitmapDrawable)?.bitmap
        ?: throw IllegalArgumentException("Drawable must be BitmapDrawable")

    // Step 2: Create file in cache directory
    val file = File(context.cacheDir, fileName)
    file.createNewFile()

    // Step 3: Write bitmap to file
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
    outputStream.flush()
    outputStream.close()

    return file
}


// ── Validators mirroring the Laravel rules in AuthController ──────────────
// register(): name max 100 | email max 150 | phone max 20 | password 6..100
//             state/city max 100 | pincode max 10

fun isValidEmail(input: String): Boolean =
    input.isNotBlank() &&
        input.length <= 150 &&
        android.util.Patterns.EMAIL_ADDRESS.matcher(input.trim()).matches()

/** Phone is optional on the API; when present it must fit `max:20`. */
fun isValidPhone(input: String): Boolean {
    val digits = input.filter { it.isDigit() }
    return input.length <= 20 && digits.length in 7..15
}

fun isValidPassword(input: String): Boolean = input.length in 6..100

fun isValidPincode(input: String): Boolean =
    input.isEmpty() || (input.length <= 10 && input.all { it.isDigit() })

/** Blank optional fields must go to the API as null, not "". */
fun String.orNullIfBlank(): String? = trim().takeIf { it.isNotEmpty() }

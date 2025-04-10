package com.techsavvy.showcaseme.widgets.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class GetContentContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)

            type = input ?: "image/*"
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && intent != null) intent.data else null
    }
}
class GetContentContractMultiple(private val isMultiple: Boolean = false) : ActivityResultContract<String, List<Uri>>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = input ?: "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
        }

        if (isMultiple) {
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()

        if (resultCode == Activity.RESULT_OK && intent != null) {
            intent.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    uris.add(uri)
                }
            }

            if (intent.data != null) {
                uris.add(intent.data!!)
            }
        }

        return uris
    }
}
@Composable
fun rememberGetContentContractLauncherMultiple(
    isMultiple: Boolean = true,
    onResult: (List<Uri>) -> Unit
): ManagedActivityResultLauncher<String, List<Uri>> {
    val getContentContract = remember { GetContentContractMultiple(isMultiple) }
    return rememberLauncherForActivityResult(contract = getContentContract) { result ->
        onResult(result)
    }
}


@Composable
fun rememberGetContentContractLauncher(
    onResult: (Uri?) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    val getContentContract = remember { GetContentContract() }
    return rememberLauncherForActivityResult(contract = getContentContract) { result ->
        onResult(result)
    }
}
package com.example.testopenglapplication.util

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

object UriUtil {
    fun toUri(context: Context, path: String): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                context, context.packageName + ".fileProvider", File(path)
            )
        } else {
            return Uri.fromFile(File(path))
        }
    }
}
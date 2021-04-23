package com.example.testopenglapplication.util

import android.graphics.Bitmap
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtil {

    /**
     * 保存图片
     *
     */
    fun saveBitmapToStorage(bitmap: Bitmap, savePath: String) {
        try {
            val out = FileOutputStream(savePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
package com.example.testopenglapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.GLES30
import android.opengl.GLUtils
import android.provider.MediaStore


object GLTextureUtil {


    /**
     * 资源id 加载纹理
     *
     * @param ctx        上下文
     * @param resId      资源id
     * @param repeatType 重复方式 {@link RepeatType}
     * @return 纹理id
     */
    fun  loadTexture(context: Context,resId:Int,repeatType:RepeatType?=RepeatType.NONE):Int{
        var bitmap=BitmapFactory.decodeResource(context.resources,resId)
        return loadTexture(bitmap, repeatType)
    }

    fun  loadTexture(context: Context,uri:Uri,repeatType:RepeatType?=RepeatType.NONE):Int{
        var bitmap=BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        return loadTexture(bitmap, repeatType)
    }

    /**
     * bitmap 加载纹理
     *
     * @param bitmap     bitmap
     * @param repeatType 重复方式 {@link RepeatType}
     * @return 纹理id
     */
        fun loadTexture(bitmap:Bitmap,repeatType:RepeatType?=RepeatType.NONE):Int{
//生成纹理ID
        //生成纹理ID
        var textures = IntArray(1)

        //(产生的纹理id的数量,纹理id的数组,偏移量)
        GLES30.glGenTextures(1, textures, 0)
        var textureId = textures[0]

        //绑定纹理id
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        //采样方式MIN
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        var wrapS = 0
        var wrapT = 0
        when (repeatType) {
            RepeatType.NONE -> {
                wrapS = GLES30.GL_CLAMP_TO_EDGE
                wrapT = GLES30.GL_CLAMP_TO_EDGE
            }
            RepeatType.REPEAT_X -> {
                wrapS = GLES30.GL_REPEAT
                wrapT = GLES30.GL_CLAMP_TO_EDGE
            }
            RepeatType.REPEAT_Y -> {
                wrapS = GLES30.GL_CLAMP_TO_EDGE
                wrapT = GLES30.GL_REPEAT
            }
            RepeatType.REPEAT -> {
                wrapS = GLES30.GL_REPEAT
                wrapT = GLES30.GL_REPEAT
            }
        }

        //设置s轴拉伸方式---重复
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, wrapS.toFloat())

        //设置t轴拉伸方式---重复
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, wrapT.toFloat())

        //实际加载纹理(纹理类型,纹理的层次,纹理图像,纹理边框尺寸)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle() //纹理加载成功后释放图片

        return textureId
        }


     enum class RepeatType {
        NONE,  //不重复
        REPEAT_X,  //仅x轴重复
        REPEAT_Y,  //仅y轴重复
        REPEAT //x,y重复
    }
}
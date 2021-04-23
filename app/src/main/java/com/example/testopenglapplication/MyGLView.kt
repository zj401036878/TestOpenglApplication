package com.example.testopenglapplication

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLES30
import android.opengl.GLException
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import com.example.testopenglapplication.GLShader.GLTeXiaoTexture
import com.example.testopenglapplication.GLShader.GLTexture
import com.example.testopenglapplication.GLShader.GlPotion
import com.example.testopenglapplication.util.BitmapUtil
import com.example.testopenglapplication.util.ShaderUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyGLView : GLSurfaceView, GLSurfaceView.Renderer {

    private lateinit var glPotion: GlPotion
    private lateinit var glTexture: GLTexture
    private lateinit var glTeXiao: GLTeXiaoTexture


    //Model View Projection Matrix--模型视图投影矩阵
    private var mMVPMatrix = FloatArray(16)
    private var tempMatrix = FloatArray(16)

    //投影矩阵 mProjectionMatrix
    private var mProjectionMatrix = FloatArray(16)

    //视图矩阵 mViewMatrix
    private var mViewMatrix = FloatArray(16)

    // 旋转矩阵
    private val mRotateMatrix = floatArrayOf(1f,0f,0f,0f,
        0f,1f,0f,0f,
        0f,0f,1f,0f,
        0f,0f,0f,1f)


    //截图1
    private var printEnable: Boolean = false
    lateinit var mCaptureBuffer: ByteBuffer
    lateinit var mBitmap: Bitmap
    private var outWidth: Int = 0
    private var outHeight: Int = 0
    lateinit var savePath: String


    constructor(context: Context) : super(context) {
        GLSurfaceView(context, null)
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {

        setEGLContextClientVersion(3)
        setRenderer(this)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
//        glPotion= GlPotion()
//        glTexture= GLTexture(context)


        glTeXiao = GLTeXiaoTexture(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        GLES30.glViewport(0, 0, width, height)
        ShaderUtil.Logi("w=${width} h=${height}")
        val ratio = width.toFloat() / height

        //透视投影矩阵--截锥
        Matrix.frustumM(
            mProjectionMatrix, 0,//接收透视投影的变换矩阵,变换矩阵的起始位置（偏移量）
            -ratio, ratio, -1f, 1f,//相对观察点近面的left right bottom top边距
            3f, 7f)//相对观察点近面距离,相对观察点远面距离
        // 设置相机位置(视图矩阵)
        Matrix.setLookAtM(
            mViewMatrix, 0,//接收相机变换矩阵,变换矩阵的起始位置（偏移量）
            0f, 0f, 3f,//相机位置
            0f, 0f, 0f, //观察点位置
            0.0f, 1.0f, 0.0f
        )//up向量在xyz上的分量


        //截图
        outWidth = width
        outHeight = height
        mCaptureBuffer = ByteBuffer.allocate(width * height * 4)
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    override fun onDrawFrame(gl: GL10?) {

        Matrix.multiplyMM(
            tempMatrix, 0,//接收相乘结果,接收矩阵的起始位置（偏移量）
            mProjectionMatrix, 0,
            mViewMatrix, 0
        )
        //旋转
        Matrix.multiplyMM(
            mMVPMatrix, 0,//接收相乘结果,接收矩阵的起始位置（偏移量）
            tempMatrix, 0,
            mRotateMatrix, 0
        )

//        glPotion.draw(mMVPMatrix)
//        glTexture.draw(mMVPMatrix)
        glTeXiao.draw(mMVPMatrix)

        if (printEnable) {
            printEnable = false
//            mCaptureBuffer.rewind()
//            GLES30.glReadPixels(0,0,outWidth,outHeight,GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE,mCaptureBuffer)
//            mCaptureBuffer.rewind()
//            mBitmap.copyPixelsFromBuffer(mCaptureBuffer)
//            BitmapUtil.saveBitmapToStorage(mBitmap,savePath)
            var tempBitmap = tackPic()
            tempBitmap?.let {
                BitmapUtil.saveBitmapToStorage(it, savePath)
            }
        }

    }


    fun setThreshold(num: Float) {
        glTeXiao.setThreshold(num)
    }

    fun setXiaoGuoType(num: Int) {
        glTeXiao.setXiaoGuoType(num)
    }

    fun setTimeProgress(num: Float) {
        glTeXiao.setTimeProgress(num)
    }

    fun setTextureByALBUM(uri: Uri?) {

        glTeXiao.setTextureByALBUM(1)
    }

    fun saveBitmap(imgWidth: Int, imgHeight: Int, savePath: String) {
        printEnable = true
        this.savePath = savePath
//        glTeXiao.saveBitmap(imgWidth,imgHeight,savepath)
    }

    fun rotatePic(angle:Float){
        Matrix.rotateM(mRotateMatrix, 0, 4f, 0f, 1f, 0f)
    }

    private fun tackPic(): Bitmap? {
        var bitmapBuffer = IntArray(outWidth * outHeight)
        var bitmapSource = IntArray(outWidth * outHeight)
        var intBuffer: IntBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        try {
            GLES30.glReadPixels(
                0, 0, outWidth, outHeight, GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE, intBuffer
            )
            var offset1: Int
            var offset2: Int
            //解决方向颠倒问题
            for (i in 0 until outHeight) {
                offset1 = i * outWidth
                offset2 = (outHeight - i - 1) * outWidth
                for (j in 0 until outWidth) {
                    val texturePixel = bitmapBuffer[offset1 + j]
                    val blue = texturePixel shr 16 and 0xff
                    val red = texturePixel shl 16 and 0x00ff0000
                    val pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            return null
        }
        return Bitmap.createBitmap(bitmapSource, outWidth, outHeight, Bitmap.Config.ARGB_8888)
    }
}
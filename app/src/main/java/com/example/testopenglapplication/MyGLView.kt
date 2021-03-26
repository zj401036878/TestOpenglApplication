package com.example.testopenglapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.example.testopenglapplication.GLShader.GLTeXiaoTexture
import com.example.testopenglapplication.GLShader.GLTexture
import com.example.testopenglapplication.GLShader.GlPotion
import com.example.testopenglapplication.util.GLTextureUtil
import com.example.testopenglapplication.util.ShaderUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLView :GLSurfaceView,GLSurfaceView.Renderer{

    private lateinit var glPotion: GlPotion
    private lateinit var glTexture: GLTexture
    private lateinit var glTeXiao: GLTeXiaoTexture

    var changePic:Boolean=false
    var changeUri:Uri?=null

    //Model View Projection Matrix--模型视图投影矩阵
    private var mMVPMatrix = FloatArray(16)
    //投影矩阵 mProjectionMatrix
    private var mProjectionMatrix = FloatArray(16)
    //视图矩阵 mViewMatrix
    private var mViewMatrix = FloatArray(16)

    constructor(context: Context):super(context){
        GLSurfaceView(context,null)
    }

    constructor(context: Context,attr:AttributeSet):super(context,attr){

        setEGLContextClientVersion(3)
        setRenderer(this)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES30.glClearColor(1f,1f,1f,1f)
//        glPotion= GlPotion()
//        glTexture= GLTexture(context)

        glTeXiao= GLTeXiaoTexture(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        GLES30.glViewport(0,0,width, height)

        val ratio = width.toFloat() / height

        //透视投影矩阵--截锥
        Matrix.frustumM(mProjectionMatrix, 0,//接收透视投影的变换矩阵,变换矩阵的起始位置（偏移量）
            -ratio, ratio, -1f, 1f,//相对观察点近面的left right bottom top边距
            3f, 7f);//相对观察点近面距离,相对观察点远面距离
        // 设置相机位置(视图矩阵)
        Matrix.setLookAtM(mViewMatrix, 0,//接收相机变换矩阵,变换矩阵的起始位置（偏移量）
            0f, 0f, 4f,//相机位置
            0f, 0f, 0f, //观察点位置
            0f, 1.0f, 0.0f)//up向量在xyz上的分量

    }

    override fun onDrawFrame(gl: GL10?) {

        Matrix.multiplyMM(
            mMVPMatrix, 0,//接收相乘结果,接收矩阵的起始位置（偏移量）
            mProjectionMatrix, 0,
            mViewMatrix, 0)
//        glPotion.draw(mMVPMatrix)
//        glTexture.draw(mMVPMatrix)

        if(changePic){
            changePic=false
            //
        }
            glTeXiao.draw(mMVPMatrix)

    }



    fun setThreshold(num:Float){
        glTeXiao.setThreshold(num)
    }

    fun setXiaoGuoType(num:Int){
        glTeXiao.setXiaoGuoType(num)
    }
    fun setTimeProgress(num:Float){
        glTeXiao.setTimeProgress(num)
    }

    fun setTextureByALBUM(uri: Uri?){
        changeUri=uri
        changePic=true

        glTeXiao.setTextureByALBUM(1)
    }


    private fun getTextureId(){
        Glide.with(this).asBitmap().load(changeUri).into(object : CustomViewTarget<View, Bitmap>(this){
            override fun onLoadFailed(errorDrawable: Drawable?) {
                ShaderUtil.Logi("onLoadFailed-thread=${Thread.currentThread().name}")
            }

            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                ShaderUtil.Logi("onLoadFailed-thread=${Thread.currentThread().name}")
                var textureId=GLTextureUtil.loadTexture(resource)
                glTeXiao.setTextureByALBUM(textureId)
                glTeXiao.draw(mMVPMatrix)
            }

            override fun onResourceCleared(placeholder: Drawable?) {
                ShaderUtil.Logi("onResourceCleared-thread=${Thread.currentThread().name}")
            }

        })
    }

}
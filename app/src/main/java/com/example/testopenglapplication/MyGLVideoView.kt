package com.example.testopenglapplication

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.testopenglapplication.GLRander.GLVideoRander
import com.example.testopenglapplication.util.ShaderUtil
import com.example.testopenglapplication.util.UriUtil

class MyGLVideoView :GLSurfaceView{

     lateinit var videoRander: GLVideoRander

    constructor(context: Context):super(context){
        GLSurfaceView(context,null)
    }

    constructor(context: Context, attr: AttributeSet):super(context,attr){
        var path="${context.externalCacheDir!!.toString()}/aaa1.mp4"
        var uri=UriUtil.toUri(context,path)
//        ShaderUtil.Logi("path:${path}")
        videoRander= GLVideoRander(context,uri,path)
        setEGLContextClientVersion(3)
        setRenderer(videoRander)
    }

    fun setThreshold(num:Float){
        videoRander.videoDrawer?.setThreshold(num)
    }

}
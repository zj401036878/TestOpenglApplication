package com.example.testopenglapplication.GLRander

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.Surface
import com.example.testopenglapplication.GLShader.GLVideoShader
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GLVideoRander : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener,MediaPlayer.OnVideoSizeChangedListener{

    private var context:Context?=null
    private var uri:Uri?=null
    private var path:String=""
    var videoDrawer:GLVideoShader?=null

    private var viewWidth:Int=0
    private var viewHeight:Int=0
    private var videoWidth:Int=0
    private var videoHeight:Int=0

    @Volatile
    private var updateSurface = false
    private var textureId:Int=0
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var surfaceTexture:SurfaceTexture

    private var mProjectionMatrix = FloatArray(16)
    private var sTMatrix = FloatArray(16)

    constructor(context: Context,uri:Uri,path:String){
        this.context=context
        this.uri=uri
        this.path=path

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        videoDrawer=GLVideoShader(this.context!!)
        initMediaPlayer()
        mediaPlayer.start()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glClearColor(1f,1f,1f,1f)
        this.viewWidth=width
        this.viewHeight=height
        updateProjection()
        GLES30.glViewport(0,0,width, height)
    }

    private fun updateProjection() {
        var screenRatio = viewWidth.toFloat() / viewHeight
        var videoRatio = videoWidth.toFloat() / videoHeight
        //正交投影矩阵 -1, 1, -1, 1, 表明填充整个视图
        Matrix.orthoM(
            mProjectionMatrix, 0,
            -1f, 1f, -1f, 1f,
            -1f, 1f)
    }

    override fun onDrawFrame(gl: GL10?) {
        synchronized (this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage()
                surfaceTexture.getTransformMatrix(sTMatrix)
                updateSurface = false
            }
        }
        videoDrawer?.draw(textureId, mProjectionMatrix, sTMatrix)

    }


    private fun initMediaPlayer(){
        mediaPlayer= MediaPlayer()
        try {
            if(context!=null&&uri!=null){
                mediaPlayer.setDataSource(context!!,uri!!)

                //设置音频流类型
                var audioAttr=AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
                mediaPlayer.setAudioAttributes(audioAttr)

//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                // 设置循环播放
                mediaPlayer.isLooping=true
                // 设置视频尺寸变化监听器
                mediaPlayer.setOnVideoSizeChangedListener(this)

                // 创建 surface
                var textures = IntArray(1)
                GLES30.glGenTextures(1, textures, 0)
                textureId = textures[0]

                //绑定纹理 id
                GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId)
                surfaceTexture= SurfaceTexture(textureId)
                surfaceTexture.setOnFrameAvailableListener(this)
                var surface=Surface(surfaceTexture)

                // 设置 surface
                mediaPlayer.setSurface(surface)
                surface.release()

                mediaPlayer.prepare()
            }
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        updateSurface = true
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        videoWidth=width
        videoHeight=height
    }

}
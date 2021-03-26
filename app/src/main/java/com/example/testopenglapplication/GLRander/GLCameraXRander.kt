package com.example.testopenglapplication.GLRander

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.Surface
import androidx.camera.core.Preview
import com.example.testopenglapplication.CameraxActivity
import com.example.testopenglapplication.GLShader.GLVideoShader
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCameraXRander : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener{

    private var mProjectionMatrix = FloatArray(16)
    private var sTMatrix = FloatArray(16)

    var textureId:Int=0

    var videoDrawer: GLVideoShader?=null

    @Volatile
    private var updateSurface = false

    private  var preview:Preview?=null

    private lateinit var surfaceTexture:SurfaceTexture
    var previewSurfaceProvider:CameraxActivity.PreviewSurfaceProvider?=null
    var surface:Surface?=null
    var executor:Executor?=null

    constructor( preview: Preview){
        this.preview=preview
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            try{
                videoDrawer=GLVideoShader(null)
                // 创建 surface
                var textures = IntArray(1)
                GLES30.glGenTextures(1, textures, 0)
                textureId = textures[0]
                //绑定纹理 id
                GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId)

                surfaceTexture= SurfaceTexture(textureId)
                surfaceTexture.setOnFrameAvailableListener(this)
                surfaceTexture.attachToGLContext(textureId)
                surface= Surface(surfaceTexture)
                // 设置 surface
                 executor = Executors.newSingleThreadExecutor()
                 previewSurfaceProvider =
                    CameraxActivity.PreviewSurfaceProvider(surface!!, executor!!)



            }catch (e:IOException){
        e.printStackTrace()
    }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glClearColor(1f,1f,1f,1f)
        updateProjection()
        GLES30.glViewport(0,0,width, height)
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

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        updateSurface = true
    }


    private fun updateProjection() {
        //正交投影矩阵 -1, 1, -1, 1, 表明填充整个视图
        Matrix.orthoM(
            mProjectionMatrix, 0,
            -1f, 1f, -1f, 1f,
            -1f, 1f)
    }
}
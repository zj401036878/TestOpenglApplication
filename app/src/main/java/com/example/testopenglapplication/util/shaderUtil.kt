package com.example.testopenglapplication.util

import android.opengl.GLES30
import android.util.Log
import com.example.testopenglapplication.BaseApplication
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.charset.Charset

object ShaderUtil{

    fun transformFloatBuffer(vertexs: FloatArray): FloatBuffer? {
        var buffer: FloatBuffer
        ///每个浮点数:坐标个数* 4字节
        var qbb: ByteBuffer = ByteBuffer.allocateDirect(vertexs.size * 4)
        //使用本机硬件设备的字节顺序
        qbb.order(ByteOrder.nativeOrder())
        // 从字节缓冲区创建浮点缓冲区
        buffer = qbb.asFloatBuffer()
        // 将坐标添加到FloatBuffer
        buffer.put(vertexs)
        //设置缓冲区以读取第一个坐标
        buffer.position(0)
        return buffer
    }

    fun loadShader(type:Int,shaderCodeFromAssets:String):Int{
        var shader= GLES30.glCreateShader(type)
       var shaderCode=readStringFromAssets(shaderCodeFromAssets)
        GLES30.glShaderSource(shader,shaderCode)
        GLES30.glCompileShader(shader)
        return shader
    }

    /**
     * 读取asset file
     */
    fun readStringFromAssets(fileName: String, encodingCode: String = "UTF-8"): String {
        var inputStream: InputStream? = null
        try {
            inputStream = BaseApplication.instance?.resources?.assets?.open(fileName)
            if (inputStream != null) {
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                return String(buffer, Charset.forName(encodingCode))
            }
        } catch (e: Exception) {
            Logi("${e.message}")
        } finally {
            inputStream?.let {
                inputStream.close()
            }
        }
        return ""
    }


     fun initProgram(vsh:String,fsh:String): Int {
        var program: Int
        ////顶点shader代码加载
        var vertexShader: Int =ShaderUtil.loadShader(GLES30.GL_VERTEX_SHADER, vsh)

        //片段shader代码加载
        var fragmentShader: Int = ShaderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, fsh)

        program = GLES30.glCreateProgram() //创建空的OpenGL ES 程序
        GLES30.glAttachShader(program, vertexShader) //加入顶点着色器
        GLES30.glAttachShader(program, fragmentShader) //加入片元着色器
        GLES30.glLinkProgram(program) //创建可执行的OpenGL ES项目
        return program
    }


    val TAG:String="MyGL"
    fun Logi(msg:String,tag:String?= TAG){
            Log.i(tag,msg)
    }
}
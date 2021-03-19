package com.example.testopenglapplication.GLShader

import android.content.Context
import android.icu.text.CaseMap
import android.opengl.GLES11Ext
import android.opengl.GLES30
import com.example.testopenglapplication.R
import com.example.testopenglapplication.util.GLTexture
import com.example.testopenglapplication.util.ShaderUtil
import java.nio.FloatBuffer

class GLVideoShader {
    //顶点数组

    private var vertexes = floatArrayOf(
        1.0f, -1.0f, 0.0f,//原点
        -1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f,
    )

    //贴图数组
    private var textures = floatArrayOf(
        1.0f,0.0f,
        0.0f,0.0f,
        1.0f,1.0f,
        0.0f,1.0f
    )

    private var program = 0
    private var VERTEX_DIMENSION = 3
    private var TEXTURE_DIMENSION = 2

    private var vertBuffer: FloatBuffer? = null
    private var textureBuffer: FloatBuffer? = null

    private var aPosition = 0 //位置的句柄
    private var aTexCoord = 1 //纹理坐标的句柄
    private var uProjectionMatrix :Int=-1//顶点变换矩阵句柄
    private var uTMatrix :Int=-1//纹理变换矩阵句柄
    private var uTexture:Int=-1//纹理句柄

    private var uThreshold:Int=-1//黑白阈值句柄

    private var threshold:Float=0.4f//黑白阈值

    constructor(context: Context){

        program = ShaderUtil.initProgram("video.vsh.glsl","video.fsh.glsl")
        vertBuffer= ShaderUtil.transformFloatBuffer(vertexes)
        textureBuffer= ShaderUtil.transformFloatBuffer(textures)

        uProjectionMatrix = GLES30.glGetUniformLocation(program, "uProjectionMatrix")
        uTMatrix= GLES30.glGetUniformLocation(program, "uTMatrix")
        uTexture=GLES30.glGetUniformLocation(program, "uTexture")

        uThreshold=GLES30.glGetUniformLocation(program, "uThreshold")


    }

    fun draw(textureId:Int,sProjectionMatrix:FloatArray,sTMatrix:FloatArray) {


        //清除颜色缓存和深度缓存
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // 将程序添加到OpenGL ES环境中
        GLES30.glUseProgram(program)

        GLES30.glUniformMatrix4fv(uProjectionMatrix, 1, false,sProjectionMatrix, 0)
        GLES30.glUniformMatrix4fv(uTMatrix, 1, false,sTMatrix, 0)

        //启用顶点句柄
        GLES30.glEnableVertexAttribArray(aPosition)
        //启用纹理坐标句柄
        GLES30.glEnableVertexAttribArray(aTexCoord)
        //准备顶点坐标数据
        GLES30.glVertexAttribPointer(
            aPosition, VERTEX_DIMENSION,
            GLES30.GL_FLOAT, false,
            VERTEX_DIMENSION * 4, vertBuffer
        )

        //准备纹理坐标数据
        GLES30.glVertexAttribPointer(
            aTexCoord, TEXTURE_DIMENSION,
            GLES30.GL_FLOAT, false,
            TEXTURE_DIMENSION *4, textureBuffer
        )




        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat())

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES30.glUniform1i(uTexture, 0)
        //传入参数
        GLES30.glUniform1f(uThreshold,threshold)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)


    }

    fun setThreshold(num:Float){
        threshold=num
    }
}
package com.example.testopenglapplication.GLShader

import android.opengl.GLES30
import android.util.Log
import com.example.testopenglapplication.util.ShaderUtil
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin


class GlPotion {

    //顶点数组
    private var vertexes = floatArrayOf()//以逆时针顺序

    // 颜色数组
    private var colors = floatArrayOf()

    private var program = 0
    private var VERTEX_DIMENSION = 3
    private var COLOR_DIMENSION = 4

    private var vertBuffer: FloatBuffer? = null
    private var colorBuffer: FloatBuffer? = null

    private var aPosition = 0 //位置的句柄
    private var aColor = 1 //颜色的句柄
    private  var uMVPMatrix :Int=-1//顶点变换矩阵句柄


    private fun initData(){

        var splitCount=16
        var r=0.5f
        //顶点坐标数据的初始化

        //顶点坐标数据的初始化
        var verticeCount: Int = splitCount + 2
        vertexes = FloatArray(verticeCount * 3) //坐标数据

        colors = FloatArray(verticeCount * 4) //颜色数据

        var thta: Float = 360f / splitCount
        vertexes[0] = 0f
        vertexes[1] = 0f
        vertexes[2] = 0f
        colors[0] = 1f
        colors[1] = 1f
        colors[2] = 1f
        colors[3] = 1f


//        Log.i("cos0=${cos(Math.toRadians(0.0))} cos90=${cos((90.0))} ","myGL")

        for (n in 1..verticeCount - 1) {
            vertexes[n * 3] = r * cos(Math.toRadians((n - 1) * thta.toDouble()).toFloat()) //x
            vertexes[n * 3 + 1] = r * sin(Math.toRadians((n - 1) * thta.toDouble()).toFloat()) //y
            vertexes[n * 3 + 2] = 0f //z
            colors[4 * n] = 1f
            colors[4 * n + 1] = 0f
            colors[4 * n + 2] = 0f
            colors[4 * n + 3] = 1.0f

//            Log.i("x=${vertexes[n * 3]} y=${vertexes[n * 3+1]}","myGL")
        }
    }


    constructor(){
        initData()
        program = initProgram()
        vertBuffer= ShaderUtil.transformFloatBuffer(vertexes)
        colorBuffer= ShaderUtil.transformFloatBuffer(colors)
        uMVPMatrix = GLES30.glGetUniformLocation(program, "uMVPMatrix")
//        Log.i("uMVPMatrix:${uMVPMatrix}","MyGL")
    }


    private fun initProgram(): Int {
        var program: Int
        ////顶点shader代码加载
        var vertexShader: Int =ShaderUtil.loadShader(GLES30.GL_VERTEX_SHADER, "base.vsh.glsl")

        //片段shader代码加载
        var fragmentShader: Int = ShaderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, "base.fsh.glsl")

        program = GLES30.glCreateProgram() //创建空的OpenGL ES 程序
        GLES30.glAttachShader(program, vertexShader) //加入顶点着色器
        GLES30.glAttachShader(program, fragmentShader) //加入片元着色器
        GLES30.glLinkProgram(program) //创建可执行的OpenGL ES项目
        return program
    }

    fun draw(mvpMatrix:FloatArray) {
        //清除颜色缓存和深度缓存
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // 将程序添加到OpenGL ES环境中
        GLES30.glUseProgram(program)
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false,mvpMatrix, 0)

        //启用顶点句柄
        GLES30.glEnableVertexAttribArray(aPosition)
        //启用颜色句柄
        GLES30.glEnableVertexAttribArray(aColor)
        //准备坐标数据
        GLES30.glVertexAttribPointer(
            aPosition, VERTEX_DIMENSION,
            GLES30.GL_FLOAT, false,
            VERTEX_DIMENSION * 4, vertBuffer
        )

        //准备颜色数据
        GLES30.glVertexAttribPointer(
            aColor, COLOR_DIMENSION,
            GLES30.GL_FLOAT, false,
            COLOR_DIMENSION * 4, colorBuffer
        )

        //绘制点
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexes.size/ VERTEX_DIMENSION)

        //绘制线
//        GLES30.glLineWidth(5f)
//        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP,0,vertexes.size/ VERTEX_DIMENSION)

        //绘制三角
        GLES30.glLineWidth(5f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN,0,vertexes.size/ VERTEX_DIMENSION)

        //禁用顶点数组
        GLES30.glDisableVertexAttribArray(aPosition)
        GLES30.glDisableVertexAttribArray(aColor)
    }



}
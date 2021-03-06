package com.example.testopenglapplication.GLShader

import android.content.Context
import android.opengl.GLES30
import com.example.testopenglapplication.R
import com.example.testopenglapplication.util.GLTextureUtil
import com.example.testopenglapplication.util.ShaderUtil
import java.nio.FloatBuffer

class GLTeXiaoTexture {
    //顶点数组
    private var vertexes = floatArrayOf(
        1.0f, 1.0f, 0.0f,//原点
        -1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
    )

    //贴图数组
    private var textures = floatArrayOf(
        1.0f,0.0f,
        0.0f,0.0f,
        0.0f,1.0f,
        1.0f,1.0f
    )

    private var program = 0
    private var VERTEX_DIMENSION = 3
    private var TEXTURE_DIMENSION = 2

    private var vertBuffer: FloatBuffer? = null
    private var textureBuffer: FloatBuffer? = null
    private var textureId:Int=-1

    private var textureId1:Int=-1
    private var textureId2:Int=-1

    private var aPosition = 0 //位置的句柄
    private var aTexCoord = 1 //纹理坐标的句柄
    private var uMVPMatrix :Int=-1//顶点变换矩阵句柄
    private var uTexture1 = -1 //纹理1的句柄
    private var uThreshold=-1 //改变阈值的句柄
    private var uXiaoGuo=-1//选择呈现的效果句柄
    private var uProgress=-1//时间句柄

    private var threshold:Float=0.4f
    private var xiaoGuoType:Int=99
    private var timeProgree:Float=0.0f

     lateinit var context:Context

    constructor(context: Context){
        this.context=context
        ShaderUtil.Logi("GLTeXiaoTexture-constructor-thread-${Thread.currentThread().name}")
        textureId1= GLTextureUtil.loadTexture(context, R.mipmap.texture1)
        textureId2= GLTextureUtil.loadTexture(context, R.mipmap.texture2)
        textureId= textureId1
        ShaderUtil.Logi("first--textureId=${textureId}")
        program = ShaderUtil.initProgram("texture.vsh.glsl","txTexture.fsh.glsl")
        vertBuffer= ShaderUtil.transformFloatBuffer(vertexes)
        textureBuffer= ShaderUtil.transformFloatBuffer(textures)
        uMVPMatrix = GLES30.glGetUniformLocation(program, "uMVPMatrix")
        uTexture1= GLES30.glGetUniformLocation(program, "uTexture")
        uThreshold=GLES30.glGetUniformLocation(program,"uThreshold")
        uXiaoGuo=GLES30.glGetUniformLocation(program,"uXiaoGuo")
        uProgress=GLES30.glGetUniformLocation(program,"uProgress")

//        ShaderUtil.Logi("uMVPMatrix=${uMVPMatrix} uTexture1=${uTexture1}")
    }



    fun draw(mvpMatrix:FloatArray) {
        //清除颜色缓存和深度缓存
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // 将程序添加到OpenGL ES环境中
        GLES30.glUseProgram(program)
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false,mvpMatrix, 0)

        //启用顶点句柄
        GLES30.glEnableVertexAttribArray(aPosition)
        //启用纹理坐标句柄
        GLES30.glEnableVertexAttribArray(aTexCoord)
        //准备坐标数据
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



        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glUniform1i(uTexture1,0)

        GLES30.glUniform1f(uThreshold,threshold)
        GLES30.glUniform1i(uXiaoGuo,xiaoGuoType)
        GLES30.glUniform1f(uProgress,timeProgree)


        //绘制
        GLES30.glLineWidth(10f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexes.size / VERTEX_DIMENSION)

        //禁用顶点数组
        GLES30.glDisableVertexAttribArray(aPosition)
        GLES30.glDisableVertexAttribArray(aTexCoord)
    }

    fun setThreshold(num:Float){
        threshold=num
    }

    fun setXiaoGuoType(num:Int){
        xiaoGuoType=num
    }

    fun setTimeProgress(num:Float){
        timeProgree=num
    }

    fun setTextureByALBUM(tempTextureId:Int){
        ShaderUtil.Logi("textureId=${tempTextureId}")
//        textureId=tempTextureId
        textureId = if(textureId== textureId1){
            textureId2
        }else{
            textureId1
        }
    }

}
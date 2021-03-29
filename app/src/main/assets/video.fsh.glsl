#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require

precision highp  float;
in vec2 vTexCoord;
out vec4 outColor;

uniform samplerExternalOES uTexture;
uniform float uThreshold;//阈值


const  float centerX=0.5;  //圆点x
const  float centerY =0.5;  //圆点y
const  float raduius =0.25; //局部半径

void main() {

    vec2 pos = vTexCoord.xy;
    //位置
//    if ((pos.x-centerX)*(pos.x-centerX)+(pos.y-centerY)*(pos.y-centerY)<raduius*raduius){
    if (pos.x>0.2&&pos.x<0.8&&pos.y>0.2&&pos.y<0.8){
        //马赛克处理
//        float cellX= 2.0;//  rowCount/cellx-->一行的数量
//        float cellY= 2.0;//  rowCount/cellx-->一列的数量
//        float rowCount=400.0;//扩大坐标
//        if ((pos.x-centerX)*(pos.x-centerX)+(pos.y-centerY)*(pos.y-centerY)<raduius*raduius){
//            //局部---圆内
//            pos.x = pos.x*rowCount;//对纹理图片进行放大
//            pos.y = pos.y*rowCount;//对纹理图片进行放大
//            pos = vec2(floor(pos.x/cellX)*cellX/rowCount, floor(pos.y/cellY)*cellY/(rowCount));//求出原来某点(x,y)应对应的位置
//        }

    } else {

    }

    vec3 color = texture(uTexture, pos).rgb;


    //颜色
//if ((pos.x-centerX)*(pos.x-centerX)+(pos.y-centerY)*(pos.y-centerY)<raduius*raduius){
    if (pos.x>0.0&&pos.x<0.5&&pos.y>0.0&&pos.y<0.5){
    //在圆内

            float mean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
            if(mean>= uThreshold){
                color.r=1.0;
                color.g=1.0;
                color.b=1.0;
            }else{
                color.r=0.0;
                color.g=0.0;
                color.b=1.0;
            }
}else if(pos.x>0.5&&pos.x<1.0&&pos.y>0.0&&pos.y<0.5){
        float mean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
        if(mean>= uThreshold){
            color.r=1.0;
            color.g=1.0;
            color.b=1.0;
        }else{
            color.r=0.0;
            color.g=0.0;
            color.b=0.0;
        }
    }
    else if(pos.x>0.0&&pos.x<0.5&&pos.y>0.5&&pos.y<1.0){
        float mean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
        if(mean>= uThreshold){
            color.r=1.0;
            color.g=1.0;
            color.b=1.0;
        }else{
            color.r=1.0;
            color.g=0.0;
            color.b=0.0;
        }
    }
    else if(pos.x>0.5&&pos.x<1.0&&pos.y>0.5&&pos.y<1.0){
        float mean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
        if(mean>= uThreshold){
            color.r=1.0;
            color.g=1.0;
            color.b=1.0;
        }else{
            color.r=0.3;
            color.g=0.5;
            color.b=0.1;
        }
    }

    else{
    //圆外

            float rate= 2264.0 / 1080.0;
            float cellX= 3.0;
            float cellY= 3.0;
            float rowCount=600.0;

            vec2 sizeFmt=vec2(rowCount, rowCount/rate);
            vec2 sizeMsk=vec2(cellX, cellY);
            vec2 posFmt = vec2(vTexCoord.x*sizeFmt.x, vTexCoord.y*sizeFmt.y);
            vec2 posMsk = vec2(floor(posFmt.x/sizeMsk.x)*sizeMsk.x, floor(posFmt.y/sizeMsk.y)*sizeMsk.y)+ 0.5*sizeMsk;
            float del = length(posMsk - posFmt);
            vec2 UVMosaic = vec2(posMsk.x/sizeFmt.x, posMsk.y/sizeFmt.y);

            vec4 result;
            if (del< cellX/2.0)
            result = texture(uTexture, UVMosaic);
            else
            result = vec4(1.0,1.0,1.0,0.0);
            outColor = result;
}


    outColor=vec4(color.rgb, 1.0);

}
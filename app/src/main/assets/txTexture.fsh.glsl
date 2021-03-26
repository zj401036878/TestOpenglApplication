#version 300 es

precision mediump float;
out vec4 outColor;

in vec2 vTexCoord;
uniform sampler2D uTexture;

uniform float uThreshold;//阈值 纯黑白

uniform int uXiaoGuo;//uniform入参

uniform float uProgress;////传递进来的时间

const  float centerX=0.55;  //圆点x
const  float centerY =0.3;  //圆点y
const  float raduius =0.35; //局部半径
//const  float centerX=0.5;  //圆点x
//const  float centerY =0.5;  //圆点y
//const  float raduius =0.35; //局部半径

void main() {


    //改变位置
    vec2 pos = vTexCoord.xy;
    //左右分镜
    //    if (pos.x > 0.5) {
    //        pos.x = 1.0 - pos.x;
    //    }

    //马赛克处理
    float cellX= 2.0;//  rowCount/cellx-->一行的数量
    float cellY= 2.0;//  rowCount/cellx-->一列的数量
    float rowCount=300.0;//扩大坐标
    if ((pos.x-centerX)*(pos.x-centerX)+(pos.y-centerY)*(pos.y-centerY)<raduius*raduius){
        //局部---圆内
        pos.x = pos.x*rowCount;//对纹理图片进行放大
        pos.y = pos.y*rowCount;//对纹理图片进行放大
        pos = vec2(floor(pos.x/cellX)*cellX/rowCount, floor(pos.y/cellY)*cellY/(rowCount));//求出原来某点(x,y)应对应的位置
    }

    //放大
    float t = 0.7; //周期
    float maxAlpha = 0.4;//第二图最大透明度
    float maxScale = 2.8;//第二图放大最大比率
    //进度
    float progress = mod(uProgress, t) / t; // 0~1
    //当前的透明度
    float alpha = maxAlpha * (1.0 - progress);

    //当前的放大比例
    float scale = 1.0 + (maxScale - 1.0) * progress;
    //根据放大比例获取新的图层纹理坐标
    vec2 weakPos = vec2(0.5 + (pos.x - 0.5) / scale, 0.5 + (pos.y - 0.5) / scale);
    vec4 wekColor=texture(uTexture,weakPos);
    vec4 mask= texture(uTexture, pos);

    vec4 color= mask*0.7+wekColor*0.3;
    float r = color.r;
    float g = color.g;
    float b = color.b;


    //[黑白]
    if (uXiaoGuo==0){
        if ((pos.x-centerX)*(pos.x-centerX)+(pos.y-centerY)*(pos.y-centerY)<raduius*raduius){
            //在圆内
            g = r * 0.3 + g * 0.59 + b * 0.11;
            g= g <= uThreshold ? 0.0 : 1.0;//阈值--超过的色值为白
        } else {
            //圆外

        }
    }

    //[负片]
    if (uXiaoGuo==1){
        r = 1.0 - color.r;
        g = 1.0 - color.g;
        b = 1.0 - color.b;
    }

    //[怀旧]
    if (uXiaoGuo==2){
        r = 0.393* r + 0.769 * g + 0.189* b;
        g = 0.349 * r + 0.686 * g + 0.168 * b;
        b = 0.272 * r + 0.534 * g + 0.131 * b;
    }

    //[冷调]
    if (uXiaoGuo==3){
        b = 0.393* r + 0.769 * g + 0.189* b;
        g = 0.349 * r + 0.686 * g + 0.168 * b;
        r = 0.272 * r + 0.534 * g + 0.131 * b;
    }

    if (uXiaoGuo==0){
        outColor = vec4(g, g, g, 1.0);
    } else {
        outColor = vec4(r, g, b, 1.0);
    }

}

#version 300 es

precision mediump float;
out vec4 outColor;

in vec2 vTexCoord;
uniform sampler2D uTexture;
uniform sampler2D uTexture2;//多加一个纹理量

const float uT=0.3;

void main() {

    vec4 color= texture(uTexture, vTexCoord);
    vec4 color2 = texture(uTexture2, vTexCoord);//从纹理中采样出颜色值2

    outColor = color*(1.0-uT) + color2*uT;// 混合两个颜色值

}

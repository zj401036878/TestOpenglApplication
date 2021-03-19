#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
in vec2 vTexCoord;
out vec4 outColor;

uniform samplerExternalOES uTexture;
uniform float uThreshold;//阈值

void main() {

    //纯红白
//    vec3 color = texture(uTexture, vTexCoord).rgb;
//    float mean = (color.r + color.g + color.b) / 3.0;
//    color.g = color.b = mean >= uThreshold ? 1.0 : 0.0;
//    outColor =vec4(1, color.gb, 1.0);


    float rate= 2264.0 / 1080.0;
    float cellX= 3.0;
    float cellY= 3.0;
    float rowCount=500.0;

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
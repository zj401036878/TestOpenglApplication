//片段着色器
#version 300 es
precision highp float;
in vec2 v_texCoord;
layout(location = 0) out vec4 outColor;
uniform sampler2D s_TextureMap;
uniform vec2 u_texSize;//图像分辨率
uniform float u_needRotate;//判断是否需要做形变
uniform float u_rotateAngle;//通过旋转角度控制形变的程度

vec2 rotate(float radius, float angle, vec2 texSize, vec2 texCoord)
{
    vec2 newTexCoord = texCoord;
    vec2 center = vec2(texSize.x / 2.0, texSize.y / 2.0);
    vec2 tc = texCoord * texSize;
    tc -= center;
    float dist = length(tc);
    if (dist < radius) {
        float percent = (radius - dist) / radius;
        float theta = percent * percent * angle * 8.0;
        float s = sin(theta);
        float c = cos(theta);
        tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
        tc += center;

        newTexCoord = tc / texSize;
    }
    return newTexCoord;
}
void main()
{
    vec2 texCoord = v_texCoord;

    if(u_needRotate > 0.0)
    {
        texCoord = rotate(0.5, u_rotateAngle, u_texSize, v_texCoord);
    }

    outColor = texture(s_TextureMap, texCoord);
    if (outColor.a < 0.6) discard;
}
             
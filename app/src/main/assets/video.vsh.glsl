#version 300 es

layout(location=0) in vec4 aPosition;
layout(location=1) in vec4 aTexCoord;
uniform mat4 uProjectionMatrix;
uniform mat4 uTMatrix;

out vec2 vTexCoord;

void main() {
    vTexCoord = (uTMatrix*aTexCoord).xy;
    gl_Position = uProjectionMatrix*aPosition;
}

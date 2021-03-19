#version 300 es

layout(location=0) in vec3 aPosition;
layout(location=1) in vec2 aTexCoord;
uniform mat4 uMVPMatrix;
out vec2 vTexCoord;

void main() {
    gl_Position=uMVPMatrix * vec4(aPosition.x,aPosition.y, aPosition.z, 1.0);
    vTexCoord = aTexCoord;

}

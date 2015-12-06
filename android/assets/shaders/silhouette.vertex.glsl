attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

uniform float u_offset;

void main() {
   vec4 pos = vec4(a_position + a_normal * u_offset, 1.0);
   gl_Position = u_projViewTrans * u_worldTrans * pos;
}
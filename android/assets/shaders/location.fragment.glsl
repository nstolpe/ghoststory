#ifdef GL_ES 
precision mediump float;
#endif

uniform vec3 u_color;

void main() {
    gl_FragColor = u_color;
}
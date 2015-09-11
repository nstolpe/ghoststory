//layout(location = 0) in vec3 position;
varying vec4 v_position;

uniform mat4 cameraToShadowView;
uniform mat4 cameraToShadowProjector;
uniform mat4 model;

void main() {
	gl_Position = cameraToShadowProjector * model * vec4(position, 1.0);
	v_position  = gl_Position;
};
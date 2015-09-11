#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

uniform float u_cameraFar;

varying vec4 v_position;
uniform vec3 u_lightPosition;

void main() {
	// Simple depth calculation, just the length of the vector light-current position
	gl_FragColor = vec4(length(v_position.xyz - u_lightPosition) / u_cameraFar);

	// VSM attempt
	float depth = clamp(length(u_lightPosition)/u_cameraFar, 0.0, 1.0);
	float dx = dFdx(depth);
    float dy = dFdy(depth);
	gl_FragColor = vec4(depth, pow(depth, 2.0) + 0.25*(dx*dx + dy*dy), 0.0, 1.0);
}


#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

uniform sampler2D u_texture;

varying MED vec2 v_texCoords0;
varying MED vec2 v_texCoords1;
varying MED vec2 v_texCoords2;
varying MED vec2 v_texCoords3;
varying MED vec2 v_texCoords4;

void main() {
	const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);

	float depth = abs(
		dot(texture2D(u_texture, v_texCoords0), bitShifts) +
		dot(texture2D(u_texture, v_texCoords1), bitShifts) -
		dot(4.0 * texture2D(u_texture, v_texCoords2), bitShifts) +
		dot(texture2D(u_texture, v_texCoords3), bitShifts) +
		dot(texture2D(u_texture, v_texCoords4), bitShifts)
	);

	if (depth > 0.0004)
		gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	else
		gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);

}
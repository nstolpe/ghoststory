#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

uniform sampler2D u_texture;
uniform vec2 u_size;

//varying MED vec2 v_texCoords0;
//varying MED vec2 v_texCoords1;
varying MED vec2 v_texCoords2;
//varying MED vec2 v_texCoords3;
//varying MED vec2 v_texCoords4;

float threshold(float thr1, float thr2 , float val) {
 if (val < thr1) { return 0.0; }
 if (val > thr2) { return 1.0; }
 return val;
}

void main() {
	const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);
	vec2 offsets = vec2(1.0 / u_size.x, 1.0 / u_size.y);
	int k = -1;
	float pix[9];

	for (float i = -1.0; i <= 1.0; i++) {
		for (float j = -1.0; j <= 1.0; j++) {
			k++;
			pix[k] = dot(texture2D(u_texture, v_texCoords2 + vec2(i * offsets.x, j * offsets.y)), bitShifts);
		}
	}

	float depth = (
		abs(pix[1] - pix[7]) +
		abs(pix[5] - pix[3]) +
		abs(pix[0] - pix[8]) +
		abs(pix[2] - pix[6])
	) / 4.;
//	float depth = abs(
//		dot(texture2D(u_texture, v_texCoords0), bitShifts) +
//		dot(texture2D(u_texture, v_texCoords1), bitShifts) -
//		dot(4.0 * texture2D(u_texture, v_texCoords2), bitShifts) +
//		dot(texture2D(u_texture, v_texCoords3), bitShifts) +
//		dot(texture2D(u_texture, v_texCoords4), bitShifts)
//	);

	if (depth > 0.0004)
		gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	else
		gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);

}
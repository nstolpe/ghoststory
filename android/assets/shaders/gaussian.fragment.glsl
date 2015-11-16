#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float invWidth;
uniform float invHeight;
uniform int horizontal;

float weight[5];

void main() {
	weight[0] = 0.2270270270;
	weight[1] = 0.1945945946;
	weight[2] = 0.1216216216;
	weight[3] = 0.0540540541;
	weight[4] = 0.0162162162;
	vec2 offset = vec2(invWidth, invHeight);
	vec3 result = texture2D(u_texture, v_texCoords).rgb * weight[0];
	if(horizontal == 1) {
		for(int i = 1; i < 5; ++i) {
			result += texture2D(u_texture, v_texCoords + vec2(offset.x * float(i), 0.0)).rgb * weight[i];
			result += texture2D(u_texture, v_texCoords - vec2(offset.x * float(i), 0.0)).rgb * weight[i];
		}
	} else {
		for(int i = 1; i < 5; ++i) {
			result += texture2D(u_texture, v_texCoords + vec2(0.0, offset.y * float(i))).rgb * weight[i];
			result += texture2D(u_texture, v_texCoords - vec2(0.0, offset.y * float(i))).rgb * weight[i];
		}
	}
//	 gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * foo;
	gl_FragColor = vec4(result, 0.5);
}
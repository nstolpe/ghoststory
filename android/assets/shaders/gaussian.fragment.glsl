//#ifdef GL_ES
//#define LOWP lowp
//precision mediump float;
//#else
//#define LOWP
//#endif
//varying LOWP vec4 v_color;
//varying vec2 v_texCoords;
//uniform sampler2D u_texture;
//uniform float invWidth;
//uniform float invHeight;
//uniform int horizontal;
//
//float weight[5];
//
//void main() {
//	weight[0] = 0.2270270270;
//	weight[1] = 0.1945945946;
//	weight[2] = 0.1216216216;
//	weight[3] = 0.0540540541;
//	weight[4] = 0.0162162162;
//	vec2 offset = vec2(invWidth, invHeight);
//	vec3 result = texture2D(u_texture, v_texCoords).rgb * weight[0];
//	if(horizontal == 1) {
//		for(int i = 1; i < 5; ++i) {
//			result += texture2D(u_texture, v_texCoords + vec2(offset.x * float(i), 0.0)).rgb * weight[i];
//			result += texture2D(u_texture, v_texCoords - vec2(offset.x * float(i), 0.0)).rgb * weight[i];
//		}
//	} else {
//		for(int i = 1; i < 5; ++i) {
//			result += texture2D(u_texture, v_texCoords + vec2(0.0, offset.y * float(i))).rgb * weight[i];
//			result += texture2D(u_texture, v_texCoords - vec2(0.0, offset.y * float(i))).rgb * weight[i];
//		}
//	}
////	 gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * foo;
//	gl_FragColor = vec4(result, 0.5);
//}

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float width;
uniform float height;
uniform float radius;
uniform vec2 dir;

void main() {
	vec4 sum = vec4(0.0);
	vec2 tc = v_texCoords;
	float blur_x = radius / width;
	float blur_y = radius / height;

    float hstep = dir.x;
    float vstep = dir.y;

	sum += texture2D(u_texture, vec2(tc.x - 4.0 * blur_x * hstep, tc.y - 4.0 * blur_y * vstep)) * 0.05;
	sum += texture2D(u_texture, vec2(tc.x - 3.0 * blur_x * hstep, tc.y - 3.0 * blur_y * vstep)) * 0.09;
	sum += texture2D(u_texture, vec2(tc.x - 2.0 * blur_x * hstep, tc.y - 2.0 * blur_y * vstep)) * 0.12;
	sum += texture2D(u_texture, vec2(tc.x - 1.0 * blur_x * hstep, tc.y - 1.0 * blur_y * vstep)) * 0.15;

	sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.16;

	sum += texture2D(u_texture, vec2(tc.x + 1.0 * blur_x * hstep, tc.y + 1.0 * blur_y * vstep)) * 0.15;
	sum += texture2D(u_texture, vec2(tc.x + 2.0 * blur_x * hstep, tc.y + 2.0 * blur_y * vstep)) * 0.12;
	sum += texture2D(u_texture, vec2(tc.x + 3.0 * blur_x * hstep, tc.y + 3.0 * blur_y * vstep)) * 0.09;
	sum += texture2D(u_texture, vec2(tc.x + 4.0 * blur_x * hstep, tc.y + 4.0 * blur_y * vstep)) * 0.05;

	float alpha;
	if (sum.a <= 0.2) {
		alpha =  0.0;
//	} else if (sum.a < 0.6) {
//		alpha = sum.a;
	} else {
		alpha = 1.0;
	}

	gl_FragColor = v_color * vec4(sum.rgb, sum.a);
}
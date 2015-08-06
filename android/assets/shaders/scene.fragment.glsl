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

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_shadows;
uniform float u_screenWidth;
uniform float u_screenHeight;

varying vec2 v_texCoords0;
varying float v_intensity;

// new
uniform vec4 u_diffuseColor;
varying MED vec2 v_diffuseUV;

void main() {
	vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;

	// finalColor might not be needed, diffuse is working now. maybe don't need v_texCoords0 either.
	vec4 finalColor = texture2D(u_diffuseTexture, v_texCoords0);
	finalColor.rgb = finalColor.rgb * v_intensity;

	// Retrieve the shadow color from shadow map
	vec2 c = gl_FragCoord.xy;
	c.x /= u_screenWidth;
	c.y /= u_screenHeight;
	vec4 color = texture2D(u_shadows, c);
	
	// Apply shadow
	diffuse.rgb *= (0.4 + 0.6 * color.a);
	
	gl_FragColor = diffuse;
	
	
}


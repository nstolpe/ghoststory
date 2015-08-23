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
//#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
//#endif
//#ifdef diffuseTextureFlag
varying MED vec2 v_diffuseUV;
//#endif

//#ifdef lightingFlag
varying vec3 v_lightDiffuse;
//#endif //lightingFlag

//#ifdef specularColorFlag
uniform vec4 u_specularColor;
//#endif

//#ifdef specularFlag
varying vec3 v_lightSpecular;
//#endif //specularFlag
void main() {
	// #elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
	vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;

	// #elif defined(specularColorFlag)
	vec3 specular = u_specularColor.rgb * v_lightSpecular;

	// finalColor might not be needed, diffuse is working now. maybe don't need v_texCoords0 either.
//	vec4 finalColor = texture2D(u_diffuseTexture, v_texCoords0);
//	finalColor.rgb = finalColor.rgb * v_intensity;
float bias = 0.005;
float visibility = 1.0;
if ( texture2D( u_shadows, gl_FragCoord.xy ).z < gl_FragCoord.z-bias) {
    visibility = 0.5;
}
	// Retrieve the shadow color from shadow map
	vec2 c = gl_FragCoord.xy;
	c.x /= u_screenWidth;
	c.y /= u_screenHeight;
	vec4 color = texture2D(u_shadows, c);
	
	// Apply shadow
	diffuse.rgb *= (0.4 + 0.6 * color.a);
//	vec3 red = vec3(1.0, 0, 0);
//	diffuse.rgb *= red;
//	gl_FragColor = diffuse;
	gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular;
	
	
}


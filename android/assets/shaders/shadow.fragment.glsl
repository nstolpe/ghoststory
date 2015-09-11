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

uniform sampler2D u_depthMapDir;
uniform samplerCube u_depthMapCube;
uniform float u_cameraFar;
uniform vec3 u_lightPosition;
uniform float u_type;


varying vec4 v_position;
varying vec4 v_positionLightTrans;

// VSM
float linstep(float low, float high, float v){
    return clamp((v-low)/(high-low), 0.0, 1.0);
}
// compare is the light position, think this should be in depth
float VSM(sampler2D depths, vec2 uv, float compare){
    vec2 moments = texture2D(depths, uv).xy;
    float p = smoothstep(compare-0.02, compare, moments.x);
    float variance = max(moments.y - moments.x*moments.x, -0.001);
    float d = compare - moments.x;
    float p_max = linstep(0.2, 1.0, variance / (variance + d*d));
    return clamp(max(p, p_max), 0.0, 1.0);
}

void main()
{
	// Default is to not add any color
	float intensity = 0.0;
	// Vector light-current position
	vec3 lightDirection = v_position.xyz-u_lightPosition;
	float lenToLight=length(lightDirection) / u_cameraFar;
	// By default assume shadow
	float lenDepthMap = -1.0;
	
	// Directional light, check if in field of view and get the depth
	if(u_type == 1.0) {
		vec3 depth = (v_positionLightTrans.xyz / v_positionLightTrans.w) * 0.5 + 0.5;
		if (v_positionLightTrans.z >= 0.0 && (depth.x >= 0.0) && (depth.x <= 1.0) && (depth.y >= 0.0) && (depth.y <= 1.0)) {
			lenDepthMap = texture2D(u_depthMapDir, depth.xy).a;
		}
	}
	// Point light, just get the depth given light vector
	else if(u_type == 2.0){
		lenDepthMap = textureCube(u_depthMapCube, lightDirection).a;
	}
	
	// If not in shadow, add some light
	if(lenDepthMap >= lenToLight - 0.005){
		intensity = 0.5 * (1.0 - lenToLight);
	}
// not sure if below was here for some specific reason.
//	if(lenDepthMap < lenToLight - 0.005){
//	}else{
//		intensity = 0.5 * (1.0 - lenToLight);
//	}
	
	gl_FragColor = vec4(intensity);

}


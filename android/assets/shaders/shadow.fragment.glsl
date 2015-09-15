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
uniform int depthMapSize;

varying vec4 v_position;
varying vec4 v_positionLightTrans;

float texture2DCompare(sampler2D depths, vec2 uv, float compare) {
    float depth = texture2D(depths, uv).r;
    return step(compare, depth);
}

float texture2DShadowLerp(sampler2D depths, vec2 size, vec2 uv, float compare) {
    vec2 texelSize = vec2(1.0)/size;
    vec2 f = fract(uv*size+0.5);
    vec2 centroidUV = floor(uv*size+0.5)/size;

    float lb = texture2DCompare(depths, centroidUV+texelSize*vec2(0.0, 0.0), compare);
    float lt = texture2DCompare(depths, centroidUV+texelSize*vec2(0.0, 1.0), compare);
    float rb = texture2DCompare(depths, centroidUV+texelSize*vec2(1.0, 0.0), compare);
    float rt = texture2DCompare(depths, centroidUV+texelSize*vec2(1.0, 1.0), compare);
    float a = mix(lb, lt, f.y);
    float b = mix(rb, rt, f.y);
    float c = mix(a, b, f.x);
    return c;
}

float cubePCF() {
	return 1.0;
}
void main()
{
	// Default is to not add any color
	float intensity = 0.0;
	// Vector light-current position
	vec3 lightDirection = v_position.xyz - u_lightPosition;
	float lenToLight = length(lightDirection) / u_cameraFar;
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
//		float shadow = 0;
//		for (float x = -2.0; x <= 2.0; x++) {
//			for (float y = -2.0; y <= 2.0; y++) {
//				shadow += textureCube(u_depthMapCube, vec3(v_positionLightTrans.x + x, v_positionLightTrans.y + y, v_positionLightTrans.z)).a;
//			}
//		}
//		lenDepthMap = shadow/25;
//		float shadow = 0;
//		for (float i = -2.0; i <= 2.0; i++) {
//			shadow += textureCube(u_depthMapCube, vec3(lightDirection.x + i, lightDirection.y + i, lightDirection.z + i)).a;
//        }
//        lenDepthMap = shadow / 5.0;
//		float result = 0.0;

//		for (float i = -2; i < 2; i++) {
//			result += textureCube(u_depthMapCube, lightDirection * i).a;
//		}
//		lenDepthMap = result / 5;
	}

//PCF(u_depthMapCube, lightDepthSize, );
	// If not in shadow, add some light
	if(lenDepthMap >= lenToLight - 0.005){
		intensity = 0.5 * (1.0 - lenToLight);
	}
// not sure if below was here for some specific reason.
//	if(lenDepthMap < lenToLight - 0.005){
//	}else{
//		intensity = 0.5 * (1.0 - lenToLight);
//	}

	intensity += depthMapSize;
	intensity -= depthMapSize;
	gl_FragColor = vec4(intensity);

}


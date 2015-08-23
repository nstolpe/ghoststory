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

attribute vec3 a_position;
attribute vec2 a_texCoord0;
attribute vec3 a_normal;


uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;

varying vec2 v_texCoords0;
varying float v_intensity;

// new
//#ifdef diffuseTextureFlag
uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;
//#endif

//#ifdef lightingFlag
varying vec3 v_lightDiffuse;

//#ifdef ambientCubemapFlag
uniform vec3 u_ambientCubemap[6];
//#endif // ambientCubemapFlag

//#ifdef specularFlag
varying vec3 v_lightSpecular;
//#endif // specularFlag
//#endif // lightingFlag

void main() {
	vec3 normal = normalize(u_normalMatrix * a_normal);


	// Vertex position after transformation
    vec4 pos = u_worldTrans * vec4(a_position, 1.0);
    gl_Position = u_projViewTrans * pos;
	// #ifdef diffuseTextureFlag
    v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    // #endif

	//#ifdef specularFlag
	// v_lightSpecular gets modified by lights, once added
	v_lightSpecular = vec3(0.0);
	vec3 viewVec = normalize(pos.xyz);
	//#endif // specularFlag

	// ambient stuff
	vec3 ambientLight = vec3(0.0);
//    #ifdef ambientCubemapFlag
        vec3 squaredNormal = normal * normal;
        vec3 isPositive  = step(0.0, normal);
        ambientLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
                squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
                squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
//    #endif // ambientCubemapFlag
	v_lightDiffuse = ambientLight;


    v_texCoords0 = a_texCoord0;
    
    // Just add some basic self shadow
//    vec3 normal = normalize(u_normalMatrix * a_normal);
//	v_intensity = 1.0;
//   	if(normal.y<0.5){
//		if(normal.x>0.5 || normal.x<-0.5)
//			v_intensity*=0.8;
//		if(normal.z>0.5 || normal.z<-0.5)
//			v_intensity*=0.6;
//	}
}

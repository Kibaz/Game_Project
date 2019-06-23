#version 330

in vec2 pass_texCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVec;

layout (location = 0) out vec4 out_colour;

uniform sampler2D textureSampler;
uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;

void main(void)
{
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVecToCam = normalize(toCameraVec);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i = 0; i < 4; i++)
	{
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDot1 = dot(unitNormal,unitLightVector);
		float brightness = max(nDot1,0.2);
		vec3 lightDir = -unitLightVector;
		vec3 reflectedLightDir = reflect(lightDir,unitNormal);
		float specularFactor = dot(reflectedLightDir, unitVecToCam);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	
	vec4 textureColour = texture(textureSampler,pass_texCoords);
	if(textureColour.a<0.5)
	{
		discard;
	}
	
	out_colour = vec4(totalDiffuse,1.0) * textureColour + vec4(totalSpecular,1.0);
}
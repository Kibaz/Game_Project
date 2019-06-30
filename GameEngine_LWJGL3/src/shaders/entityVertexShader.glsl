#version 330 

const int MAX_JOINTS = 150;
const int MAX_WEIGHTS = 4; 

in vec3 position;
in vec2 texCoords;
in vec3 normal;
in ivec4 jointIndices;
in vec4 weights;

out vec2 pass_texCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVec;

uniform float numberOfRows;
uniform vec2 offset;

uniform float useFakeLighting;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float hasAnimation;
uniform vec3 lightPosition[4];
uniform vec4 plane;


uniform mat4 jointTransforms[MAX_JOINTS];


void main(void)
{
	vec4 totalPos = vec4(0.0);
	vec4 normalPos = vec4(0.0);
	
	
	if(hasAnimation > 0.5)
	{
		for(int i = 0; i < MAX_WEIGHTS; i++)
		{
			float weight = weights[i];
			if(weight > 0)
			{
				vec4 tempPos = jointTransforms[jointIndices[i]] * vec4(position,1.0);
				vec4 tempNormal = jointTransforms[jointIndices[i]] * vec4(normal,0.0);
				totalPos += tempPos * weight;
				normalPos += tempNormal * weight;
			}
		}
	}
	else
	{
		totalPos = vec4(position,1.0);
		normalPos = vec4(normal,0.0);
	}

	


	vec4 worldPosition = transformationMatrix * totalPos;
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	pass_texCoords = (texCoords/numberOfRows) + offset;
	
	vec3 actualNormal = normalPos.xyz;
	if(useFakeLighting > 0.5)
	{
		actualNormal = vec3(0.0,1.0,0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;
	for(int i = 0; i < 4; i++)
	{
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	
	toCameraVec = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;

}
#version 140

in vec2 textureCoords;

out vec4 colour;

uniform sampler2D colourTexture;

void main(void)
{
	vec4 scene = texture(colourTexture, textureCoords);
	if(scene.r == 1.0)
	{
		colour = scene;
	}
	else
	{
		colour = vec4(0.0);
	}
	
	
}
#version 140

in vec2 textureCoords;

out vec4 colour;

uniform sampler2D colourTexture;
uniform sampler2D effectTexture;

void main(void)
{
	vec4 scene = texture(colourTexture, textureCoords);
	vec4 effectColour = texture(effectTexture, textureCoords);
	colour = scene + effectColour * 10;	
}
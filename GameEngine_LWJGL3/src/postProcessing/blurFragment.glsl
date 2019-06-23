#version 150

in vec2 blurTextureCoords[11];

out vec4 colour;

uniform sampler2D originTexture;

void main(void)
{
	colour = vec4(0.0);
	colour += texture(originTexture, blurTextureCoords[0]) * 0.0093;
	colour += texture(originTexture, blurTextureCoords[1]) * 0.028002;
	colour += texture(originTexture, blurTextureCoords[2]) * 0.065984;
	colour += texture(originTexture, blurTextureCoords[3]) * 0.121703;
	colour += texture(originTexture, blurTextureCoords[4]) * 0.175713;
	colour += texture(originTexture, blurTextureCoords[5]) * 0.198596;
	colour += texture(originTexture, blurTextureCoords[6]) * 0.175713;
	colour += texture(originTexture, blurTextureCoords[7]) * 0.121703;
	colour += texture(originTexture, blurTextureCoords[8]) * 0.065984;
	colour += texture(originTexture, blurTextureCoords[9]) * 0.028002;
	colour += texture(originTexture, blurTextureCoords[10]) * 0.0093;
}
# version 140

out vec4 colour;

uniform float isEnemy;

void main(void)
{
	if(isEnemy < 0.5)
	{
		colour = vec4(0.0,0.7,1.0,0.5);	
	}
	else
	{
		colour = vec4(0.92,0.38,0.13,0.5);	
	}
	
}
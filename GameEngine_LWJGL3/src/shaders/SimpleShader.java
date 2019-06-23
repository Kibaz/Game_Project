package shaders;

public class SimpleShader extends ShaderProgram{
	
	private static final String VERTEX_SHADER = "src/shaders/simpleVS.txt";
	private static final String FRAGMENT_SHADER = "src/shaders/simpleFS.txt";

	public SimpleShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		// TODO Auto-generated method stub
		
	}
	
	

}

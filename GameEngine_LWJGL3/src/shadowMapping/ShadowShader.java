package shadowMapping;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_PATH = "src/shadowMapping/shadowVertexShader.txt";
	private static final String FRAGMENT_PATH = "src/shadowMapping/shadowFragmentShader.txt";
	
	private int location_mvpMatrix;
	
	private int location_isAnimated;
	
	private int location_jointTransforms[];
	
	public ShadowShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
		super.bindAttribute(3, "jointIndices");
		super.bindAttribute(4, "weights");
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		location_isAnimated = super.getUniformLocation("isAnimated");
		
		location_jointTransforms = new int[150];
		for(int i = 0; i < 150; i++)
		{
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
		
	}
	
	public void loadMVPMatrix(Matrix4f mvpMatrix)
	{
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}
	
	public void loadAnimated(boolean isAnimated)
	{
		super.loadBoolean(location_isAnimated, isAnimated);
	}
	
	public void loadJointTransforms(Matrix4f[] transforms)
	{
		for(int i = 0; i < transforms.length; i++)
		{
			super.loadMatrix(location_jointTransforms[i], transforms[i]);
		}
	}
	
	

	

}

package shaders;


import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import utils.Maths;

public class StencilShader extends ShaderProgram {

	private static final String VERTEX_SHADER = "src/shaders/entityVertexShader.glsl";
	private static final String FRAGMENT_SHADER = "src/shaders/entityStencil_fs.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_jointTransforms[];
	private int location_hasAnimation;
	private int location_hostile;
	
	public StencilShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_hasAnimation = super.getUniformLocation("hasAnimation");
		location_jointTransforms = new int[150];
		for(int i = 0; i < location_jointTransforms.length; i++)
		{
			int loc = super.getUniformLocation("jointTransforms[" + i + "]");
			location_jointTransforms[i] = loc;
		}
		location_hostile = super.getUniformLocation("hostile");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection)
	{
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadHostility(boolean hostile)
	{
		super.loadBoolean(location_hostile, hostile);
	}
	
	public void loadJointTransforms(Matrix4f[] jointTransforms)
	{
		for(int i = 0; i < jointTransforms.length; i++)
		{
			super.loadMatrix(location_jointTransforms[i], jointTransforms[i]);
		}
	}
	
	public void loadAnimationComponent(boolean hasAnimation)
	{
		super.loadBoolean(location_hasAnimation, hasAnimation);
	}

}

package skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import rendering.Window;
import shaders.ShaderProgram;
import utils.Maths;

public class SkyboxShader extends ShaderProgram {
	
	private static final String VERTEX_PATH = "src/skybox/skyboxVertexShader";
	private static final String FRAGMENT_PATH = "src/skybox/skyboxFragmentShader";
	
	private static final float ROTATE_SPEED = 1f;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColour;
	private int location_cubeMap;
	private int location_cubeMap2;
	private int location_blend;
	
	private float currentRot = 0;
	
	public SkyboxShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColour = super.getUniformLocation("fogColour");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
		location_blend = super.getUniformLocation("blend");
	}
	
	public void loadBlend(float blend)
	{
		super.loadFloat(location_blend, blend);
	}
	
	public void connectTextureUnits()
	{
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
	}
	
	public void loadFogColour(float r, float g, float b)
	{
		super.loadVector(location_fogColour, new Vector3f(r,g,b));
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		currentRot += ROTATE_SPEED * Window.getFrameTime();
		Matrix4f.rotate((float) Math.toRadians(currentRot), new Vector3f(0,1,0), matrix, matrix);
		super.loadMatrix(location_viewMatrix,matrix);
	}
	
	


}

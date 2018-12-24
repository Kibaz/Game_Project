package water;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import entities.Player;
import shaders.ShaderProgram;
import utils.Maths;

public class WaterShader extends ShaderProgram{
	
	private final static String VERTEX_PATH = "src/water/waterVertexShader.txt";
	private final static String FRAGMENT_PATH = "src/water/waterFragmentShader.txt";
	
	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_waveMotion;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_depthMap;
	private int location_near;
	private int location_far;
	private int location_time;
	private int location_applyOffset;
	private int location_heightmap;

	public WaterShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		location_modelMatrix = super.getUniformLocation("modelMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_reflectionTexture = super.getUniformLocation("reflectionTexture");
		location_refractionTexture = super.getUniformLocation("refractionTexture");
		location_dudvMap = super.getUniformLocation("dudvMap");
		location_waveMotion = super.getUniformLocation("waveMotion");
		location_cameraPosition = super.getUniformLocation("cameraPosition");
		location_normalMap = super.getUniformLocation("normalMap");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour");
		location_depthMap = super.getUniformLocation("depthMap");
		location_near = super.getUniformLocation("near");
		location_far = super.getUniformLocation("far");
		location_time = super.getUniformLocation("waveTime");
		location_applyOffset = super.getUniformLocation("applyOffset");
		location_heightmap = super.getUniformLocation("heightmap");
	}
	
	public void loadFarPlane(float value)
	{
		super.loadFloat(location_far, value);
	}
	
	public void loadNearPlane(float value)
	{
		super.loadFloat(location_near, value);
	}
	
	public void loadLight(Light sun)
	{
		super.loadVector(location_lightColour, sun.getColour());
		super.loadVector(location_lightPosition, sun.getPosition());
	}
	
	public void loadWaveMotion(float factor)
	{
		super.loadFloat(location_waveMotion, factor);
	}
	
	public void connectTextureUnits()
	{
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}
	
	public void loadModelMatrix(Matrix4f modelMatrix)
	{
		super.loadMatrix(location_modelMatrix, modelMatrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, camera.getPosition());
	}
	
	public void loadProjectionMatrix(Matrix4f projectionMatrix)
	{
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}
	
	public void loadTime(float time)
	{
		super.loadFloat(location_time, time);
	}
	
	public void applyOffset(boolean apply)
	{
		super.loadBoolean(location_applyOffset, apply);
	}

}

package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import utils.Maths;

public class TerrainShader extends ShaderProgram{
	
	private static final String VERTEX_PATH = "src/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_PATH = "src/shaders/terrainFragmentShader.txt";
	
	private static final int MAX_LIGHTS = 4;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_plane;
	private int location_causticTexture;
	private int location_causticEffect;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	private int location_shadowMapSize;
	
	public TerrainShader()
	{
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_plane = super.getUniformLocation("plane");
		location_causticEffect = super.getUniformLocation("causticEffect");
		location_causticTexture = super.getUniformLocation("causticTexture");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_shadowMapSize = super.getUniformLocation("mapSize");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++)
		{
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	
	public void loadCausticTexture()
	{
		super.loadInt(location_causticTexture, 1);
	}
	
	public void loadCausticEffect(float blend)
	{
		super.loadFloat(location_causticEffect, blend);
	}
	
	public void loadClipPlane(Vector4f plane)
	{
		super.load4DVector(location_plane, plane);
	}
	
	public void loadShineVariables(float shineDamper, float reflect)
	{
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflect);
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
	
	public void loadLights(List<Light> lights)
	{
		for(int i = 0; i < MAX_LIGHTS; i++)
		{
			if(i < lights.size())
			{
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}else
			{
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
				super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadProjectionMatrix(Matrix4f projection)
	{
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadToShadowMapSpace(Matrix4f matrix)
	{
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}
	
	public void connectTextureUnits()
	{
		super.loadInt(location_shadowMap, 2);
	}
	
	public void loadShadowMapSize(float shadowMapSize)
	{
		super.loadFloat(location_shadowMapSize, shadowMapSize);
	}

}

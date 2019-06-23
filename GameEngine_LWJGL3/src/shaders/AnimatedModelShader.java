package shaders;

import org.lwjgl.util.vector.Matrix4f;
import entities.Camera;
import entities.Light;
import utils.Maths;

public class AnimatedModelShader extends ShaderProgram {
	
	
	private static final String VERTEX_SHADER = "src/shaders/animModelVS.txt";
	private static final String FRAGMENT_SHADER = "src/shaders/animModelFS.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_jointTransforms[];

	public AnimatedModelShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "jointIndices");
		super.bindAttribute(4, "weights");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightPosition = super.getUniformLocation("lightColour");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_jointTransforms = new int[150];
		for(int i = 0; i < location_jointTransforms.length; i++)
		{
			int loc = super.getUniformLocation("jointTransforms[" + i + "]");
			location_jointTransforms[i] = loc;
		}
		
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
	
	public void loadLight(Light light)
	{
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	}
	
	public void loadProjectionMatrix(Matrix4f projection)
	{
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadMatrixArray(Matrix4f[] matrices)
	{
		for(int i = 0; i < matrices.length; i++)
		{
			super.loadMatrix(location_jointTransforms[i], matrices[i]);
		}
	}

}

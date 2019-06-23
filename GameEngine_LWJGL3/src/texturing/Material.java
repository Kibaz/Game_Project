package texturing;

import org.lwjgl.util.vector.Vector4f;

public class Material {
	
	public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f,1.0f,1.0f,1.0f);
	
	private Vector4f diffuseColour;
	
	private Vector4f specularColour;
	
	private Vector4f ambient;
	
	private float reflectivity;
	
	private ModelTexture texture;
	
	private ModelTexture normalMap;
	
	public Material()
	{
		this.diffuseColour = DEFAULT_COLOUR;
		this.specularColour = DEFAULT_COLOUR;
		this.ambient = DEFAULT_COLOUR;
		this.texture = null;
		this.reflectivity = 0;
	}
	
	
	
	public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, ModelTexture texture, float reflectivity)
	{
		this.diffuseColour = diffuse;
		this.specularColour = specular;
		this.texture = texture;
		this.reflectivity = reflectivity;
	}



	public Vector4f getDiffuseColour() {
		return diffuseColour;
	}



	public Vector4f getSpecularColour() {
		return specularColour;
	}



	public Vector4f getAmbient() {
		return ambient;
	}



	public float getReflectivity() {
		return reflectivity;
	}



	public ModelTexture getTexture() {
		return texture;
	}



	public ModelTexture getNormalMap() {
		return normalMap;
	}
	
	
	
	

}

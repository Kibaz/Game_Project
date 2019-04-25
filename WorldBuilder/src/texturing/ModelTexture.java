package texturing;

import org.lwjgl.util.vector.Vector4f;

public class ModelTexture {

	private int texID;
	
	public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f,1.0f,1.0f,1.0f);
	
	private Vector4f ambient, diffuse, specular;
	
	private float shineDamper =  1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int id)
	{
		this.texID = id;
		this.ambient = DEFAULT_COLOUR;
		this.diffuse = DEFAULT_COLOUR;
		this.specular = DEFAULT_COLOUR;
	}
	
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}





	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}


	public boolean isHasTransparency() {
		return hasTransparency;
	}


	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}





	public int getNumberOfRows() {
		return numberOfRows;
	}


	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}



	public int getTextureID()
	{
		return this.texID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}
	
	
	
	
	
	
}

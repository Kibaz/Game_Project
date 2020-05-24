package professions;

import org.lwjgl.util.vector.Vector3f;

import models.BaseModel;
import models.TexturedModel;
import rendering.Loader;
import texturing.ModelTexture;
import utils.StaticModelLoader;

public class Mineral {
	
	public static final int MAX_CAPACITY = 5;
	
	private TexturedModel model;
	
	private int iconTexture;
	
	private Vector3f[] positionOffsets;
	private Vector3f[] rotationOffsets;
	
	public Mineral(Loader loader,String modelFile,String textureFile,String iconFile,
			Vector3f[] positionOffsets,Vector3f[] rotationOffsets)
	{
		BaseModel[] models = StaticModelLoader.load("res/" + modelFile, loader);
		int texture = loader.loadTexture("res/" + textureFile);
		this.iconTexture = loader.loadTexture("res/"+iconFile);
		this.model = new TexturedModel(models[0],new ModelTexture(texture));
		this.positionOffsets = positionOffsets;
		this.rotationOffsets = rotationOffsets;
	}

	public TexturedModel getModel() {
		return model;
	}

	public int getIconTexture() {
		return iconTexture;
	}

	public Vector3f[] getPositionOffsets() {
		return positionOffsets;
	}

	public Vector3f[] getRotationOffsets() {
		return rotationOffsets;
	}
	
	
	
	

}

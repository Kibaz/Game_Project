package models;

import texturing.ModelTexture;

public class TexturedModel {

	private BaseModel baseModel;
	private ModelTexture texture;
	
	public TexturedModel(BaseModel model, ModelTexture texture)
	{
		this.baseModel = model;
		this.texture = texture;
	}

	public BaseModel getBaseModel() {
		return baseModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	
	
}

package models;

import texturing.ModelTexture;

public class TexturedModel {

	private BaseModel model;
	private ModelTexture texture;
	
	public TexturedModel(BaseModel model, ModelTexture texture)
	{
		this.model = model;
		this.texture = texture;
	}

	public BaseModel getBaseModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	
	
}

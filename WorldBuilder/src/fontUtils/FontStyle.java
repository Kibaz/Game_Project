package fontUtils;

import java.io.File;

public class FontStyle {
	
	private int textureAtlas;
	private TextMeshLoader loader;
	
	public FontStyle(int textureID, File fontFile)
	{
		this.textureAtlas = textureID;
		this.loader = new TextMeshLoader(fontFile);
	}
	
	public int getTextureAtlas()
	{
		return this.textureAtlas;
	}
	
	public TextMeshData loadText(GUIText text)
	{
		return loader.createMesh(text);
	}

}

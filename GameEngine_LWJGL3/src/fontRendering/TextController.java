package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fontUtils.FontStyle;
import fontUtils.GUIText;
import fontUtils.TextMeshData;
import rendering.Loader;

public class TextController {
	
	private static Loader loader;
	private static Map<FontStyle, List<GUIText>> texts = new HashMap<>();
	
	private static FontRenderer renderer;
	
	public static void init(Loader theLoader)
	{
		renderer = new FontRenderer();
		loader = theLoader;
	}
	
	public static void render()
	{
		renderer.render(texts);
	}
	
	public static void loadText(GUIText text)
	{
		FontStyle font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTexCoords());
		text.setMesh(vao, data.getVertCount());
		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null)
		{
			textBatch = new ArrayList<>();
			texts.put(font, textBatch);
		}
		if(textBatch.contains(text))
		{
			return;
		}
		textBatch.add(text);
	}
	
	public static void removeText(GUIText text)
	{
		List<GUIText> textBatch = texts.get(text.getFont());
		if(textBatch == null)
		{
			return;
		}
		textBatch.remove(text);
		if(textBatch.isEmpty())
		{
			texts.remove(text.getFont());
		}
	}
	
	public static void cleanUp()
	{
		renderer.cleanUp();
	}

}

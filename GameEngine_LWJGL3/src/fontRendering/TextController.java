package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import fontUtils.FontStyle;
import fontUtils.GUIText;
import fontUtils.TextMeshData;
import rendering.Loader;

public class TextController {
	
	private static Loader loader;
	private static Map<FontStyle, List<GUIText>> texts = new HashMap<>();
	
	private static List<GUIText> removeQueue = new ArrayList<>();
	
	private static FontRenderer renderer;
	
	public static void init(Loader theLoader,Matrix4f projectionMatrix)
	{
		renderer = new FontRenderer(projectionMatrix);
		loader = theLoader;
	}
	
	public static void render(Camera camera)
	{
		renderer.render(texts,camera);
	}
	
	public static void updateTexts()
	{
		for(Entry<FontStyle,List<GUIText>> entry: texts.entrySet())
		{
			List<GUIText> relTexts = entry.getValue();
			for(GUIText text: relTexts)
			{
				if(text.isFloating())
				{
					text.animate();
				}
			}
		}
		
		// Remove any texts in the removal queue
		for(GUIText text: removeQueue)
		{
			TextController.removeText(text);
		}
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
	
	public static void addToRemovalQueue(GUIText text)
	{
		removeQueue.add(text);
	}

}

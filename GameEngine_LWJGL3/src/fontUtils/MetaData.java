package fontUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rendering.Window;

public class MetaData {
	
	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;
	
	private static final int DESIRED_PADDING = 3;
	
	private static final String SPLIT_STR = " ";
	private static final String NUMBER_SPLIT = ",";
	
	private double aspectRatio;
	
	private double verticalPerPixelSize;
	private double horizontalPerPixelSize;
	private double spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;
	
	private Map<Integer, Character> metaData = new HashMap<>();
	
	private BufferedReader reader;
	private Map<String, String> values = new HashMap<>();
	
	public MetaData(File file)
	{
		this.aspectRatio = (double) Window.getWidth() / (double) Window.getHeight();
		openFile(file);
		loadPaddingData();
		loadLineSizes();
		int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		closeFile();
	}

	public double getSpaceWidth() {
		return spaceWidth;
	}
	
	public Character getCharacter(int ascii)
	{
		return metaData.get(ascii);
	}
	
	private boolean processNextLine()
	{
		values.clear();
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(line == null)
		{
			return false;
		}
		for(String part: line.split(SPLIT_STR))
		{
			String[] valuePairs = part.split("=");
			if(valuePairs.length == 2)
			{
				values.put(valuePairs[0], valuePairs[1]);
			}
		}
		
		return true;
	}
	
	public int getValueOfVariable(String var)
	{
		return Integer.parseInt(values.get(var));
	}
	
	private int[] getValuesOfVariable(String var)
	{
		String[] numbers = values.get(var).split(NUMBER_SPLIT);
		int[] actualValues = new int[numbers.length];
		for(int i = 0; i < actualValues.length; i++)
		{
			actualValues[i] = Integer.parseInt(numbers[i]);
		}
		return actualValues;
	}
	
	private void openFile(File file)
	{
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Couldn't open font meta data file!");
		}
	}
	
	private void closeFile()
	{
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPaddingData()
	{
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}
	
	private void loadLineSizes()
	{
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextMeshLoader.LINE_HEIGHT / (double) lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
	}
	
	private void loadCharacterData(int imageWidth)
	{
		processNextLine();
		processNextLine();
		while(processNextLine())
		{
			Character c = loadCharacter(imageWidth);
			if(c != null)
			{
				metaData.put(c.getId(), c);
			}
		}
	}
	
	private Character loadCharacter(int imageSize)
	{
        int id = getValueOfVariable("id");
        if (id == TextMeshLoader.SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
        return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}
	
	

}

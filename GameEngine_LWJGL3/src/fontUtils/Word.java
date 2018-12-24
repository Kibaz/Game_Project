package fontUtils;

import java.util.ArrayList;
import java.util.List;

public class Word {
	
	private List<Character> characters = new ArrayList<>();
	private double width = 0;
	private double fontSize;
	
	public Word(double fontSize)
	{
		this.fontSize = fontSize;
	}
	
	public void addCharacter(Character character)
	{
		characters.add(character);
		width += character.getxAdvance() * fontSize;
	}
	
	protected List<Character> getCharacters()
	{
		return characters;
	}
	
	public double getWidth()
	{
		return width;
	}
	

}

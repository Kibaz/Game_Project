package inputs;

public class KeyInput extends Input{
	
	private char key; // Store the key pressed

	public KeyInput(float timeStamp, char key) {
		super(timeStamp);
		this.key = key;
	}
	
	public char getKey()
	{
		return this.key;
	}

}

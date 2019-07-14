package inputs;

public class Input{
	
	// Fields
	private float time; // Store timeStamp of input
	
	private String inputStr;
	
	// Constructor 
	public Input(String inputStr,float time)
	{
		this.time = time;
		this.inputStr = inputStr;
	}

	// Getters
	public float getTime() {
		return time;
	}
	
	public String getInput()
	{
		return inputStr;
	}
	
	public byte[] toBytes()
	{
		String message = inputStr + "," + time;
		return message.getBytes();
	}
	
	

}

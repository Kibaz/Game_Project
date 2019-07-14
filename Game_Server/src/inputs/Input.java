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

	public float getTime() {
		return time;
	}
	
	public String getInput()
	{
		return inputStr;
	}
	
	

}

package inputs;

import java.util.concurrent.atomic.AtomicInteger;

public class Input {
	
	// Automatic increments of index
	private static final AtomicInteger count = new AtomicInteger(0);
	// Fields
	private int index; // Hold order reference of inputs
	private float timeStamp; // Store timeStamp of input
	
	// Constructor 
	public Input(float timeStamp)
	{
		this.timeStamp = timeStamp;
		// Every time a new input is registered, increment index automatically
		this.index = count.incrementAndGet();
	}

	// Getters
	public int getIndex() {
		return index;
	}

	public double getTimeStamp() {
		return timeStamp;
	}
	
	

}

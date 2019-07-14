package networking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import inputs.Input;

public class InputSnapshot {
	
	private static final AtomicInteger snapshotCounter = new AtomicInteger(0);
	
	private List<Input> inputs;
	
	private int index;
	
	public InputSnapshot()
	{
		this.index = snapshotCounter.incrementAndGet();
		this.inputs = new ArrayList<>();
	}
	
	public void addInput(Input input)
	{
		inputs.add(input);
	}
	
	public void addInputs(List<Input> inputs)
	{
		this.inputs.addAll(inputs);
	}
	
	public boolean containsSpaceKey()
	{
		for(Input input: inputs)
		{
			if(input.getInput().startsWith("space "))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public byte[] convertToByteArray()
	{
		String result = "INPUT SNAPSHOT:";
		
		for(int i = 0; i < inputs.size(); i++)
		{
			if(i == inputs.size() - 1)
			{
				result += inputs.get(i).getInput() + " " + inputs.get(i).getTime();
				break;
			}
			
			result += inputs.get(i).getInput() + " " + inputs.get(i).getTime() + ",";
			
			
		}
		
		return result.getBytes();
	}

	public int getIndex() {
		return index;
	}

	public List<Input> getInputs() {
		return inputs;
	}
	
	
	
	
	
	

}

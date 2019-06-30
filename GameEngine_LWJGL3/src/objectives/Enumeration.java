package objectives;

public class Enumeration extends Objective {
	
	private Task task;
	
	private String assocEntity;
	
	private int capacity;
	
	private int count;
	
	public Enumeration(String assocEntity, int capacity, Task task)
	{
		super();
		this.assocEntity = assocEntity;
		this.capacity = capacity;
		this.task = task;
		this.count = 0;
	}

	@Override
	protected void buildDescription() {
		if(task == Task.PURGE)
		{
			this.description = count + "/" + capacity + " " + assocEntity + "s slain.";
		}
	}

	@Override
	public void update() {
		count++;
		calculateProgress();
		if(progress == 1)
		{
			completed = true;
		}
	}

	@Override
	protected void calculateProgress() {
		this.progress = (float) count / (float) capacity;
	}

	public String getAssocEntity() {
		return assocEntity;
	}
	
	

}

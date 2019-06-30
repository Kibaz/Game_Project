package objectives;

public abstract class Objective {
	
	protected String description;
	
	protected boolean completed;
	
	protected float progress;
	
	public Objective()
	{
		completed = false;
		progress = 0;
		buildDescription();
	}
	
	protected abstract void buildDescription();
	
	public abstract void update();
	
	protected abstract void calculateProgress();

	public String getDescription() {
		return description;
	}

	public boolean isCompleted() {
		return completed;
	}

	public float getProgress() {
		return progress;
	}
	
	

}

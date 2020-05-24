package animation;

public class Animation {
	
	public static final int MAX_WEIGHTS = 4;
	
	private double duration;
	
	private double ticksPerSecond;
	
	private String name;
	
	private Node rootNode;
	
	private AnimationType type;
	
	public Animation(String name,double ticksPerSecond,double duration, Node rootNode)
	{
		this.name = name;
		this.duration = duration;
		this.rootNode = rootNode;
		this.ticksPerSecond = ticksPerSecond;
		this.type = AnimationType.LOOP; // Loop by default
	}

	public double getDuration() {
		return duration;
	}

	public String getName() {
		return name;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public double getTicksPerSecond() {
		return ticksPerSecond;
	}
	
	public AnimationType getType()
	{
		return type;
	}
	
	public void setType(AnimationType type)
	{
		this.type = type;
	}
	
	
	
	
	
	
	
	

}

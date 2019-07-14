package animation;

public class Animation {
	
	public static final int MAX_WEIGHTS = 4;
	
	private double duration;
	
	private double ticksPerSecond;
	
	private String name;
	
	private Node rootNode;
	
	public Animation(String name,double ticksPerSecond,double duration, Node rootNode)
	{
		this.name = name;
		this.duration = duration;
		this.rootNode = rootNode;
		this.ticksPerSecond = ticksPerSecond;
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
	
	
	
	
	
	

}

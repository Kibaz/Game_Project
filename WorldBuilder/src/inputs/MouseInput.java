package inputs;

public class MouseInput extends Input {
	
	private int button;
	
	public MouseInput(float timeStamp, int button)
	{
		super(timeStamp);
		this.button = button;
	}
	
	public int getButton()
	{
		return button;
	}

}

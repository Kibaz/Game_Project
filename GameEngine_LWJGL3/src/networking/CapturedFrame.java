package networking;

public class CapturedFrame {
	
	private int index;
	
	private float frameTime;
	
	public CapturedFrame(int index, float frameTime)
	{
		this.index = index;
		this.frameTime = frameTime;
	}
	
	public int getIndex() {
		return index;
	}

	public float getFrameTime() {
		return frameTime;
	}
	
	
	
	

}

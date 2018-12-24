package animation;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import models.BaseModel;

public class Animation {
	
	private int currFrame;
	
	private List<AnimatedFrame> frames;
	
	private BaseModel[] models;
	
	private String name;
	
	private double duration;
	
	public Animation(String name, List<AnimatedFrame> frames, double duration)
	{
		this.name = name;
		this.frames = frames;
		currFrame = 0;
		this.duration = duration;
	}

	public AnimatedFrame getCurrentFrame() {
		return this.frames.get(currFrame);
	}
	
	public int getCurrFrameIndex()
	{
		return currFrame;
	}
	
	public AnimatedFrame getNextFrame()
	{
		nextFrame();
		return this.frames.get(currFrame);
	}

	public List<AnimatedFrame> getFrames() {
		return frames;
	}

	public String getName() {
		return name;
	}

	public double getDuration() {
		return duration;
	}
	
	public BaseModel[] getModels()
	{
		return models;
	}
	
	public void setModels(BaseModel[] models)
	{
		this.models = models;
	}
	
	public void nextFrame()
	{
		int nextFrame = currFrame + 1;
		if(nextFrame > frames.size() - 1)
		{
			currFrame = 0;
		}
		else
		{
			currFrame = nextFrame;
		}
	}
	

}

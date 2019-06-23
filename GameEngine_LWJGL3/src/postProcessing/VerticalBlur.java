package postProcessing;

public class VerticalBlur extends PostProcessingEffect{
	
	public VerticalBlur(int fboWidth, int fboHeight)
	{
		super(new VerticalShader());
		shader.start();
		((VerticalShader)shader).loadFBOHeight(fboHeight);
		shader.stop();
		super.renderer = new FboRenderer(fboWidth,fboHeight);
	}

}

package postProcessing;

public class HorizontalBlur extends PostProcessingEffect{
	
	public HorizontalBlur(int fboWidth, int fboHeight)
	{
		super(new HorizontalShader());
		shader.start();
		((HorizontalShader)shader).loadFBOWidth(fboWidth);
		shader.stop();
		super.renderer = new FboRenderer(fboWidth,fboHeight);
	}

}

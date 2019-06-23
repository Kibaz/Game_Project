package postProcessing;

public class BlurFilter extends PostProcessingEffect{

	public BlurFilter(int width, int height) {
		super(new BlurFilterShader());
		super.renderer = new FboRenderer(width,height);
	}

}

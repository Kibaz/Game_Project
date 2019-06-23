package combat;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import rendering.Loader;

public class AbilityRenderer {
	
	private IndicatorRenderer indicatorRenderer;
	
	public AbilityRenderer(Loader loader,Matrix4f projectionMatrix)
	{
		indicatorRenderer = new IndicatorRenderer(projectionMatrix);
	}
	
	public void render(List<Ability> abilities, Camera camera)
	{
		for(Ability ability: abilities)
		{
			if(ability.getGui().isHovered())
			{
				indicatorRenderer.render(ability.getDamageIndicator(), camera);
			}
		}
	}
	
	public void cleanUp()
	{
		indicatorRenderer.cleanUp();
	}

}

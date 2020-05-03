package combat;

import java.util.List;

import org.lwjgl.opengl.GL11;
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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		for(Ability ability: abilities)
		{
			if(ability.getGui() != null)
			{
				if(ability.getGui().isHovered())
				{
					indicatorRenderer.render(ability.getDamageIndicator(), camera);
				}
			}
			else
			{
				indicatorRenderer.render(ability.getDamageIndicator(), camera);
			}

		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void cleanUp()
	{
		indicatorRenderer.cleanUp();
	}

}

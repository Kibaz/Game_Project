package equip;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import components.AnimationComponent;
import components.EntityInformation;
import entities.Camera;
import entities.Entity;
import entities.Light;
import rendering.EntityRenderer;
import shaders.EntityShader;

public class EquipDisplayRenderer {
	
	private EntityRenderer renderer;
	private EntityShader shader;
	private EquipInventory equipInventory;
	
	public EquipDisplayRenderer(Matrix4f projectionMatrix,EquipInventory inventory)
	{
		this.shader = new EntityShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		this.renderer = new EntityRenderer(shader,null,projectionMatrix);
		this.equipInventory = inventory;
	}
	
	public void render(Camera camera,List<Light> lights)
	{
		prepare();
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadLights(lights);
		shader.stop();
		Entity copy = new Entity(equipInventory.getEntity().getModel(),new Vector3f(0,0,0),0,0,0,1);
		copy.addComponent(equipInventory.getEntity().getComponentByType(AnimationComponent.class));
		EquipInventory copyInventory = new EquipInventory(false);
		copy.addComponent(copyInventory);
		
		renderer.renderSingleEntity(copy); // Render copy of main entity
		
		// Render copies of the items
		Map<EquipSlot,Entity> inventory = equipInventory.getInventory();
		for(Entry<EquipSlot,Entity> entry: inventory.entrySet())
		{
			Entity item = entry.getValue();
			if(item != null)
			{
				Entity itemCopy = new Entity(item.getModel(),new Vector3f(0,0,0),0,0,0,1);
				EquipItem equipItem = item.getComponentByType(EquipItem.class);
				itemCopy.addComponent(new EquipItem("TEST",
						equipItem.getMaxDurability(),
						equipItem.getDurability(),
						equipItem.getAttachPoint(),
						equipItem.getEquipSlot(),
						0,
						0));
				copyInventory.equip(itemCopy);
				renderer.renderSingleEntity(itemCopy);
			}
		}
	}
	
	private void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.42f,0.42f,0.42f,0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

}

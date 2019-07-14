package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.util.vector.Vector3f;

import components.Component;
import models.Model;

public class Entity {
	
	private UUID id;
	
	private Vector3f position;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float scale;
	
	private Model model;
	
	private Map<String,Component> components;
	
	public Entity(Vector3f position, float rotX, float rotY, float rotZ, float scale)
	{
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.components = new HashMap<>();
		this.id = UUID.randomUUID();
	}
	
	public void increasePosition(float dx, float dy, float dz)
	{
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz)
	{
		this.rotX+=dx;
		this.rotY+=dy;
		this.rotZ+=dz;
	}
	
	public void update()
	{
		for(String componentName: components.keySet())
		{
			components.get(componentName).udpate();
		}
	}
	
	public void addComponent(Component component)
	{
		component.setEntity(this);
		this.components.put(component.getName(), component);
	}
	
	public Component getComponentByName(String name)
	{
		return components.get(name);
	}
	
	public <T extends Component> T getComponentByType(Class<T> type)
	{
		for(String componentName: components.keySet())
		{
			if(components.get(componentName).getClass() == type)
			{
				return type.cast(components.get(componentName));
			}
		}
		
		return null;
	}

	public UUID getId() {
		return id;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRotX() {
		return rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public float getScale() {
		return scale;
	}
	
	

}

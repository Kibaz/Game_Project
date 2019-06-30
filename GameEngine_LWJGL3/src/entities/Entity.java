package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.util.vector.Vector3f;

import components.Component;
import models.TexturedModel;
import networking.Client;
import physics.AABB;
import physics.Triangle;
import rendering.Window;

public class Entity {

	private AABB aabb;
	private TexturedModel texturedModel;
	private Vector3f position;
	private float rotX, rotY, rotZ, scale;
	private Triangle[] triangles;
	
	private UUID id;
	
	private boolean staticModel = false;
	private boolean clickable = false;
	private boolean clicked = false;
	private boolean hovered = false;
	
	private float clickRange = 100f;
	
	private int textureIndex = 0;
	
	// Component list to retain a component based architecture
	private Map<String,Component> components;
	
	public Entity(TexturedModel model, Vector3f position, 
			float rotX, float rotY, float rotZ, float scale)
	{
		this.texturedModel = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.aabb = new AABB(this, this.position);
		this.triangles = new Triangle[this.getModel().getBaseModel().getIndices().length/3];
		this.components = new HashMap<>();
		this.id = UUID.randomUUID();
		setTriangles();
	}
	
	public Entity(TexturedModel model, int index,  Vector3f position, 
			float rotX, float rotY, float rotZ, float scale)
	{
		this.textureIndex = index;
		this.texturedModel = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.aabb = new AABB(this, this.position);
		this.triangles = new Triangle[this.getModel().getBaseModel().getIndices().length/3];
		this.components = new HashMap<>();
		this.id = UUID.randomUUID();
		setTriangles();
	}
	
	public void update()
	{
		// Update all components
		for(String componentName: components.keySet())
		{
			components.get(componentName).update();
		}
	}
	
	public float getTextureXOffset()
	{
		int column = textureIndex%texturedModel.getTexture().getNumberOfRows();
		return (float)column/(float)texturedModel.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset()
	{
		int row = textureIndex/texturedModel.getTexture().getNumberOfRows();
		return (float)row/(float)texturedModel.getTexture().getNumberOfRows();
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
	
	public void cleanUpComponents()
	{
		for(String componentName: components.keySet())
		{
			components.get(componentName).cleanUp();
		}
	}
	
	public TexturedModel getModel() {
		return texturedModel;
	}
	public void setModel(TexturedModel model) {
		this.texturedModel = model;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public float getRotX() {
		return rotX;
	}
	public void setRotX(float rotX) {
		this.rotX = rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public void setRotY(float rotY) {
		this.rotY = rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public AABB getAABB()
	{
		return aabb;
	}
	
	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	public float getClickRange()
	{
		return clickRange;
	}
	
	public void setClickRange(float range)
	{
		this.clickRange = range;
	}
	
	public boolean isClicked()
	{
		return clicked;
	}
	
	public void setClicked(boolean clicked)
	{
		this.clicked = clicked;
	}
	
	// Interpolate position
	public void interpolatePosition(Vector3f prevPosition, Vector3f nextPos)
	{	
		if(prevPosition == null || nextPos == null)
		{
			return;
		}
		Vector3f diffPos = Vector3f.sub(nextPos, prevPosition, null);
		float velX = diffPos.x / 0.125f;
		float velY = diffPos.y / 0.125f;
		float velZ = diffPos.z / 0.125f;
		
		float dx = velX * Window.getFrameTime();
		float dy = velY * Window.getFrameTime();
		float dz = velZ * Window.getFrameTime();
		
		increasePosition(dx,dy,dz);
		
	}
	
	// Interpolate rotation
	public void interpolateRotation(float prevRotX, float prevRotY, float prevRotZ,
									float nextRotX, float nextRotY, float nextRotZ)
	{
		if(Client.getCurrentPlayerPosition() == null || Client.getUpdateTime() == 0)
		{
			return;
		}
		
		float diffX = nextRotX - prevRotX;
		float diffY = nextRotY - prevRotY;
		float diffZ = nextRotZ - prevRotZ;
		
		float rx = (diffX / Client.getUpdateTime()) * Window.getFrameTime();
		float ry = (diffY / Client.getUpdateTime()) * Window.getFrameTime();
		float rz = (diffZ / Client.getUpdateTime()) * Window.getFrameTime();
		
		increaseRotation(rx,ry,rz);
	}
	
	public boolean isPlayerInClickRange(Entity player)
	{
		Vector3f centre = aabb.getCentre(); // Get centre of the entity's model
		
		// Calculate boundary for click range
		Vector3f boundaryPos = new Vector3f(centre.x + clickRange, centre.y + clickRange, centre.z + clickRange);
		Vector3f boundaryNeg = new Vector3f(centre.x - clickRange, centre.y - clickRange, centre.z - clickRange);
		float distXPos = boundaryPos.x - player.getPosition().x;
		float distYPos = boundaryPos.y - player.getPosition().y;
		float distZPos = boundaryPos.z - player.getPosition().z;
		
		float distXNeg = boundaryNeg.x - player.getPosition().x;
		float distYNeg = boundaryNeg.y - player.getPosition().y;
		float distZNeg = boundaryNeg.z - player.getPosition().z;
		
		if(distXPos > 0 && distXNeg < 0 &&
		   distYPos > 0 && distYNeg < 0 &&
		   distZPos > 0 && distZNeg < 0)
		{
			return true;
		}
		
		return false;
	}

	private void setTriangles()
	{
		int vertCounter = 0;
		// Convert floating points into Vector3f vertices
		float[] vertices = this.getModel().getBaseModel().getVertices();
		Vector3f[] verts = new Vector3f[this.getModel().getBaseModel().getVertices().length/3];
		for(int i = 0; i < verts.length; i++)
		{
			float value1 = vertices[vertCounter];
			vertCounter++;
			float value2 = vertices[vertCounter];
			vertCounter++;
			float value3 = vertices[vertCounter];
			vertCounter++;
			verts[i] = new Vector3f(value1*scale, value2*scale, value3*scale);
		}
		
		for(Vector3f vert: verts)
		{
			vert.x += position.x;
			vert.y += position.y;
			vert.z += position.z;
		}
		
		int[] indices = this.getModel().getBaseModel().getIndices();
		int indexCounter = 0;
		for(int i = 0; i < triangles.length; i++)
		{
			int index1 = indices[indexCounter];
			indexCounter++;
			int index2 = indices[indexCounter];
			indexCounter++;
			int index3 = indices[indexCounter];
			indexCounter++;
			
			triangles[i] = new Triangle(verts[index1], verts[index2], verts[index3]);
		}
	}
	
	public Triangle[] getTriangles()
	{
		return triangles;
	}
	
	public void setStaticModel(boolean isStatic)
	{
		this.staticModel = isStatic;
	}
	
	public boolean isStaticModel()
	{
		return staticModel;
	}
	
	public boolean isHovered()
	{
		return hovered;
	}
	
	public void setHovered(boolean hovered)
	{
		this.hovered = hovered;
	}
	
	public UUID getID()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return this.id == ((Entity)(obj)).id;
	}
	
	
	
	
	
}

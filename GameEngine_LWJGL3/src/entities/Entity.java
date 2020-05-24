package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.util.vector.Vector3f;

import components.Component;
import models.BaseModel;
import models.TexturedModel;
import physics.AABB;
import physics.CollisionBox;
import physics.Triangle;
import rendering.Window;

public class Entity {

	private AABB aabb;
	private TexturedModel model;
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
	
	// Generate list of collision boxes for physics engine
	private List<CollisionBox> collisionBoxes;
	
	private List<Entity> addedItems;
	
	public Entity parent;
	
	public Entity(TexturedModel model, Vector3f position, 
			float rotX, float rotY, float rotZ, float scale)
	{
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.aabb = new AABB(this, this.position);
		this.components = new HashMap<>();
		this.id = UUID.randomUUID();
		this.triangles = new Triangle[this.getModel().getBaseModel().getIndices().length/3];
		this.addedItems = new ArrayList<>();
		setTriangles();
	}
	
	public Entity(TexturedModel model, int index,  Vector3f position, 
			float rotX, float rotY, float rotZ, float scale)
	{
		this.textureIndex = index;
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.aabb = new AABB(this, this.position);
		this.components = new HashMap<>();
		this.id = UUID.randomUUID();
		this.triangles = new Triangle[this.getModel().getBaseModel().getIndices().length/3];
		this.addedItems = new ArrayList<>();
		setTriangles();
	}
	
	public void update()
	{
		// Update all components
		for(String componentName: components.keySet())
		{
			components.get(componentName).update();
		}
		
		for(Entity item: addedItems)
		{
			item.setPosition(this.getPosition());
			item.setRotX(this.getRotX());
			item.setRotY(this.getRotY());
			item.setRotZ(this.getRotZ());
		}
	}
	
	public void addItem(Entity item)
	{
		this.addedItems.add(item);
	}
	
	public List<Entity> getAddedItems()
	{
		return addedItems;
	}
	
	public float getTextureXOffset()
	{
		int column = textureIndex%model.getTexture().getNumberOfRows();
		return (float)column/(float)model.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset()
	{
		int row = textureIndex/model.getTexture().getNumberOfRows();
		return (float)row/(float)model.getTexture().getNumberOfRows();
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
		component.init(); // Initialise component
		this.components.put(component.getName(), component);
	}
	
	public void addCollisionBox(CollisionBox collisionBox)
	{
		collisionBoxes.add(collisionBox);
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
	
	public boolean hasComponent(Class<?> type)
	{
		for(String componentName: components.keySet())
		{
			if(components.get(componentName).getClass().equals(type))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void cleanUpComponents()
	{
		for(String componentName: components.keySet())
		{
			components.get(componentName).cleanUp();
		}
	}
	
	public TexturedModel getModel() {
		return model;
	}
	public void setModels(TexturedModel model) {
		this.model = model;
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
	
	public Vector3f findMinVertex()
	{
		return this.model.getBaseModel().findMinVertex();
	}
	
	public Vector3f findMaxVertex()
	{
		return this.model.getBaseModel().findMaxVertex();
	}
	
	public float getModelHeight()
	{
		float height = 0;
		Vector3f min = this.findMinVertex();
		Vector3f max = this.findMaxVertex();
		
		height = max.y - min.y;
		
		return height;
	}
	
	public float getModelWidth()
	{
		float width = 0;
		Vector3f min = this.findMinVertex();
		Vector3f max = this.findMaxVertex();
		
		width = max.x - min.x;
		
		return width;
	}
	
	public float getModelZWidth()
	{
		float length = 0;
		Vector3f min = this.findMinVertex();
		Vector3f max = this.findMaxVertex();
		
		length = max.z - min.z;
		
		return length;
	}
	
	public Vector3f calculateCentre()
	{
		Vector3f centre;
		float midPointX = findMinVertex().x + ((findMaxVertex().x - findMinVertex().x)/2);
		float midPointY = findMinVertex().y + ((findMaxVertex().y - findMinVertex().y)/2);
		float midPointZ = findMinVertex().z + ((findMaxVertex().z - findMinVertex().z)/2);
		centre = new Vector3f(midPointX, midPointY, midPointZ);
		return centre;
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
	
	public void setID(UUID id)
	{
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return this.id == ((Entity)(obj)).id;
	}
	
	
	
	
	
}

package inputs;

import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import combat.Ability;
import entities.Camera;
import entities.Entity;
import entities.Player;
import guis.GUI;
import guis.GUITexture;
import physics.AABB;
import rendering.Window;
import terrains.Terrain;
import utils.Maths;
import worldData.World;

public class MousePicker {
	
	private final int RAY_LENGTH = 600;
	private final int RECURSION_LIMIT = 200;
	
	private Vector3f ray;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	
	private Camera camera;
	
	private Terrain terrain;
	private Vector3f currentTerrainPoint;
	
	private Entity currentHoveredEntity;
	private Entity previousHoveredEntity;

	private int prevLeftMouseState = GLFW.GLFW_RELEASE;
	private int prevRightMouseState = GLFW.GLFW_RELEASE;
	
	public MousePicker(Camera camera, Matrix4f projection, Terrain terrain)
	{
		this.camera = camera;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.createViewMatrix(camera);
		this.terrain = terrain;
	}
	
	public Vector3f getCurrentTerrainPoint()
	{
		return currentTerrainPoint;
	}
	
	public Entity getCurrentHoveredEntity()
	{
		return currentHoveredEntity;
	}
	
	public Entity getPreviousHoveredEntity()
	{
		return previousHoveredEntity;
	}

	public Vector3f getRay()
	{
		return ray;
	}
	
	public void update(Player player, List<Entity> entities, List<GUI> guis, List<Ability> abilities)
	{
		viewMatrix = Maths.createViewMatrix(camera);
		ray = calculateMouseRay(); // Also equal to the "Direction" vector
		if(intersectionInRange(0, RAY_LENGTH, ray))
		{
			currentTerrainPoint = binarySearch(0,0,RAY_LENGTH,ray);
		}
		else
		{
			currentTerrainPoint = null;
		}

		// Calculate point of origin of the ray
		Vector3f start = getPointOnRay(ray, 0);
		
		// For each existing entity
		for(Entity entity: World.worldObjects)
		{
			// If the entity is clickable
			
			if(entity.isClickable())
			{
				if(rayIntersectsEntity(entity,ray,start)) {
					// Set entity as hovered
					entity.setHovered(true);
					if(entity.isPlayerInClickRange(player))
					{
						if(leftMouseClicked())
						{
							previousHoveredEntity = null;
							currentHoveredEntity = entity;
						}
						
						if(rightMouseClicked())
						{
							// Trigger event on clicked entity
						}
						
					}
				}
				else
				{
					if(entity.isHovered())
					{
						entity.setHovered(false);
					}
					
				}
				
				
			}
			
		}
		
		if(leftMouseClicked())
		{
			boolean clickedWhiteSpace = true;
			for(Ability ability: abilities)
			{
				if(intersectsGUI(ability.getGui()))
				{
					ability.doEffect(entities);
					clickedWhiteSpace = false;
				}
			}
			
			if(currentHoveredEntity != null && clickedWhiteSpace)
			{
				previousHoveredEntity = currentHoveredEntity;
				currentHoveredEntity = null;
			}
			
		}
		
		for(GUI gui: guis)
		{
			if(gui.isClickable())
			{
				if(intersectsGUI(gui))
				{
					gui.setHovered(true);
				}
				else
				{
					gui.setHovered(false);
				}
			}
		}
		
	}
	
	private boolean leftMouseClicked()
	{
		boolean clicked = false;
		int newState = GLFW.glfwGetMouseButton(Window.getWindowID(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
		if(newState == GLFW.GLFW_RELEASE && prevLeftMouseState == GLFW.GLFW_PRESS)
		{
			clicked = true;
		}
		
		prevLeftMouseState = newState;
		return clicked;
	}
	
	private boolean rightMouseClicked()
	{
		boolean clicked = false;
		int newState = GLFW.glfwGetMouseButton(Window.getWindowID(), GLFW.GLFW_MOUSE_BUTTON_RIGHT);
		
		if(newState == GLFW.GLFW_RELEASE && prevRightMouseState == GLFW.GLFW_PRESS)
		{
			clicked = true;
		}
		
		prevRightMouseState = newState;
		return clicked;
	}
	
	private Vector3f calculateMouseRay()
	{
		float mouseX = MouseCursor.getXPos();
		float mouseY = MouseCursor.getYPos();
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX,mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
		Vector4f eyeCoords = toEyeSpace(clipCoords);
		Vector3f worldRay = toWorldSpace(eyeCoords);
		return worldRay;
	}
	
	private Vector3f toWorldSpace(Vector4f eyeCoords)
	{
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y,rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}
	
	private Vector4f toEyeSpace(Vector4f clipCoords)
	{
		Matrix4f ivertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(ivertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}
	
	private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY)
	{
		float x = (2f*mouseX) / Window.getWidth() - 1f;
		float y = (2f*mouseY) / Window.getHeight() - 1f;
		return new Vector2f(x,-y);
	}
	
	private boolean intersectsGUI(GUI gui)
	{
		float minX = gui.getGUITexture().getPosition().x - (gui.getGUITexture().getScale().x);
		float minY = gui.getGUITexture().getPosition().y - (gui.getGUITexture().getScale().y);
		float maxX = gui.getGUITexture().getPosition().x + (gui.getGUITexture().getScale().x);
		float maxY = gui.getGUITexture().getPosition().y + (gui.getGUITexture().getScale().y);
		
		Vector2f normalisedCoords = getNormalizedDeviceCoords(MouseCursor.getXPos(),MouseCursor.getYPos());
		
		if(normalisedCoords.x > minX &&
			normalisedCoords.y > minY &&
			normalisedCoords.x < maxX &&
			normalisedCoords.y < maxY)
		{
			return true;
		}
		
		return false;
	}
	
	// Calculate the current point on the ray being analysed
	private Vector3f getPointOnRay(Vector3f ray, float distance)
	{
		Vector3f cameraPosition = camera.getPosition();
		Vector3f start = new Vector3f(cameraPosition.x, cameraPosition.y, cameraPosition.z);
		Vector3f rayByDistance = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, rayByDistance, null);
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_LIMIT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean rayIntersectsEntity(Entity entity, Vector3f dir, Vector3f origin)
	{
		float[] tMax = calculateRayLineVariantsMax(entity, dir, origin);
		float[] tMin = calculateRayLineVariantsMin(entity, dir, origin);
		
		float tmin = Math.max(Math.max(Math.min(tMin[0],tMax[0]), Math.min(tMin[1],tMax[1])), Math.min(tMin[2], tMax[2]));
		float tmax = Math.min(Math.min(Math.max(tMin[0],tMax[0]), Math.max(tMin[1],tMax[1])), Math.max(tMin[2], tMax[2]));
		
		if(tmax < 0)
		{
			return false;
		}
		
		if(tmin > tmax)
		{
			return false;
		}
		
		return true;
	}
	
	private float[] calculateRayLineVariantsMax(Entity entity, Vector3f dir, Vector3f origin)
	{
		float[] lineVariants = new float[3]; // store resultant values
		// Get AABB bound to entity
		AABB box = entity.getAABB();
		float dirX = 1.0f/dir.x; float dirY = 1.0f/dir.y; float dirZ = 1.0f/dir.z;
		float Tx = (box.getMaxPoint().x - origin.x) * dirX;
		float Ty = (box.getMaxPoint().y - origin.y) * dirY;
		float Tz = (box.getMaxPoint().z - origin.z) * dirZ;
		lineVariants[0] = Tx;
		lineVariants[1] = Ty;
		lineVariants[2] = Tz;
		return lineVariants;
	}
	
	private float[] calculateRayLineVariantsMin(Entity entity, Vector3f dir, Vector3f origin)
	{
		float[] lineVariants = new float[3]; // store resultant values
		// Get AABB bound to entity
		AABB box = entity.getAABB();
		float Tx = (box.getMinPoint().x - origin.x) / dir.x;
		float Ty = (box.getMinPoint().y - origin.y) / dir.y;
		float Tz = (box.getMinPoint().z - origin.z) / dir.z;
		lineVariants[0] = Tx;
		lineVariants[1] = Ty;
		lineVariants[2] = Tz;
		return lineVariants;
	}
	
	private boolean isUnderGround(Vector3f testPoint) {
		Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (terrain != null) {
			height = terrain.getTerrainHeight(testPoint.getX(), testPoint.getZ());
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Terrain getTerrain(float worldX, float worldZ) {
		return terrain;
	}
	
	

}

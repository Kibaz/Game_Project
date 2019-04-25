package pathfinding;

import org.lwjgl.util.vector.Vector2f;

import entities.Entity;
import terrains.Terrain;

public class GridSquare implements Comparable<GridSquare>{
	
	private enum State {
		UNVISITED, OPEN, CLOSED
	}
	
	// Specify grid positions
	private float x;
	private float z;
	private float size;
	
	private float startCost;
	private float costToTarget;
	private float totalCost;
	
	private State state;
	
	private GridSquare parent;
	
	// Set up grid square
	public GridSquare(float x, float z,float size)
	{
		this.x = x;
		this.z = z;
		this.size = size;
		this.state = State.UNVISITED;
		this.parent = null;
		this.startCost = 0;
		this.costToTarget = 0;
		this.totalCost = 0;
	}
	
	public void updateCosts()
	{
		this.totalCost = (this.startCost + this.costToTarget);
	}
	
	
	public boolean isClosed()
	{
		return this.state == State.CLOSED;
	}
	
	public boolean isOpen()
	{
		return this.state == State.OPEN;
	}
	

	public State getState() {
		return state;
	}
	
	public void setClosed()
	{
		this.state = State.CLOSED;
	}
	
	public void setOpen()
	{
		this.state = State.OPEN;
	}
	
	public void setUnvisited()
	{
		this.state = State.UNVISITED;
	}


	public float getStartCost() {
		return startCost;
	}



	public void setStartCost(float startCost) {
		this.startCost = startCost;
	}



	public float getCostToTarget() {
		return costToTarget;
	}



	public void setCostToTarget(float costToTarget) {
		this.costToTarget = costToTarget;
	}



	public float getTotalCost() {
		return totalCost;
	}



	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}



	public GridSquare getParent() {
		return parent;
	}



	public void setParent(GridSquare parent) {
		this.parent = parent;
	}



	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
	
	public boolean isOnSquare(Entity entity, Terrain terrain, Graph grid)
	{
		float terrainX = entity.getPosition().x - terrain.getX();
		float terrainZ = entity.getPosition().z - terrain.getZ();
		int gridX = (int) Math.floor(terrainX / size);
		int gridZ = (int) Math.floor(terrainZ / size);
		GridSquare gs = terrain.getGrid().getGrid()[gridX][gridZ];
		if(gs.equals(this))
		{
			return true;
		}
		
		return false;
	}

	@Override
	public int compareTo(GridSquare gs) {
		if(this.getTotalCost() < gs.getTotalCost())
		{
			return -1;
		}
		else if(this.getTotalCost() > gs.getTotalCost())
		{
			return 1;
		}
		
		return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null)
		{
			return false;
		}
		
		GridSquare gs = (GridSquare) o;
		Vector2f firstPosition = new Vector2f(this.getX(),this.getZ());
		Vector2f secondPosition = new Vector2f(gs.getX(),gs.getZ());
		return firstPosition.x == secondPosition.x && firstPosition.y == secondPosition.y;
	}
	

	
	

}

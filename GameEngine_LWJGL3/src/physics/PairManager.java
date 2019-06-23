package physics;

import entities.Entity;

public class PairManager {
	
	/*
	 * Used as part of the Physics engine
	 * Purpose is to retain information of entity pairs
	 * with the potential to collide - not necessarily colliding
	 * based on overlaps of the min and max points of their
	 * bounding boxes.
	 */
	
	/* Fields */
	
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;
	
	private Entity first;
	private Entity second;
	
	
	/* Constructor */
	public PairManager(Entity first, Entity second)
	{
		this.first = first;
		this.second = second;
	}
	
	public void setFirst(Entity entity)
	{
		this.first = entity;
	}
	
	public void setSecond(Entity entity)
	{
		this.second = entity;
	}
	
	public Entity getFirst()
	{
		return first;
	}
	
	public Entity getSecond()
	{
		return second;
	}
	
	/*
	 * Calculate distance between First entity's min
	 * point and Second entity's max point on the basis
	 * of a provided axis, i.e X,Y,Z
	 */
	private float getDistanceA(int axis)
	{
		return first.getAABB().getMin()[axis].getValue() - 
				second.getAABB().getMax()[axis].getValue();
	}
	
	/*
	 * Calculate distance between Second entity's min
	 * point and First entity's max point on the basis
	 * of a provided axis, i.e X,Y,Z
	 */
	
	private float getDistanceB(int axis)
	{
		return second.getAABB().getMin()[axis].getValue() - 
				first.getAABB().getMax()[axis].getValue();
	}
	
	public float getLowestDistance(int axis)
	{
		float distA = getDistanceA(axis);
		float distB = getDistanceB(axis);
		
		return (distA < distB) ? distA : distB;
	}
	
	
	/*
	 * Determines whether two instances of the "PairManager" class
	 * are equal on the basis of their stored entities' bounding
	 * boxes, and whether these are equal in value
	 * 
	 * Utilises the comparison "isEqual" method in the AABB class
	 */
	
	public boolean isEqual(PairManager other)
	{
		if((this.getFirst().equals(other.getFirst()) && this.getSecond().equals(other.getSecond())) ||
				(this.getFirst().equals(other.second) && this.getSecond().equals(other.getFirst())))
		{
			return true;
		}
		
		return false;
		
		
	}

}

package physics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import worldData.World;

public class SAP {
	
	/* Retain a list of all objects that will move around the world
	 * I.E Player, AI, Mob etc...
	 * These will have the potential to collide with static models
	 * No point in checking for collisions with static models
	 */
	
	private List<Entity> movingEntities;
	
	// Sweep and Prune Class - Type of Broad Phase algorithm
	private List<EndPoint> xAxis;
	private List<EndPoint> yAxis;
	private List<EndPoint> zAxis;
	
	private List<PairManager> activeListX;
	private List<PairManager> activeListY;
	private List<PairManager> activeListZ;
	
	public static List<PairManager> activeList = new ArrayList<>();
	
	public SAP()
	{
		movingEntities = new ArrayList<>();
		xAxis = new ArrayList<>();
		yAxis = new ArrayList<>();
		zAxis = new ArrayList<>();
		activeListX = new ArrayList<>();
		activeListY = new ArrayList<>();
		activeListZ = new ArrayList<>();
		fill();
	}
	
	// Fill the axes with End Points of each Entitie's AABBs
	// Add record of moving objects
	private void fill()
	{
		// Retrieve world data
		for(Entity entity: World.worldObjects)
		{	
			// If entity is moving/movable
			// Keep separate from static models
			if(!entity.isStaticModel())
			{
				movingEntities.add(entity);
			}
			else
			{
				// fill with minX value : X = index 0
				xAxis.add(entity.getAABB().getMin()[0]);
				// fill with maxX value : X = index 0
				xAxis.add(entity.getAABB().getMax()[0]);
				
				// fill with minY value : Y = index 1
				yAxis.add(entity.getAABB().getMin()[1]);
				// fill with maxY value : Y = index 1
				yAxis.add(entity.getAABB().getMax()[1]);
				
				// fill with minZ value : Z = index 2
				zAxis.add(entity.getAABB().getMin()[2]);
				// fill with maxZ value : Z = index 2
				zAxis.add(entity.getAABB().getMax()[2]);
			}
		}
	}
	
	/*
	 * Method to carry out all code required to be ran per frame
	 * Update the active list of possible collisions
	 * Clear Active List
	 * Verify active list
	 * Allow for collisions to be checked when necessary
	 * Prune unworthy pairs
	 */
	public void update()
	{
		activeList.clear();
		scanAxes();
	}
	
	// Verify if potential collisions exist
	// Check all axes for collidable pairs
	private boolean doPotentialCollisionsExist()
	{
		if(activeListX.isEmpty() || activeListY.isEmpty() || activeListZ.isEmpty())
		{
			return false;
		}
		
		return true;
	}
	
	/* 
	 * Scan the X Axis for Overlaps
	 * If overlap is found, add to activeList
	 * Clear active list per iteration
	 */
	private void scanXAxis()
	{
		activeListX.clear();
		
		// Check for possible collisions for each movable object
		for(Entity entity: movingEntities)
		{
			for(int i = 0; i < xAxis.size(); i++)
			{
				if(entity.getAABB().intersectOnXAxis(xAxis.get(i).getBox()))
				{
					activeListX.add(new PairManager(entity, xAxis.get(i).getBox().getEntity()));
				}
			}
		}
		
		/*
		 * Sort pairs in current axis list
		 * Ensures that pair with most potential to
		 * collide are checked before any others
		 */
		sortAxis(activeListX, PairManager.X_AXIS);
		
	}
	
	/* 
	 * Scan the Y Axis for Overlaps
	 * If overlap is found, add to activeList
	 * Clear active list per iteration
	 */
	private void scanYAxis()
	{
		activeListY.clear();
		
		// Check for possible collisions for each movable object
		for(Entity entity: movingEntities)
		{
			for(int i = 0; i < yAxis.size(); i++)
			{
				if(entity.getAABB().intersectOnYAxis(yAxis.get(i).getBox()))
				{
					activeListY.add(new PairManager(entity, yAxis.get(i).getBox().getEntity()));
				}
			}
		}
		
		/*
		 * Sort pairs in current axis list
		 * Ensures that pair with most potential to
		 * collide are checked before any others
		 */
		sortAxis(activeListY, PairManager.Y_AXIS);
		
	}

	/*
	 * Scan the Y Axis for Overlaps
	 * If overlap is found, add to activeList
	 * Clear active list per iteration
	 */
	private void scanZAxis()
	{
		activeListZ.clear();
		
		// Check for possible collisions for each movable object
		for(Entity entity: movingEntities)
		{
			for(int i = 0; i < zAxis.size(); i++)
			{
				if(entity.getAABB().intersectOnZAxis(zAxis.get(i).getBox()))
				{
					activeListZ.add(new PairManager(entity, zAxis.get(i).getBox().getEntity()));
				}
			}
		}
		
		/*
		 * Sort pairs in current axis list
		 * Ensures that pair with most potential to
		 * collide are checked before any others
		 */
		sortAxis(activeListZ, PairManager.Z_AXIS);
		
	}
	
	/*
	 * Scan all axes
	 * Carry out checks in active list
	 * Create final active list
	 */
	private void scanAxes()
	{
		scanXAxis();
		scanYAxis();
		scanZAxis();
		
		// Check if lists are empty
		if(!doPotentialCollisionsExist())
		{
			return;
		}
		
		// All lists have entries, check lists contain same entries
		// Add entries if a pair is found
		listsContainSamePair(activeListX, activeListY,activeListZ);
	}
	
	// Check that one list contains the same pair as another list
	private void listsContainSamePair(List<PairManager> list1, List<PairManager> list2, List<PairManager> list3)
	{
		int numMatches = 0;
		List<PairManager> temp = new ArrayList<>();
		
		for(int i = 0; i < list1.size(); i++)
		{
			for(int j = 0 ; j < list2.size(); j++)
			{
				if(list1.get(i).isEqual(list2.get(j)))
				{
					for(int k = 0; k < list3.size(); k++)
					{
						if(list2.get(j).isEqual(list3.get(k)))
						{
							temp.add(list3.get(k));
						}
					}
				}
			}
		}
		

		for(int i = 0; i < temp.size(); i++)
		{
			if(!alreadyExists(temp.get(i), activeList))
			{
				activeList.add(temp.get(i));
			}
		}
	}
	
	/*
	 * Advanced Sorting Algorithm - Implementation adapts
	 * the "Quick Sort" algorithm to ensure that the closest
	 * model to the player is the one being checked first before
	 * any other potential collision detection.
	 */
	
	private void sortAxis(List<PairManager> axisList, int axis)
	{
		if(axisList.isEmpty() || axisList == null)
		{
			return;
		}
		int length = axisList.size();
		quickSort(0, length-1, axisList, axis);
	}
	
	private void quickSort(int lowerIndex, int higherIndex, List<PairManager> axisList, int axis)
	{
		int i = lowerIndex;
		int j = higherIndex;
		
		PairManager iPair = axisList.get(i);
		PairManager jPair = axisList.get(j);
		
		PairManager pivot = axisList.get(lowerIndex +(higherIndex-lowerIndex)/2);
		
		float iDist = iPair.getLowestDistance(axis);
		float jDist = jPair.getLowestDistance(axis);
		float pivotDist = pivot.getLowestDistance(axis);
		
		while(i <= j)
		{

			while(iDist < pivotDist)
			{
				i++;
				iPair = axisList.get(i);
				iDist = iPair.getLowestDistance(axis);
			}
			while(jDist > pivotDist)
			{
				j--;
				jPair = axisList.get(j);
				jDist = jPair.getLowestDistance(axis);
			}
			
			if(i <= j)
			{
				swapNumbers(i,j,axisList);
				i++;
				j--;
			}
		}
		
		if(lowerIndex < j)
		{
			quickSort(lowerIndex,j,axisList,axis);
		}
		if(i < higherIndex)
		{
			quickSort(i,higherIndex,axisList,axis);
		}
	}
	
	private void swapNumbers(int i, int j, List<PairManager> list)
	{
		PairManager temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}
	
	
	/*
	 * Method to ensure an instance of the current PairManager object
	 * does not already exist within the ActiveList
	 * This prevents any duplicate pairs being checked for collisions
	 */
	private boolean alreadyExists(PairManager current, List<PairManager> list)
	{
		boolean result = false;
		for(PairManager pair: list)
		{
			// Attempt to find matching Bounding Box
			if(current.isEqual(pair))
			{
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	
}

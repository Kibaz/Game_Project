package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.lwjgl.util.vector.Vector2f;

public class Graph {
	
	private GridSquare[][] grid;
	private Vector2f startPosition;
	private Vector2f targetPosition;
	
	private float gridSquareSize;
	
	private PriorityQueue<GridSquare> openSquares;
	
	private Set<GridSquare> closedSquares;
	
	public Graph(int gridSize, float gridSquareSize)
	{
		grid = new GridSquare[gridSize][gridSize];
		startPosition = new Vector2f(0,0);
		targetPosition = new Vector2f(0,0);
		openSquares = new PriorityQueue<>();
		closedSquares = new HashSet<>();
		this.gridSquareSize = gridSquareSize;
	}
	
	public float getGridSquareSize()
	{
		return gridSquareSize;
	}

	public GridSquare getGridSquare(Vector2f position)
	{
		return grid[(int) position.x][(int) position.y];
	}
	
	public void setGridSquare(Vector2f position, GridSquare square)
	{
		grid[(int) position.x][(int) position.y] = square;
	}

	public Vector2f getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Vector2f startPosition) {
		this.startPosition = startPosition;
	}

	public Vector2f getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Vector2f targetPosition) {
		this.targetPosition = targetPosition;
	}
	
	public int getSize()
	{
		return grid.length;
	}
	
	public GridSquare[][] getGrid()
	{
		return grid;
	}
	
	public boolean isOnGrid(Vector2f coord)
	{
		return ((coord.getX() >= 0) && (coord.getX() < getSize()) && (coord.getY() >= 0) && (coord.getY() < getSize()));
	}
	
	public Set<GridSquare> getNeighbouringSquares(GridSquare square)
	{
		Set<GridSquare> neighbours = new HashSet<>();
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				if(!(i==0 && j==0))
				{
					if(isOnGrid(new Vector2f(square.getX()+i,square.getZ()+j)))
					{
						GridSquare gs = getGridSquare(new Vector2f(square.getX() + i, square.getZ() + j));
						neighbours.add(gs);
					}
				}
			}
		}
		
		return neighbours;
	}
	
	public float calculateDistance(Vector2f first, Vector2f second)
	{
		return (float) Math.pow(Math.pow(first.getX()-second.getX(), 2) + Math.pow(first.getY()-second.getY(), 2), 0.5);
	}
	
	public ArrayList<GridSquare> buildPath(GridSquare target)
	{
		ArrayList<GridSquare> path = new ArrayList<>();
		GridSquare current = target;
		while(current.getParent() != null)
		{
			path.add(current.getParent());
			current = current.getParent();
		}
		
		Collections.reverse(path);
		return path;
	}
	
	public void printPath(ArrayList<GridSquare> path)
	{
		for(int i = 0; i < path.size(); i++)
		{
			GridSquare gridSquare = path.get(i);
			System.out.println("Grid Square: (" + gridSquare.getX() + "," + gridSquare.getZ() + ")");
		}
	}
	
	public void addToOpenSquares(GridSquare square)
	{
		square.setOpen();
		openSquares.add(square);
	}
	
	public void addToClosedSquares(GridSquare square)
	{
		square.setClosed();
		closedSquares.add(square);
	}
	
	public ArrayList<GridSquare> executePathfinder()
	{
		GridSquare start = getGridSquare(getStartPosition());
		GridSquare goal = getGridSquare(getTargetPosition());
		addToOpenSquares(start);
		
		start.setStartCost(0);
		start.setTotalCost(start.getStartCost() + calculateDistance(new Vector2f(start.getX(),start.getZ()),new Vector2f(goal.getX(),goal.getZ())));
		while(!openSquares.isEmpty())
		{
			GridSquare current = openSquares.remove();
			if(current.equals(goal))
			{
				return buildPath(goal);
			}
			
			addToClosedSquares(current);
			Set<GridSquare> neighbours = getNeighbouringSquares(current);
			for(GridSquare neighbour: neighbours)
			{
				if(!neighbour.isClosed())
				{
					float tempCost = current.getStartCost() + calculateDistance(new Vector2f(current.getX(),current.getZ()),
							new Vector2f(neighbour.getX(),neighbour.getZ()));
					if(!neighbour.isOpen() || (tempCost < neighbour.getStartCost()))
					{
						neighbour.setParent(current);
						neighbour.setStartCost(tempCost);
						neighbour.setTotalCost(neighbour.getStartCost() + calculateDistance(new Vector2f(neighbour.getX(),neighbour.getZ()),new Vector2f(start.getX(),start.getZ())));
						if(!neighbour.isOpen())
						{
							addToOpenSquares(neighbour);
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public void resetPathfinder()
	{
		for(int i = 0; i < getSize(); i++)
		{
			for(int j = 0; j < getSize(); j++)
			{
				grid[i][j].setUnvisited();
				grid[i][j].setParent(null);
				grid[i][j].setTotalCost(0);
				grid[i][j].setStartCost(0);
				grid[i][j].setCostToTarget(0);
			}
		}
		
		openSquares.clear();
		closedSquares.clear();
	}
	
}

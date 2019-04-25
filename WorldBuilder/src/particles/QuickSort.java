package particles;

import java.util.List;

public class QuickSort {
	
	private List<Particle> particles;
	
	public QuickSort()
	{
		
	}
	
	
	public void sort(List<Particle> particles)
	{
		if(particles.isEmpty()) 
		{
			return;
		}
		this.particles = particles;
		quickSort(0, particles.size()-1);
		
	}
	
	private void quickSort(int lowerIndex, int higherIndex)
	{
		int i = lowerIndex;
		int j = higherIndex;
		
		Particle pivot = particles.get(lowerIndex +(higherIndex-lowerIndex)/2);
		
		while(i <= j)
		{
			while(particles.get(i).getDistance() < pivot.getDistance())
			{
				i++;
			}
			while(particles.get(j).getDistance() > pivot.getDistance())
			{
				j--;
			}
			if(i <= j)
			{
				swapNumbers(i,j);
				
				i++;
				j--;
			}
		}
		
		if(lowerIndex < j)
		{
			quickSort(lowerIndex, j);
		}
		
		if(i < higherIndex)
		{
			quickSort(i, higherIndex);
		}
	}
	
	private void swapNumbers(int i, int j)
	{
		Particle temp = particles.get(i);
		particles.set(i, particles.get(j));
		particles.set(j, temp);
	}

}

package particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import rendering.Loader;

public class ParticleManager {
	
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
	
	private static ParticleRenderer renderer;
	
	private static QuickSort quickSort;
	
	public static void init(Loader loader, Matrix4f projectionMatrix)
	{
		renderer = new ParticleRenderer(loader,projectionMatrix);
		quickSort = new QuickSort();
	}
	
	public static void update(Camera camera)
	{
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext())
		{
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			while(iterator.hasNext())
			{
				Particle p = iterator.next();
				boolean alive = p.update(camera);
				if(!alive)
				{
					iterator.remove();
					if(list.isEmpty())
					{
						mapIterator.remove();
					}
				}
			}
			
			quickSort.sort(list);
		}
		

	}
	
	public static void render(Camera camera)
	{
		renderer.render(particles, camera);
	}
	
	public static void cleanUp()
	{
		renderer.cleanUp();
	}
	
	public static void addParticle(Particle particle)
	{
		List<Particle> list = particles.get(particle.getTexture());
		if(list == null)
		{
			list = new ArrayList<>();
			particles.put(particle.getTexture(), list);
		}
		list.add(particle);
	}

}

package professions;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import rendering.Loader;

public class NodeBank {
	
	private static int offsetCount = 0;
	
	private static Vector3f[] positionOffsets = new Vector3f[Mineral.MAX_CAPACITY];
	private static Vector3f[] rotationOffsets = new Vector3f[Mineral.MAX_CAPACITY];
	
	private static Map<String,Mineral> minerals;
	
	private NodeBank() {}
	
	public static void init(Loader loader)
	{
		initMinerals(loader);
	}
	
	private static void initMinerals(Loader loader)
	{
		minerals = new HashMap<>();
		createOffset(-0.707f,2.651f,0.502f,-130.553f,-3.072f,98.778f);
		createOffset(-0.602f,3.192f,-2.016f,49.349f,30.24f,267.332f);
		createOffset(0.454f,4.198f,-0.401f,-53.006f,20.722f,125.643f);
		createOffset(1.208f,2.18f,0.218f,201.905f,4.746f,310.948f);
		createOffset(1.305f,3.192f,-1.852f,166.727f,14.45f,283.852f);
		createMineral(loader,"Copper","copper_ore.obj","copper_vein.png","copper_ore_icon.png");
	}
	
	private static void createOffset(float x, float y, float z,float rotX,float rotY,float rotZ)
	{
		positionOffsets[offsetCount] = new Vector3f(x,y,z);
		rotationOffsets[offsetCount] = new Vector3f(rotX,rotY,rotZ);
		offsetCount++;
		if(offsetCount >= Mineral.MAX_CAPACITY)
		{
			offsetCount = 0;
		}
	}
	
	private static void createMineral(Loader loader,String name,String modelFile,
			String textureFile,String iconFile)
	{
		Mineral mineral = new Mineral(loader,modelFile,textureFile,iconFile,
				positionOffsets,rotationOffsets);
		minerals.put(name, mineral);
	}
	
	public static Mineral getMineralByName(String name)
	{
		return minerals.get(name);
	}

}

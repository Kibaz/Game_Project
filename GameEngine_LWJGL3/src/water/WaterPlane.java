package water;

import entities.Camera;
import entities.Entity;
import rendering.Loader;
import texturing.CausticTexture;

public class WaterPlane {
	
	private final int NUMBER_CAUSTICS = 32;
	
	private final int HEIGHT_FIELD_SIZE = 25;
	
	public static final float SIZE = 100;
	
	private float[][] heightmap;
	
	private int heightmapTexture;
	
	private float[] vertices;
	private float[] textureCoords;
	
	private float height;
	private float x,z;
	
	private int vaoID;
	
	private CausticTexture[] causticTextures;
	
	public WaterPlane(Loader loader, float centerX, float centerZ, float height)
	{
		this.x = centerX;
		this.z = centerZ;
		this.height = height;
		this.causticTextures = new CausticTexture[NUMBER_CAUSTICS];
		this.heightmap = new float[HEIGHT_FIELD_SIZE][HEIGHT_FIELD_SIZE];
		constructHeightmap();
		setupVertices();
		setupTextureCoords();
		loadToVAO(loader);
	}
	
	private void constructHeightmap()
	{
		for(int i = 0; i < HEIGHT_FIELD_SIZE; i++)
		{
			for(int j = 0; j < HEIGHT_FIELD_SIZE; j++)
			{
				heightmap[j][i] = 0;
			}
		}
	}
	
	private void setupVertices()
	{
		vertices = new float[HEIGHT_FIELD_SIZE * HEIGHT_FIELD_SIZE * 3];
		int counter = 0;
		for(int i = 0; i < HEIGHT_FIELD_SIZE; i++)
		{
			for(int j = 0; j < HEIGHT_FIELD_SIZE; j++)
			{
				vertices[counter * 3] = (float)j / ((float)HEIGHT_FIELD_SIZE - 1);
				vertices[counter * 3 + 1] = 0; 
				vertices[counter * 3 + 2] = (float)i / ((float)HEIGHT_FIELD_SIZE - 1);
				counter++;
			}
		}
	}
	
	private void setupTextureCoords()
	{
		textureCoords = new float[HEIGHT_FIELD_SIZE * HEIGHT_FIELD_SIZE * 2];
		int counter = 0;
		for(int i = 0; i < HEIGHT_FIELD_SIZE; i++)
		{
			for(int j = 0; j < HEIGHT_FIELD_SIZE; j++)
			{
				textureCoords[counter * 2] = (float)j / ((float)HEIGHT_FIELD_SIZE - 1);
				textureCoords[counter * 2 + 1] = (float)i / ((float)HEIGHT_FIELD_SIZE - 1);
				counter++;
			}
		}
	}
	
	private void updateHeightmap(Entity player)
	{

	}
	
	public void update(Entity player)
	{
		if(!isPlayerInWater(player))
		{
			return;
		}
		updateHeightmap(player);
	}
	
	public boolean isCameraUnderWater(Camera camera)
	{
		float waterStartZ = z - SIZE;
		float waterEndZ = z + SIZE;
		float waterStartX = x - SIZE;
		float waterEndX = x + SIZE;
		
		if(camera.getPosition().x >= waterStartX && 
			camera.getPosition().x <= waterEndX &&
			camera.getPosition().z >= waterStartX &&
			camera.getPosition().z <= waterEndX &&
				camera.getPosition().y < height)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isPlayerInWater(Entity player)
	{
		float waterStartZ = z - SIZE;
		float waterEndZ = z + SIZE;
		float waterStartX = x - SIZE;
		float waterEndX = x + SIZE;
		if(player.getPosition().x >= waterStartX &&
			player.getPosition().z >= waterStartZ &&
			player.getPosition().x <= waterEndX &&
			player.getPosition().z <= waterEndZ &&
			player.getPosition().y <= height)
		{
			return true;
		}
		
		return false;
	}
	
	private void loadToVAO(Loader loader)
	{
		vaoID = loader.loadToVAO(vertices, textureCoords);
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}
	
	public float getZ() {
		return z;
	}

	public CausticTexture[] getCausticTextures() {
		return causticTextures;
	}

	public void setCausticTextures(int index, CausticTexture texture) {
		this.causticTextures[index] = texture;
	}
	
	public int getHeightMapTexture()
	{
		return heightmapTexture;
	}
	
	public float[] getVertices()
	{
		return vertices;
	}

	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertCount()
	{
		return vertices.length / 2;
	}
	
	
	

}

package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import models.BaseModel;
import rendering.Loader;
import texturing.CausticTexture;
import texturing.ModelTexture;
import utils.Maths;

public class Terrain {
	
	private static final float SIZE =800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
	
	private float x; // World position: X
	private float z; // World position: Z
	
	private float[][] heights;
	
	private BaseModel baseModel; // Terrain model
	private ModelTexture texture; // Terrain texture
	private CausticTexture causticTexture; // Current caustic texture to be rendered
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, String heightMap)
	{
		this.texture = texture;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.baseModel = generateTerrain(loader, heightMap);
	}
	
	public CausticTexture getCausticTexture() {
		return causticTexture;
	}

	public void setCausticTexture(CausticTexture causticTexture) {
		this.causticTexture = causticTexture;
	}


	private BaseModel generateTerrain(Loader loader, String heightMap)
	{
		
		// Load Height Map
		BufferedImage image = null;
		try 
		{
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		int VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1)*(VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i<VERTEX_COUNT; i++) {
			for (int j = 0; j<VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float)j / ((float)VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float)i / ((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calcNormal(j, i, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float)j / ((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float)i / ((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz<VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx<VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz*VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1)*VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public BaseModel getBaseModel() {
		return baseModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	private Vector3f calcNormal(int x, int z, BufferedImage image)
	{
		float heightL = getHeight(x-1, z, image);
		float heightR = getHeight(x+1, z, image);
		float heightD = getHeight(x, z-1, image);
		float heightU = getHeight(x, z+1, image);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}
	
	private float getHeight(int x, int z, BufferedImage image)
	{
		if(x < 0 || x >= image.getHeight() || z < 0 || z>= image.getHeight())
		{
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOUR/2f;
		height /= MAX_PIXEL_COLOUR/2f;
		height *= MAX_HEIGHT;
		return height;
	}
	
	public float getTerrainHeight(float worldX, float worldZ)
	{
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / (float) (heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if(gridX >= heights.length-1 || gridZ >= heights.length -1 || gridX < 0 || gridZ < 0)
		{
			return 0;
		}
		
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float result;
		if(xCoord <= (1-zCoord))
		{
			result = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), 
					new Vector3f(1, heights[gridX+1][gridZ], 0),
					new Vector3f(0, heights[gridX][gridZ+1], 1), 
					new Vector2f(xCoord, zCoord));
		}
		else 
		{
			result = Maths.barryCentric(new Vector3f(1, heights[gridX+1][gridZ], 0), 
					new Vector3f(1, heights[gridX+1][gridZ+1], 1),
					new Vector3f(0, heights[gridX][gridZ+1], 1), 
					new Vector2f(xCoord, zCoord));
		}
		return result;
	}
	
	public boolean isPlayerOnTerrain(Player player)
	{
		if(player.getPosition().x >= this.getX() && 
				player.getPosition().z >= this.getZ() && 
				player.getPosition().x <= this.x + SIZE &&
				player.getPosition().z <= this.z + SIZE)
		{
			return true;
		}
		
		return false;
	}

}

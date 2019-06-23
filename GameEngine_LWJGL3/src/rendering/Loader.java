package rendering;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import models.BaseModel;
import texturing.TextureData;

public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();

	public Loader()
	{
		
	}
	
	public int loadTexture(String file)
	{	
		ByteBuffer image;
		int width, height;
		
		int tID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tID);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic)
		{
			float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		}
		else
		{
			System.out.println("Anisotropic Filtering Not Supported!");
		}
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);
			
			STBImage.stbi_set_flip_vertically_on_load(true);
			image = STBImage.stbi_load(file, w, h, comp, 4);
			if(image == null)
			{
				throw new RuntimeException("Failed to load a texture file!"
						+ System.lineSeparator() + STBImage.stbi_failure_reason());
			}
			
			width = w.get();
			height = h.get();
			
		}
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return tID;
	}
	
	public int loadFontTexture(String file)
	{	
		ByteBuffer image;
		int width, height;
		
		int tID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tID);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);
			
			STBImage.stbi_set_flip_vertically_on_load(false);
			image = STBImage.stbi_load(file, w, h, comp, 4);
			if(image == null)
			{
				throw new RuntimeException("Failed to load a texture file!"
						+ System.lineSeparator() + STBImage.stbi_failure_reason());
			}
			
			width = w.get();
			height = h.get();
			
		}
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return tID;
	}
	
	public int decodePNGTexture(String filename, float blend)
	{
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		
		try {
			FileInputStream in = new FileInputStream(filename);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, blend);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return texture;
		
		
	}
	
	private TextureData loadTextureFile(String filepath)
	{
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);
			
			STBImage.stbi_set_flip_vertically_on_load(false);
			buffer = STBImage.stbi_load("res/" + filepath, w, h, comp, 4);
			if(buffer == null)
			{
				throw new RuntimeException("Failed to load a texture file!"
						+ System.lineSeparator() + STBImage.stbi_failure_reason());
			}
			
			width = w.get();
			height = h.get();
			
		}
		
		return new TextureData(buffer, width, height);
	}
	
	// Load up a cube map in OpenGL
	public int loadCubeMap(String[] texturePaths)
	{
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		for(int i = 0; i < texturePaths.length; i++)
		{
			TextureData data = loadTextureFile(texturePaths[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, 
					data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, 
					GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(texID);
		return texID;
	}
	
	
	public BaseModel loadToVAO(float[] positions, float[] texCoords, float[] normals, int[] indices)
	{
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,texCoords);
		storeDataInAttributeList(2,3,normals);
		unbindVAO();
		return new BaseModel(vaoID, indices.length, positions, indices);
	}
	
	/*
	 * Overidden LoadToVAO method for GUIs 
	 * Only requires position coordinates
	*/
	
	public BaseModel loadToVAO(float[] positions, int dimensions)
	{
		int vaoID = createVAO();
		storeDataInAttributeList(0,dimensions,positions);
		unbindVAO();
		BaseModel model = new BaseModel(vaoID, positions.length/dimensions);
		model.setVertices(positions);
		return model;
	}
	
	/*
	 * Overidden LoadToVAO method for Font/Text
	 * Must consist of position coords and texture coords
	 * VAO ID to be returned as an Integer - no need for BaseModel type
	*/
	
	public int loadToVAO(float[] positions, float[] texCoords)
	{
		int vaoID = createVAO();
		storeDataInAttributeList(0,2,positions);
		storeDataInAttributeList(1,2,texCoords);
		unbindVAO();
		return vaoID;
	}
	
	
	/*
	 * Overidden LoadToVAO method for Animated Models
	 * Must contain Bone Indexes and Skin Weights data
	 * Extra data should be loaded to a VAO
	*/
	public BaseModel loadToVAO(float[] positions, float[] texCoords, float[] normals, int[] indices, int[] boneIds, float[] weights)
	{
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,texCoords);
		storeDataInAttributeList(2,3,normals);
		storeDataInAttributeList(3,4,boneIds);
		storeDataInAttributeList(4,4,weights);
		unbindVAO();
		return new BaseModel(vaoID, indices.length, positions, indices);
	}
	
	public void cleanUp()
	{
		for(int vao: vaos)
		{
			GL30.glDeleteVertexArrays(vao);
		}
		
		for(int vbo: vbos)
		{
			GL15.glDeleteBuffers(vbo);
		}
		
		for(int texture:textures)
		{
			GL11.glDeleteTextures(texture);
		}
	}
	
	public int createVAO()
	{
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public int createEmptyVBO(int numFloats)
	{
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, numFloats * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}
	
	public void addInstancedAttrbute(int vao, int vbo, int attrib, int dataSize, int dataLength, int offset)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attrib, dataSize, GL11.GL_FLOAT, false, dataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attrib, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer)
	{
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void storeDataInAttributeList(int attribNum, int coordSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribNum, coordSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void storeDataInAttributeList(int attribNum, int coordSize, int[] data)
	{
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attribNum, coordSize, GL11.GL_INT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVAO()
	{
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer(int[] indices)
	{
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	
	
}

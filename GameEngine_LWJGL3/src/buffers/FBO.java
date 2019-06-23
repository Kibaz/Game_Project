package buffers;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import rendering.Window;

public class FBO {
	
	public static final int NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_RENDER_BUFFER = 2;
	
	private int width, height;
	
	private int frameBuffer;
	
	private int depthTexture;
	private int colourTexture;
	
	private int depthBuffer;
	
	private List<Integer> colourBuffers = new ArrayList<>();
	
	private boolean multisampleAndRenderTargets = false;
	
	public FBO(int width, int height, int type)
	{
		this.width = width;
		this.height = height;
		initBuffer(type);
	}
	
	public FBO(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.multisampleAndRenderTargets = true;
		initBuffer(DEPTH_RENDER_BUFFER);
	}
	
	public void resolveToFBO(int attachment,FBO output)
	{
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,output.frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glReadBuffer(attachment);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, output.width, output.height, GL11.GL_COLOR_BUFFER_BIT | 
				GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbindFrameBuffer();
	}
	
	public void resolveToScreen()
	{
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Window.getWidth(), Window.getHeight(), GL11.GL_COLOR_BUFFER_BIT | 
				GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbindFrameBuffer();
	}
	
	private void initBuffer(int type)
	{
		createFrameBuffer();
		if(multisampleAndRenderTargets)
		{
			colourBuffers.add(createMultisampleColourAttachment(GL30.GL_COLOR_ATTACHMENT0));
			colourBuffers.add(createMultisampleColourAttachment(GL30.GL_COLOR_ATTACHMENT1));
		}
		else
		{
			createColourAttachment();
		}
		
		if(type == DEPTH_RENDER_BUFFER)
		{
			createDepthBufferAttachment();
		}
		else if(type == DEPTH_TEXTURE)
		{
			createDepthAttachment();
		}
		
		unbindFrameBuffer();
	}
	
	public void bindFrameBuffer()
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}
	
	public void unbindFrameBuffer()
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Window.getWidth(), Window.getHeight());
	}
	
	private void createFrameBuffer()
	{
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		createDrawBuffers();
	}
	
	private void createDrawBuffers()
	{
		IntBuffer drawBuffers = BufferUtils.createIntBuffer(2);
		drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
		if(this.multisampleAndRenderTargets)
		{
			drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1);
		}
		drawBuffers.flip();
		GL20.glDrawBuffers(drawBuffers);
	}
	
	private void createColourAttachment()
	{
		colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture, 0);
	}
	
	private void createDepthAttachment()
	{
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}
	
	private void createDepthBufferAttachment()
	{
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		// Changed from GL_DEPTH_COMPONENT to GL_DEPTH24_STENCIL8 to allow for stencil buffer
		if(!multisampleAndRenderTargets)
		{
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
		}else
		{
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL30.GL_DEPTH24_STENCIL8, width, height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
	}
	
	private int createMultisampleColourAttachment()
	{
		int colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
		GL40.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colourBuffer);
		return colourBuffer;
	}
	
	// For rendering to multiple targets
	private int createMultisampleColourAttachment(int attachment)
	{
		int colourBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
		GL40.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, colourBuffer);
		return colourBuffer;
	}
	
	public void cleanUp()
	{
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colourTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
		for(Integer buffer: colourBuffers)
		{
			GL30.glDeleteRenderbuffers(buffer);
		}
	}

	public int getDepthTexture() {
		return depthTexture;
	}

	public int getColourTexture() {
		return colourTexture;
	}

	public int getDepthBuffer() {
		return depthBuffer;
	}
	
	
	

}

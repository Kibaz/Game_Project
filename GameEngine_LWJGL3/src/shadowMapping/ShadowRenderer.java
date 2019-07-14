package shadowMapping;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;

public class ShadowRenderer {
	
	public static final int MAP_SIZE = 2048;
	
	private ShadowFBO fbo;
	private ShadowShader shader;
	private BoundingBox shadowBox;
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f lightViewMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();
	private Matrix4f offset = createOffset();
	
	private ShadowEntityRenderer entityRenderer;
	
	public ShadowRenderer(Camera camera)
	{
		shader = new ShadowShader();
		shadowBox = new BoundingBox(lightViewMatrix, camera);
		fbo = new ShadowFBO(MAP_SIZE, MAP_SIZE);
		entityRenderer = new ShadowEntityRenderer(shader, projectionViewMatrix);
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities,Light sun)
	{
		shadowBox.update();
		Vector3f sunPosition = sun.getPosition();
		Vector3f lightDir = new Vector3f(-sunPosition.x, -sunPosition.y, -sunPosition.z);
		prepare(lightDir, shadowBox);
		entityRenderer.render(entities);
		finish();
	}
	
	public Matrix4f getToShadowMapSpaceMatrix()
	{
		return Matrix4f.mul(offset, projectionViewMatrix, null);
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
		fbo.cleanUp();
	}
	
	public int getShadowMap()
	{
		return fbo.getShadowMap();
	}
	
	private void prepare(Vector3f lightDir, BoundingBox box)
	{
		updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
		updateLightViewMatrix(lightDir, box.getCenter());
		Matrix4f.mul(projectionMatrix, lightViewMatrix, projectionViewMatrix);
		fbo.bindFrameBuffer();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		shader.start();
	}
	
	private void finish()
	{
		shader.stop();
		fbo.unbindFrameBuffer();
	}
	
    private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
        direction.normalise();
        center.negate();
        lightViewMatrix.setIdentity();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        Matrix4f.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0), lightViewMatrix,
                lightViewMatrix);
        Matrix4f.translate(center, lightViewMatrix, lightViewMatrix);
    }
    
    private void updateOrthoProjectionMatrix(float width, float height, float length) {
        projectionMatrix.setIdentity();
        projectionMatrix.m00 = 2f / width;
        projectionMatrix.m11 = 2f / height;
        projectionMatrix.m22 = -2f / length;
        projectionMatrix.m33 = 1;
    }
    
    private static Matrix4f createOffset() {
        Matrix4f offset = new Matrix4f();
        offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
        offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
        return offset;
    }
	

}

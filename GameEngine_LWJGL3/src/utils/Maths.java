package utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animation.JointTransform;
import entities.Camera;

public class Maths {
	
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos)
	{
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z); 
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;  
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translate, float rx, float ry, float rz, float scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translate, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y,1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f inverseCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(inverseCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f quatToMatrix4f(Quaternion q)
	{
		Matrix4f mat = new Matrix4f();
		float xy = q.x * q.y;
		float xz = q.x * q.z;
		float xw = q.x * q.w;
		float yz = q.y * q.z;
		float yw = q.y * q.w;
		float zw = q.z * q.w;
		
		float xSqrd = q.x * q.x;
		float ySqrd = q.y * q.y;
		float zSqrd = q.z * q.z;
		
		mat.m00 = 1 - 2 * (ySqrd + zSqrd);
		mat.m01 = 2 * (xy - zw);
		mat.m02 = 2 * (xz + yw);
		mat.m03 = 0;
		mat.m10 = 2 * (xy + zw);
		mat.m11 = 1 - 2 * (xSqrd + zSqrd);
		mat.m12 = 2 * (yz - xw);
		mat.m13 = 0;
		mat.m20 = 2 * (xz - yw);
		mat.m21 = 2 * (yz + xw);
		mat.m22 = 1 - 2 * (xSqrd + ySqrd);
		mat.m23 = 0;
		mat.m30 = 0;
		mat.m31 = 0;
		mat.m32 = 0;
		mat.m33 = 1;
		
		return mat;
	}
	
	public static Quaternion fromMatrix4f(Matrix4f matrix)
	{
		float x,y,z,w;
		float diagonal = matrix.m00 + matrix.m11 + matrix.m22;
		if(diagonal > 0)
		{
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (matrix.m21 - matrix.m12) / w4;
			y = (matrix.m02 - matrix.m20) / w4;
			z = (matrix.m10 - matrix.m01) / w4;
		}
		else if((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22))
		{
			float x4 = (float) (Math.sqrt(1f + matrix.m00 - matrix.m11 - matrix.m22) * 2f);
			w = (matrix.m21 - matrix.m12) / x4;
			x = x4 / 4f;
			y = (matrix.m01 + matrix.m10) / x4;
			z = (matrix.m02 + matrix.m20) / x4;
		}
		else if(matrix.m11 > matrix.m22)
		{
			float y4 = (float) (Math.sqrt(1f + matrix.m11 - matrix.m00 - matrix.m22) * 2f);
			w = (matrix.m02 - matrix.m20) / y4;
			x = (matrix.m01 + matrix.m10) / y4;
			y = y4 / 4f;
			z = (matrix.m12 + matrix.m21) / y4;
		}
		else
		{
			float z4 = (float) (Math.sqrt(1f + matrix.m22 - matrix.m00 - matrix.m11) * 2f);
			w = (matrix.m10 - matrix.m01) / z4;
			x = (matrix.m02 + matrix.m20) / z4;
			y = (matrix.m12 + matrix.m21) / z4;
			z = z4 / 4f;
		}
		
		return new Quaternion(x,y,z,w);
	}
	
	public static Quaternion interpolate(Quaternion a, Quaternion b, float blend)
	{
		Quaternion result = new Quaternion(0,0,0,1);
		float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
		float blendI = 1f - blend;
		
		if(dot < 0)
		{
			result.w = blendI * a.w + blend * -b.w;
			result.x = blendI * a.x + blend * -b.x;
			result.y = blendI * a.y + blend * -b.y;
			result.z = blendI * a.z + blend * -b.z;
		}
		else
		{
			result.w = blendI * a.w + blend * b.w;
			result.x = blendI * a.x + blend * b.x;
			result.y = blendI * a.y + blend * b.y;
			result.z = blendI * a.z + blend * b.z;
		}
		result.normalise();
		return result;
	
	}
	
	public static Matrix4f interpolate(JointTransform a, JointTransform b, float progression)
	{
		Matrix4f result = new Matrix4f();
		
		Vector3f position = interpolate(a.getPosition(),b.getPosition(),progression);
		Quaternion rotation = interpolate(a.getRotation(),b.getRotation(),progression);
		
		result.translate(position);
		Matrix4f rotMatrix = quatToMatrix4f(rotation);
		Matrix4f.mul(result, rotMatrix, result);
		
		return result;
		
	}
	
	private static Vector3f interpolate(Vector3f start, Vector3f end, float progression)
	{
		float x = start.x + (end.x - start.x) * progression;
		float y = start.y + (end.y - start.y) * progression;
		float z = start.z + (end.z - start.z) * progression;
		
		return new Vector3f(x,y,z);
	}
	
	public static Vector3f getTranslationFromMat4(Matrix4f matrix)
	{
		Vector3f result = new Vector3f();
		
		// Extract last column of the 4x4 Matrix
		result.x = matrix.m30;
		result.y = matrix.m31;
		result.z = matrix.m32;
		
		return result;
	}
	
	private static float findLengthOfVector3f(Vector3f vector)
	{
		float result = 0;
		float xSqrd = vector.x * vector.x;
		float ySqrd = vector.y * vector.y;
		float zSqrd = vector.z * vector.z;
		
		float total = xSqrd + ySqrd + zSqrd;
		
		result = (float) Math.sqrt(total);
		
		return result;
	}
}

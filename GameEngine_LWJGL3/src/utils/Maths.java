package utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import entities.Camera;
import rendering.Window;

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
	
	/*
	 * Use: for displaying 2D GUIs & Text in 3D Space
	 */
	public static Matrix4f createModelViewMatrix(Vector3f position, float rotation, Vector3f scale, Matrix4f viewMatrix)
	{
		Matrix4f transformationMatrix = new Matrix4f();
		Matrix4f.translate(position, transformationMatrix, transformationMatrix);
		transformationMatrix.m00 = viewMatrix.m00;
		transformationMatrix.m01 = viewMatrix.m10;
		transformationMatrix.m02 = viewMatrix.m20;
		transformationMatrix.m10 = viewMatrix.m01;
		transformationMatrix.m11 = viewMatrix.m11;
		transformationMatrix.m12 = viewMatrix.m21;
		transformationMatrix.m20 = viewMatrix.m02;
		transformationMatrix.m21 = viewMatrix.m12;
		transformationMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,0,1), transformationMatrix, transformationMatrix);
		Matrix4f.scale(scale, transformationMatrix, transformationMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, transformationMatrix, null);
		return modelViewMatrix;
	}
	
	public static Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY)
	{
		float x = (2f*mouseX) / Window.getWidth() - 1f;
		float y = (2f*mouseY) / Window.getHeight() - 1f;
		return new Vector2f(x,-y);
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
	
	public static Vector3f covertCoordinates(Vector3f position, Camera camera, Matrix4f pm)
	{
		
		Vector4f coordinates = new Vector4f(position.x, position.y, position.z,1f);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		Matrix4f.transform(viewMatrix, coordinates, coordinates);
		Matrix4f.transform(pm, coordinates, coordinates);
		if(coordinates.w < 0)
		{
			return null;
		}
		
		Vector3f screenCoordinates = new Vector3f(((coordinates.x/coordinates.w)+1)/2f, 
				1 - (((coordinates.y / coordinates.w) + 1)/2f), coordinates.z);
		return screenCoordinates;
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
	
	public static Vector3f covertWorldToScreenSpace(Vector3f worldPos, Matrix4f viewMatrix, Matrix4f projectionMatrix)
	{
		Vector4f coordinates = new Vector4f(worldPos.x,worldPos.y,worldPos.z,1f);
		Matrix4f.transform(viewMatrix, coordinates, coordinates);
		Matrix4f.transform(projectionMatrix, coordinates, coordinates);
		if(coordinates.w < 0)
		{
			return null;
		}
		Vector3f screenCoordinates = new Vector3f(((coordinates.x/coordinates.w)+1)/2f,
				1-(((coordinates.y / coordinates.w)+1)/2f), coordinates.z);
		return screenCoordinates;
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
	
	public static Quaternion slerp(Quaternion start, Quaternion end, float factor)
	{
		Quaternion result = new Quaternion(0,0,0,0);
		
		float d = start.x * end.x + start.y * end.y + start.z * end.z + start.w * end.w;
		float absDot = d < 0.f ? -d : d;
		
		float scale0 = 1f - factor;
		float scale1 = factor;
		
		if((1 - absDot) > 0.1)
		{
			float angle = (float) Math.acos(absDot);
			float invSinTheta = 1f / (float) Math.sin(angle);
			
			scale0 = ((float) Math.sin((1f - factor) * angle) * invSinTheta);
			scale1 = ((float) Math.sin((factor * angle)) * invSinTheta);
		}
		
		if(d < 0.f) scale1 = -scale1;
		
		result.x = (scale0 * start.x) + (scale1 * end.x);
		result.y = (scale0 * start.y) + (scale1 * end.y);
		result.z = (scale0 * start.z) + (scale1 * end.z);
		result.w = (scale0 * start.w) + (scale1 * end.w);
		
		Quaternion.normalise(result, result);
		return result;
	}
	
	public static Vector3f rotate3DVector(Vector3f vector, Vector3f rotation)
	{
		Matrix4f matrix = new Matrix4f();
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), matrix, matrix);
		Vector4f tempVector = new Vector4f(vector.x, vector.y, vector.z, 1.0f);
		Matrix4f.transform(matrix, tempVector, tempVector);
		return new Vector3f(tempVector.x,tempVector.y,tempVector.z);
	}
	
	public static float squaredEuclideanDistance(Vector2f first, Vector2f second)
	{
		float distX = first.x - second.x;
		float distY = first.y - second.y;
		
		return (distX * distX) + (distY * distY);
	}
	
	public static float distance(Vector2f start, Vector2f finish)
	{
		return Vector2f.sub(start, finish, null).length();
	}
	
	public static float distance(Vector3f start, Vector3f finish)
	{
		return Vector3f.sub(start, finish, null).length();
	}
	
	public static void truncate(Vector2f vector, float scaleFactor)
	{
		if(vector.length() > scaleFactor)
		{
			vector.normalise();
			
			vector = new Vector2f(vector.x * scaleFactor, vector.y * scaleFactor);
		}

	}
	
	public static Vector2f mulScalar(Vector2f vector, float scalar)
	{
		return new Vector2f(vector.x * scalar,vector.y * scalar);
	}
	
	public static Vector3f mulScalar(Vector3f vector, float scalar)
	{
		return new Vector3f(vector.x * scalar,vector.y * scalar,vector.z * scalar);
	}
	
	public static float findVectorAngle(Vector2f vector)
	{
		
		return (float) (Math.toDegrees(Math.atan2(vector.x, vector.y)));
	}
	
	/*
	 * Ray cast calculations
	 */
	
	
}

package utils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.util.vector.Matrix4f;

public class Utils {
	
	public static float[] floatListToArray(List<Float> floats)
	{
		float[] array = new float[floats.size()];
		for(int i = 0; i < floats.size(); i++)
		{
			array[i] = floats.get(i);
		}
		
		return array;
	}
	
	public static float[] combineArrays(float[] first, float[] second)
	{
		float[] result = new float[first.length + second.length];
		
		int count = 0;
		for(int i = 0; i < result.length; i++)
		{
			if(i < first.length)
			{
				result[i] = first[i];
			}
			else
			{
				result[i] = second[count];
				count++;
			}
		}
		
		return result;
	}
	
	public static int[] combineArrays(int[] first, int[] second)
	{
		int[] result = new int[first.length + second.length];
		
		int count = 0;
		for(int i = 0; i < result.length; i++)
		{
			if(i < first.length)
			{
				result[i] = first[i];
			}
			else
			{
				result[i] = second[count];
				count++;
			}
		}
		
		return result;
	}

	
	public static int[] intListToArray(List<Integer> ints)
	{
		int[] array = ints.stream().mapToInt((Integer i)->i).toArray();
		return array;
	}
	
	public static IntBuffer createIntBuffer(int size)
	{
		return BufferUtils.createIntBuffer(size);
	}
	
	public static Matrix4f convertAssimpToLWJGLMat4(AIMatrix4x4 mat4)
	{
		Matrix4f result = new Matrix4f();
		result.m00 = mat4.a1();
		result.m10 = mat4.a2();
		result.m20 = mat4.a3();
		result.m30 = mat4.a4();
		
		result.m01 = mat4.b1();
		result.m11 = mat4.b2();
		result.m21 = mat4.b3();
		result.m31 = mat4.b4();
		
		result.m02 = mat4.c1();
		result.m12 = mat4.c2();
		result.m22 = mat4.c3();
		result.m32 = mat4.c4();
		
		result.m03 = mat4.d1();
		result.m13 = mat4.d2();
		result.m23 = mat4.d3();
		result.m33 = mat4.d4();
		
		return result;
	}
	
	public static void printMatrix4f(Matrix4f mat)
	{
		String str = "";
		
		str += "M00: " + mat.m00 + ", ";
		str += "M01: " + mat.m01 + ", ";
		str += "M02: " + mat.m02 + ", ";
		str += "M03: " + mat.m03 + "\n";
		
		str += "M10: " + mat.m10 + ", ";
		str += "M11: " + mat.m11 + ", ";
		str += "M12: " + mat.m12 + ", ";
		str += "M13: " + mat.m13 + "\n";	
		
		str += "M20: " + mat.m20 + ", ";
		str += "M21: " + mat.m21 + ", ";
		str += "M22: " + mat.m22 + ", ";
		str += "M23: " + mat.m23 + "\n";	
		
		str += "M30: " + mat.m30 + ", ";
		str += "M31: " + mat.m31 + ", ";
		str += "M32: " + mat.m32 + ", ";
		str += "M33: " + mat.m33 + "\n";	
		
		System.out.println(str);
	}
	
	public static boolean isMatrixEqual(Matrix4f first, Matrix4f second)
	{
		boolean equal = false;
		
		if(first.m00 == second.m00 &&
			first.m01 == second.m01 &&
			first.m02 == second.m02 &&
			first.m03 == second.m03 &&
			first.m10 == second.m10 &&
			first.m11 == second.m11 &&
			first.m12 == second.m12 &&
			first.m13 == second.m13 &&
			first.m20 == second.m20 &&
			first.m21 == second.m21 &&
			first.m22 == second.m22 &&
			first.m23 == second.m23 &&
			first.m30 == second.m30 &&
			first.m31 == second.m31 &&
			first.m32 == second.m32 &&
			first.m33 == second.m33)
		{
			equal = true;
		}
		
		return equal;
	}
	
	public static byte[] stringToBytes(String str)
	{
		return str.getBytes();
	}
}

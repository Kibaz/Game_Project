package utils;

import java.nio.ByteBuffer;

public class DataTransfer {
	
	public static byte[] floatToBytes(float value)
	{
		byte[] data = new byte[4];
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.putFloat(value);
		return buffer.array();
	}
	
	public static byte[] integerToBytes(int value)
	{
		byte[] data = new byte[4];
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.putInt(value);
		return buffer.array();
	}
	
	public static float byteArrayToFloat(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		return buffer.getFloat();
	}
	
	public static int byteArrayToInteger(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		return buffer.getInt();
	}

}

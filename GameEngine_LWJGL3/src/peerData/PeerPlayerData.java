package peerData;

import org.lwjgl.util.vector.Vector3f;

import utils.DataTransfer;

public class PeerPlayerData {
	
	private int clientID; // store id of client as reference for the data
	
	private int modelID; // corresponds to the "VAO ID" of the model for the player
	
	private Vector3f position;
	
	private float rotX, rotY, rotZ;
	
	public PeerPlayerData(byte[] data)
	{
		getDataFromBytes(data);
	}
	
	
	
	public int getClientID() {
		return clientID;
	}



	public int getModelID() {
		return modelID;
	}



	public Vector3f getPosition() {
		return position;
	}



	public float getRotX() {
		return rotX;
	}



	public float getRotY() {
		return rotY;
	}



	public float getRotZ() {
		return rotZ;
	}



	private void getDataFromBytes(byte[] data)
	{
		byte[] x = new byte[4]; // store float data
		byte[] y = new byte[4]; // store float data
		byte[] z = new byte[4]; // store float data
		byte[] rx = new byte[4]; // store float data
		byte[] ry = new byte[4]; // store float data
		byte[] rz = new byte[4]; // store float data
		//byte[] modelData = new byte[4]; // store integer data
		byte[] clientData = new byte[4]; // store integer data
		/*
		 * "PlayerData" header consists of 10 bytes, thus loop begins at
		 * index of 9 to exclude the header of the packet!
		 */
		// counter retaining current index of important data
		int counter = 0;
		for(int i = 10; i < data.length; i++) {
			if(counter < 4)
			{
				x[counter%4] = data[i];
			}
			else if(counter < 8)
			{
				y[counter%4] = data[i];
			}
			else if(counter < 12)
			{
				z[counter%4] = data[i];
			}
			else if(counter < 16)
			{
				rx[counter%4] = data[i];
			}
			else if(counter < 20)
			{
				ry[counter%4] = data[i];
			}
			else if(counter < 24)
			{
				rz[counter%4] = data[i];
			}
			else if(counter < 28)
			{
				clientData[counter%4] = data[i];
			}
			counter++;
		}
		
		float posX = DataTransfer.byteArrayToFloat(x);
		float posY = DataTransfer.byteArrayToFloat(y);
		float posZ = DataTransfer.byteArrayToFloat(z);
		position = new Vector3f(posX,posY,posZ);
		rotX = DataTransfer.byteArrayToFloat(rx);
		rotY = DataTransfer.byteArrayToFloat(ry);
		rotZ = DataTransfer.byteArrayToFloat(rz);
		//modelID = DataTransfer.byteArrayToInteger(modelData);
		clientID = DataTransfer.byteArrayToInteger(clientData);
	}

}

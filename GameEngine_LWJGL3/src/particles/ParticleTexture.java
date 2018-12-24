package particles;

public class ParticleTexture {
	
	private int id;
	private int numRows;
	
	
	private boolean additive;
	
	
	public ParticleTexture(int id, int numRows, boolean additive) {
		this.id = id;
		this.numRows = numRows;
		this.additive = additive;
	}
	
	
	public int getID() {
		return id;
	}
	public int getNumRows() {
		return numRows;
	}


	public boolean isAdditive() {
		return additive;
	}


	public void setAdditive(boolean additive) {
		this.additive = additive;
	}
	
	
	
	

}

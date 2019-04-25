package fontUtils;

public class Character {
	
	private int id;
	private double xTexCoord;
	private double yTexCoord;
	private double xMaxTexCoord;
	private double yMaxTexCoord;
	private double xOffset;
	private double yOffset;
	private double sizeX;
	private double sizeY;
	private double xAdvance;
	
	public Character(int id, double xTexCoord, double yTexCoord, double xTexSize, double yTexSize,
			double xOffset, double yOffset, double sizeX, double sizeY, double xAdvance) {
		super();
		this.id = id;
		this.xTexCoord = xTexCoord;
		this.yTexCoord = yTexCoord;
		this.xMaxTexCoord = xTexSize + xTexCoord;
		this.yMaxTexCoord = yTexSize + yTexCoord;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.xAdvance = xAdvance;
	}

	public int getId() {
		return id;
	}

	public double getxTexCoord() {
		return xTexCoord;
	}

	public double getyTexCoord() {
		return yTexCoord;
	}

	public double getxMaxTexCoord() {
		return xMaxTexCoord;
	}

	public double getyMaxTexCoord() {
		return yMaxTexCoord;
	}

	public double getxOffset() {
		return xOffset;
	}

	public double getyOffset() {
		return yOffset;
	}

	public double getSizeX() {
		return sizeX;
	}

	public double getSizeY() {
		return sizeY;
	}

	public double getxAdvance() {
		return xAdvance;
	}
	
	
	
	

}

package guis;

public class GUI{
	
	private GUITexture texture;
	
	private boolean clickable;
	private boolean hovered;
	private boolean visible;
	
	public GUI(GUITexture texture)
	{
		this.texture = texture;
		this.visible = true;
	}

	public GUITexture getGUITexture() {
		return texture;
	}

	public boolean isClickable() {
		return clickable;
	}
	
	public void setClickable(boolean clickable)
	{
		this.clickable = clickable;
	}
	
	public boolean isHovered()
	{
		return hovered;
	}
	
	public void setHovered(boolean hovered)
	{
		this.hovered = hovered;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	
	
	

}

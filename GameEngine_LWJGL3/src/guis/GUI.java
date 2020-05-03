package guis;

import eventListeners.ButtonListener;

public class GUI{
	
	private GUITexture texture;
	
	private boolean clickable;
	private boolean hovered;
	private boolean visible;
	
	private boolean clicked;
	private boolean fbo;
	
	public GUI(GUITexture texture)
	{
		this.texture = texture;
		this.visible = true;
		this.clicked = false;
		this.fbo = false;
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

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public boolean isFbo() {
		return fbo;
	}

	public void setFbo(boolean fbo) {
		this.fbo = fbo;
	}
	
	
	
	
	
	
	
	

}

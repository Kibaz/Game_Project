package animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class Node {
	
	private List<Node> children;
	
	private List<PositionTransform> positions;
	private List<RotationTransform> rotations;
	private List<ScaleTransform> scalings;
	
	private String name;
	
	private Node parent;
	
	private Matrix4f transformation;
	
	private boolean animNode = false;
	
	public Node(String name, Node parent)
	{
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.positions = new ArrayList<>();
		this.rotations = new ArrayList<>();
		this.scalings = new ArrayList<>();
	}
	
	public Node findByName(String name)
	{
		Node result = null;
		if(this.name.equals(name))
		{
			result = this;
		}
		else
		{
			for(Node child: children)
			{
				result = child.findByName(name);
				if(result != null)
				{
					break;
				}
			}
		}
		
		return result;
	}
	
	public void addChild(Node node)
	{
		this.children.add(node);
	}

	public List<Node> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public Node getParent() {
		return parent;
	}

	public List<PositionTransform> getPositions() {
		return positions;
	}

	public List<RotationTransform> getRotations() {
		return rotations;
	}

	public List<ScaleTransform> getScalings() {
		return scalings;
	}
	
	public Matrix4f getTransformation() {
		return transformation;
	}

	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}

	public void addPosition(PositionTransform position)
	{
		positions.add(position);
	}
	
	public void addScale(ScaleTransform scale)
	{
		scalings.add(scale);
	}
	
	public void addRotation(RotationTransform rotation)
	{
		rotations.add(rotation);
	}
	
	public void setAnimatedNode(boolean isAnimNode)
	{
		this.animNode = isAnimNode;
	}
	
	public boolean isAnimationNode()
	{
		return animNode;
	}
	
	
	
	

}

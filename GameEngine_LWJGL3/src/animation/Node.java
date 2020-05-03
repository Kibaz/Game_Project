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
	
	private Map<String,List<PositionTransform>> positions;
	private Map<String,List<RotationTransform>> rotations;
	private Map<String,List<ScaleTransform>> scalings;
	
	private String name;
	
	private Node parent;
	
	private Matrix4f transformation;
	
	private boolean animNode = false;
	
	public Node(String name, Node parent)
	{
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.positions = new HashMap<>();
		this.rotations = new HashMap<>();
		this.scalings = new HashMap<>();
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

	public Map<String,List<PositionTransform>> getPositions() {
		return positions;
	}

	public Map<String,List<RotationTransform>> getRotations() {
		return rotations;
	}

	public Map<String,List<ScaleTransform>> getScalings() {
		return scalings;
	}
	
	public Matrix4f getTransformation() {
		return transformation;
	}

	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}

	public void addPosition(String animName,PositionTransform position)
	{
		positions.get(animName).add(position);
	}
	
	public void addScale(String animName, ScaleTransform scale)
	{
		scalings.get(animName).add(scale);
	}
	
	public void addRotation(String animName,RotationTransform rotation)
	{
		rotations.get(animName).add(rotation);
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

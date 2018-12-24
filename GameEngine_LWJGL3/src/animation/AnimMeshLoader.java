package animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import models.BaseModel;
import rendering.Loader;
import utils.Maths;
import utils.Utils;

public class AnimMeshLoader {
	
	/*public static AnimEntity loadAnimEntity(String filepath, Loader loader) throws Exception
	{
		AIScene scene = Assimp.aiImportFile(filepath, 
				Assimp.aiProcess_GenSmoothNormals |
				Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_Triangulate |
				Assimp.aiProcess_FixInfacingNormals |
				Assimp.aiProcess_LimitBoneWeights);
		
		if(scene == null)
		{
			throw new Exception("Error loading model");
		}
		
		List<Bone> boneList = new ArrayList<>();
		int numMeshes = scene.mNumMeshes();
		PointerBuffer meshes = scene.mMeshes();
		BaseModel[] models = new BaseModel[numMeshes];
		
		for(int i = 0; i < numMeshes; i++)
		{
			AIMesh mesh = AIMesh.create(meshes.get(i));
			BaseModel model = processMesh(mesh,boneList, loader);
			models[i] = model;
		}
		
		AINode aiRootNode = scene.mRootNode();
		Matrix4f rootTransform = Utils.convertAssimpToLWJGLMat4(aiRootNode.mTransformation()); 
		Node rootNode = processNodesHierarchy(aiRootNode, null);
		Map<String, Animation> animations = processAnimations(scene,boneList,rootNode,rootTransform);
		AnimEntity animEntity = new AnimEntity(models,animations);
		return animEntity;

	}*/
	
	public static Animation loadAnimation(String filepath, Loader loader) throws Exception
	{
		AIScene scene = Assimp.aiImportFile(filepath, 
				Assimp.aiProcess_GenSmoothNormals |
				Assimp.aiProcess_JoinIdenticalVertices |
				Assimp.aiProcess_Triangulate |
				Assimp.aiProcess_FixInfacingNormals |
				Assimp.aiProcess_LimitBoneWeights);
		
		if(scene == null)
		{
			throw new Exception("Error loading model");
		}
		
		List<Bone> boneList = new ArrayList<>();
		int numMeshes = scene.mNumMeshes();
		PointerBuffer meshes = scene.mMeshes();
		BaseModel[] models = new BaseModel[numMeshes];
		
		for(int i = 0; i < numMeshes; i++)
		{
			AIMesh mesh = AIMesh.create(meshes.get(i));
			BaseModel model = processMesh(mesh,boneList, loader);
			models[i] = model;
		}
		
		AINode aiRootNode = scene.mRootNode();
		Matrix4f rootTransform = Utils.convertAssimpToLWJGLMat4(aiRootNode.mTransformation()); 
		Node rootNode = processNodesHierarchy(aiRootNode, null);
		//Map<String, Animation> animations = processAnimations(scene,boneList,rootNode,rootTransform);
		Animation animation = processAnimation(scene,boneList,rootNode,rootTransform);
		animation.setModels(models);
		return animation;

	}
	
	private static BaseModel processMesh(AIMesh mesh, List<Bone> boneList, Loader loader)
	{
		List<Float> verts = new ArrayList<>();
		List<Float> uvs = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Float> weights = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Integer> boneIds = new ArrayList<>();
		
		storeVertices(mesh, verts);
		storeNormals(mesh, normals);
		storeUVs(mesh, uvs);
		storeIndices(mesh, indices);
		storeBones(mesh,boneList,boneIds,weights);
		
		float[] vertArray = Utils.floatListToArray(verts);
		float[] textArray = Utils.floatListToArray(uvs);
		float[] normalArray = Utils.floatListToArray(normals);
		float[] weightsArray = Utils.floatListToArray(weights);
		int[] indexArray = Utils.intListToArray(indices);
		int[] boneIDsArray = Utils.intListToArray(boneIds);
		
		return loader.loadToVAO(vertArray, textArray, normalArray, indexArray, boneIDsArray, weightsArray);
	}
	
	private static Node processNodesHierarchy(AINode aiNode, Node parentNode)
	{
		String nodeName = aiNode.mName().dataString();
		Node node = new Node(nodeName, parentNode);
		
		int numChildren = aiNode.mNumChildren();
		PointerBuffer aiChildren = aiNode.mChildren();
		for(int i = 0; i < numChildren; i++)
		{
			AINode aiChildNode = AINode.create(aiChildren.get(i));
			Node childNode = processNodesHierarchy(aiChildNode, node);
			node.addChild(childNode);
		}
		
		return node;
	}
	
	private static Map<String, Animation> processAnimations(AIScene aiScene, List<Bone> boneList, Node rootNode, 
			Matrix4f rootTransformation)
	{
		Map<String, Animation> animations = new HashMap<>();
		
		// Process all animations
		int numAnims = aiScene.mNumAnimations();
		PointerBuffer aiAnimations = aiScene.mAnimations();
		for(int i = 0; i < numAnims; i++)
		{
			AIAnimation aiAnim = AIAnimation.create(aiAnimations.get(i));
			
			// Calculate transformation matrices for each node
			int numChannels = aiAnim.mNumChannels();
			PointerBuffer aiChannels = aiAnim.mChannels();
			List<Double> timeStamps = new ArrayList<>();
			AINodeAnim highest = AINodeAnim.create(aiChannels.get(0));
			for(int j = 0; j < numChannels; j++)
			{
				AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
				if(aiNodeAnim.mNumPositionKeys() > highest.mNumPositionKeys())
				{
					highest = aiNodeAnim;
				}
				String nodeName = aiNodeAnim.mNodeName().dataString();
				Node node = rootNode.findByName(nodeName);
				buildTransformationMatrices(aiNodeAnim, node);
			}
			
			List<AnimatedFrame> frames = buildAnimationFrames(boneList, rootNode, rootTransformation);
			System.out.println(frames.size());
			// Grab frame time stamps
			AIVectorKey.Buffer posKeys = highest.mPositionKeys();
			for(int j = 0; j < frames.size(); j++)
			{
				AIVectorKey vecKey = posKeys.get(j);
				AnimatedFrame curr = frames.get(j);
				curr.setTimeStamp(vecKey.mTime());
			}
			
			Animation animation = new Animation(aiAnim.mName().dataString(), frames, aiAnim.mDuration());
			animations.put(animation.getName(), animation);
		}
		return animations;
		
	}
	
	private static Animation processAnimation(AIScene aiScene, List<Bone> boneList, Node rootNode, 
			Matrix4f rootTransformation)
	{
		Animation animation = null;
		// Process this animation
		PointerBuffer aiAnimations = aiScene.mAnimations();
		AIAnimation aiAnim = AIAnimation.create(aiAnimations.get(0));
			
		// Calculate transformation matrices for each node
		int numChannels = aiAnim.mNumChannels();
		PointerBuffer aiChannels = aiAnim.mChannels();
		List<Double> timeStamps = new ArrayList<>();
		AINodeAnim highest = AINodeAnim.create(aiChannels.get(0));
		for(int j = 0; j < numChannels; j++)
		{
			AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
			if(aiNodeAnim.mNumPositionKeys() > highest.mNumPositionKeys())
			{
				highest = aiNodeAnim;
			}
			String nodeName = aiNodeAnim.mNodeName().dataString();
			Node node = rootNode.findByName(nodeName);
			buildTransformationMatrices(aiNodeAnim, node);
		}
			
		List<AnimatedFrame> frames = buildAnimationFrames(boneList, rootNode, rootTransformation);
		System.out.println(frames.size());
		// Grab frame time stamps
		AIVectorKey.Buffer posKeys = highest.mPositionKeys();
		for(int j = 0; j < frames.size(); j++)
		{
			AIVectorKey vecKey = posKeys.get(j);
			AnimatedFrame curr = frames.get(j);
			curr.setTimeStamp(vecKey.mTime());
		}
			
		animation = new Animation(aiAnim.mName().dataString(), frames, aiAnim.mDuration());
		return animation;
		
	}
	
	private static void buildTransformationMatrices(AINodeAnim aiNodeAnim, Node node)
	{
		int numFrames = aiNodeAnim.mNumPositionKeys();
		AIVectorKey.Buffer posKeys = aiNodeAnim.mPositionKeys();
		AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
		AIQuatKey.Buffer rotKeys = aiNodeAnim.mRotationKeys();
		for(int i = 0; i < numFrames; i++)
		{
			AIVectorKey aiVecKey = posKeys.get(i);
			AIVector3D vec = aiVecKey.mValue();
			Matrix4f transfMat = new Matrix4f();
			Matrix4f.translate(new Vector3f(vec.x(),vec.y(),vec.z()), transfMat, transfMat);
			
			AIQuatKey quatKey = rotKeys.get(i);
			AIQuaternion aiQuat = quatKey.mValue();
			Quaternion quat = new Quaternion(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
			Matrix4f rotMatrix = Maths.quatToMatrix4f(quat);
			Matrix4f.mul(transfMat, rotMatrix, transfMat);
			
			if(i < aiNodeAnim.mNumScalingKeys())
			{
				aiVecKey = scalingKeys.get(i);
				vec = aiVecKey.mValue();
				Matrix4f.scale(new Vector3f(vec.x(),vec.y(),vec.z()), transfMat, transfMat);
			}
			node.addTransformation(transfMat);
		}
	}
	
	private static List<AnimatedFrame> buildAnimationFrames(List<Bone> boneList, Node rootNode, Matrix4f rootTransformation)
	{
		int numFrames = rootNode.getAnimationFrames();
		List<AnimatedFrame> frameList = new ArrayList<>();
		for(int i = 0; i < numFrames; i++)
		{
			AnimatedFrame frame = new AnimatedFrame();
			frameList.add(frame);
			
			int numBones = boneList.size();
			for(int j = 0; j < numBones; j++)
			{
				Bone bone = boneList.get(j);
				Node node = rootNode.findByName(bone.getBoneName());
				Matrix4f boneMatrix = Node.getParentTransforms(node, i);
				Matrix4f.mul(boneMatrix, bone.getOffsetMatrix(), boneMatrix);
				Matrix4f temp = new Matrix4f(rootTransformation);
				Matrix4f.mul(temp, boneMatrix, boneMatrix);
				frame.setMatrix(j, boneMatrix);
				
			}
		}
		
		return frameList;
	}
	
	private static void storeBones(AIMesh mesh, List<Bone> boneList, List<Integer> boneIds, List<Float> weights)
	{
		Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
		int numBones = mesh.mNumBones();
		PointerBuffer bones = mesh.mBones();
		
		for(int i = 0; i < numBones; i++)
		{
			AIBone aiBone = AIBone.create(bones.get(i));
			int id = boneList.size();
			Bone bone = new Bone(id,aiBone.mName().dataString(),Utils.convertAssimpToLWJGLMat4(aiBone.mOffsetMatrix()));
			boneList.add(bone);
			
			int numWeights = aiBone.mNumWeights();
			AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
			for(int j = 0; j < numWeights; j++)
			{
				AIVertexWeight aiWeight = aiWeights.get(j);
				VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(), aiWeight.mWeight());
				List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
				if(vertexWeightList == null)
				{
					vertexWeightList = new ArrayList<>();
					weightSet.put(vw.getVertexId(), vertexWeightList);
				}
				
				vertexWeightList.add(vw);
			}
		}
		
		int numVerts = mesh.mNumVertices();
		for(int i = 0; i < numVerts; i++)
		{
			List<VertexWeight> vertexWeightList = weightSet.get(i);
			int size = vertexWeightList != null ? vertexWeightList.size() : 0;
			for(int j = 0; j < AnimatedCharacter.MAX_WEIGHTS; j++)
			{
				if(j < size)
				{
					VertexWeight vw = vertexWeightList.get(j);
					weights.add(vw.getWeight());
					boneIds.add(vw.getBoneId());
				}
				else
				{
					weights.add(0.0f);
					boneIds.add(0);
				}
			}
		}
	}
	
	private static void storeVertices(AIMesh mesh, List<Float> vertices)
	{
		AIVector3D.Buffer verts = mesh.mVertices();
		
		while(verts.remaining() > 0)
		{
			AIVector3D vertex = verts.get();
			vertices.add(vertex.x());
			vertices.add(vertex.y());
			vertices.add(vertex.z());
		}
	}
	
	private static void storeNormals(AIMesh mesh, List<Float> normals)
	{
		for(int i = 0; i < mesh.mNumVertices(); i++)
		{
			AIVector3D normal = mesh.mNormals().get(i);
			normals.add(normal.x());
			normals.add(normal.y());
			normals.add(normal.z());
		}
		
	}
	
	private static void storeUVs(AIMesh mesh, List<Float> uvs)
	{
		AIVector3D.Buffer aiUVs = mesh.mTextureCoords(0);
		
		for(int i = 0; i < mesh.mNumVertices(); i++)
		{
			AIVector3D uv = aiUVs.get(i);
			uvs.add(uv.x());
			uvs.add(uv.y());
		}
	}
	
	private static void storeIndices(AIMesh mesh, List<Integer> indices)
	{
		for(int i = 0; i < mesh.mNumFaces(); i++)
		{
			AIFace face = mesh.mFaces().get(i);
			for(int j = 0; j < face.mNumIndices(); j++)
			{
				indices.add(face.mIndices().get(j));
			}
		}
	}

}

package animation;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import components.AnimationComponent;
import entities.Entity;
import models.BaseModel;
import models.Mesh;
import models.TexturedModel;
import rendering.Loader;
import runtime.Main;
import texturing.Material;
import texturing.ModelTexture;
import utils.Utils;

public class AnimationLoader {
	
	public static Entity loadAnimatedFile(String filePath, String texturePath, Vector3f position, float rotX, float rotY, float rotZ, float scale, Loader loader) throws RuntimeException
	{	
		
		AIScene scene = Assimp.aiImportFile(filePath, 
											Assimp.aiProcess_Triangulate |
											Assimp.aiProcess_GenSmoothNormals |
											Assimp.aiProcess_LimitBoneWeights |
											Assimp.aiProcess_FixInfacingNormals |
											Assimp.aiProcess_JoinIdenticalVertices);
		
		if(scene == null || scene.mNumAnimations() == 0)
		{
			throw new RuntimeException("Could not load file or animation data was not found!");
		}
		
		// Retrieve material data from animation file
		int numMats = scene.mNumMaterials();
		PointerBuffer aiMats = scene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for(int i = 0; i < numMats; i++)
		{
			AIMaterial aiMat = AIMaterial.create(aiMats.get(i));
			processMaterial(aiMat,materials,loader);
		}
		
		
		// Access and create meshes
		List<Bone> boneList = new ArrayList<>();
		int numMeshes = scene.mNumMeshes();
		PointerBuffer meshBuffer = scene.mMeshes();
		BaseModel[] meshes = new BaseModel[numMeshes];
		for(int i = 0; i < numMeshes; i++)
		{
			AIMesh aiMesh = AIMesh.create(meshBuffer.get(i));
			BaseModel mesh = processMesh(aiMesh,materials,boneList,loader);
			meshes[i] = mesh;
		}
		
		Bone[] bones = new Bone[boneList.size()];
		for(int i = 0; i < boneList.size(); i++)
		{
			bones[i] = boneList.get(i);
		}
		
		AINode aiRootNode = scene.mRootNode();
		
		Node rootNode = readNodes(aiRootNode,null); 
		
		AIMatrix4x4 inverseRootTransform = aiRootNode.mTransformation();
		Matrix4f inverseRootTransformation = Utils.convertAssimpToLWJGLMat4(inverseRootTransform);
		rootNode.setTransformation(inverseRootTransformation); // Required transform for correctly position model
		ModelTexture texture = new ModelTexture(loader.loadTexture(texturePath));
		/*
		 * Create a regular entity using mesh data, 
		 * texture data and specified position,
		 * rotation and scale data
		 */
		Main.testModel = new TexturedModel(meshes[0],texture);
		Entity entity = new Entity(Main.testModel,position,rotX,rotY,rotZ,scale);
		// Create an animated component using the processed animation data
		AnimationComponent animationComponent = new AnimationComponent("animated_component");
		animationComponent.setBones(bones);
		Matrix4f[] jointTransforms = new Matrix4f[bones.length];
		Arrays.fill(jointTransforms, new Matrix4f());
		animationComponent.setJointTransforms(jointTransforms);
		animationComponent.setGlobalTransformationMatrix(inverseRootTransformation);
		
		// Build animations
		readAnimations(animationComponent,scene,rootNode);
		entity.addComponent(animationComponent);
		return entity;
		
	}
	
	private static void readAnimations(AnimationComponent animationComponent, AIScene scene, Node root)
	{
		int numAnims = scene.mNumAnimations();
		PointerBuffer animations = scene.mAnimations();
		for(int i = 0; i < numAnims; i++)
		{
			AIAnimation aiAnimation = AIAnimation.create(animations.get(i));
			
			int numChannels = aiAnimation.mNumChannels();
			PointerBuffer channels = aiAnimation.mChannels();
			for(int j = 0; j < numChannels; j++)
			{
				AINodeAnim aiNodeAnim = AINodeAnim.create(channels.get(j));
				Node node = root.findByName(aiNodeAnim.mNodeName().dataString());
				node.setAnimatedNode(true);
				readTransformParams(aiNodeAnim,node);
			}
			
			double duration = aiAnimation.mDuration();
			double ticksPerSecond = aiAnimation.mTicksPerSecond();
			Animation animation = new Animation(aiAnimation.mName().dataString(),ticksPerSecond,duration,root);
			animationComponent.submitAnimation(animation.getName(), animation);
		}
	}
	
	private static void readTransformParams(AINodeAnim animNode, Node node)
	{
		AIVectorKey.Buffer positionKeys = animNode.mPositionKeys();
		AIQuatKey.Buffer rotationKeys = animNode.mRotationKeys();
		AIVectorKey.Buffer scalingKeys = animNode.mScalingKeys();
		
		int numPosKeys = animNode.mNumPositionKeys();
		int numRotKeys = animNode.mNumRotationKeys();
		int numScaleKeys = animNode.mNumScalingKeys();
		
		for(int i = 0; i < numPosKeys; i++)
		{
			AIVectorKey posKey = positionKeys.get(i);
			Vector3f position = new Vector3f(posKey.mValue().x(),posKey.mValue().y(),posKey.mValue().z());
			PositionTransform positionTransform = new PositionTransform(position,posKey.mTime());
			node.addPosition(positionTransform);
		}
		
		for(int i = 0; i < numRotKeys; i++)
		{
			AIQuatKey rotKey = rotationKeys.get(i);
			Quaternion rotation = new Quaternion(rotKey.mValue().x(),rotKey.mValue().y(),rotKey.mValue().z(),rotKey.mValue().w());
			RotationTransform rotationTransform = new RotationTransform(rotation,rotKey.mTime());
			node.addRotation(rotationTransform);
		}
		
		for(int i = 0; i < numScaleKeys; i++)
		{
			AIVectorKey scaleKey = scalingKeys.get(i);
			Vector3f scale = new Vector3f(scaleKey.mValue().x(),scaleKey.mValue().y(),scaleKey.mValue().z());
			ScaleTransform scaleTransform = new ScaleTransform(scale,scaleKey.mTime());
			node.addScale(scaleTransform);
		}
	}
	
	private static Node readNodes(AINode aiNode, Node parent)
	{
		String nodeName = aiNode.mName().dataString();
		Node node = new Node(nodeName,parent);
		
		int numChildren = aiNode.mNumChildren();
		PointerBuffer aiChildren = aiNode.mChildren();
		for(int i = 0; i < numChildren; i++)
		{
			AINode aiChildNode = AINode.create(aiChildren.get(i));
			Node childNode = readNodes(aiChildNode,node);
			childNode.setTransformation(Utils.convertAssimpToLWJGLMat4(aiChildNode.mTransformation()));
			node.addChild(childNode);
		}
		
		return node;
	}
	
	private static void processMaterial(AIMaterial aiMat,List<Material> materials, Loader loader)
	{
		AIColor4D colour = AIColor4D.create();
		
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(aiMat, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null); 
		String texturePath = path.dataString();
		
		ModelTexture texture = null;
		if(texturePath != null && texturePath.length() > 0)
		{
			String[] splitPath = texturePath.split("/");
			String finalPath = splitPath[splitPath.length-1];
			texture = new ModelTexture(loader.loadTexture("res/" + finalPath));
		}
		
		
		// Get ambient colour
		Vector4f ambient = Material.DEFAULT_COLOUR;
		int result = Assimp.aiGetMaterialColor(aiMat, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, colour);
		if(result == 0)
		{
			ambient = new Vector4f(colour.r(),colour.g(),colour.b(),colour.a());
		}
		
		// Get diffuse colour
		Vector4f diffuse = Material.DEFAULT_COLOUR;
		result = Assimp.aiGetMaterialColor(aiMat, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, colour);
		if(result == 0)
		{
			diffuse = new Vector4f(colour.r(),colour.g(),colour.b(),colour.a());
		}
		
		// Get specular colour
		Vector4f specular = Material.DEFAULT_COLOUR;
		result = Assimp.aiGetMaterialColor(aiMat, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, colour);
		if(result == 0)
		{
			specular = new Vector4f(colour.r(),colour.g(),colour.b(),colour.a());
		}
		
		Material material = new Material(ambient,diffuse,specular,texture,1.0f);
		materials.add(material);
	}
	
	private static BaseModel processMesh(AIMesh aiMesh, List<Material> materials, List<Bone>boneList, Loader loader)
	{
		List<Float> vertices = new ArrayList<>();
		List<Float> uvs = new ArrayList<>();
		List<Float> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		List<Integer> boneIds = new ArrayList<>();
		List<Float> weights = new ArrayList<>();
		
		// Store vertices
		storeVertices(aiMesh,vertices);
		
		// Store normals
		storeNormals(aiMesh,normals);
		
		// Store UVs / Texture coords
		storeUVS(aiMesh,uvs);
		
		// Store indices
		storeIndices(aiMesh,indices);
		
		// Store bones, bone indexes and weights
		storeBones(aiMesh,boneList,boneIds,weights);
		
		
		BaseModel mesh = loader.loadToVAO(Utils.floatListToArray(vertices),
				Utils.floatListToArray(uvs),
				Utils.floatListToArray(normals),
				Utils.intListToArray(indices),
				Utils.intListToArray(boneIds),
				Utils.floatListToArray(weights));
		
		Material material;
		
		int matIndex = aiMesh.mMaterialIndex();
		
		if(matIndex >= 0 && matIndex < materials.size())
		{
			material = materials.get(matIndex);
		}
		else
		{
			material = new Material();
		}
		
		mesh.setMaterial(material);
		
		return mesh;
		
	}
	
	private static void storeBones(AIMesh aiMesh,List<Bone> boneList,List<Integer> boneIds,List<Float> weights)
	{
	    Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
	    int numBones = aiMesh.mNumBones();
	    PointerBuffer aiBones = aiMesh.mBones();
	    for (int i = 0; i < numBones; i++) {
	        AIBone aiBone = AIBone.create(aiBones.get(i));
	        int id = boneList.size();
	        Bone bone = new Bone(id, aiBone.mName().dataString(), Utils.convertAssimpToLWJGLMat4(aiBone.mOffsetMatrix()));
	        boneList.add(bone);
	        int numWeights = aiBone.mNumWeights();
	        AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
	        for (int j = 0; j < numWeights; j++) {
	            AIVertexWeight aiWeight = aiWeights.get(j);
	            VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
	                    aiWeight.mWeight());
	            List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
	            if (vertexWeightList == null) {
	                vertexWeightList = new ArrayList<>();
	                weightSet.put(vw.getVertexId(), vertexWeightList);
	            }
	            vertexWeightList.add(vw);
	        }
	    }

	    int numVertices = aiMesh.mNumVertices();
	    for (int i = 0; i < numVertices; i++) {
	        List<VertexWeight> vertexWeightList = weightSet.get(i);
	        int size = vertexWeightList != null ? vertexWeightList.size() : 0;
	        for (int j = 0; j < Animation.MAX_WEIGHTS; j++) {
	            if (j < size) {
	                VertexWeight vw = vertexWeightList.get(j);
	                weights.add(vw.getWeight());
	                boneIds.add(vw.getBoneId());
	            } else {
	                weights.add(0.0f);
	                boneIds.add(0);
	            }
	        }
	    }
	}
	
	private static void storeVertices(AIMesh aiMesh, List<Float>vertices)
	{
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		while(aiVertices.remaining() > 0)
		{
			AIVector3D aiVertex = aiVertices.get();
			vertices.add(aiVertex.x());
			vertices.add(aiVertex.y());
			vertices.add(aiVertex.z());
		}
	}
	
	private static void storeUVS(AIMesh aiMesh, List<Float> uvs)
	{
		AIVector3D.Buffer aiUVs = aiMesh.mTextureCoords(0);
		if(aiUVs != null)
		{
			for(int i = 0; i < aiMesh.mNumVertices(); i++)
			{
				AIVector3D uv = aiUVs.get(i);
				uvs.add(uv.x());
				uvs.add(uv.y());
			}
		}
	}
	
	private static void storeNormals(AIMesh aiMesh, List<Float> normals)
	{
		AIVector3D.Buffer aiNormals = aiMesh.mNormals();
		while(aiNormals.hasRemaining())
		{
			AIVector3D aiNormal = aiNormals.get();
			normals.add(aiNormal.x());
			normals.add(aiNormal.y());
			normals.add(aiNormal.z());
		}
	}
	
	private static void storeIndices(AIMesh aiMesh, List<Integer> indices)
	{
		for(int i = 0; i < aiMesh.mNumFaces(); i++)
		{
			AIFace face = aiMesh.mFaces().get(i);
			for(int j = 0; j < face.mNumIndices(); j++)
			{
				indices.add(face.mIndices().get(j));
			}
		}
	}
	
	

}

package com.kujatas.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import com.kujatas.model.loader.ALoader;
import com.kujatas.model.loader.HrcLoader;
import com.kujatas.model.loader.PLoader;
import com.kujatas.model.loader.RsdLoader;


public class AnimatedSkeleton {
	
	private TransformGroup rootTG;
	private Animation animation;
	private Map<String, Bone> boneMap = new HashMap<String, Bone>();
	private Map<Bone, BranchGroup> boneBGMap = new HashMap<Bone, BranchGroup>();
	private Map<Bone, TransformGroup> boneTGMap = new HashMap<Bone, TransformGroup>();
	private Skeleton skeleton;
	private int currentFrameIndex;
	private FF7Character ff7Character;
	
	public static List<Shape3D> createBoneComponents(String pBaseFilename, List<String> textureBaseFilenames) throws KjataException {
		List<Shape3D> components = new ArrayList<Shape3D>();
		PLoader pLoader = new PLoader();
		Model model = pLoader.load(pBaseFilename);
		List<PolygonGroup> groups = model.getPolygonGroups();
		for (int i=0; i<groups.size(); i++) {
			TriangleMesh triangleMesh = new TriangleMesh(model, i, textureBaseFilenames);
			components.add(triangleMesh);
		}
		return components;
	}
	
	public AnimatedSkeleton(FF7Character ff7Character, ToonAnimation toonAnimation) throws KjataException {
		
		this.ff7Character = ff7Character;
		
		String hrcBaseFilename = FF7File.getHrcBaseFilename(ff7Character);
		String aBaseFilename = FF7File.getAnimationBaseFilename(ff7Character, toonAnimation);
		
		ALoader aLoader = ALoader.getInstance();
		this.animation = aLoader.load(aBaseFilename);
		
		currentFrameIndex = 0;
		
		AnimationFrame currentFrame = animation.animationFrames.get(currentFrameIndex);
		
		this.rootTG = new TransformGroup();
		BranchGroup rootBG = new BranchGroup();
		rootTG.addChild(rootBG);
		rotateY(rootTG, currentFrame.rootRotation.y);
		rotateX(rootTG, currentFrame.rootRotation.x);
		rotateZ(rootTG, currentFrame.rootRotation.z);
		
		this.boneMap = new HashMap<String, Bone>();
		this.boneBGMap = new HashMap<Bone, BranchGroup>();
		this.boneTGMap = new HashMap<Bone, TransformGroup>();
		HrcLoader hrcLoader = new HrcLoader();
		this.skeleton = hrcLoader.load(hrcBaseFilename);
		List<Bone> bones = skeleton.getBones();
		RsdLoader rsdLoader = new RsdLoader();
		for (int i=0; i<bones.size(); i++) {
			Bone bone = bones.get(i);
			boneMap.put(bone.getName(), bone);
			List<String> rsdBaseFilenames = bone.getRsdBaseFilenames();
			// TODO: Handle scenarios where an HRC contains multiple RSDs (rare, but example is bzhf.hrc)
			String rsdBaseFilename = rsdBaseFilenames == null ? null : rsdBaseFilenames.size() == 0 ? null : rsdBaseFilenames.get(0);
			TransformGroup boneTG = new TransformGroup();
			boneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			boneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			BranchGroup boneBG = new BranchGroup();
			boneTG.addChild(boneBG);
			if (rsdBaseFilename != null) {
				BoneResource boneResource = rsdLoader.load(rsdBaseFilename);
				String pBaseFilename = boneResource.getPolygonFilename();
				List<String> textureBaseFilenames = boneResource.getTextureBaseFilenames();
				List<Shape3D> boneComponents = createBoneComponents(pBaseFilename, textureBaseFilenames);
				for (Shape3D boneComponent : boneComponents) {
					boneBG.addChild(boneComponent);
				}
			}
			boneBGMap.put(bone, boneBG);
			boneTGMap.put(bone, boneTG);
			
			Transform3D boneTransformation = new Transform3D();
			
			if (bone.getParentBoneName().equals("root")) {
				// link bone TG+BG to its parent TG+BG
				rootBG.addChild(boneTG);
			} else {
				// link bone TG+BG to its parent TG+BG
				String parentBoneName = bone.getParentBoneName();
				Bone parentBone = boneMap.get(parentBoneName);
				BranchGroup parentBG = boneBGMap.get(parentBone);
				parentBG.addChild(boneTG);
				// do translation before rotation
				Transform3D boneTranslation = new Transform3D();
				double boneLength = parentBone.getLength();
				boneTranslation.setTranslation(new Vector3f(0.0f, 0.0f, -(float)boneLength));
				boneTransformation.set(boneTranslation);
			}
			
			// do rotation
			Transform3D rotX = new Transform3D(); rotX.rotX(currentFrame.boneRotations.get(i).x * Math.PI / 180.0f);
			Transform3D rotY = new Transform3D(); rotY.rotY(currentFrame.boneRotations.get(i).y * Math.PI / 180.0f);
			Transform3D rotZ = new Transform3D(); rotZ.rotZ(currentFrame.boneRotations.get(i).z * Math.PI / 180.0f);
			boneTransformation.mul(rotY);
			boneTransformation.mul(rotX);
			boneTransformation.mul(rotZ);
			boneTG.setTransform(boneTransformation);
		}
		float ff7CharacterHalfHeight = 1.5f;
		translate(rootTG, 0.0f, ff7CharacterHalfHeight, 0.0f);
		scale(rootTG, 0.10f);
		rotateX(rootTG, 180.0f);
	}
	
	public TransformGroup getRootTG() {
		return this.rootTG;
	}
	
	public void changeAnimationTo(ToonAnimation toonAnimation) throws KjataException {
		String aBaseFilename = FF7File.getAnimationBaseFilename(ff7Character, toonAnimation);
		ALoader aLoader = ALoader.getInstance();
		this.animation = aLoader.load(aBaseFilename);
		this.currentFrameIndex = 0;
		updateSkeletonFrame();
	}
	
	public void advanceFrame() {
		if (animation.animationFrames.size() <= 1) {
			return;  // only one frame, so make no changes
		}
		currentFrameIndex++;
		if (currentFrameIndex == animation.animationFrames.size()) {
			currentFrameIndex = 0;
		}
		updateSkeletonFrame();
	}

	// Call this method after setting animation and animationFrames and currentFrameIndex
	public void updateSkeletonFrame() {
		AnimationFrame currentFrame = animation.animationFrames.get(currentFrameIndex);
		List<Bone> bones = skeleton.getBones();
		for (int i=0; i<bones.size(); i++) {
			Bone bone = bones.get(i);
			TransformGroup boneTG = boneTGMap.get(bone);
			Transform3D boneTransformation = new Transform3D();
			if (!(bone.getParentBoneName().equals("root"))) {
				String parentBoneName = bone.getParentBoneName();
				Bone parentBone = boneMap.get(parentBoneName);
				// do translation before rotation
				Transform3D boneTranslation = new Transform3D();
				double boneLength = parentBone.getLength();
				boneTranslation.setTranslation(new Vector3f(0.0f, 0.0f, -(float)boneLength));
				boneTransformation.set(boneTranslation);
			}
			// do rotation
			Transform3D rotX = new Transform3D(); rotX.rotX(currentFrame.boneRotations.get(i).x * Math.PI / 180.0f);
			Transform3D rotY = new Transform3D(); rotY.rotY(currentFrame.boneRotations.get(i).y * Math.PI / 180.0f);
			Transform3D rotZ = new Transform3D(); rotZ.rotZ(currentFrame.boneRotations.get(i).z * Math.PI / 180.0f);
			boneTransformation.mul(rotY);
			boneTransformation.mul(rotX);
			boneTransformation.mul(rotZ);
			boneTG.setTransform(boneTransformation);
		}
	}

	private void translate(TransformGroup tg, float x, float y, float z) {
		Transform3D translation = new Transform3D();
		translation.setTranslation(new Vector3f(x, y, z));
		Transform3D tgTransform = new Transform3D();
		tg.getTransform(tgTransform);
		tgTransform.set(translation);
		tg.setTransform(tgTransform);
	}

	private void rotateX(TransformGroup tg, float degrees) {
		Transform3D rotation = new Transform3D();
		rotation.rotX(degrees * Math.PI / 180.0f);
		Transform3D tgTransform = new Transform3D();
		tg.getTransform(tgTransform);
		tgTransform.mul(rotation);
		tg.setTransform(tgTransform);
	}
	
	private void rotateY(TransformGroup tg, float degrees) {
		Transform3D rotation = new Transform3D();
		rotation.rotY(degrees * Math.PI / 180.0f);
		Transform3D tgTransform = new Transform3D();
		tg.getTransform(tgTransform);
		tgTransform.mul(rotation);
		tg.setTransform(tgTransform);
	}
	
	private void rotateZ(TransformGroup tg, float degrees) {
		Transform3D rotation = new Transform3D();
		rotation.rotZ(degrees * Math.PI / 180.0f);
		Transform3D tgTransform = new Transform3D();
		tg.getTransform(tgTransform);
		tgTransform.mul(rotation);
		tg.setTransform(tgTransform);
	}
	
	private void scale(TransformGroup tg, float scaleAmount) {
		Transform3D scaleTransform = new Transform3D();
		scaleTransform.setScale(scaleAmount);
		Transform3D tgTransform = new Transform3D();
		tg.getTransform(tgTransform);
		tgTransform.mul(scaleTransform);
		tg.setTransform(tgTransform);
	}

}

package com.kujatas.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoneNode {
	
	private Bone bone;
	private BoneNode parentNode;
	private List<BoneNode> childNodes;
	
	public BoneNode(Bone bone, BoneNode parentNode) {
		this.bone = bone;
		this.parentNode = parentNode;
		this.childNodes = new ArrayList<BoneNode>();
		if (parentNode != null) {
			parentNode.addChild(this);
		}
	}
	
	private void addChild(BoneNode boneNode) {
		this.childNodes.add(boneNode);
	}
	
	public Bone getBone() {
		return this.bone;
	}
	
	public BoneNode getParentNode() {
		return this.parentNode;
	}
	
	public List<BoneNode> getChildNodes() {
		return this.childNodes;
	}
	
	public static BoneNode createTree(Skeleton skeleton) {
		Map<String, BoneNode> nodeMap = new HashMap<String, BoneNode>();
		BoneNode rootNode = new BoneNode(null, null);
		nodeMap.put("root", rootNode);
		List<Bone> allBones = skeleton.getBones();
		for (Bone bone : allBones) {
			BoneNode parentNode = nodeMap.get(bone.getParentBoneName());
			BoneNode node = new BoneNode(bone, parentNode);
			nodeMap.put(bone.getName(), node);
		}
		return rootNode;
	}
}

package com.kujatas.model;

import java.util.ArrayList;
import java.util.List;

public class Skeleton {
	
	private String name;
	private List<Bone> bones;
	
	public Skeleton(String name) {
		this.name = name;
		this.bones = new ArrayList<Bone>();
	}
	
	public void addBone(Bone bone) {
		this.bones.add(bone);
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Bone> getBones() {
		return this.bones;
	}
	
	@Override
	public String toString() {
		return "Skeleton[name=" + name + ", bones=" + bones + "]";
	}
}

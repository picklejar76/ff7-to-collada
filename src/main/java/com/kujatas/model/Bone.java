package com.kujatas.model;

import java.util.List;

public class Bone {
	
	private int boneIndex;
	private String name;
	private String parent;
	private double length;
	private List<String> rsdBaseFilenames;
	
	public Bone(int boneIndex, String name, String parent, double length, List<String> rsdFilenames) {
		this.boneIndex = boneIndex;
		this.name = name;
		this.parent = parent;
		this.length = length;
		this.rsdBaseFilenames = rsdFilenames;
	}
	
	public int getBoneIndex() {
		return this.boneIndex;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getParentBoneName() {
		return this.parent;
	}
	
	public double getLength() {
		return this.length;
	}
	
	public List<String> getRsdBaseFilenames() {
		return this.rsdBaseFilenames;
	}
	
	@Override
	public String toString() {
		return "Bone[" + name + ":" + parent + ":" + length + ":" + getRsdBaseFilenames() + "]\n";
	}
}

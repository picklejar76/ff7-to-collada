package com.kujatas.model;

import java.util.ArrayList;
import java.util.List;

public class Animation {
	
	public int version;
	public int numFrames;
	public int numBones;
	public byte rotationOrder1;
	public byte rotationOrder2;
	public byte rotationOrder3;
	public List<AnimationFrame> animationFrames;
	
	public Animation() {
		this.animationFrames = new ArrayList<AnimationFrame>();
	}

	@Override
	public String toString() {
		return "version=" + version + ", numFrames=" + numFrames + ", numBones=" + numBones +
				", rotationOrder=" + rotationOrder1 + ":" + rotationOrder2 + ":" + rotationOrder3 +
				",\nanimationFrames=" + animationFrames;
	}
}

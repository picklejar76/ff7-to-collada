package com.kujatas.model;

import java.util.ArrayList;
import java.util.List;

public class AnimationFrame {
	
	public Point3D rootRotation;
	public Point3D rootTranslation;
	public List<Point3D> boneRotations;
	
	public AnimationFrame() {
		this.boneRotations = new ArrayList<Point3D>();
	}
	
	@Override
	public String toString() {
		return "AnimationFrame[\n" +
				"rootRotation=" + rootRotation +
				",\nrootTranslation=" + rootTranslation +
				",\nboneRotations=" + pointListToString(boneRotations) + "]";
	}
	
	private String pointListToString(List<Point3D> points) {
		StringBuilder s = new StringBuilder("[\n");
		for (Point3D point : points) {
			s.append("\t" + point + ";\n");
		}
		s.append("]\n");
		return s.toString();
	}
}

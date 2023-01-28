package com.kujatas.model;

public class Point3D {
	
	public float x;
	public float y;
	public float z;
	
	public Point3D() {
	}
	
	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}

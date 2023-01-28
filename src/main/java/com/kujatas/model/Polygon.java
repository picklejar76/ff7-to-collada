package com.kujatas.model;

public class Polygon {
	/*
	public Point3D vertex1, vertex2, vertex3;
	public Point3D normal1, normal2, normal3;
	*/
	public int vertexIndex1, vertexIndex2, vertexIndex3;
	public int normalIndex1, normalIndex2, normalIndex3;
	public int otherIndex1, otherIndex2, otherIndex3; // TODO: rename to edgeIndex or whatever it should be
	
	@Override
	public String toString() {
		return
			"vertexIndices: (" + vertexIndex1 + ", " + vertexIndex2 + ", " + vertexIndex3 + "), " +
			"normalIndices: (" + normalIndex1 + ", " + normalIndex2 + ", " + normalIndex3 + "), " +
			"otherIndices:  (" + otherIndex1  + ", " + otherIndex2  + ", " + otherIndex3  + ")";
	}

}

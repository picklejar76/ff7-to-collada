package com.kujatas.model;

public class PolygonGroup {
	
	public int polygonType; // 1=non-textured, 2=textured with normals, 3=textures without normals
	public int offsetPolyIndex; // the first polygon used in this group
	public int numPolysInGroup;
	public int offsetVertexIndex;
	public int numVerticesInGroup;
	public int offsetEdgeIndex;
	public int offsetTextureCoordinateIndex;
	public int isTextureUsed;
	public int textureIndex;
	
	@Override
	public String toString() {
		return "PolygonGroup[polygonType=" + polygonType + ", " +
				"offsetPolyIndex=" + offsetPolyIndex + ", " +
				"numPolysInGroup=" + numPolysInGroup + ", " +
				"offsetVertexIndex=" + offsetVertexIndex + ", " +
				"numVerticesInGroup=" + numVerticesInGroup + ", " +
				"offsetEdgeIndex=" + offsetEdgeIndex + ", " +
				"offsetTextureCoordinateIndex=" + offsetTextureCoordinateIndex + ", " +
				"isTextureUsed=" + isTextureUsed + ", " +
				"textureIndex=" + textureIndex +
				"]";
	}
}

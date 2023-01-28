package com.kujatas.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
	
	private List<Point3D> vertices;
	private List<Point3D> normals;
	private List<Point2D> textureCoordinates; // TODO: Consider using u,v instead of x,y
	private List<Color4D> vertexColors;
	private List<Color4D> polygonColors;
	private List<Polygon> polygons;
	private List<PolygonGroup> polygonGroups;
	
	public Model() {
		this.vertices = new ArrayList<Point3D>();
		this.normals = new ArrayList<Point3D>();
		this.textureCoordinates = new ArrayList<Point2D>();
		this.vertexColors = new ArrayList<Color4D>();
		this.polygonColors = new ArrayList<Color4D>();
		this.polygons = new ArrayList<Polygon>();
		this.polygonGroups = new ArrayList<PolygonGroup>();
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("vertices: " + listToString(vertices) + "\n");
		s.append("normals: " + listToString(normals) + "\n");
		s.append("textureCoordinates: " + listToString(textureCoordinates) + "\n");
		s.append("vertexColors: " + listToString(vertexColors) + "\n");
		s.append("polygonColors: " + listToString(polygonColors) + "\n");
		s.append("polygons: " + listToString(polygons) + "\n");
		s.append("polygonGroups: " + listToString(polygonGroups) + "\n");
		return s.toString();
	}
	
	private String listToString(List<?> list) {
		StringBuilder s = new StringBuilder();
		s.append("[\n");
		for (Object obj: list) {
			s.append("  " + obj + "\n");
		}
		s.append("]\n");
		return s.toString();
	}
	
	public void addVertex(float x, float y, float z) {
		Point3D vertex = new Point3D(x, y, z);
		vertices.add(vertex);
	}
	
	public void addNormal(float x, float y, float z) {
		Point3D normal = new Point3D(x, y, z);
		normals.add(normal);
	}
	
	public void addTextureCoordinate(float x, float y) {
		float actualX = x;
		float actualY = y;
		Point2D textureCoordinate = new Point2D(actualX, actualY);
		textureCoordinates.add(textureCoordinate);
	}
	
	public void addVertexColor(byte r, byte g, byte b, byte a) {
		Color4D vertexColor = new Color4D(r, g, b, a);
		vertexColors.add(vertexColor);
	}
	
	public void addPolygonColor(byte r, byte g, byte b, byte a) {
		Color4D polygonColor = new Color4D(r, g, b, a);
		polygonColors.add(polygonColor);
	}
	
	public void addPolygon(short vertexIndex1, short vertexIndex2, short vertexIndex3,
			short normalIndex1, short normalIndex2, short normalIndex3,
			short otherIndex1,  short otherIndex2,  short otherIndex3) {
		Polygon polygon = new Polygon();
		polygon.vertexIndex1 = vertexIndex1;
		polygon.vertexIndex2 = vertexIndex2;
		polygon.vertexIndex3 = vertexIndex3;
		polygon.normalIndex1 = normalIndex1;
		polygon.normalIndex2 = normalIndex2;
		polygon.normalIndex3 = normalIndex3;
		polygon.otherIndex1 = otherIndex1;
		polygon.otherIndex2 = otherIndex2;
		polygon.otherIndex3 = otherIndex3;
		polygons.add(polygon);
	}
	
	public void addPolygonGroup(PolygonGroup polygonGroup) {
		polygonGroups.add(polygonGroup);
	}
	
	public List<Point3D> getVertices() {
		return this.vertices;
	}
	
	public List<Point3D> getNormals() {
		return this.normals;
	}
	
	public List<Point2D> getTextureCoordinates() {
		return this.textureCoordinates;
	}
	
	public List<Polygon> getPolygons() {
		return this.polygons;
	}
	
	public List<Color4D> getPolygonColors() {
		return this.polygonColors;
	}
	
	public List<Color4D> getVertexColors() {
		return this.vertexColors;
	}
	
	public List<PolygonGroup> getPolygonGroups() {
		return this.polygonGroups;
	}
}

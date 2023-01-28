package com.kujatas.model;

import static javax.media.j3d.GeometryArray.COLOR_3;
import static javax.media.j3d.GeometryArray.COLOR_4;
import static javax.media.j3d.GeometryArray.COORDINATES;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


import com.kujatas.model.loader.TexLoader;
import com.sun.j3d.utils.image.TextureLoader;

public class TriangleMesh extends Shape3D {
	
	private TriangleArray triangleArray;
	
	public TriangleMesh(Model model, int groupIndex, List<String> textureFilenames) throws KjataException {
		PolygonGroup group = model.getPolygonGroups().get(groupIndex);
		List<Color4D> triangleColors = model.getPolygonColors();
		List<Polygon> triangles = model.getPolygons();
		int numTriangles = group.numPolysInGroup;
		int totalPoints = numTriangles * 3;
		if (group.isTextureUsed > 0) {
			triangleArray = new TriangleArray(totalPoints, COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		} else {
			triangleArray = new TriangleArray(totalPoints, COORDINATES | COLOR_4);
		}
		// create geometry
	    Point3f[] points = new Point3f[totalPoints];
	    Vector3f[] normals = new Vector3f[totalPoints];
	    Color4f colors[] = new Color4f[totalPoints];
	    int i=0;
	    Map<Integer, Integer> vertexArrayIndexMap = new HashMap<Integer, Integer>();
	    for (int t=0; t<numTriangles; t++) {
	    	int polygonIndex = group.offsetPolyIndex + t;
	    	Polygon triangle = triangles.get(polygonIndex);
	    	Color4D triangleColor = triangleColors.get(polygonIndex);
	    	Point3D vertex1 = model.getVertices().get(triangle.vertexIndex1 + group.offsetVertexIndex);
	    	Point3D vertex2 = model.getVertices().get(triangle.vertexIndex2 + group.offsetVertexIndex);
	    	Point3D vertex3 = model.getVertices().get(triangle.vertexIndex3 + group.offsetVertexIndex);
	    	Point3f point1 = new Point3f(vertex1.x, vertex1.y, vertex1.z);
	    	Point3f point2 = new Point3f(vertex2.x, vertex2.y, vertex2.z);
	    	Point3f point3 = new Point3f(vertex3.x, vertex3.y, vertex3.z);
			short r = (short)(triangleColor.r & 0xFF);
			short g = (short)(triangleColor.g & 0xFF);
			short b = (short)(triangleColor.b & 0xFF);
			short a = (short)(triangleColor.a & 0xFF);
			Color4f color = new Color4f(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
			if (group.isTextureUsed > 0) {
				color = new Color4f(1.0f, 0.0f, 0.0f, 1.0f);
			}
			if (vertexArrayIndexMap.get(i) == null) { vertexArrayIndexMap.put(i, triangle.vertexIndex1); }
			if (vertexArrayIndexMap.get(i+1) == null) { vertexArrayIndexMap.put(i+1, triangle.vertexIndex2); }
			if (vertexArrayIndexMap.get(i+2) == null) { vertexArrayIndexMap.put(i+2, triangle.vertexIndex3); }
	    	points[i] = point1;
	    	points[i+1] = point2;
	    	points[i+2] = point3;
	    	//normals[i] = normal1;
	    	//normals[i+1] = normal2;
	    	//normals[i+2] = normal3;
	    	colors[i] = color;
	    	colors[i+1] = color;
	    	colors[i+2] = color;
	    	i += 3;
	    }
	    triangleArray.setCoordinates(0, points);
	    // triangleArray.setNormals(0, normals);
		if (group.isTextureUsed == 0) {
		    triangleArray.setColors(0, colors);
		}
	    
	    if (group.isTextureUsed > 0) {
	    	List<Point2D> textureCoordinates = model.getTextureCoordinates();
	    	// TODO: Optimize this section
	    	float maxX = Float.MIN_VALUE;
	    	float maxY = Float.MIN_VALUE;
	    	for (int v=0; v<totalPoints; v++) {
	    		int textureCoordinateIndex = vertexArrayIndexMap.get(v) + group.offsetTextureCoordinateIndex;
	    		Point2D textureCoordinateRaw = textureCoordinates.get(textureCoordinateIndex);
	    		if (textureCoordinateRaw.x > maxX) {
	    			maxX = textureCoordinateRaw.x;
	    		}
	    		if (textureCoordinateRaw.y > maxY) {
	    			maxY = textureCoordinateRaw.y;
	    		}
	    	}
	    	float xOffset = 0.0f;
	    	float yOffset = 0.0f;
	    	if (maxX > 1.0) {
	    		xOffset = (float)Math.floor(maxX);
	    	}
	    	if (maxY > 1.0) {
	    		yOffset = (float)Math.floor(maxY);
	    	}
	    	// END of section to be optimized
	    	for (int v=0; v<totalPoints; v++) {
	    		int textureCoordinateIndex = vertexArrayIndexMap.get(v) + group.offsetTextureCoordinateIndex;
	    		Point2D textureCoordinateRaw = textureCoordinates.get(textureCoordinateIndex);
	    		Point2f textureCoordinate = new Point2f(textureCoordinateRaw.x - xOffset, 1.0f-(textureCoordinateRaw.y-yOffset));
	    		System.out.println("Setting vertex " + v + " to textureCoordinate " + textureCoordinate);
	    		triangleArray.setTextureCoordinate(v, textureCoordinate); // before setGeometry()
	    	}
	    }
	    
	    setGeometry(triangleArray);
	    // create appearance
	    Appearance app = new Appearance();
	    PolygonAttributes pa = new PolygonAttributes();
	    pa.setCullFace(PolygonAttributes.CULL_NONE); // visible from both sides
	    app.setPolygonAttributes(pa);
	    if (group.isTextureUsed > 0) {
	    	int textureIndex = group.textureIndex;
	    	String textureBaseFilename = textureFilenames.get(textureIndex);
	    	//String textureFilename = FF7File.BASE_TEXTURE_DIRECTORY + textureBaseFilename + ".png";
	    	//System.out.println("Adding texture: " + textureFilename);
	    	//TextureLoader textureLoader = new TextureLoader(textureFilename, null);
	    	//ImageComponent2D image = textureLoader.getImage();
	    	BufferedImage image = TexLoader.getInstance().load(textureBaseFilename);
	    	ImageComponent2D imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, image);
	    	Texture texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
	    	texture.setImage(0, imageComponent); // 0 means no mip mapping
	    	app.setTexture(texture);
	    	//TextureAttributes textureAttributes = new TextureAttributes();
	    	//textureAttributes.setTextureMode(TextureAttributes.MODULATE);
	    	//app.setTextureAttributes(textureAttributes);
	    	TransparencyAttributes ta = new TransparencyAttributes();
	    	ta.setTransparencyMode(TransparencyAttributes.NICEST);
	    	ta.setTransparency(0.0f);
	    	app.setTransparencyAttributes(ta); // uncomment to re-enable transparency
	    }
	    setAppearance(app);
	}
}

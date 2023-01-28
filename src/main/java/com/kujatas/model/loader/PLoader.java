package com.kujatas.model.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.kujatas.model.FF7File;
import com.kujatas.model.KjataException;
import com.kujatas.model.Model;
import com.kujatas.model.Point2D;
import com.kujatas.model.Point3D;
import com.kujatas.model.PolygonGroup;


public class PLoader {
	
	public PLoader() {
	}
	
	public Model load(String baseFilename) throws KjataException {
		try {
			File pFile = new File(FF7File.BASE_DIRECTORY + baseFilename + ".p");
			Model model = new Model();
			byte[] fileContents = new byte[16000];
			FileInputStream fileInputStream = new FileInputStream(pFile);
			int fileSizeBytes = fileInputStream.read(fileContents);
			ByteBuffer in = ByteBuffer.allocate(fileSizeBytes);
			in.order(ByteOrder.LITTLE_ENDIAN);
			in.put(fileContents, 0, fileSizeBytes);
			in.rewind();
			in.getInt(); // unknown tag
			in.getInt(); // unknown tag
			int doVerticesHaveColor = in.getInt();
			int numVertices = in.getInt();
			int numNormals = in.getInt();
			in.getInt(); // unknown tag
			int numTextureCoords = in.getInt();
			int numNormalIndices = in.getInt();
			int numEdges = in.getInt();
			int numPolygons = in.getInt();
			in.getInt(); // unknown tag
			in.getInt(); // unknown tag
			int numHundreds = in.getInt();
			int numGroups = in.getInt();
			in.getInt(); // unknown tag
			in.getInt(); // unknown tag
			for (int i=0; i<16; i++) {
				in.getInt(); // read 16 more unknown 4-byte values
			}
			/*
			System.out.println("doVerticesHaveColor = " + doVerticesHaveColor);
			System.out.println("numVertices = " + numVertices);
			System.out.println("numNormals = " + numNormals);
			System.out.println("numTextureCoords = " + numTextureCoords);
			System.out.println("numNormalIndices = " + numNormalIndices);
			System.out.println("numEdges = " + numEdges);
			System.out.println("numPolygons = " + numPolygons);
			System.out.println("numHundreds = " + numHundreds);
			System.out.println("numGroups = " + numGroups);
			*/
			for (int i=0; i<numVertices; i++) {
				float x = in.getFloat();
				float y = in.getFloat();
				float z = in.getFloat();
				model.addVertex(x, y, z);
			}
			for (int i=0; i<numNormals; i++) {
				float x = in.getFloat();
				float y = in.getFloat();
				float z = in.getFloat();
				model.addNormal(x, y, z);
			}
			for (int i=0; i<numTextureCoords; i++) {
				float x = in.getFloat();
				float y = in.getFloat();
				Point2D textureCoordinate = new Point2D(x, y);
				//System.out.println("TextureCoordinate[" + i + "] = " + textureCoordinate);
				model.addTextureCoordinate(x, y);
			}
			for (int i=0; i<numVertices; i++) {
				byte b = in.get();
				byte g = in.get();
				byte r = in.get();
				byte a = in.get();
				model.addVertexColor(r, g, b, a);
			}
			for (int i=0; i<numPolygons; i++) {
				byte b = in.get();
				byte g = in.get();
				byte r = in.get();
				byte a = in.get();
				model.addPolygonColor(r, g, b, a);
			}
			for (int i=0; i<numEdges; i++) {
				short vertexIndex1 = in.getShort();
				short vertexIndex2 = in.getShort();
			}
			for (int i=0; i<numPolygons; i++) {
				in.getShort(); // unknown tag
				short vertexIndex1 = in.getShort();
				short vertexIndex2 = in.getShort();
				short vertexIndex3 = in.getShort();
				short normalIndex1 = in.getShort();
				short normalIndex2 = in.getShort();
				short normalIndex3 = in.getShort();
				// not sure what these "other" indexes are? doc said maybe edge index? i'm thinking possibly a texture coordinate index?
				short otherIndex1 = in.getShort();
				short otherIndex2 = in.getShort();
				short otherIndex3 = in.getShort();
				in.getInt(); // unknown tag
				model.addPolygon(vertexIndex1, vertexIndex2, vertexIndex3,
						normalIndex1, normalIndex2, normalIndex3,
						otherIndex1, otherIndex2, otherIndex3);
				/*
				System.out.println("polygon " + i + ": " +
						"  V[" + vertexIndex1 + "]:V[" + vertexIndex2 + "]:V[" + vertexIndex3 + "] " +
						"  N[" + normalIndex1 + "]:N[" + normalIndex2 + "]:N[" + normalIndex3 + "] " +
						"  E[" + edgeIndex1   + "]:E[" + edgeIndex2   + "]:E[" + edgeIndex3 + "]");
				*/
			}
			for (int i=0; i<numHundreds; i++) {
				// TODO: Find out what these mean.
				int 	off00 = in.getInt();	// 0x00000001
				int 	off04 = in.getInt();	// 0x00000001
				byte	off08 = in.get();      	// 0x00
				byte	off09 = in.get();   	// 0x82		0x86		0x86
				short	off0a = in.getShort();	// 0x0003
				byte	off0c = in.get();   	// 0x02
				byte	off0d = in.get();   	// 0x00		0x04		0x04
				short	off0e = in.getShort();	// 0x0002
				int 	off10 = in.getInt();	// 0x00000000	0x00000000	0x00000001
				int 	off14 = in.getInt();	// 0x00000000
				int 	off18 = in.getInt();	// 0x00000000	0x00000001	0x00000001
				int 	off1c = in.getInt();	// 0x00000000	0x00000025	0x00000025
				byte	off20 = in.get();   	// 0x00		x00 x40 x80	x00 x40 x80
				byte	off21 = in.get();   	// 0x00		0x78		0x78
				short	off22 = in.getShort();	// 0x0000
				int 	off24 = in.getInt();	// 0x00000002	0x00000001	0x00000001
				int 	off28 = in.getInt();	// 0xFFFFFFFF
				int 	off2c = in.getInt();	// 0x00000000
				int 	off30 = in.getInt();	// 0x00000000
				int 	off34 = in.getInt();	// 0x00000002	0x00000005	0x00000005
				int 	off38 = in.getInt();	// 0x00000001	0x00000006	0x00000006
				int 	off3c = in.getInt();	// 0x00000002
				int 	off40 = in.getInt();	// 0x00000000
				int 	off44 = in.getInt();	// 0x00000004	0x00000000	0x00000000
				int 	off48 = in.getInt();	// 0x00000000
				int 	off4c = in.getInt();	// 0x00000000
				int 	off50 = in.getInt();	// 0x00000000
				int 	off54 = in.getInt();	// 0x00000000
				int 	off58 = in.getInt();	// 0x00000000
				int 	off5c = in.getInt();	// 0x000000FF	0x00000080	0x00000080
				int 	off60 = in.getInt();	// 0x00000000
				/*
				System.out.println("HundredInfo[" + off00 + " " + off04 + " " + off08 + " " + off09 + " " + off0a + " " + off0c + " " + off0d + " " +
						off0e + " " + off10 + " " + off14 + " " + off18 + " " + off1c + " " + off20 + " " + off21 + " " + off22 + " " + off24 + " " +
						off28 + " " + off2c + " " + off30 + " " + off34 + " " + off38 + " " + off3c + " " + off40 + " " + off44 + " " + off48 + " " + 
						off4c + " " + off50 + " " + off54 + " " + off58 + " " + off5c + " " + off60);
				*/
			}
			for (int i=0; i<numGroups; i++) {
				PolygonGroup group = new PolygonGroup();
				group.polygonType = in.getInt();
				group.offsetPolyIndex = in.getInt();
				group.numPolysInGroup = in.getInt();
				group.offsetVertexIndex = in.getInt();
				group.numVerticesInGroup = in.getInt();
				group.offsetEdgeIndex = in.getInt();
				in.getInt(); // unknown tag (thought to be numEdges but it's always zero)
				int u1 = in.getInt(); // unknown tag
				int u2 = in.getInt(); // unknown tag
				int u3 = in.getInt(); // unknown tag
				int u4 = in.getInt(); // unknown tag
				//System.out.println("u1=" + u1 + ", u2=" + u2 + ", u3=" + u3 + ", u4=" + u4);
				group.offsetTextureCoordinateIndex = in.getInt();
				group.isTextureUsed = in.getInt();
				group.textureIndex = in.getInt();
				model.addPolygonGroup(group);
				//System.out.println("Group " + i + ": " + group);
			}
			in.getInt(); // these four bytes are 0/skipped for some reason
			float maxX = in.getFloat();
			float maxY = in.getFloat();
			float maxZ = in.getFloat();
			float minX = in.getFloat();
			float minY = in.getFloat();
			float minZ = in.getFloat();
			Point3D maxPoint = new Point3D(maxX, maxY, maxZ);
			Point3D minPoint = new Point3D(minX, minY, minZ);
			//System.out.println("boundingBox = " + maxPoint + ":" + minPoint);
			// Normal Index Table
			for (int i=0; i<numNormalIndices; i++) {
				int ithNormalIndex = in.getInt();
				//System.out.println("Vertex #" + i + " uses Normal #" + ithNormalIndex);
			}
			if (in.position() != fileSizeBytes) {
				//System.out.println("WARNING: Did not reach end of file data!");
			}
			return model;
		} catch (IOException e) {
			throw new KjataException("IOException while reading P file.", e);
		}
	}
	
	public static void main(String[] args) {
		try {
			PLoader pLoader = new PLoader();
			// aaba = cloud's head, aahb = tifa's head
			String baseFilename = "cnfd";
			Model model = pLoader.load(baseFilename);
			System.out.println(model.toString());
			System.out.println("size of normals = " + model.getNormals().size());
		} catch (KjataException e) {
			System.out.println(e.toString());
		}
	}
}

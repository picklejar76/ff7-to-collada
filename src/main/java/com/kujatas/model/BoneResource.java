package com.kujatas.model;

import java.util.ArrayList;
import java.util.List;

public class BoneResource {
	
	private String polygonFilename;
	private List<String> textureBaseFilenames;
	
	public BoneResource(String polygonFilename) {
		this.polygonFilename = polygonFilename;
		this.textureBaseFilenames = new ArrayList<String>();
	}
	
	public void addTextureBaseFilename(String textureBaseFilename) {
		this.textureBaseFilenames.add(textureBaseFilename);
	}
	
	public String getPolygonFilename() {
		return this.polygonFilename;
	}
	
	public List<String> getTextureBaseFilenames() {
		return this.textureBaseFilenames;
	}
	
	@Override
	public String toString() {
		return BoneResource.class.getSimpleName() + "[polygonFilename=" + polygonFilename + ", textureFilenames=" + textureBaseFilenames + "]";
	}
}

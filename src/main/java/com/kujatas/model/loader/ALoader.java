package com.kujatas.model.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import com.kujatas.model.Animation;
import com.kujatas.model.AnimationFrame;
import com.kujatas.model.FF7File;
import com.kujatas.model.KjataException;
import com.kujatas.model.Point3D;


public class ALoader {
	
	private static ALoader INSTANCE;
	
	private Map<String, Animation> cache;
	
	private ALoader() {
		this.cache = new HashMap<String, Animation>();
	}
	
	public static ALoader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ALoader();
		}
		return INSTANCE;
	}
	
	public Animation load(String baseFilename) throws KjataException {
		Animation cachedAnimation = cache.get(baseFilename);
		if (cachedAnimation != null) {
			return cachedAnimation;
		}
		try {
			File aFile = new File(FF7File.BASE_DIRECTORY + baseFilename + ".a");
			Animation animation = new Animation();
			byte[] fileContents = new byte[16000];
			FileInputStream fileInputStream = new FileInputStream(aFile);
			int numBytes = fileInputStream.read(fileContents);
			//System.out.println("numBytes = " + numBytes);
			ByteBuffer in = ByteBuffer.allocate(numBytes);
			in.order(ByteOrder.LITTLE_ENDIAN);
			in.put(fileContents, 0, numBytes);
			in.rewind();
			animation.version = in.getInt();
			animation.numFrames = in.getInt();
			animation.numBones = in.getInt();
			animation.rotationOrder1 = in.get();
			animation.rotationOrder2 = in.get();
			animation.rotationOrder3 = in.get();
			in.get(); // unused
			in.getInt();
			in.getInt();
			in.getInt();
			in.getInt();
			in.getInt();
			for (int i=0; i<animation.numFrames; i++) {
				AnimationFrame animationFrame = new AnimationFrame();
				// System.out.println("FRAME " + i + ":");
				float rootRotX = in.getFloat();
				float rootRotY = in.getFloat();
				float rootRotZ = in.getFloat();
				float rootTrnX = in.getFloat();
				float rootTrnY = in.getFloat();
				float rootTrnZ = in.getFloat();
				animationFrame.rootRotation = new Point3D(rootRotX, rootRotY, rootRotZ);
				animationFrame.rootTranslation = new Point3D(rootTrnX, rootTrnY, rootTrnZ);
				//System.out.println("version = " + animation.version);
				//System.out.println("numFrames = " + animation.numFrames);
				//System.out.println("numBones = " + animation.numBones);
				//System.out.println("rotationOrder = " + animation.rotationOrder1);
				//System.out.println("rootRotation = " + animationFrame.rootRotation);
				//System.out.println("rootTranslation = " + animationFrame.rootTranslation);
				for (int j=0; j<animation.numBones; j++) {
					float rotX = in.getFloat();
					float rotY = in.getFloat();
					float rotZ = in.getFloat();
					Point3D boneRotation = new Point3D(rotX, rotY, rotZ);
					animationFrame.boneRotations.add(boneRotation);
					// System.out.println(" bone " + j + " rotation = " + boneRotation);
				}
				animation.animationFrames.add(animationFrame);
			}
			// System.out.println("final position = " + in.position());
			// System.out.println("Full animation contents = " + animation.toString());
			cache.put(baseFilename, animation);
			fileInputStream.close();
			return animation;
		} catch (IOException e) {
			throw new KjataException("IOException while reading A file.", e);
		}
	}
	
	public static void main(String[] args) {
		try {
			ALoader aLoader = ALoader.getInstance();
			// aaga.a = cloud running
			// aafe.a = cloud standing still
			String baseFilename = "aafe";
			Animation animation = aLoader.load(baseFilename);
			System.out.println("animation=" + animation);
		} catch (KjataException e) {
			System.out.println(e.toString());
		}
	}
}

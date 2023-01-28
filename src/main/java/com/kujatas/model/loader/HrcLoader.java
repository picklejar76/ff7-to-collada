package com.kujatas.model.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.kujatas.model.Bone;
import com.kujatas.model.FF7File;
import com.kujatas.model.KjataException;
import com.kujatas.model.Skeleton;

public class HrcLoader {
	
	private static String FIRST_LINE = ":HEADER_BLOCK 2";
	private static String SKELETON_LINE_PREFIX = ":SKELETON "; // final space IS needed
	private static String BONES_LINE_PREFIX = ":BONES "; // final space IS needed
	
	public HrcLoader() {
	}

	// read the next line (that's not a comment line)
	public String readNextLine(BufferedReader in) throws IOException {
		String line = in.readLine();
		while (line.startsWith("#")) {
			line = in.readLine();
		}
		return line;
	}
	
	public List<String> parseRsdBaseFilenames(String rsdDescription) throws KjataException {
		// rsdDescription is something like one of these:
		//   "0"
		//   "1 ZYXW"
		//   "12 BZIA BZIC BZIE BZJA BZJC BZJE CAAA CAAC CAAE CABA CABC CABE"
		String[] tokens = rsdDescription.split("\\W+");
		if (tokens == null || tokens.length == 0 || tokens[0] == "0") {
			return new ArrayList<String>();
		}
		try {
			List<String> rsdBaseFilenames = new ArrayList<String>();
			for (int i=1; i<tokens.length; i++) {
				rsdBaseFilenames.add(tokens[i]);
			}
			int expectedCount = Integer.parseInt(tokens[0]);
			if (expectedCount != rsdBaseFilenames.size()) {
				throw new KjataException("rsdDescription not valid, count did not match: " + rsdDescription, null);
			}
			return rsdBaseFilenames;
		} catch (Exception e) {
			throw new KjataException("Unexpected Exception while parsing rsdDescription: " + rsdDescription, e);
		}

	}
	
	public Skeleton load(String baseFilename) throws KjataException {
		try {
			File hrcFile = new File(FF7File.BASE_DIRECTORY + baseFilename + ".hrc");
			BufferedReader in = new BufferedReader(new FileReader(hrcFile));			
			String line = readNextLine(in);
			if (!line.startsWith(FIRST_LINE)) {
				throw new KjataException("Expected first line to be: " + FIRST_LINE, null);
			}
			line = readNextLine(in);
			if (!line.startsWith(SKELETON_LINE_PREFIX)) {
				throw new KjataException("Expected second line to start with: " + SKELETON_LINE_PREFIX, null);
			}
			String skeletonName = line.substring(SKELETON_LINE_PREFIX.length());
			Skeleton skeleton = new Skeleton(skeletonName);
			line = readNextLine(in);
			if (!line.startsWith(BONES_LINE_PREFIX)) {
				throw new KjataException("Expected third line to start with: " + BONES_LINE_PREFIX, null);
			}
			int numBones = Integer.parseInt(line.substring(BONES_LINE_PREFIX.length()));
			if (numBones == 0) {
				numBones = 1; // when it says 0, it really means 1 bone with name "null" and no children
			}
			for (int boneIndex=0; boneIndex<numBones; boneIndex++) {
				line = readNextLine(in);
				if (line.length() > 0) {
					throw new KjataException("Expected blank line", null);
				}
				String boneName = readNextLine(in);
				String parentBoneName = readNextLine(in);
				double boneLength = Double.parseDouble(readNextLine(in));
				String rsdDescription = readNextLine(in);
				List<String> rsdBaseFilenames = parseRsdBaseFilenames(rsdDescription);
				Bone bone = new Bone(boneIndex, boneName, parentBoneName, boneLength, rsdBaseFilenames);
				skeleton.addBone(bone);
			}
			return skeleton;
		} catch (IOException e) {
			throw new KjataException("IOException while reading HRC file.", e);
		}
	}
	
	public static void main(String[] args) {
		try {
			HrcLoader hrcLoader = new HrcLoader();
			// aaaa = cloud, aagb = tifa
			String hrcBaseFilename = "bzhf";
			Skeleton skeleton = hrcLoader.load(hrcBaseFilename);
			System.out.println(skeleton.toString());
			HashMap<String, Bone> boneMap = new HashMap<String, Bone>();
			for (int boneIndex=0; boneIndex<skeleton.getBones().size(); boneIndex++) {
				Bone bone = skeleton.getBones().get(boneIndex);
				boneMap.put(bone.getName(), bone);
			}
			for (int boneIndex=0; boneIndex<skeleton.getBones().size(); boneIndex++) {
				Bone bone = skeleton.getBones().get(boneIndex);
				Bone parentBone = boneMap.get(bone.getParentBoneName());
				int parentBoneIndex = parentBone == null ? -1 : parentBone.getBoneIndex();
				String parentBoneName = parentBone == null ? "ROOT" : parentBone.getName();
				System.out.println(bone.getBoneIndex() + "\t" + bone.getName() + "\t" + parentBoneIndex + "\t" + parentBoneName);
			}
		} catch (KjataException e) {
			System.out.println(e.toString());
		}
	}
}

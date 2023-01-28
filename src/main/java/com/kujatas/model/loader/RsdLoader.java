package com.kujatas.model.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.kujatas.model.BoneResource;
import com.kujatas.model.FF7File;
import com.kujatas.model.KjataException;


public class RsdLoader {
	
	private static String FIRST_LINE = "@RSD";
	private static String PLY_LINE_PREFIX = "PLY=";
	private static String MAT_LINE_PREFIX = "MAT=";
	private static String GRP_LINE_PREFIX = "GRP=";
	private static String NTEX_LINE_PREFIX = "NTEX=";
	
	public RsdLoader() {
		
	}
	
	public BoneResource load(String baseFilename) throws KjataException {
		try {
			File hrcFile = new File(FF7File.BASE_DIRECTORY + baseFilename + ".rsd");
			//System.out.println("Loading: " + hrcFile.getAbsolutePath());
			BufferedReader in = new BufferedReader(new FileReader(hrcFile));			
			String line = readLineSkipCommentLine(in);
			if (!line.startsWith(FIRST_LINE)) {
				throw new KjataException("Expected line to start with: " + FIRST_LINE, null);
			}
			line = readLineSkipCommentLine(in);
			if (!line.startsWith(PLY_LINE_PREFIX)) {
				throw new KjataException("Expected line to start with: " + PLY_LINE_PREFIX, null);
			}
			String plyFile = removeFilenameExtension(line.substring(PLY_LINE_PREFIX.length()));
			line = readLineSkipCommentLine(in);
			if (!line.startsWith(MAT_LINE_PREFIX)) {
				throw new KjataException("Expected line to start with: " + MAT_LINE_PREFIX, null);
			}
			String matFile = removeFilenameExtension(line.substring(MAT_LINE_PREFIX.length()));
			if (!matFile.equals(plyFile)) {
				throw new KjataException("Expected MAT file to be same as PLY file", null);
			}
			line = readLineSkipCommentLine(in);
			if (!line.startsWith(GRP_LINE_PREFIX)) {
				throw new KjataException("Expected line to start with: " + GRP_LINE_PREFIX, null);
			}
			String grpFile = removeFilenameExtension(line.substring(GRP_LINE_PREFIX.length()));
			if (!grpFile.equals(plyFile)) {
				throw new KjataException("Expected GRP file to be same as PLY file", null);
			}
			line = readLineSkipCommentLine(in);
			if (!line.startsWith(NTEX_LINE_PREFIX)) {
				throw new KjataException("Expected line to start with: " + NTEX_LINE_PREFIX, null);
			}
			BoneResource resource = new BoneResource(plyFile);
			Integer numTextures = Integer.parseInt(line.substring(NTEX_LINE_PREFIX.length()));
			for (int i=0; i<numTextures; i++) {
				String expectedPrefix = "TEX[" + i + "]=";
				line = readLineSkipCommentLine(in);
				if (!line.startsWith(expectedPrefix)) {
					throw new KjataException("Expected line to start with: " + expectedPrefix, null);
				}
				String nthTextureFilename = removeFilenameExtension(line.substring(expectedPrefix.length()));
				resource.addTextureBaseFilename(nthTextureFilename);
			}
			return resource;
		} catch (IOException e) {
			throw new KjataException("IOException while reading RSD file.", e);
		}
	}
	
	public String readLineSkipCommentLine(BufferedReader in) throws IOException {
		String line = "#";
		while (line.startsWith("#")) {
			line = in.readLine();
		}
		return line;
	}
	
	private String removeFilenameExtension(String filenameWithExtension) {
		int periodPosition = filenameWithExtension.indexOf(".");
		return filenameWithExtension.substring(0, periodPosition);
	}
	
	public static void main(String[] args) {
		try {
			RsdLoader rsdLoader = new RsdLoader();
			// aaaf = cloud's head, aaha = tifa's head
			String baseFilename = "aaha";
			BoneResource resource = rsdLoader.load(baseFilename);
			System.out.println(resource.toString());
		} catch (KjataException e) {
			System.out.println(e.toString());
		}
	}
}

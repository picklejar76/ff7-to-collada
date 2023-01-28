package com.kujatas.model.loader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.kujatas.model.Animation;
import com.kujatas.model.AnimationFrame;
import com.kujatas.model.FF7File;
import com.kujatas.model.KjataException;
import com.kujatas.model.Point3D;


public class TexLoader {
	
	private static TexLoader INSTANCE;
	
	//private Map<String, Animation> cache;
	
	private TexLoader() {
		//this.cache = new HashMap<String, Animation>();
	}
	
	public static TexLoader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TexLoader();
		}
		return INSTANCE;
	}
	
	public BufferedImage load(String baseFilename) throws KjataException {
		//Animation cachedAnimation = cache.get(baseFilename);
		//if (cachedAnimation != null) {
			//return cachedAnimation;
		//}
		try {
			File aFile = new File(FF7File.BASE_DIRECTORY + baseFilename + ".tex");
			//Animation animation = new Animation();
			byte[] fileContents = new byte[16000];
			FileInputStream fileInputStream = new FileInputStream(aFile);
			int numBytes = fileInputStream.read(fileContents);
			ByteBuffer in = ByteBuffer.allocate(numBytes);
			in.order(ByteOrder.LITTLE_ENDIAN);
			in.put(fileContents, 0, numBytes);
			in.rewind();
			int version = in.getInt();
			int off04 = in.getInt();
			int colorKeyFlag = in.getInt();
			int off0c = in.getInt();
			int off10 = in.getInt();
			int minBitsPerColor = in.getInt();
			int maxBitsPerColor = in.getInt();
			int minAlphaBits = in.getInt();
			int maxAlphaBits = in.getInt();
			int minBitsPerPixel = in.getInt();
			int maxBitsPerPixel = in.getInt();
			int off2c = in.getInt();
			int numPalettes = in.getInt();
			int numColorsPerPalette = in.getInt();
			int bitDepth = in.getInt();
			int imageWidth = in.getInt();
			int imageHeight = in.getInt();
			int pitch = in.getInt(); // bytes per row, usually ignored and assumed to be bytes per pixel * width
			int off48 = in.getInt();
			int hasPalette = in.getInt();
			int bitsPerIndex = in.getInt(); // always zero for non-paletted images
			int indexedTo8BitFlag = in.getInt(); // always zero for FF7
			int paletteSize = in.getInt(); // always numPalettes * numColorsPerPalette
			int alternateNumColorsPerPalette = in.getInt(); // sometimes zero, just ignore it
			int runTimeData = in.getInt(); // ignored on load
			int bitsPerPixel = in.getInt();
			int bytesPerPixel = in.getInt(); // if 1, read 1 byte per pixel, regardless of bit depth
			/*
			System.out.println("version=" + version);
			System.out.println("colorKeyFlag=" + colorKeyFlag);
			System.out.println("minBitsPerColor=" + minBitsPerColor);
			System.out.println("maxBitsPerColor=" + maxBitsPerColor);
			System.out.println("minAlphaBits=" + minAlphaBits);
			System.out.println("maxAlphaBits=" + maxAlphaBits);
			System.out.println("minBitsPerPixel=" + minBitsPerPixel);
			System.out.println("maxBitsPerPixel=" + maxBitsPerPixel);
			System.out.println("numPalettes=" + numPalettes);
			System.out.println("numColorsPerPalette=" + numColorsPerPalette);
			System.out.println("bitDepth=" + bitDepth);
			System.out.println("imageWidth=" + imageWidth);
			System.out.println("imageHeight=" + imageHeight);
			System.out.println("pitch=" + pitch); // bytes per row, usually ignored and assumed to be bytes per pixel * width
			System.out.println("hasPalette=" + hasPalette);
			System.out.println("bitsPerIndex=" + bitsPerIndex); // always zero for non-paletted images
			System.out.println("indexedTo8BitFlag=" + indexedTo8BitFlag); // always zero for FF7
			System.out.println("paletteSize=" + paletteSize); // always numPalettes * numColorsPerPalette
			System.out.println("alternateNumColorsPerPalette=" + alternateNumColorsPerPalette); // sometimes zero, just ignore it
			System.out.println("runTimeData=" + String.format("%x", runTimeData)); // ignored on load
			System.out.println("bitsPerPixel=" + bitsPerPixel);
			System.out.println("bytesPerPixel=" + bytesPerPixel);
			*/
			// START OF PIXEL FORMAT
			int numRedBits = in.getInt();
			int numGreenBits = in.getInt();
			int numBlueBits = in.getInt();
			int numAlphaBits = in.getInt();
			int redBitMask = in.getInt();
			int greenBitMask = in.getInt();
			int blueBitMask = in.getInt();
			int alphaBitMask = in.getInt();
			int redShift = in.getInt();
			int greenShift = in.getInt();
			int blueShift = in.getInt();
			int alphaShift = in.getInt();
			int altNumRedBits = in.getInt();
			int altNumGreenBits = in.getInt();
			int altNumBlueBits = in.getInt();
			int altNumAlphaBits = in.getInt();
			int redMax = in.getInt();
			int greenMax = in.getInt();
			int blueMax = in.getInt();
			int alphaMax = in.getInt();
			// END OF PIXEL FORMAT
			int hasColorKeyArray = in.getInt();
			int runtimeData2 = in.getInt();
			int referenceAlpha = in.getInt();
			int runtimeData3 = in.getInt();
			int unknown = in.getInt();
			int paletteIndex = in.getInt(); // runtime data
			int runtimeData4 = in.getInt();
			int runtimeData5 = in.getInt();
			int unknown1 = in.getInt();
			int unknown2 = in.getInt();
			int unknown3 = in.getInt();
			int unknown4 = in.getInt();
			/*
			System.out.println("hasColorKeyArray=" + hasColorKeyArray);
			System.out.println("runtimeData2=" + runtimeData2);
			System.out.println("referenceAlpha=" + referenceAlpha);
			System.out.println("runtimeData3=" + runtimeData3);
			System.out.println("unknown=" + unknown);
			System.out.println("paletteIndex=" + paletteIndex);
			System.out.println("runtimeData4=" + String.format("%x", runtimeData4));
			System.out.println("runtimeData5=" + runtimeData5);
			System.out.println("unknown1=" + unknown1);
			System.out.println("unknown2=" + unknown2);
			System.out.println("unknown3=" + unknown3);
			System.out.println("unknown4=" + unknown4);
			*/
			int[] argb = new int[paletteSize];
			if (hasPalette == 1) {
				for (int i=0; i<paletteSize; i++) {
					byte b = in.get();
					byte g = in.get();
					byte r = in.get();
					byte a = in.get();
					argb[i] = (a<<24)|(r<<16)|(g <<8)|b;
					/*
					int rr = (r & 0xff);
					int gg = (g & 0xff);
					int bb = (b & 0xff);
					int aa = (a & 0xff);
					System.out.println("Color " + i + " = (" + rr + ", " + gg + ", " + bb + ", " + aa + ")");
					*/
				}
			}
			int numPixels = imageHeight * imageWidth;
			int[] pixelArray = new int[numPixels];
			int idx = 0;
			if (bytesPerPixel == 1) {
				for (int y=0; y<imageHeight; y++) {
					for (int x=0; x<imageWidth; x++) {
						byte i = in.get();
						//System.out.print(String.format("%03d ", i));
						pixelArray[idx++] = argb[i];
					}
					//System.out.println();
				}
			} else {
				//throw new KjataException("Unsupported bytesPerPixel=" + bytesPerPixel, null);
			}
			return convertArgbArrayToImage(imageWidth, imageHeight, pixelArray);
			// System.out.println("Full animation contents = " + animation.toString());
			//cache.put(baseFilename, animation);
			//return animation;
		} catch (IOException e) {
			throw new KjataException("IOException while reading TEX file.", e);
		}
	}
	
	private BufferedImage convertArgbArrayToImage(int w, int h, int[] argbPixelArray) throws IOException {
		BufferedImage finalImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);	
		finalImage.setRGB(0, 0, w, h, argbPixelArray, 0 ,w);
		return finalImage;
	}
	
	public static void main(String[] args) throws IOException {
		try {
			TexLoader texLoader = TexLoader.getInstance();
			// aabb.tex = cloud's left eye
			// aabc.tex = cloud's right eye
			// aabd.tex = cloud's mouth
			// aahc.tex = tifa's
			// aahd.tex = tifa's
			// aahe.tex = tifa's
			String baseFilename = "aabb";
			BufferedImage finalImage = texLoader.load(baseFilename);
			ImageIO.write(finalImage, "png", new File("c:\\ff7_models\\test7.png"));
		} catch (KjataException e) {
			System.out.println(e.toString());
		}
	}
}

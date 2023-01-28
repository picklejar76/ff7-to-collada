package com.kujatas.collada;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.kujatas.model.KjataException;
import com.kujatas.model.loader.TexLoader;

public class TextureExporter {
	
	private String outputDirectory;

	// outputDirectory is:
	//   ExporterConfig.OUTPUT_BASE_DIRECTORY + "\\textures" for the Collada exporter
	//   JsonExporter.etc for the JSON exporter
	public TextureExporter(String outputDirectory) {
		this.outputDirectory = outputDirectory;
		new File(outputDirectory).mkdirs();
	}
	
	public void texToPng(String baseFilename) throws KjataException {
		BufferedImage image;
		try {
			//System.out.println("Reading texture file: " + baseFilename);
			image = TexLoader.getInstance().load(baseFilename);
		} catch (KjataException e) {
			throw new KjataException("Exception while reading texture file: " + baseFilename, e);
		}
		String outputFilename = outputDirectory + "/" + baseFilename.toLowerCase() + ".png";
		try {
			//System.out.println("Writing texture file: " + outputFilename);
			ImageIO.write(image, "png", new File(outputFilename));
			//System.out.println("Finished writing texture file: " + outputFilename);
		} catch (IOException e) {
			throw new KjataException("Exception while writing texture file: " + outputFilename, e);
		}
	}
	
	public static void main(String[] args) throws KjataException {
		// sample texture files:
		// aabb.tex = cloud's left eye
		// aabc.tex = cloud's right eye
		// aabd.tex = cloud's mouth
		// aahc.tex = tifa's
		// aahd.tex = tifa's
		// aahe.tex = tifa's
		// adeb.tex = redxiii's only texture?
		new TextureExporter(ExporterConfig.OUTPUT_BASE_DIRECTORY).texToPng("adeb");
	}

}

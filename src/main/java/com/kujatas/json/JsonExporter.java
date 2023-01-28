package com.kujatas.json;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kujatas.collada.TextureExporter;
import com.kujatas.model.Animation;
import com.kujatas.model.BoneResource;
import com.kujatas.model.FF7File;
import com.kujatas.model.Model;
import com.kujatas.model.Skeleton;
import com.kujatas.model.loader.ALoader;
import com.kujatas.model.loader.HrcLoader;
import com.kujatas.model.loader.PLoader;
import com.kujatas.model.loader.RsdLoader;

public class JsonExporter {
	
	private static String OUTPUT_JSON_DIRECTORY = "/git/ff7/picklejar76/ff7-translator/src/main/resources/json";
	private static String OUTPUT_PNG_DIRECTORY = "/git/ff7/picklejar76/ff7-translator/src/main/resources/png";
	private static String SUBFOLDER_ANIMATIONS = "animations";
	private static String SUBFOLDER_SKELETONS = "skeletons";
	private static String SUBFOLDER_BONES = "bones";
	private static String SUBFOLDER_MODELS = "models";
	private static String SUBFOLDER_TEXTURES = "textures";
	private HrcLoader hrcLoader;
	private RsdLoader rsdLoader;
	private PLoader pLoader;
	private ALoader aLoader;
	private TextureExporter textureExporter;
	private ObjectMapper mapper;
	private ObjectWriter jsonFileWriter;
	
	public JsonExporter() {
		this.hrcLoader = new HrcLoader();
		this.rsdLoader = new RsdLoader();
		this.pLoader = new PLoader();
		this.aLoader = ALoader.getInstance();
		//this.textureExporter = new TextureExporter(OUTPUT_JSON_DIRECTORY + "/" + SUBFOLDER_TEXTURES);
		this.mapper = new ObjectMapper();
		this.jsonFileWriter = mapper.writerWithDefaultPrettyPrinter();
	}
	
	public void translateToJson() throws Exception {
		
		String[] subfolderNames = {SUBFOLDER_ANIMATIONS, SUBFOLDER_SKELETONS, SUBFOLDER_BONES, SUBFOLDER_MODELS, SUBFOLDER_TEXTURES};
		for (String subfolderName : subfolderNames) {
			new File(OUTPUT_JSON_DIRECTORY + "/" + subfolderName).mkdirs();
		}
		
		File inputDirectory = new File(FF7File.BASE_DIRECTORY);
		for (final File fileEntry : inputDirectory.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            System.out.println("Ignoring subdirectory: " + fileEntry.getName());
	        } else {
	            String inputFilename = fileEntry.getName().toLowerCase();
	            int pos = inputFilename.lastIndexOf(".");
	            if (pos < 0) {
		            System.out.println("Skipping filename without extension: " + inputFilename);
	            } else {
	            	String baseFilename = inputFilename.substring(0, pos);
	            	String extension = inputFilename.substring(pos + 1);
	            	String outputFilename = inputFilename + ".json"; // aaaa.hrc.json
	            	//System.out.println("Processing base=" + baseFilename + ", ext=" + extension);
	            	try {
			            if (extension.equals("a")) {
			            	Animation animation = aLoader.load(baseFilename);
			            	serializeToJsonFile(animation, SUBFOLDER_ANIMATIONS, outputFilename);
			            }
			            if (extension.equals("hrc")) {
			            	Skeleton skeleton = hrcLoader.load(baseFilename);
			            	serializeToJsonFile(skeleton, SUBFOLDER_SKELETONS, outputFilename);
			            }
			            if (extension.equals("rsd")) {
			            	BoneResource boneResource = rsdLoader.load(baseFilename);
			            	serializeToJsonFile(boneResource, SUBFOLDER_BONES, outputFilename);
			            }
			            if (extension.equals("p")) {
			            	Model model = pLoader.load(baseFilename);
			            	serializeToJsonFile(model, SUBFOLDER_MODELS, outputFilename);
			            }
			            if (extension.equals("tex")) {
			            	textureExporter.texToPng(baseFilename);
			            	System.out.println("Wrote: " + OUTPUT_PNG_DIRECTORY + "/" + SUBFOLDER_TEXTURES + "/" + baseFilename + ".png");
			            }
	            	} catch (Exception e) {
	            		System.err.println("ERROR while translating " + inputFilename);
	            		e.printStackTrace();
	            	}
	            }
	        }
	    }
	}
	
	public void serializeToJsonFile(Object object, String subfolder, String filename) throws Exception {
		File outputFile = new File(OUTPUT_JSON_DIRECTORY + "/" + subfolder + "/" + filename);
		jsonFileWriter.writeValue(outputFile, object);
		System.out.println("Wrote: " + outputFile.getAbsolutePath());
	}
	
	public static void main(String[] args) throws Exception {
		new JsonExporter().translateToJson();
	}
}

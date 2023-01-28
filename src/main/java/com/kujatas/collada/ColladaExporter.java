package com.kujatas.collada;

import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.collada.model.Accessor;
import org.collada.model.Asset;
import org.collada.model.BindMaterial;
import org.collada.model.COLLADA;
import org.collada.model.COLLADA.Scene;
import org.collada.model.CommonColorOrTextureType;
import org.collada.model.CommonColorOrTextureType.Color;
import org.collada.model.CommonColorOrTextureType.Texture;
import org.collada.model.CommonNewparamType;
import org.collada.model.Effect;
import org.collada.model.FloatArray;
import org.collada.model.FxSampler2DCommon;
import org.collada.model.FxSurfaceCommon;
import org.collada.model.FxSurfaceInitFromCommon;
import org.collada.model.Geometry;
import org.collada.model.Image;
import org.collada.model.InputLocal;
import org.collada.model.InputLocalOffset;
import org.collada.model.InstanceEffect;
import org.collada.model.InstanceGeometry;
import org.collada.model.InstanceMaterial;
import org.collada.model.InstanceWithExtra;
import org.collada.model.LibraryEffects;
import org.collada.model.LibraryGeometries;
import org.collada.model.LibraryImages;
import org.collada.model.LibraryMaterials;
import org.collada.model.LibraryVisualScenes;
import org.collada.model.Material;
import org.collada.model.Mesh;
import org.collada.model.Node;
import org.collada.model.ObjectFactory;
import org.collada.model.Param;
import org.collada.model.ProfileCOMMON;
import org.collada.model.ProfileCOMMON.Technique;
import org.collada.model.ProfileCOMMON.Technique.Blinn;
import org.collada.model.ProfileCOMMON.Technique.Phong;
import org.collada.model.Rotate;
import org.collada.model.Source;
import org.collada.model.Source.TechniqueCommon;
import org.collada.model.TargetableFloat3;
import org.collada.model.Triangles;
import org.collada.model.UpAxisType;
import org.collada.model.Vertices;
import org.collada.model.VisualScene;

import com.kujatas.model.Bone;
import com.kujatas.model.BoneResource;
import com.kujatas.model.Color4D;
import com.kujatas.model.KjataException;
import com.kujatas.model.Model;
import com.kujatas.model.Point2D;
import com.kujatas.model.Point3D;
import com.kujatas.model.Polygon;
import com.kujatas.model.PolygonGroup;
import com.kujatas.model.Skeleton;
import com.kujatas.model.loader.ALoader;
import com.kujatas.model.loader.HrcLoader;
import com.kujatas.model.loader.PLoader;
import com.kujatas.model.loader.RsdLoader;

public class ColladaExporter {
	
	private static ObjectFactory FACTORY = new ObjectFactory();
	private final XMLGregorianCalendar createdDate;
	private String hrcId;
	private String initialAnimationId;
	private COLLADA collada;
	private Scene scene;
	private InstanceWithExtra instanceVisualScene;
	private Asset asset;
	private LibraryImages libraryImages;
	private LibraryEffects libraryEffects;
	private Effect plainEffect;
	private LibraryMaterials libraryMaterials;
	private Material plainMaterial;
	private LibraryGeometries libraryGeometries;
	private LibraryVisualScenes libraryVisualScenes;
	private VisualScene visualScene;
	private HrcLoader hrcLoader;
	private RsdLoader rsdLoader;
	private PLoader pLoader;
	private ALoader aLoader;
	private TextureExporter textureExporter;
	private Skeleton skeleton;
	private com.kujatas.model.Animation initialAnimation;
	private com.kujatas.model.AnimationFrame initialAnimationFrame;

	private Map<String, Bone> boneMap;
	private Map<String, Integer> boneIndexMap;
	// export options
	private static boolean EXPORT_TEXTURES = true;
	private static String MODELS_DIRECTORY_ABSOLUTE = ExporterConfig.OUTPUT_BASE_DIRECTORY + "\\models";
	private static String TEXTURES_DIRECTORY_RELATIVE = "../textures";

	public ColladaExporter() {
		try {
			this.createdDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected exception while getting XMLGregorianCalendar", e);
		}
	}

	public static void main(String[] args) throws KjataException {
		
		// HRC files:
		//   aaaa = cloud
		String hrcId = "aaaa";
		
		// A files:
		//   aafe.a = cloud standing still
		String initialAnimationId = "aafe";
		
		// RSD files:
		//   aaaf = cloud's head
		//   aaha = tifa's head
		//   addf = redxiii's head
		
		ColladaExporter exporter = new ColladaExporter();
		exporter.exportModelToCollada(hrcId, initialAnimationId);
	}
	
	public void exportModelToCollada(String hrcBaseFilename, String initialAnimationBaseFilename) throws KjataException {
		
		this.hrcId = hrcBaseFilename.toLowerCase();
		this.initialAnimationId = initialAnimationBaseFilename.toLowerCase();
		
		collada = new COLLADA();

		scene = new Scene();
		collada.setScene(scene);
		instanceVisualScene = new InstanceWithExtra();
		scene.setInstanceVisualScene(instanceVisualScene);
		instanceVisualScene.setUrl("#" + hrcId + "-scene");
		
		asset = new Asset();
		collada.setAsset(asset);
		asset.setCreated(this.createdDate);
		asset.setUpAxis(UpAxisType.Y_UP);
		
		libraryImages = new LibraryImages();
		collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryImages);
		
		libraryEffects = new LibraryEffects();
		collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryEffects);
		plainEffect = createPlainEffect();
		libraryEffects.getEffect().add(plainEffect);
		
		libraryMaterials = new LibraryMaterials();
		collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryMaterials);
		plainMaterial = createMaterial("plain-material", "#" + plainEffect.getId());
		libraryMaterials.getMaterial().add(plainMaterial);
		
		libraryGeometries = new LibraryGeometries();
		collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryGeometries);

		libraryVisualScenes = new LibraryVisualScenes();
		collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryVisualScenes);
		
		visualScene = new VisualScene();
		libraryVisualScenes.getVisualScene().add(visualScene);
		visualScene.setId(hrcId + "-scene");
		
		hrcLoader = new HrcLoader();
		rsdLoader = new RsdLoader();
		pLoader = new PLoader();
		aLoader = ALoader.getInstance();
		textureExporter = new TextureExporter(ExporterConfig.OUTPUT_BASE_DIRECTORY + "/textures");

		skeleton = hrcLoader.load(hrcBaseFilename);
		initialAnimation = aLoader.load(initialAnimationBaseFilename);
		initialAnimationFrame = initialAnimation.animationFrames.get(0);
		
		boneMap = new HashMap<String, Bone>();
		boneIndexMap = new HashMap<String, Integer>();
		for (int i=0; i<skeleton.getBones().size(); i++) {
			Bone bone = skeleton.getBones().get(i);
			boneMap.put(bone.getName(), bone);
			boneIndexMap.put(bone.getName(), i);
		}
		
		processBone(null, visualScene);

		try {
			//StringWriter writer = new StringWriter();
			new File(MODELS_DIRECTORY_ABSOLUTE).mkdirs();
			FileWriter writer = new FileWriter(MODELS_DIRECTORY_ABSOLUTE + "/" + hrcId + ".hrc.dae");
			JAXBContext context = JAXBContext.newInstance(COLLADA.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(collada, writer);
			writer.close();
			System.out.println("finished");
			
		} catch (Exception e) {
			throw new KjataException("Exception while exporting to COLLADA", e);
		}
	}

	// parentNode should be either a VisualScene (if root) or a Node (all others)
	private void processBone(Bone bone, Object parentNode) throws KjataException {

		String rsdBaseFilename = null;
		String boneName = null; // the friendly name in the FF7 file, e.g. "root", "hip", "chest", "head", etc.
		String nodeName = null; // a generic name for animations to work, e.g. "bone-root", "bone-0", "bone-1", etc.
		Bone parentBone = null;
		
		if (bone == null) {
			boneName = "root";
			nodeName = "bone-root";
		} else {
			boneName = bone.getName();
			nodeName = "bone-" + bone.getBoneIndex();
			parentBone = boneMap.get(bone.getParentBoneName());
			List<String> rsdBaseFilenames = bone.getRsdBaseFilenames();
			// TODO: Support HRC models having multiple RSD resources
			rsdBaseFilename = rsdBaseFilenames.size() == 0 ? null : rsdBaseFilenames.get(0);
		}
		System.out.println("BONE: " + boneName);
		
		Node geometryNode = new Node();
		if (parentNode instanceof VisualScene) {
			((VisualScene)parentNode).getNode().add(geometryNode);
		} else if (parentNode instanceof Node) {
			((Node)parentNode).getNode().add(geometryNode);
		} else {
			throw new RuntimeException("Unexpected parentNode type: " + parentNode.getClass().getCanonicalName());
		}
		//geometryNode.setId(boneName + "-node");
		geometryNode.setId(nodeName);

		// translation
		if (parentBone != null) {
			double parentBoneLength = parentBone.getLength();
			TargetableFloat3 translate = createTargetableFloat3(0.0, 0.0, -parentBoneLength);
			geometryNode.getLookatOrMatrixOrRotate().add(FACTORY.createTranslate(translate));
		}
		
		// rotation
		/*
		 * 	Transform3D rotX = new Transform3D(); rotX.rotX(currentFrame.boneRotations.get(i).x * Math.PI / 180.0f);
			Transform3D rotY = new Transform3D(); rotY.rotY(currentFrame.boneRotations.get(i).y * Math.PI / 180.0f);
			Transform3D rotZ = new Transform3D(); rotZ.rotZ(currentFrame.boneRotations.get(i).z * Math.PI / 180.0f);
			boneTransformation.mul(rotY);
			boneTransformation.mul(rotX);
			boneTransformation.mul(rotZ);
		 */
		if (!boneName.equals("root")) {
			int i = boneIndexMap.get(boneName);
			Rotate rotateX = createRotate(null, 1.0, 0.0, 0.0, (double)initialAnimationFrame.boneRotations.get(i).x);
			Rotate rotateY = createRotate(null, 0.0, 1.0, 0.0, (double)initialAnimationFrame.boneRotations.get(i).y);
			Rotate rotateZ = createRotate(null, 0.0, 0.0, 1.0, (double)initialAnimationFrame.boneRotations.get(i).z);
			geometryNode.getLookatOrMatrixOrRotate().add(rotateY);
			geometryNode.getLookatOrMatrixOrRotate().add(rotateX);
			geometryNode.getLookatOrMatrixOrRotate().add(rotateZ);
		}
		
		if (rsdBaseFilename == null) {
			
			System.out.println("Skipping bone without RSD base filename, boneName=" + boneName);
			
		} else {

			// LOAD FF7 RSD FILE
			BoneResource boneResource = rsdLoader.load(rsdBaseFilename);

			String rsdId = rsdBaseFilename.toLowerCase();

			// LOAD FF7 P FILE
			String polygonFilename = boneResource.getPolygonFilename(); // aaba = cloud's head, aahb = tifa's head
			Model model = pLoader.load(polygonFilename);

			List<Point2D> adjustedTextureCoordinates = new ArrayList<Point2D>();
			for (Point2D point : model.getTextureCoordinates()) {
				float x = point.x;
				float y = point.y;
				if (x > 1.0) {
					x = x - (float)Math.floor(x);
				}
				if (y > 1.0) {
					y = y - (float)Math.floor(y);
				}
				y = 1.0f - y;
				adjustedTextureCoordinates.add(new Point2D(x, y));
			}
			
			Source positionsSource = this.createSourceFrom3DPoints(rsdId, "positions", model.getVertices());
			Source normalsSource = this.createSourceFrom3DPoints(rsdId, "normals", model.getNormals());
			Source texcoordsSource = this.createSourceFrom2DPoints(rsdId, "texcoords", adjustedTextureCoordinates);
			Source colorsSource = this.createSourceFromColors(rsdId, "colors", model.getVertexColors());
			Vertices vertices = this.createVertices(rsdId);
			
			Mesh mesh = new Mesh();
			mesh.getSource().add(positionsSource);
			mesh.getSource().add(normalsSource);
			mesh.getSource().add(texcoordsSource);
			mesh.getSource().add(colorsSource);
			mesh.setVertices(vertices);

			Geometry geometry = new Geometry();
			geometry.setId(rsdId + "-geometry");
			geometry.setName(bone.getName());
			geometry.setMesh(mesh);
			libraryGeometries.getGeometry().add(geometry);
			
			InstanceGeometry instanceGeometry = new InstanceGeometry();
			geometryNode.getInstanceGeometry().add(instanceGeometry);
			instanceGeometry.setUrl("#" + rsdId + "-geometry");
			BindMaterial bindMaterial = new BindMaterial();
			instanceGeometry.setBindMaterial(bindMaterial);
			BindMaterial.TechniqueCommon techniqueCommon = new BindMaterial.TechniqueCommon();
			bindMaterial.setTechniqueCommon(techniqueCommon);
			InstanceMaterial plainInstanceMaterial = new InstanceMaterial();
			plainInstanceMaterial.setSymbol("plain-material-symbol");
			plainInstanceMaterial.setTarget("#plain-material");
			techniqueCommon.getInstanceMaterial().add(plainInstanceMaterial);

			// The order in which we export triangle groups matters.
			// We must export textured triangles (types 2 and 3) before non-textured triangles (type 1),
			// otherwise the textured triangles will not show up in Unity.
			int[] orderedPolygonTypes = {2, 3, 1};
			for (int polygonType : orderedPolygonTypes) {
				for (int polygonGroupIndex = 0; polygonGroupIndex < model.getPolygonGroups().size(); polygonGroupIndex++) {
					PolygonGroup polygonGroup = model.getPolygonGroups().get(polygonGroupIndex);
					if (polygonGroup.polygonType == polygonType) {
						Triangles triangles = this.createTriangles(rsdId, polygonGroupIndex, model.getPolygonGroups(), model.getPolygons(), boneResource.getTextureBaseFilenames());
						mesh.getLinesOrLinestripsOrPolygons().add(triangles);
					}
				}
			}

			for (String texBaseFilename : boneResource.getTextureBaseFilenames()) {
				
				String texId = texBaseFilename.toLowerCase();
				Image image = new Image();
				image.setId(texId + "-png");
				image.setName(texId + "-png");
				image.setInitFrom(TEXTURES_DIRECTORY_RELATIVE + "/" + texId + ".png");
				libraryImages.getImage().add(image);
				
				Effect pngEffect = createPngEffect(image);
				libraryEffects.getEffect().add(pngEffect);
				Material textureMaterial = createMaterial(texId + "-material", "#" + pngEffect.getId());
				libraryMaterials.getMaterial().add(textureMaterial);
				InstanceMaterial textureInstanceMaterial = new InstanceMaterial();
				textureInstanceMaterial.setSymbol(texId + "-material-symbol");
				textureInstanceMaterial.setTarget("#" + textureMaterial.getId());
				techniqueCommon.getInstanceMaterial().add(textureInstanceMaterial);
				
				if (EXPORT_TEXTURES) {
					System.out.println("Exporting texture: " + texBaseFilename);
					new TextureExporter(ExporterConfig.OUTPUT_BASE_DIRECTORY + "/textures").texToPng(texBaseFilename);
				}
			}
		}
		
		for (Bone otherBone : skeleton.getBones()) {
			if (boneName.equals(otherBone.getParentBoneName())) {
				// process child bone recursively
				processBone(otherBone, geometryNode);
			}
		}

	}
	
	//private Triangles createTriangles(String rsdId, List<Polygon> polygons) {
	private Triangles createTriangles(String rsdId, int polygonGroupIndex, List<PolygonGroup> polygonGroups, List<Polygon> polygons, List<String> textureBaseFilenames) {
		
		PolygonGroup polygonGroup = polygonGroups.get(polygonGroupIndex);
		
		Triangles triangles = new Triangles();
		triangles.setCount(BigInteger.valueOf(polygonGroup.numPolysInGroup)); // previously polygons.size()
		//triangles.setMaterial("Bricks");
		
		int offset = 0;
		InputLocalOffset vertexInput = this.createInput("VERTEX", "#" + rsdId + "-vertices", offset++);
		InputLocalOffset normalInput = this.createInput("NORMAL", "#" + rsdId + "-normals", offset++);
		InputLocalOffset texcoordInput = this.createInput("TEXCOORD", "#" + rsdId + "-texcoords", offset++);
		
		triangles.getInput().add(vertexInput);
		triangles.getInput().add(normalInput);
		if (polygonGroup.isTextureUsed > 0) {
			triangles.getInput().add(texcoordInput);
		}
		
		if (polygonGroup.isTextureUsed > 0) {
			String textureId = textureBaseFilenames.get(polygonGroup.textureIndex).toLowerCase();
			triangles.setMaterial(textureId + "-material-symbol");
		} else {
			triangles.setMaterial("plain-material-symbol");
		}
		
		for (int polygonIndex = 0; polygonIndex < polygonGroup.numPolysInGroup; polygonIndex++) {
			
			Polygon polygon = polygons.get(polygonGroup.offsetPolyIndex + polygonIndex);
			
			triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetVertexIndex + polygon.vertexIndex3));
			triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetVertexIndex + polygon.normalIndex3));
			if (polygonGroup.isTextureUsed > 0) {
				triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetTextureCoordinateIndex + polygon.vertexIndex3));
			}

			triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetVertexIndex + polygon.vertexIndex2));
			triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetVertexIndex + polygon.normalIndex2));
			if (polygonGroup.isTextureUsed > 0) {
				triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetTextureCoordinateIndex + polygon.vertexIndex2));
			}
			
			triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetVertexIndex + polygon.vertexIndex1));
			triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetVertexIndex + polygon.normalIndex1));
			if (polygonGroup.isTextureUsed > 0) {
				triangles.getP().add(BigInteger.valueOf(polygonGroup.offsetTextureCoordinateIndex + polygon.vertexIndex1));
			}
		}
		
		return triangles;
	}
	
	private Vertices createVertices(String rsdId) {
		
		InputLocal input = this.createInput("POSITION", "#" + rsdId + "-positions");
		InputLocal inputColor = this.createInput("COLOR", "#" + rsdId + "-colors");
		
		Vertices vertices = new Vertices();
		vertices.setId(rsdId + "-vertices");
		vertices.setName(rsdId + "-vertices");
		vertices.getInput().add(input);
		vertices.getInput().add(inputColor);
		
		return vertices;
	}

	// sourceType is something like "positions" or "normals"
	private Source createSourceFrom3DPoints(String rsdId, String sourceType, List<Point3D> points) {
		
		FloatArray floatArray = new FloatArray();
		floatArray.setId(rsdId + "-" + sourceType + "-array");
		floatArray.setName(rsdId + "-" + sourceType + "-array");
		floatArray.setCount(BigInteger.valueOf(points.size() * 3));
		for (Point3D point : points) {
			floatArray.getValue().add((double)point.x);
			floatArray.getValue().add((double)point.y);
			floatArray.getValue().add((double)point.z);
		}
		
		Accessor accessor = new Accessor();
		accessor.setCount(BigInteger.valueOf(points.size()));
		accessor.setSource("#" + rsdId + "-" + sourceType + "-array");
		accessor.setStride(BigInteger.valueOf(3));
		accessor.getParam().add(createParam("X", "float"));
		accessor.getParam().add(createParam("Y", "float"));
		accessor.getParam().add(createParam("Z", "float"));
		
		TechniqueCommon techniqueCommon = new TechniqueCommon();
		techniqueCommon.setAccessor(accessor);
		
		Source source = new Source();
		source.setId(rsdId + "-" + sourceType);
		source.setName(rsdId + "-" + sourceType);
		source.setFloatArray(floatArray);
		source.setTechniqueCommon(techniqueCommon);
		
		return source;

	}
	
	// sourceType is something like "texcoords"
	private Source createSourceFrom2DPoints(String rsdId, String sourceType, List<Point2D> points) {
		
		FloatArray floatArray = new FloatArray();
		floatArray.setId(rsdId + "-" + sourceType + "-array");
		floatArray.setName(rsdId + "-" + sourceType + "-array");
		floatArray.setCount(BigInteger.valueOf(points.size() * 2));
		for (Point2D point : points) {
			floatArray.getValue().add((double)point.x);
			floatArray.getValue().add((double)point.y);
		}
		
		Accessor accessor = new Accessor();
		accessor.setCount(BigInteger.valueOf(points.size()));
		accessor.setSource("#" + rsdId + "-" + sourceType + "-array");
		accessor.setStride(BigInteger.valueOf(2));
		accessor.getParam().add(createParam("X", "float")); // A? X? F? Y?
		accessor.getParam().add(createParam("Y", "float")); // A? X? F? Y?
		
		TechniqueCommon techniqueCommon = new TechniqueCommon();
		techniqueCommon.setAccessor(accessor);
		
		Source source = new Source();
		source.setId(rsdId + "-" + sourceType);
		source.setName(rsdId + "-" + sourceType);
		source.setFloatArray(floatArray);
		source.setTechniqueCommon(techniqueCommon);
		
		return source;

	}
	
	// sourceType is something like "colors"
	private Source createSourceFromColors(String rsdId, String sourceType, List<Color4D> colors) {
		
		FloatArray floatArray = new FloatArray();
		floatArray.setId(rsdId + "-" + sourceType + "-array");
		floatArray.setName(rsdId + "-" + sourceType + "-array");
		floatArray.setCount(BigInteger.valueOf(colors.size() * 3));
		for (Color4D color : colors) {
			floatArray.getValue().add(color.rAsDouble());
			floatArray.getValue().add(color.gAsDouble());
			floatArray.getValue().add(color.bAsDouble());
		}
		
		Accessor accessor = new Accessor();
		accessor.setCount(BigInteger.valueOf(colors.size()));
		accessor.setSource("#" + rsdId + "-" + sourceType + "-array");
		accessor.setStride(BigInteger.valueOf(3));
		accessor.getParam().add(createParam("R", "float"));
		accessor.getParam().add(createParam("G", "float"));
		accessor.getParam().add(createParam("B", "float"));
		
		TechniqueCommon techniqueCommon = new TechniqueCommon();
		techniqueCommon.setAccessor(accessor);
		
		Source source = new Source();
		source.setId(rsdId + "-" + sourceType);
		source.setName(rsdId + "-" + sourceType);
		source.setFloatArray(floatArray);
		source.setTechniqueCommon(techniqueCommon);
		
		return source;

	}
	
	private InputLocal createInput(String semantic, String source) {
		InputLocal input = new InputLocal();
		input.setSemantic(semantic);
		input.setSource(source);
		return input;
	}
	
	private InputLocalOffset createInput(String semantic, String source, long offset) {
		InputLocalOffset input = new InputLocalOffset();
		input.setSemantic(semantic);
		input.setSource(source);
		input.setOffset(BigInteger.valueOf(offset));
		return input;
	}
	
	// Example: name="X", type="float"
	private static Param createParam(String name, String type) {
		Param param = new Param();
		param.setName(name);
		param.setType(type);
		return param;
	}
	
	private static Effect createPlainEffect() {
		Effect effect = new Effect();
		effect.setId("plain-effect");
		
		ProfileCOMMON profileCommon = new ProfileCOMMON();
		effect.getFxProfileAbstract().add(FACTORY.createProfileCOMMON(profileCommon));
		
		Technique technique = new Technique();
		profileCommon.setTechnique(technique);
		technique.setSid("common");
		
		Blinn blinn = new Blinn();
		technique.setBlinn(blinn);
		
		CommonColorOrTextureType diffuse = FACTORY.createCommonColorOrTextureType();
		blinn.setDiffuse(diffuse);
		
		Color color = new Color();
		color.getValue().add(1.0);
		color.getValue().add(1.0);
		color.getValue().add(1.0);
		color.getValue().add(1.0);
		diffuse.setColor(color);
		
		return effect;
	}

	private static Effect createPngEffect(Image image) {
		
		Effect effect = new Effect();
		effect.setId(image.getId() + "-effect");

		ProfileCOMMON profileCommon = new ProfileCOMMON();
		effect.getFxProfileAbstract().add(FACTORY.createProfileCOMMON(profileCommon));
		
		//  <newparam sid="aabb-png-surface">
		//    <surface type="2D">
		//      <init_from>aabb-png</init_from>
		//    </surface>
		//  </newparam>
		CommonNewparamType surfaceParam = new CommonNewparamType();
		profileCommon.getImageOrNewparam().add(surfaceParam);
		surfaceParam.setSid(image.getId() + "-surface");
		
		FxSurfaceCommon surface = new FxSurfaceCommon();
		surfaceParam.setSurface(surface);
		surface.setType("2D");
		
		FxSurfaceInitFromCommon initFrom = new FxSurfaceInitFromCommon();
		initFrom.setValue(image);
		surface.getInitFrom().add(initFrom);

		//  <newparam sid="aabb-png-sampler">
		//    <sampler2D>
		//      <source>aabb-png-surface</source>
		//    </sampler2D>
		//  </newparam>
		CommonNewparamType samplerParam = new CommonNewparamType();
		profileCommon.getImageOrNewparam().add(samplerParam);
		samplerParam.setSid(image.getId() + "-sampler");
		
		FxSampler2DCommon sampler = new FxSampler2DCommon();
		samplerParam.setSampler2D(sampler);
		sampler.setSource(image.getId() + "-surface");

		//    <technique sid="common">
		//        <phong>
		//            <emission>
		//                <color sid="emission">0 0 0 1</color>
		//            </emission>
		//            <ambient>
		//                 <color sid="ambient">0 0 0 1</color>
		//            </ambient>
		//            <diffuse>
		//                <texture texture="aabb-png-sampler"/>
		//            </diffuse>
		//            <specular>
		//                <color sid="specular">0.5 0.5 0.5 1</color>
		//            </specular>
		//            <shininess>
		//                <float sid="shininess">50</float>
		//            </shininess>
		//            <index_of_refraction>
		//                <float sid="index_of_refraction">1</float>
		//            </index_of_refraction>
		//        </phong>
		//    </technique>
		
		Technique technique = new Technique();
		profileCommon.setTechnique(technique);
		technique.setSid("common");
		
		Phong phong = new Phong();
		technique.setPhong(phong);
		
		CommonColorOrTextureType diffuse = FACTORY.createCommonColorOrTextureType();
		phong.setDiffuse(diffuse);
		
		Texture texture = new Texture();
		diffuse.setTexture(texture);
		texture.setTexture(samplerParam.getSid());
		
		// TODO: Set emission, ambient, specular, shininess, index_of_refraction, if/as needed
		
		return effect;
	}
	
	private static Material createMaterial(String materialId, String instanceEffectUrl) {
		//	<material id="plain-material">
		// 		<instance_effect url="#plain-effect" />
		// 	</material>
		// 	<material id="aabb-material">
		// 		<instance_effect url="#aabb-png-effect" />
		// 	</material>
		Material material = new Material();
		material.setId(materialId);
		InstanceEffect instanceEffect = new InstanceEffect();
		material.setInstanceEffect(instanceEffect);
		instanceEffect.setUrl(instanceEffectUrl);
		return material;
	}
	
	private static TargetableFloat3 createTargetableFloat3(Double...doubles) {
		TargetableFloat3 t = new TargetableFloat3();
		for (Double d : doubles) {
			t.getValue().add(d);
		}
		return t;
	}
	
	// sid is optional and can be something like "rotateX", "rotateY", "rotateZ"
	private static Rotate createRotate(String sid, double d1, double d2, double d3, double d4) {
		Rotate rotate = new Rotate();
		rotate.setSid(sid);
		rotate.getValue().add(d1);
		rotate.getValue().add(d2);
		rotate.getValue().add(d3);
		rotate.getValue().add(d4);
		return rotate;
	}
}

package org.omg.bpmn.miwg.boundary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Creates for each bpmn file listed, three png files: one with rectangles that
 * have the bounds of the labels, a second with rectangles that have the bounds
 * of the shapes and the paths of the edges, a third that is the combination of
 * the two previous images.
 * 
 * The first one has a postfix to the bpmn file name of _label_bounds, the
 * second a postfix of _shapes_bounds and the thirs _all_bounds. Ex :
 * A.1.0_label_bounds.png, A.1.0_shapes_bounds.png, A.1.0_all_bounds.png
 * 
 * @author aferreira
 * @author sringuette
 * 
 */
public class BoundaryCreator {

	static String BPMNDI_NS = "http://www.omg.org/spec/BPMN/20100524/DI";
	static String DI_NS = "http://www.omg.org/spec/DD/20100524/DI";
	static String DC_NS = "http://www.omg.org/spec/DD/20100524/DC";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("OMG MIWG Boundary Generator");
			System.err.println("Two parameters required: BPMNFOLDER PNGOUTPUTFOLDER");
			return;
		}
		generate(new File(args[0]), new File(args[1]));
	}

	public static void generate(File sourceFolder, File targetFolder)
			throws IOException, ParserConfigurationException, SAXException {
		if (!sourceFolder.exists()) {
			System.err.println("Source folder does not exists: " + sourceFolder.getCanonicalPath());
			return;
		}
		targetFolder.mkdirs();
		System.out.println("Generating Bounds for BPMN files: " + sourceFolder.getCanonicalPath());
		File[] bpmnFiles = sourceFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".bpmn");
			}
		});

		for (int i = 0; i < bpmnFiles.length; i++) {

			generateFile(bpmnFiles[i], targetFolder);

		}
		System.out.println("Done!");

	}

	public static void generateFile(File bpmnFile, File targetFolder)
			throws ParserConfigurationException, SAXException, IOException {
		// Load BPMN XML File as a DOM
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(bpmnFile);
		doc.getDocumentElement().normalize();

		// get the first BPMNDiagram (we output only the first
		NodeList diagrams = doc.getElementsByTagNameNS(BPMNDI_NS, "BPMNDiagram");
		String bpmnFileWithoutExtension = bpmnFile.getName().substring(0, bpmnFile.getName().length() - 5);
		String labelBoundsFileName = bpmnFileWithoutExtension + "_label_bounds.png";
		String shapesBoundsFileName = bpmnFileWithoutExtension + "_shapes_bounds.png";
		String allBoundsFileName = bpmnFileWithoutExtension + "_all_bounds.png";
		Node diagram = diagrams.item(0);

		if (diagram == null) {
			System.err.println("No diagram found in file " + bpmnFile.getCanonicalPath());
			return;
		} else {
			System.out.println("Processing " + bpmnFile.getCanonicalPath());
		}

		// calculate the image size
		Rectangle imageSize = calculateImageRectangle((Element) diagram);
		int diagramWidth = imageSize.width + 100;
		int diagramHeight = imageSize.height + 100;
		NodeList bounds = ((Element) diagram).getElementsByTagNameNS(DC_NS, "Bounds");
		NodeList edges = ((Element) diagram).getElementsByTagNameNS(BPMNDI_NS, "BPMNEdge");
		try {
			BufferedImage biLabels = new BufferedImage(diagramWidth, diagramHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2Labels = biLabels.createGraphics();
			BufferedImage biShapes = new BufferedImage(diagramWidth, diagramHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2Shapes = biShapes.createGraphics();
			BufferedImage biAll = new BufferedImage(diagramWidth, diagramHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2All = biAll.createGraphics();
			// create the files header
			createFilesHeader(ig2All, ig2Shapes, ig2Labels, bpmnFileWithoutExtension);
			// create the bounds
			for (int j = 0; j < bounds.getLength(); j++) {
				ig2Labels.setPaint(Color.red);
				ig2Shapes.setPaint(Color.black);
				Element bound = (Element) bounds.item(j);
				Node parent = bound.getParentNode();
				int x = (int) Math.round(Double.parseDouble(bound.getAttribute("x")));
				int y = (int) Math.round(Double.parseDouble(bound.getAttribute("y")));
				int width = (int) Math.round(Double.parseDouble(bound.getAttribute("width")));
				int height = (int) Math.round(Double.parseDouble(bound.getAttribute("height")));
				if (parent.getNodeName().equals("bpmndi:BPMNLabel")) {
					ig2Labels.drawRect(x, y, width, height);
					ig2All.setPaint(Color.red);
					ig2All.drawRect(x, y, width, height);
				} else {
					ig2Shapes.drawRect(x, y, width, height);
					ig2All.setPaint(Color.black);
					ig2All.drawRect(x, y, width, height);
				}
			}
			for (int j = 0; j < edges.getLength(); j++) {
				Element edge = (Element) edges.item(j);
				ig2All.setPaint(Color.blue);
				ig2Shapes.setPaint(Color.blue);
				// get waypoints
				NodeList wayPoints = ((Element) edge).getElementsByTagNameNS(DI_NS, "waypoint");
				int[] xPoints = new int[wayPoints.getLength()];
				int[] yPoints = new int[wayPoints.getLength()];
				for (int k = 0; k < wayPoints.getLength(); k++) {
					Element wayPoint = (Element) wayPoints.item(k);
					xPoints[k] = (int) Math.round(Double.parseDouble(wayPoint.getAttribute("x")));
					yPoints[k] = (int) Math.round(Double.parseDouble(wayPoint.getAttribute("y")));
				}
				if (wayPoints.getLength() > 0) {
					ig2All.drawPolyline(xPoints, yPoints, wayPoints.getLength());
					ig2Shapes.drawPolyline(xPoints, yPoints, wayPoints.getLength());
				}
			}
			// create the edges paths
			ImageIO.write(biLabels, "PNG", new File(targetFolder, labelBoundsFileName));
			ImageIO.write(biShapes, "PNG", new File(targetFolder, shapesBoundsFileName));
			ImageIO.write(biAll, "PNG", new File(targetFolder, allBoundsFileName));
		} catch (IOException ie) {

		}
	}

	/**
	 * Calculates the size of the image based on all the bounds in the diagram
	 * 
	 * @param diagram
	 * @return the rectangle that includes the all diagram
	 */
	private static Rectangle calculateImageRectangle(Element diagram) {
		NodeList bounds = diagram.getElementsByTagNameNS(DC_NS, "Bounds");
		Rectangle cummulativeRect = new Rectangle(100, 100);
		if (bounds.getLength() > 0) {
			for (int j = 0; j < bounds.getLength(); j++) {
				Element bound = (Element) bounds.item(j);
				cummulativeRect = getCummulativeRectangle(bound, cummulativeRect);
			}
		}
		return cummulativeRect;
	}

	/**
	 * Makes the union of two rectangles.
	 * 
	 * @param bound
	 * @param cummulativeRect
	 * @return the union of the two rectangles
	 */
	private static Rectangle getCummulativeRectangle(Element bound, Rectangle cummulativeRect) {
		int x = (int) Math.round(Double.parseDouble(bound.getAttribute("x")));
		int y = (int) Math.round(Double.parseDouble(bound.getAttribute("y")));
		int width = (int) Math.round(Double.parseDouble(bound.getAttribute("width")));
		int height = (int) Math.round(Double.parseDouble(bound.getAttribute("height")));
		Rectangle rect = new Rectangle(x, y, width, height);
		return rect.union(cummulativeRect);
	}

	/**
	 * To all files add the name of the diagram and of the page. Also adds two
	 * lines that identify the coordinate 0,0 of the diagram
	 * 
	 */
	private static void createFilesHeader(Graphics2D ig2All, Graphics2D ig2Shapes, Graphics2D ig2Labels,
			String diagram) {
		// create 0,0 reference lines
		int strokeWidth = 10;
		int halfStroke = strokeWidth / 2;
		int referenceSize = 100;
		int textStartX = 120;
		int textStartY = 20;
		Stroke currentStroke = ig2All.getStroke();
		ig2All.setPaint(Color.black);
		ig2Shapes.setPaint(Color.black);
		ig2Labels.setPaint(Color.black);
		ig2All.setStroke(new BasicStroke(strokeWidth));
		ig2Shapes.setStroke(new BasicStroke(strokeWidth));
		ig2Labels.setStroke(new BasicStroke(strokeWidth));
		ig2All.drawLine(0, halfStroke, referenceSize, halfStroke);
		ig2All.drawLine(halfStroke, 0, halfStroke, referenceSize);
		ig2Shapes.drawLine(0, halfStroke, referenceSize, halfStroke);
		ig2Shapes.drawLine(halfStroke, 0, halfStroke, referenceSize);
		ig2Labels.drawLine(0, halfStroke, referenceSize, halfStroke);
		ig2Labels.drawLine(halfStroke, 0, halfStroke, referenceSize);
		ig2All.setStroke(currentStroke);
		ig2Shapes.setStroke(currentStroke);
		ig2Labels.setStroke(currentStroke);
		// create diagram name
		String diagramName = "Diagram -- ".concat(diagram);
		ig2All.drawString(diagramName, textStartX, textStartY);
		ig2Shapes.drawString(diagramName, textStartX, textStartY);
		ig2Labels.drawString(diagramName, textStartX, textStartY);
	}

}

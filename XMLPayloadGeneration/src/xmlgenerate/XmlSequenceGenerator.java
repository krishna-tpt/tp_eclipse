package xmlgenerate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * USAGE: javac XmlIdUtil.java XmlSequenceGenerator.java java
 * XmlSequenceGenerator <inputXmlPath> <outputDirectory>
 *
 * For every <Work-order>: - generates a NEW unique <sfRecordId> for the header
 * - copies that same value into <workOrderId> of every child
 * <workOrderLineItem> (this is the header<->line link) - generates a NEW unique
 * <id> for every <workOrderLineItem>
 *
 * Handles a file with a single <Work-order> root, or a file with multiple
 * <Work-order> blocks under a wrapper root - both work because we look up every
 * <Work-order> element in the document.
 */
public class XmlSequenceGenerator {

	private static final String HEADER_ID_PREFIX = "0WO";
	private static final String LINE_ID_PREFIX = "1WL";

	public static void main(String[] args) throws Exception {

		// Hard-coded directories
		Path inputDir = Paths.get("/home/dev021/Documents/Michelin/Tickets/IRM 9672/XML Payload/Input/");

		Path outputDir = Paths.get("/home/dev021/Documents/Michelin/Tickets/IRM 9672/XML Payload/Output/");

		// Get only file name from user
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter input XML file name: ");
		String inputFileName = scanner.nextLine().trim();

		Path inputPath = inputDir.resolve(inputFileName);

		// Validate input file
		if (!Files.exists(inputPath)) {
			System.err.println("Input file not found: " + inputPath.toAbsolutePath());
			System.exit(1);
		}

		// Create output directory if not exists
		Files.createDirectories(outputDir);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);

		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(inputPath.toFile());
		doc.getDocumentElement().normalize();

		Set<String> usedHeaderIds = new HashSet<>();
		Set<String> usedLineIds = new HashSet<>();

		NodeList workOrders = doc.getElementsByTagName("Work-order");

		int headerCount = 0;
		int lineCount = 0;

		for (int i = 0; i < workOrders.getLength(); i++) {

			Element workOrder = (Element) workOrders.item(i);

			// 1) New unique sfRecordId for this header
			String newSfRecordId = XmlIdUtil.generateId(HEADER_ID_PREFIX, usedHeaderIds);

			setDirectChildText(workOrder, "sfRecordId", newSfRecordId);

			headerCount++;

			// 2) For every line item under this header
			NodeList lineItems = workOrder.getElementsByTagName("workOrderLineItem");

			for (int j = 0; j < lineItems.getLength(); j++) {

				Element lineItem = (Element) lineItems.item(j);

				// Link workOrderId with parent header sfRecordId
				setDirectChildText(lineItem, "workOrderId", newSfRecordId);

				// Generate new unique line id
				String newLineId = XmlIdUtil.generateId(LINE_ID_PREFIX, usedLineIds);

				setDirectChildText(lineItem, "id", newLineId);

				lineCount++;
			}
		}

		// Generate output file name
		String fileName = inputPath.getFileName().toString();

//		String outName = fileName.replaceFirst("(\\.xml)?$", "_generated.xml");
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String outName = fileName.replaceFirst("(\\.xml)?$", "_Create_" + timestamp + ".xml");

		Path outputPath = outputDir.resolve(outName);

		// Write XML
		writeDocument(doc, outputPath);

		// Print result
		System.out.println();
		System.out.println("======================================");
		System.out.println("XML Generation Completed");
		System.out.println("======================================");
		System.out.println("Input file         : " + inputFileName);
		System.out.println("Headers updated    : " + headerCount);
		System.out.println("Line items updated : " + lineCount);
		System.out.println("Output file        : " + outName);
		System.out.println("Output path        : " + outputPath.toAbsolutePath());
		System.out.println("======================================");

		scanner.close();
	}

	/**
	 * Sets the text content of the first direct-child element with the given tag
	 * name.
	 */
	private static void setDirectChildText(Element parent, String tagName, String value) {
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(tagName)) {
				child.setTextContent(value);
				return;
			}
			child = child.getNextSibling();
		}
		// tag not present (e.g. self-closed/missing) - create it
		Element el = parent.getOwnerDocument().createElement(tagName);
		el.setTextContent(value);
		parent.appendChild(el);
	}

	private static void writeDocument(Document doc, Path outputPath) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		try (var out = Files.newOutputStream(outputPath)) {
			transformer.transform(new DOMSource(doc), new StreamResult(out));
		}
	}
}

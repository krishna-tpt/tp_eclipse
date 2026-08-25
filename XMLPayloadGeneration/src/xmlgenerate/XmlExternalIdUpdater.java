package xmlgenerate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * USAGE: javac XmlExternalIdUpdater.java java XmlExternalIdUpdater
 * <inputXmlPath> <csvPath> <outputDirectory>
 *
 * Does NOT generate any new ids/sequence. It only: - reads the CSV mapping file
 * (header row + 2 columns: "id" and "c_orderline_id") - for every
 * <workOrderLineItem> in the XML whose <id> matches a CSV "id" value, sets
 * <erpExternalId> to that row's "c_orderline_id" value
 *
 * ASSUMPTION (please confirm): the CSV's "id" column holds the SAME value as
 * <workOrderLineItem><id> in the XML (e.g. 1WLSc00000IWSTROA5), and
 * "c_orderline_id" is the value that should be written into <erpExternalId>
 * (e.g. 5216003). If your CSV column names or the mapping direction are
 * different, tell me and I will adjust.
 *
 * Also assumes a plain CSV (no quoted commas inside values). If your CSV has
 * commas inside quoted fields, tell me and I'll swap in a proper CSV parser
 * (e.g. Apache Commons CSV).
 */
public class XmlExternalIdUpdater {

	public static void main(String[] args) throws Exception {

		// Hard-coded directories
		Path inputDir = Paths.get("/home/dev021/Documents/Michelin/Tickets/IRM 9672/XML Payload/Input/");

		Path csvPath = Paths
				.get("/home/dev021/Documents/Michelin/Tickets/IRM 9672/XML Payload/update_payload_link.csv");

		Path outputDir = Paths.get("/home/dev021/Documents/Michelin/Tickets/IRM 9672/XML Payload/Output/");

		// Get XML file name from user
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter input XML file name: ");
		String inputFileName = scanner.nextLine().trim();

		Path inputPath = inputDir.resolve(inputFileName);

		// Validate input XML file
		if (!Files.exists(inputPath)) {
			System.err.println("Input XML file not found: " + inputPath.toAbsolutePath());
			scanner.close();
			System.exit(1);
		}

		// Validate CSV file
		if (!Files.exists(csvPath)) {
			System.err.println("CSV file not found: " + csvPath.toAbsolutePath());
			scanner.close();
			System.exit(1);
		}

		// Create output directory if it doesn't exist
		Files.createDirectories(outputDir);

		// Read CSV mapping
		Map<String, String> idToExternalId = readMapping(csvPath);

		// Parse XML
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);

		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(inputPath.toFile());
		doc.getDocumentElement().normalize();

		NodeList lineItems = doc.getElementsByTagName("workOrderLineItem");

		int matched = 0;
		int unmatched = 0;

		for (int i = 0; i < lineItems.getLength(); i++) {

			Element lineItem = (Element) lineItems.item(i);

			String id = getDirectChildText(lineItem, "id");

			if (id != null && idToExternalId.containsKey(id)) {

				setDirectChildText(lineItem, "erpExternalId", idToExternalId.get(id));

				matched++;

			} else {
				unmatched++;
			}
		}

		// Generate output file name
		String fileName = inputPath.getFileName().toString();

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String outName = fileName.replaceFirst("(\\.xml)?$", "_updated_" + timestamp + ".xml");

		Path outputPath = outputDir.resolve(outName);

		// Write updated XML
		writeDocument(doc, outputPath);

		// Print result
		System.out.println();
		System.out.println("======================================");
		System.out.println("XML External ID Update Completed");
		System.out.println("======================================");
		System.out.println("Input file                  : " + inputFileName);
		System.out.println("Line items matched & updated: " + matched);
		System.out.println("Line items NOT found in CSV : " + unmatched);
		System.out.println("Output file                 : " + outName);
		System.out.println("Output path                 : " + outputPath.toAbsolutePath());
		System.out.println("======================================");

		scanner.close();
	}

	private static Map<String, String> readMapping(Path csvPath) throws Exception {
		Map<String, String> map = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Files.newInputStream(csvPath), StandardCharsets.UTF_8))) {

			String headerLine = reader.readLine();
			if (headerLine == null) {
				return map;
			}
//			String[] headers = headerLine.split(",", -1);
			String[] headers = headerLine.split("\t", -1);
			int idCol = -1;
			int extCol = -1;
			for (int i = 0; i < headers.length; i++) {
				String h = headers[i].trim().toLowerCase();
				if (h.equals("id") || h.equals("header id") || h.equals("header_id")) {
					idCol = i;
				} else if (h.equals("c_orderline_id")) {
					extCol = i;
				}
			}
			if (idCol == -1 || extCol == -1) {
				throw new IllegalStateException(
						"Could not find 'id' and 'c_orderline_id' columns in CSV header: " + headerLine);
			}

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank()) {
					continue;
				}
//				String[] cols = line.split(",", -1);
				String[] cols = line.split("\t", -1);
				if (cols.length <= Math.max(idCol, extCol)) {
					continue;
				}
				String key = cols[idCol].trim();
				String value = cols[extCol].trim();
				if (!key.isEmpty()) {
					map.put(key, value);
				}
			}
		}
		return map;
	}

	private static String getDirectChildText(Element parent, String tagName) {
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(tagName)) {
				return child.getTextContent();
			}
			child = child.getNextSibling();
		}
		return null;
	}

	private static void setDirectChildText(Element parent, String tagName, String value) {
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(tagName)) {
				child.setTextContent(value);
				return;
			}
			child = child.getNextSibling();
		}
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

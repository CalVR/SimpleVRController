package net.thenumenorean.simplevrcontroller.cave;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CaveManager {

	private static CaveManager manager;

	private ArrayList<Cave> caves;

	static {
		manager = new CaveManager();
	}

	private CaveManager() {
		caves = new ArrayList<Cave>();

	}

	/**
	 * Returns the Cave with the given name.
	 * @param name Name of the cave to get
	 * @return A Cave if one exists with the given name, or null
	 */
	public Cave getCave(String name) {
		for (Cave c : caves)
			if (c.getName().equals(name))
				return c;
		return null;
	}

	/**
	 * Trys to add the given cave to the manager. There can be no duplicate caves.
	 * 
	 * @param cave The Cave to add
	 * @return True if it was added, false otherwise
	 */
	public boolean addCave(Cave cave) {
		if (caves.contains(cave))
			return false;

		caves.add(cave);

		return true;
	}

	/**
	 * Saves the Cave data as an xml file to the given OutputStream
	 * @param os OutputStream to write to
	 * @throws TransformerException If the xml cant be built
	 */
	public void save(OutputStream os) throws TransformerException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("caves");
		doc.appendChild(root);

		for (Cave c : caves) {
			Element ce = doc.createElement("cave");
			c.writeToElement(ce);
			root.appendChild(ce);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(os);

		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
		
		System.out.println("Saved data.");

	}
	
	/**
	 * Loads Cave data from the given input stream, which should feed from 
	 * @param is The inputStream to load from
	 * @throws ParserConfigurationException If it couldn't parse the xml
	 * @throws SAXException If there is an error with the xml
	 * @throws IOException If there is an exceptionloading the file
	 */
	public void load(InputStream is) throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(is);

		Element root = (Element) doc.getFirstChild();
		NodeList elements = root.getElementsByTagName("cave");
		for (int y = 0; y < elements.getLength(); y++) {
			addCave(new Cave((Element) elements.item(y)));
		}
	}
	
	/**
	 * Gets a list of all the availible Caves
	 * @return
	 */
	public ArrayList<Cave> getCaves(){
		return caves;
	}
	
	/**
	 * Gets the current instance of the CaveManager
	 * @return A cavemanager instance
	 */
	public static CaveManager getCaveManager() {
		return manager;
	}

}

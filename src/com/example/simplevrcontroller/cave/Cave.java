package com.example.simplevrcontroller.cave;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.example.simplevrcontroller.networking.NetworkManager;
import com.example.simplevrcontroller.networking.location.WirelessLocation;

public class Cave {
	
	private String address;
	private String name;
	private WirelessLocation wl;
	private int port;
	
	public Cave(String name, String address, int port){
		
		this.address = address;
		this.name = name;
		this.port = port;
		
		wl = new WirelessLocation("location");
		wl.setCave(this);
	}
	
	public Cave(Node n){
		name = n.getLocalName();
		address = n.getNodeValue();
		
		Node child = n.getFirstChild();
		while(child != null){
			if(child.getLocalName().equals("location")){
				wl = new WirelessLocation(child);
				wl.setCave(this);
			}
			child = child.getNextSibling();
		}
	}
	
	public void setWirelessLocation(NetworkManager net){
		wl.setLocation(net);
	}
	
	public WirelessLocation getWirelessLocation(){
		return wl;
	}

	/**
	 * Gets the address of this Cave, which can either be an IP or a domain.
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address of this cave. Can either be an IP or a domain.
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the name of this Cave.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public Node getXMLElement() {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
 
		
		Element root = docBuilder.newDocument().createElement(name);
		if(wl != null)
			root.appendChild(wl.getXMLRepresentation());
		
		
		return null;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Cave))
			return false;
		
		return name.equals(((Cave) o).getName());
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

}

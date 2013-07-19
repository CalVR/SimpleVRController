package com.example.simplevrcontroller.cave;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.simplevrcontroller.networking.NetworkManager;
import com.example.simplevrcontroller.networking.location.WirelessLocation;

public class Cave {
	
	private String address;
	private String name;
	private WirelessLocation wl;
	private int port;
	private int defaultPreset;
	
	public Cave(String name, String address, int port, int defaultPreset){
		
		this.address = address;
		this.name = name;
		this.port = port;
		this.defaultPreset = defaultPreset;
		
		wl = new WirelessLocation("location");
		wl.setCave(this);
	}
	
	/**
	 * Creates a new Cave based off the information in the given element.
	 * @param n The element to read.
	 */
	public Cave(Element n){
		name = n.getAttribute("name");
		address = n.getAttribute("address");
		
		try {
			port = Integer.parseInt(n.getAttribute("port"));
		} catch (NumberFormatException e){
			port = NetworkManager.DEFAULT_CAVE_PORT;
		}
		
		try {
			defaultPreset = Integer.parseInt(n.getAttribute("default"));
		} catch (NumberFormatException e){
			defaultPreset = -1;
		}
		
		NodeList elements = n.getElementsByTagName("location");
		for(int y = 0; y < elements.getLength(); y++ ){
			wl = new WirelessLocation((Element)elements.item(y));
			wl.setCave(this);
		}
		
		if(wl == null){
			wl = new WirelessLocation("location");
			wl.setCave(this);
		}
		
	}
	
	/**
	 * Sets the tablet's current location to be the location for this Cave
	 * @param net The NetworkManager to read.
	 */
	public void setWirelessLocation(NetworkManager net){
		wl.setLocation(net);
	}
	
	/**
	 * Gets this Cave's location
	 * @return A WirelessLocation
	 */
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
	
	/**
	 * Writes this cave's data to the given element.
	 * @param base Element to write to (it better not be null)
	 */
	public void writeToElement(Element base) {
		
		base.setAttribute("name", name);
		base.setAttribute("address", address);
		base.setAttribute("port", "" + port);
		base.setAttribute("default","" + defaultPreset);
		Element locEl = base.getOwnerDocument().createElement("location");
		wl.writeToElement(locEl);
		base.appendChild(locEl);
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Cave))
			return false;
		
		return name.equals(((Cave) o).getName());
	}

	/**
	 * @return This cave's port for connections
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port that this Cave connects with.
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the defaultPreset
	 */
	public int getDefaultPreset() {
		return defaultPreset;
	}

	/**
	 * @param defaultPreset the defaultPreset to set
	 */
	public void setDefaultPreset(int defaultPreset) {
		this.defaultPreset = defaultPreset;
	}

}

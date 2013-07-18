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
	
	public Cave(String name, String address, int port){
		
		this.address = address;
		this.name = name;
		this.port = port;
		
		wl = new WirelessLocation("location");
		wl.setCave(this);
	}
	
	public Cave(Element n){
		name = n.getAttribute("name");
		address = n.getAttribute("address");
		
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

	public void writeToElement(Element base) {
		
		base.setAttribute("name", name);
		base.setAttribute("address", address);
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

package com.example.simplevrcontroller.cave;

import java.util.Properties;

import com.example.simplevrcontroller.networking.location.WirelessLocation;

public class Cave {
	
	private String address;
	private String name;
	private WirelessLocation wl;
	
	public Cave(String name, String address){
		
		this.address = address;
		this.name = name;
		
		Properties prop = new Properties();
		
	}
	
	public void setWirelessLocation(WirelessLocation wl){
		this.wl = wl;
		wl.setCave(this);
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

	/**
	 * Sets the name of this cave.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}

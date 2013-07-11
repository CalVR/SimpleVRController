package com.example.simplevrcontroller.networking.location;

import java.util.Map;
import java.util.TreeMap;

import com.example.simplevrcontroller.networking.Networker;
import com.example.simplevrcontroller.networking.location.WirelessLocation.AccuracyThreshold;

public class WirelessLocator {
	
	private Map<String, WirelessLocation> locations;
	private Networker net;
	
	public WirelessLocator(Networker net){
		
		this.net = net;
		
		locations = new TreeMap<String, WirelessLocation>();
		
	}
	
	/**
	 * Gets the WirelessLocation associated with this spot, or null if it does not match any of them.
	 * @return A WirelessLocation or null.
	 */
	public WirelessLocation getCurrentLocation(){
		for(WirelessLocation loc : locations.values())
			if(loc.checkLocation(net, AccuracyThreshold.AVERAGE))
				return loc;
		return null;
	}
	
	/**
	 * Creates and stores a new WirelessLocation and sets its location to be the current area.
	 * @param name Name of the new Location.
	 * @return The new WirelessLocation
	 */
	public WirelessLocation createNewLocation(String name){
		WirelessLocation loc = new WirelessLocation(name);
		
		loc.setLocation(net);
		
		locations.put(name, loc);
		
		return loc;
	}
	
	/**
	 * Gets the WirelessLocation that goes by the given name.
	 * @param name The name of the WirelessLocation
	 * @return A WirelessLocation if one with the given name exists, or null.
	 */
	public WirelessLocation getLocation(String name){
		return locations.get(name);
	}
	
	public void saveData(){
		
	}

}

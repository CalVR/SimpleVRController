package com.example.simplevrcontroller.networking.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.simplevrcontroller.cave.Cave;
import com.example.simplevrcontroller.cave.CaveManager;
import com.example.simplevrcontroller.networking.NetworkManager;

public class WirelessLocator {
	
	private NetworkManager net;
	
	public static final int WIRELESS_THRESHOLD = -75;
	
	public WirelessLocator(NetworkManager net){
		
		this.net = net;
		
	}
	
	/**
	 * Gets the WirelessLocation associated with this spot, or null if it does not match any of them.
	 * @return A WirelessLocation or null.
	 */
	public List<WirelessLocation> getCurrentLocation(){
		
		ArrayList<WirelessLocation> locs = new ArrayList<WirelessLocation>();
		
		for(Cave c : CaveManager.getCaveManager().getCaves()){
			WirelessLocation loc = c.getWirelessLocation();
			if(loc.checkLocation(net) != null)
				locs.add(loc);
		}
		
		Collections.sort(locs, new Comparator<WirelessLocation>(){

			@Override
			public int compare(WirelessLocation arg0, WirelessLocation arg1) {
				return arg0.getLastLocateThreashold().compareTo(arg1.getLastLocateThreashold());
			}
			
		});
		
		return locs;
	}

}

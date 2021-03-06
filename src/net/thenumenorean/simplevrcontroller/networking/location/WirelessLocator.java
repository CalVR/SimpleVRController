package net.thenumenorean.simplevrcontroller.networking.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.thenumenorean.simplevrcontroller.cave.Cave;
import net.thenumenorean.simplevrcontroller.cave.CaveManager;
import net.thenumenorean.simplevrcontroller.networking.NetworkManager;

/**
 * Class for getting the current location
 * @author fmacagno
 *
 */
public class WirelessLocator {
	
	private NetworkManager net;
	
	public static final int WIRELESS_THRESHOLD = -75;
	
	/**
	 * Creates a new WirelessLocator that uses the given NetworkManager for data
	 * @param net NetworkManager to use
	 */
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

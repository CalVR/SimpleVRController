package net.thenumenorean.simplevrcontroller.networking.location;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import net.thenumenorean.simplevrcontroller.cave.Cave;
import net.thenumenorean.simplevrcontroller.networking.NetworkManager;
import net.thenumenorean.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.net.wifi.ScanResult;

/**
 * Stores data for a specific location
 * @author fmacagno
 *
 */
public class WirelessLocation {
	
	public static final int READING_AMT = 20;
	
	public enum AccuracyThreshold {
		STRONG(.75, 300),
		AVERAGE(.60, 500),
		WEAK(.50, 1000);
		
		public final double amount_present;
		public final int max_deviation;
		
		AccuracyThreshold(double amount_present, int max_dev){
			this.amount_present = amount_present;
			max_deviation = max_dev;
		}
		
		
	}

	private String name;
	private TreeMap<String, Integer> bssids;
	private String strength;
	private Cave cave;
	private AccuracyThreshold lastLocate;

	/**
	 * Creates a new WirelessLocation with the given name.
	 * @param name Name for the location
	 */
	public WirelessLocation(String name){
		this.name = name;
		
		bssids = new TreeMap<String, Integer>();
	}
	
	/**
	 * Creates a new WirelessLocation using data loaded from the given xml element
	 * @param n
	 */
	public WirelessLocation(Element n) {
		
		bssids = new TreeMap<String, Integer>();
		
		NodeList nets = n.getElementsByTagName("network");
		
		for (int y = 0; y < nets.getLength(); y++){
			Element net = (Element) nets.item(y);
			try {
				bssids.put(net.getAttribute("bssid"), Integer.parseInt(net.getAttribute("strength")));
			} catch (NumberFormatException e){
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Sets this location to be the current position in space
	 * @param networker The Networker to use to get the data
	 */
	public void setLocation(NetworkManager networker){
		bssids.clear();
		
		for(int y = 0; y < READING_AMT; y++){
			ArrayList<ScanResult> nets = networker.getFreshOrderedNetworks(WirelessLocator.WIRELESS_THRESHOLD);
			
			for(ScanResult res : nets){
				if(!bssids.containsKey(res.BSSID))
					bssids.put(res.BSSID, res.level);
				else
					bssids.put(res.BSSID, (res.level + bssids.get(res.BSSID))/2);
			}
			
		}
		
	}
	
	/**
	 * Returns true if the current location matches this WirelessLocation
	 * 
	 * This is where you want to meddle to improve wireless locating.
	 * 
	 * @return I think its obvious
	 */
	public AccuracyThreshold checkLocation(NetworkManager networker){
		
		if(bssids.size() <= 0)
			return null;
		
		List<AveragedNetworkInfo> nets = networker.getNetworkAverages(WirelessLocator.WIRELESS_THRESHOLD);
		
		if(nets.size() <= 0)
			return null;
		
		int dev = 0, cnt = 0;
		for(AveragedNetworkInfo net : nets){
			Integer i = bssids.get(net.bssid);
			int av = net.getAveragedLevel();
			if(i != null){
				dev += Math.abs(i * i - av * av);
				cnt++;
			} else {
				//dev -= WirelessLocator.WIRELESS_THRESHOLD; 
			}
			
			
			
		}
		
		lastLocate = null;
		
		if(cnt <= 0)
			return null;
		
		//TODO Test
		//dev = (int) Math.sqrt(dev / cnt);
		dev = (dev / cnt);
		
		for(AccuracyThreshold acc : AccuracyThreshold.values()){
			if(dev <= acc.max_deviation && (double)cnt / bssids.size() > .50){
				strength = acc.name() + " " + dev + "  " + acc.max_deviation;
				lastLocate = acc;
				return acc;
			}
		}
		
		
		
		return null;
	}
	
	/**
	 * Gets what the threshold was of the last successful locate
	 * @return
	 */
	public AccuracyThreshold getLastLocateThreashold(){
		return lastLocate;
	}
	
	/**
	 * Checks if the given BSSID is recognized by this WirelessLocation
	 * @param bssid The BSSID to check
	 * @return True if it is recognized, false otherwise
	 */
	public boolean checkBSSID(String bssid) {
		return bssids.containsKey(bssid);
	}
	
	/**
	 * Writes the data in this WirelessLocation to the given element
	 * @param e Element to write to
	 */
	public void writeToElement(Element e) {
		
		for(String s : bssids.keySet()){
			Element a = e.getOwnerDocument().createElement("network");
			a.setAttribute("bssid", s);
			a.setAttribute("strength", bssids.get(s).toString());
			e.appendChild(a);
		}
	}

	/**
	 * Gets the name for this WirelessLocation
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name for this location
	 * @param name The name to set it to
	 */
	public void setName(String name) {
		this.name = name;
	}

	public static final char COLON_PLACEHOLDER = '_';
	/**
	 * Gets the strength of the last successful locate
	 * @return A String or null if there is no recent successful locate
	 */
	public String getStrength(){
		return strength;
	}
	
	/**
	 * Sets the Cave this location refers to.
	 * @param cave The cave
	 */
	public void setCave(Cave cave) {
		this.cave = cave;
	}
	
	/**
	 * Gets the Cave this WirelessLocation refers to.
	 * @return
	 */
	public Cave getCave(){
		return cave;
	}

}
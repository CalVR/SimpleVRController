package com.example.simplevrcontroller.networking.location;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import android.net.wifi.ScanResult;

import com.example.simplevrcontroller.cave.Cave;
import com.example.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;
import com.example.simplevrcontroller.networking.NetworkManager;

public class WirelessLocation {
	
	public enum AccuracyThreshold {
		STRONG(.90, 5),
		AVERAGE(.70, 10),
		WEAK(.50, 20);
		
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

	public WirelessLocation(String name){
		this.name = name;
		
		bssids = new TreeMap<String, Integer>();
	}
	
	public void setLocation(NetworkManager networker){
		bssids.clear();
		
		for(int y = 0; y < 10; y++){
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
	 * @return
	 */
	public boolean checkLocation(NetworkManager networker, AccuracyThreshold thresh){
		
		ArrayList<AveragedNetworkInfo> nets = networker.getNetworkAverages(WirelessLocator.WIRELESS_THRESHOLD);
		
		int dev = 0, cnt = 0;
		for(AveragedNetworkInfo net : nets){
			Integer i = bssids.get(net.bssid);
			
			if(i != null){
				dev += Math.abs(Math.abs(i) - Math.abs(net.getAveragedLevel()));
				
			} else {
				dev -= WirelessLocator.WIRELESS_THRESHOLD; 
			}
			
			cnt++;
			
			//System.out.println(i + "  " + net.getAveragedLevel());
			//dev += i*i - res.level*res.level;
			
		}
		
		dev = Math.abs(dev / cnt);
		
		boolean is_located = dev < thresh.max_deviation;
		
		if(is_located)
			strength = thresh.name() + " " + dev + "      " + thresh.max_deviation;
		
		return is_located;
	}
	
	/**
	 * Checks if the given BSSID is recognized by this WirelessLocation
	 * @param bssid The BSSID to check
	 * @return True if it is recognized, false otherwise
	 */
	public boolean checkBSSID(String bssid) {
		return bssids.containsKey(bssid);
	}
	
	public void save(File f){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
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
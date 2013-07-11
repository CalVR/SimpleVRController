package com.example.simplevrcontroller.networking.location;

import java.io.File;
import java.util.ArrayList;

import android.net.wifi.ScanResult;

import com.example.simplevrcontroller.networking.Networker;

public class WirelessLocation {
	
	public enum AccuracyThreshold {
		STRONG(.90),
		AVERAGE(.70),
		WEAK(.50);
		
		public final double amount_present;
		
		AccuracyThreshold(double amount_present){
			this.amount_present = amount_present;
		}
		
		
	}

	private String name;
	private ArrayList<String> bssids;

	public WirelessLocation(String name){
		this.name = name;
		
		bssids = new ArrayList<String>();
	}
	
	public void setLocation(Networker networker){
		bssids.clear();
		
		for(int y = 0; y < 3; y++){
			ArrayList<ScanResult> nets = networker.getOrderedNetworks(-60);
			
			for(ScanResult res : nets)
				if(!bssids.contains(res.BSSID))
					bssids.add(res.BSSID);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns true if the current location matches this WirelessLocation
	 * @return
	 */
	public boolean checkLocation(Networker networker, AccuracyThreshold thresh){
		
		ArrayList<ScanResult> nets = networker.getOrderedNetworks(-60);
		
		int known = 0, unknown = 0;
		for(ScanResult res : nets){
			if(bssids.contains(res.BSSID))
				known++;
			else
				unknown++;
		}
		
		return known / (known + unknown + .0) > thresh.amount_present;
	}
	
	/**
	 * Checks if the given BSSID is recognized by this WirelessLocation
	 * @param bssid The BSSID to check
	 * @return True if it is recognized, false otherwise
	 */
	public boolean checkBSSID(String bssid) {
		return bssids.contains(bssid);
	}
	
	public void save(File f){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
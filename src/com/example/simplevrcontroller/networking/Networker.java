package com.example.simplevrcontroller.networking;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class Networker {
	
	private WifiManager wm;
	
	private Activity activity;

	public Networker(Activity a){
		this.activity = a;
		wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
	}
	
	public ArrayList<ScanResult> getOrderedNetworks(){
		return getOrderedNetworks(Integer.MIN_VALUE);
	}
	
	public ArrayList<ScanResult> getOrderedNetworks(int thresh){
		
		wm.startScan();
		
		List<ScanResult> results = wm.getScanResults();
		
		ArrayList<ScanResult> ordered = new ArrayList<ScanResult>();
		
		while(!results.isEmpty()){
			ordered.add(removeClosest(results));
		}
		
		ArrayList<ScanResult> remove = new ArrayList<ScanResult>();
		for(ScanResult s : ordered)
			if(s.level < thresh)
				remove.add(s);
		ordered.removeAll(remove);
		
		return ordered;
		
	}
	
	private ScanResult removeClosest(List<ScanResult> res){
		
		ScanResult highest = null;
		for(ScanResult sr : res){
			
			if(highest == null || highest.level < sr.level )
				highest = sr;
		}
		
		res.remove(highest);
		
		return highest;
	}

}

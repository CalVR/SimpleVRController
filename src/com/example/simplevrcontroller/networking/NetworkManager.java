package com.example.simplevrcontroller.networking;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class NetworkManager {

	private WifiManager wm;
	
	private Activity activity;

	public NetworkManager(Activity a){
		this.activity = a;
		wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
	}
	
	public ArrayList<ScanResult> getOrderedNetworks(){
		return getOrderedNetworks(Integer.MIN_VALUE);
	}
	
	public ArrayList<AveragedNetworkScanInfo> getOrderedNetworksAverage(long millis, int thresh){
		
		long time = System.currentTimeMillis();
		
		ArrayList<AveragedNetworkScanInfo> nets = new ArrayList<AveragedNetworkScanInfo>();
		
		while(System.currentTimeMillis() - time < millis){
			for(ScanResult res : getOrderedNetworks(thresh)){
				
				AveragedNetworkScanInfo inf = null;
				for(AveragedNetworkScanInfo n : nets){
					if(n.getScanResult().BSSID.equals(res.BSSID))
						inf = n;
				}
				
				if(inf == null)
					nets.add(new AveragedNetworkScanInfo(res));
				else
					inf.offer(res);
			}
					
		}
		
		return nets;
		
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
	
	public class AveragedNetworkScanInfo {
		
		private ScanResult result;
		private double averageLevel;
		
		public AveragedNetworkScanInfo(ScanResult res){
			result = res;
			averageLevel = res.level;
		}
		
		public void offer(ScanResult res){
			
			if(!res.BSSID.equals(result.BSSID))
				throw new InvalidParameterException("Must be for same network!");
			
			result = res;
			
			averageLevel = (averageLevel + res.level) / 2;
			
		}
		
		public ScanResult getScanResult(){
			return result;
		}
		
		public int getAveragedLevel(){
			return (int) averageLevel;
		}

	}

}

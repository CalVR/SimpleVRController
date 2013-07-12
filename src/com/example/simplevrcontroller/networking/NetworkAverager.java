package com.example.simplevrcontroller.networking;

import java.util.ArrayList;

import com.example.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;

import android.net.wifi.ScanResult;

public class NetworkAverager {
	
	private ArrayList<AveragedNetworkInfo> networks;
	
	public NetworkAverager(){
		networks = new ArrayList<AveragedNetworkInfo>();
	}
	
	public void calculateAverages(ArrayList<ScanResult> scans){
		
	}
	
	public class AveragedNetworkInfo {
		
		public final String bssid;
		public final String ssid;
		private ArrayList<Integer> nums;
		private int store;

		public AveragedNetworkInfo(String ssid, String bssid, int storage) {
			this.ssid = ssid;
			this.bssid = bssid;
			store = storage;

		}

		public void addNumber(int num){
			if(nums.size() >= store)
				nums.remove(0);
			
			nums.add(num);
		}

		public int getAveragedLevel() {
			
			int total = 0;
			
			for(Integer i : nums)
				total += i;
			
			return total / nums.size();
		}

	}

	public ArrayList<AveragedNetworkInfo> getAverages() {
		return networks;
	}

}

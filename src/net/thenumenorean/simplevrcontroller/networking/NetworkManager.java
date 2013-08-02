package net.thenumenorean.simplevrcontroller.networking;

import java.util.ArrayList;
import java.util.List;

import net.thenumenorean.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


public class NetworkManager {
	
	public static final int DEFAULT_PRESET_SERVER_PORT = 12012;
	public static final int DEFAULT_GAMEPAD_SERVER_PORT = 8888;

	private WifiManager wm;

	private Activity activity;
	private ArrayList<ScanResult> orderedNetworks;

	private NetworkAverager averager;
	private NetworkManager nm;
	private BroadcastReceiver br;

	public NetworkManager(Activity a) {
		this.activity = a;
		wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		
		orderedNetworks = new ArrayList<ScanResult>();
		
		nm = this;
		
		averager = new NetworkAverager();
		
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {
				
				List<ScanResult> results = wm.getScanResults();

				orderedNetworks.clear();

				while (!results.isEmpty()) {
					orderedNetworks.add(removeClosest(results));
				}
				
				synchronized(nm) {
					nm.notifyAll();
				}
				
				averager.calculateAverages(orderedNetworks);
				
				while (!wm.startScan()){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
	}
	
	public List<AveragedNetworkInfo> getNetworkAverages(){
		return averager.getAverages();
	}

	public List<AveragedNetworkInfo> getNetworkAverages(int thresh) {
		
		List<AveragedNetworkInfo> newList = new ArrayList<AveragedNetworkInfo>(), av = getNetworkAverages();
		
		for (AveragedNetworkInfo s : av)
			if (s.getAveragedLevel() >= thresh)
				newList.add(s);
			

		return newList;

	}

	public ArrayList<ScanResult> getOrderedNetworks() {
		return orderedNetworks;
	}

	public ArrayList<ScanResult> getOrderedNetworks(int thresh) {

		ArrayList<ScanResult> newOrdered = new ArrayList<ScanResult>();
		for (ScanResult s : orderedNetworks)
			if (s.level >= thresh)
				newOrdered.add(s);

		return newOrdered;

	}

	public ArrayList<ScanResult> getFreshOrderedNetworks() {
		try {
			synchronized(this){
				this.wait();
			} 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return orderedNetworks;
	}

	public ArrayList<ScanResult> getFreshOrderedNetworks(int thresh) {
		try {
			synchronized(this){
				this.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getOrderedNetworks(thresh);
	}
	
	private ScanResult removeClosest(List<ScanResult> res) {

			ScanResult highest = null;
			for (ScanResult sr : res) {

				if (highest == null || highest.level < sr.level)
					highest = sr;
			}

			res.remove(highest);

			return highest;
		}

	public void resume() {

		HandlerThread handlerThread = new HandlerThread("WifiThread");
		handlerThread.start();
		
		Looper looper = handlerThread.getLooper();
		// Create a handler for the service
		Handler handler = new Handler(looper);
		
		activity.registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), "" , handler);
		
		wm.startScan();
		
	}
	
	public void pause(){
		activity.unregisterReceiver(br);
	}
	
	public WifiManager getWifiManager() {
		return wm;
	}

}

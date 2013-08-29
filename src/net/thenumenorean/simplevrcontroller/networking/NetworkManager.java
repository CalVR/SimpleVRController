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
						e.printStackTrace();
					}
				}
				
			}
		};
		
	}
	
	/**
	 * Gets all the averages for the networks (helper method, returns result from NetworkAverager)
	 * @return A list 
	 */
	public List<AveragedNetworkInfo> getNetworkAverages(){
		return averager.getAverages();
	}

	/**
	 * Gets all the network averages that have an average above a certain threshold
	 * @param thresh The threshold to use
	 * @return A list
	 */
	public List<AveragedNetworkInfo> getNetworkAverages(int thresh) {
		
		List<AveragedNetworkInfo> newList = new ArrayList<AveragedNetworkInfo>(), av = getNetworkAverages();
		
		for (AveragedNetworkInfo s : av)
			if (s.getAveragedLevel() >= thresh)
				newList.add(s);
			

		return newList;

	}

	/**
	 * Gets the last set of received data, ordered with the best signal first
	 * @return A potentially empty list
	 */
	public ArrayList<ScanResult> getOrderedNetworks() {
		return orderedNetworks;
	}

	/**
	 * Same as getOrderedNetworks(), but only returns values above the given value.
	 * @param thresh The threshold for the cutof point
	 * @return A potentially empty list
	 */
	public ArrayList<ScanResult> getOrderedNetworks(int thresh) {

		ArrayList<ScanResult> newOrdered = new ArrayList<ScanResult>();
		for (ScanResult s : orderedNetworks)
			if (s.level >= thresh)
				newOrdered.add(s);

		return newOrdered;

	}

	/**
	 * Waits until a new set of network data is availible then returns it
	 * @return A potentially empty List
	 */
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

	/**
	 * Same as getFreshorderedNetworks(), but only returns them that have signal above the given
	 * threshold
	 * @param thresh The cutoff point
	 * @return A potentially empty list
	 */
	public ArrayList<ScanResult> getFreshOrderedNetworks(int thresh) {
		try {
			synchronized(this){
				this.wait();
			}
		} catch (InterruptedException e) {
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
	
	/**
	 * Gets the Wifimanager this NetworkManager uses to get data.
	 * @return A Wifimanager
	 */
	public WifiManager getWifiManager() {
		return wm;
	}

}

package com.example.simplevrcontroller.networking;

import java.util.ArrayList;
import java.util.List;

import com.example.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class NetworkManager {

	private WifiManager wm;

	private Activity activity;
	private NetworkRefresher refresher;
	private ArrayList<ScanResult> orderedNetworks;

	private NetworkAverager averager;

	public NetworkManager(Activity a) {
		this.activity = a;
		wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);

		refresher = new NetworkRefresher();
		NetworkManager[] tmp = new NetworkManager[1];
		tmp[0] = this;
		refresher.execute(tmp);
		
		averager = new NetworkAverager();
	}
	
	public ArrayList<AveragedNetworkInfo> getNetworkAverages(){
		return averager.getAverages();
	}

	public ArrayList<AveragedNetworkInfo> getNetworkAverages(int thresh) {

		ArrayList<AveragedNetworkInfo> newList = new ArrayList<AveragedNetworkInfo>();
		for (AveragedNetworkInfo s : getNetworkAverages())
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
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderedNetworks;
	}

	public ArrayList<ScanResult> getFreshOrderedNetworks(int thresh) {
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getOrderedNetworks(thresh);
	}

	public WifiManager getWifiManager() {
		return wm;
	}

	private class NetworkRefresher extends
			AsyncTask<NetworkManager, Void, Void> {

		boolean waiting = false;

		@Override
		protected Void doInBackground(NetworkManager... v) {

			NetworkManager man = v[0];

			IntentFilter i = new IntentFilter();
			i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			activity.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context c, Intent i) {
					waiting = false;
				}
			}, i);

			while (1 == 1) {

				waiting = true;

				wm.startScan();

				while (waiting) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				List<ScanResult> results = wm.getScanResults();

				orderedNetworks.clear();

				while (!results.isEmpty()) {
					orderedNetworks.add(removeClosest(results));
				}

				man.notifyAll();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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

	}

}

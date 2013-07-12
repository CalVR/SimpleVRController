package com.example.simplevrcontroller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.simplevrcontroller.networking.NetworkManager;
import com.example.simplevrcontroller.networking.NetworkManager.AveragedNetworkScanInfo;
import com.example.simplevrcontroller.networking.location.WirelessLocation;
import com.example.simplevrcontroller.networking.location.WirelessLocator;
import com.example.simplevrcontroller.tools.ListComparer;

public class WirelessActivity extends Activity {

	WirelessRunner runner;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wireless);

		NetworkManager networker = new NetworkManager(this);

		runner = new WirelessRunner(networker, this);
		
		handler = new Handler();
		
		this.getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class WirelessRunner implements Runnable {

		private NetworkManager networker;
		private WirelessActivity wa;
		private TextView top;
		private TextView list;
		private TextView time;
		private long running;
		private TextView similarity;
		private WirelessLocator locator;
		private WirelessLocation old;

		public WirelessRunner(final NetworkManager networker,
				WirelessActivity wirelessActivity) {
			this.networker = networker;
			this.wa = wirelessActivity;
			
			locator = new WirelessLocator(networker);
			
			running = 0;

			top = (TextView) wa.findViewById(R.id.topBSSID);
			top.setTextSize(30);

			list = (TextView) wa.findViewById(R.id.bssidList);
			
			time = (TextView) wa.findViewById(R.id.wirelessTime);
			
			similarity = (TextView) wa.findViewById(R.id.similarity);
			
			Button b = (Button) wa.findViewById(R.id.symbutton);
			b.setOnClickListener(new OnClickListener(){

				

				@Override
				public void onClick(View view) {
					
					String name = "Test";
					int num = 0;
					while(locator.getLocation(name + ++num) != null);
					
					locator.createNewLocation(name + num);
					
				}
				
			});
		}

		@Override
		public void run() {

				ArrayList<AveragedNetworkScanInfo> all = networker.getOrderedNetworksAverage(1000, WirelessLocator.WIRELESS_THRESHOLD);
				list.setText("");
				
				ArrayList<String> scanBSSIDS = new ArrayList<String>();

				List<WirelessLocation> locList = locator.getCurrentLocation();
				WirelessLocation wl = null;
				if(!locList.isEmpty())
					wl = locList.get(0);
				
				boolean first = true;
				for (AveragedNetworkScanInfo net : all) {
					ScanResult res = net.getScanResult();
					scanBSSIDS.add(res.BSSID);					
					String ssid = res.SSID;
					Spanned span;
					
					String bssid = res.BSSID;
					
					if(wl != null && wl.checkBSSID(bssid)){
						bssid = "<font color=\"yellow\">" + bssid + "</font>";
					} else if(old != null && old.checkBSSID(bssid))
						bssid = "<font color=\"purple\">" + bssid + "</font>";
					
					if(ssid.equals("UCSD-GUEST"))
						span = Html.fromHtml("<font color=\"green\">" + ssid + "</font>" + " {" + bssid + "} "
							+ res.level + "<br>");
					else if(ssid.equals("UCSD-PROTECTED"))
						span = Html.fromHtml("<font color=\"red\">" + ssid + "</font>" + " {" + bssid + "} "
							+ res.level + "<br>");
					else if(ssid.equals("eduroam"))
						span = Html.fromHtml("<font color=\"blue\">" + ssid + "</font>" + " {" + bssid + "} "
							+ res.level + "<br>");
					else {
						span = Html.fromHtml(ssid + " {" + bssid + "} " + net.getAveragedLevel() + "<br>");
					}
					
					if (first) {
						
						top.setText(span);
						first = false;
						
					} else
						list.append(span);
				}
				
				similarity.setText("Location: " + (wl == null ? "<unknown>" : wl.getName()));
				
				for(WirelessLocation l : locList)
					similarity.append("\n" + l.getName() + " " + l.getStrength());
				
				if(wl != null)
					old = wl;
				
				
				time.setText("Time: " + running / 10);
				
				running += 5;
				
				handler.postDelayed(runner, 0);

		}

	}

	@Override
    public void onPause() {

        if (handler != null)
            handler.removeCallbacks(runner);

        super.onPause();
	}
	
	@Override
    public void onResume() {
        super.onResume();

        if (handler != null)
            handler.post(runner);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
			
		}
		
		if(item.getTitle().equals("Gamepad")){
			
			Intent intent = new Intent(this, GamepadActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
			
			return true;
		}
		
		if (item.getTitle().equals("Wireless Manager")) {

			Intent intent = new Intent(this, WirelessActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
		
	}

}

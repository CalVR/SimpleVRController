package com.example.simplevrcontroller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.simplevrcontroller.cave.Cave;
import com.example.simplevrcontroller.cave.CaveManager;
import com.example.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;
import com.example.simplevrcontroller.networking.NetworkManager;
import com.example.simplevrcontroller.networking.location.WirelessLocation;
import com.example.simplevrcontroller.networking.location.WirelessLocator;

public class WirelessActivity extends Activity {

	private WirelessRunner runner;
	private Handler handler;
	private NetworkManager networker;
	private long start_time;
	private String currentCave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wireless);

		networker = new NetworkManager(this);

		runner = new WirelessRunner(networker, this);

		handler = new Handler();

		this.getActionBar().setHomeButtonEnabled(true);
		
		Spinner spin = (Spinner) this.findViewById(R.id.caveSpinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(spinnerAdapter);
		
		for(Cave c : CaveManager.getCaveManager().getCaves())
			spinnerAdapter.add(c.getName());
		
		spinnerAdapter.notifyDataSetChanged();
		spin.setBackgroundColor(Color.LTGRAY);
		spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				currentCave = parent.getItemAtPosition(pos).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});
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
		private TextView similarity;
		private WirelessLocator locator;
		private WirelessLocation old;

		public WirelessRunner(final NetworkManager networker,
				WirelessActivity wirelessActivity) {
			this.networker = networker;
			this.wa = wirelessActivity;

			locator = new WirelessLocator(networker);

			top = (TextView) wa.findViewById(R.id.topBSSID);
			top.setTextSize(30);

			list = (TextView) wa.findViewById(R.id.bssidList);

			time = (TextView) wa.findViewById(R.id.wirelessTime);

			similarity = (TextView) wa.findViewById(R.id.similarity);

			Button b = (Button) wa.findViewById(R.id.symbutton);
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					
					AsyncTask.execute(new Runnable(){

						@Override
						public void run() {
							CaveManager.getCaveManager().getCave(currentCave).getWirelessLocation().setLocation(networker);
							
							try {
								
								MediaPlayer mp = MediaPlayer.create(WirelessActivity.this, R.raw.beep1);
					            mp.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

					                @Override
					                public void onCompletion(MediaPlayer mp) {
					                    // TODO Auto-generated method stub
					                    mp.release();
					                }

					            });   
					            mp.start();
								
							} catch (Exception e) {
							}
						}
						
					});
					
				}

			});
		}

		@Override
		public void run() {
			

			List<AveragedNetworkInfo> all = networker
					.getNetworkAverages(WirelessLocator.WIRELESS_THRESHOLD);
			list.setText("");

			ArrayList<String> scanBSSIDS = new ArrayList<String>();

			List<WirelessLocation> locList = locator.getCurrentLocation();
			WirelessLocation wl = null;
			if (!locList.isEmpty())
				wl = locList.get(0);

			boolean first = true;
			for (AveragedNetworkInfo net : all) {
				
				scanBSSIDS.add(net.bssid);
				String ssid = net.ssid;
				Spanned span;

				String bssid = net.bssid;

				if (wl != null && wl.checkBSSID(bssid)) {
					bssid = "<font color=\"yellow\">" + bssid + "</font>";
				} else if (old != null && old.checkBSSID(bssid))
					bssid = "<font color=\"purple\">" + bssid + "</font>";

				if (ssid.equals("UCSD-GUEST"))
					span = Html.fromHtml("<font color=\"green\">" + ssid
							+ "</font>" + " {" + bssid + "} "
							+ net.getAveragedLevel() + "<br>");
				else if (ssid.equals("UCSD-PROTECTED"))
					span = Html.fromHtml("<font color=\"red\">" + ssid
							+ "</font>" + " {" + bssid + "} "
							+ net.getAveragedLevel() + "<br>");
				else if (ssid.equals("eduroam"))
					span = Html.fromHtml("<font color=\"blue\">" + ssid
							+ "</font>" + " {" + bssid + "} "
							+ net.getAveragedLevel() + "<br>");
				else {
					span = Html.fromHtml(ssid + " {" + bssid + "} "
							+ net.getAveragedLevel() + "<br>");
				}

				if (first) {

					top.setText(span);
					first = false;

				} else
					list.append(span);
			}

			similarity.setText("Location: "
					+ (wl == null ? "<unknown>" : wl.getCave().getName()));

			for (WirelessLocation l : locList)
				similarity.append("\n" + l.getCave().getName() + " " + l.getStrength());

			if (wl != null)
				old = wl;

			time.setText("Time: " + (System.currentTimeMillis() - start_time) / 1e3);

			handler.postDelayed(runner, 500);
			
			
			
		}
		

	}

	@Override
	public void onPause() {

		if (handler != null)
			handler.removeCallbacks(runner);

		networker.pause();

		super.onPause();
	}

	@Override
	public void onResume() {
		
		start_time = System.currentTimeMillis();

		if (handler != null)
			handler.post(runner);

		networker.resume();

		super.onResume();
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

		if (item.getTitle().equals("Gamepad")) {

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

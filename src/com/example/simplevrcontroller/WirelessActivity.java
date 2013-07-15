package com.example.simplevrcontroller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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

import com.example.simplevrcontroller.networking.NetworkAverager.AveragedNetworkInfo;
import com.example.simplevrcontroller.networking.NetworkManager;
import com.example.simplevrcontroller.networking.location.WirelessLocation;
import com.example.simplevrcontroller.networking.location.WirelessLocator;

public class WirelessActivity extends Activity {

	private WirelessRunner runner;
	private Handler handler;
	private NetworkManager networker;
	private long start_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wireless);

		networker = new NetworkManager(this);

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

					String name = "Test";
					int num = 0;
					while (locator.getLocation(name + ++num) != null)
						;

					locator.createNewLocation(name + num);

				}

			});
		}

		@Override
		public void run() {
			

			ArrayList<AveragedNetworkInfo> all = networker
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
					+ (wl == null ? "<unknown>" : wl.getName()));

			for (WirelessLocation l : locList)
				similarity.append("\n" + l.getName() + " " + l.getStrength());

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

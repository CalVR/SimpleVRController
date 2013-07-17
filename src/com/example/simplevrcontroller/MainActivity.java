package com.example.simplevrcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.example.simplevrcontroller.cave.Cave;
import com.example.simplevrcontroller.cave.CaveManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Connection connection;
	boolean connected;
	TextView tv;
	ScrollView tscroll;

	public static final String CAVES = "caves.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		connected = false;

		super.onCreate(savedInstanceState);
		
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().setBackgroundDrawableResource(R.drawable.techback);
		
		this.getActionBar().setHomeButtonEnabled(true);
		
		setContentView(R.layout.activity_main);
		
		tscroll = (ScrollView) this.findViewById(R.id.tscroller);

		tv = (TextView) this.findViewById(R.id.log);
		tv.setTextSize(12);
		tv.setWidth(400);
		tv.setMovementMethod(new ScrollingMovementMethod());
		
		//Load caves
		try {
			File f = new File(this.getFilesDir(), CAVES);
			f.createNewFile();
			CaveManager.getCaveManager().load(new FileInputStream(f));
		} catch (Exception e) {
			log("Error loading caves: " + e.getMessage());
			e.printStackTrace();
		}
		
		CaveManager.getCaveManager().addCave(new Cave("Tester", "137.110.119.227"));

		Spinner spin = (Spinner) this.findViewById(R.id.Hosts);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(spinnerAdapter);
		
		
		
		spinnerAdapter.add("137.110.119.227");
		spinnerAdapter.add("VROOMCalVR");
		spinnerAdapter.add("DWall");
		spinnerAdapter.add("StarCave");
		spinnerAdapter.add("NEXCave");
		spinnerAdapter.add("TourCave");
		spinnerAdapter.notifyDataSetChanged();
		spin.setBackgroundColor(Color.LTGRAY);
		spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String s = parent.getItemAtPosition(pos).toString();

				try {
					for (char c : s.replace(".", "").toCharArray())
						Integer.parseInt("" + c);

				} catch (NumberFormatException e) {
					s = s + ".calit2.net";
				}

				log("Connecting to: " + s);

				connectToServer(s, 12012);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
				
			}

		});
		
		Button recon = ((Button) this.findViewById(R.id.connect));
		recon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				connectToServer(null, 0);
				
			}
			
		});
		
		Button bAll = ((Button) this.findViewById(R.id.buttonALL));
		bAll.setOnClickListener(new BListener(0));

		bAll = ((Button) this.findViewById(R.id.ButtonCOMP));
		bAll.setOnClickListener(new BListener(4));

		bAll = ((Button) this.findViewById(R.id.buttonHEALTHY));
		bAll.setOnClickListener(new BListener(6));

		bAll = ((Button) this.findViewById(R.id.buttonINF));
		bAll.setOnClickListener(new BListener(1));

		bAll = ((Button) this.findViewById(R.id.buttonINFSYM));
		bAll.setOnClickListener(new BListener(2));

		bAll = ((Button) this.findViewById(R.id.ButtonTIME));
		bAll.setOnClickListener(new BListener(3));

		// Wrong
		bAll = ((Button) this.findViewById(R.id.buttonTOGGLE));
		bAll.setOnClickListener(new BListener(7));

		bAll = ((Button) this.findViewById(R.id.ButtonTOP200));
		bAll.setOnClickListener(new BListener(5));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean connectToServer(String server, int port) {
		if(server != null)
			connection = new Connection(server, port);
		
		if(connection == null){
			System.out.println("Connection not created!");
			return false;
		}

		try {
			Exception e = new ConnectionInitializer().execute(connection).get();
			if (e != null)
				throw e;

			connected = true;
			tv.setTextColor(Color.BLACK);
			log("Connected!");

		} catch (Exception e1) {
			// e1.printStackTrace();
			tv.setTextColor(Color.RED);
			log("Error Connecting: " + e1.getMessage());
			connected = false;
			return false;
		}

		return true;
	}

	public void log(String s) {
		tv.append(s + "\n");
		tscroll.fullScroll(View.FOCUS_DOWN);
	}

	private class BListener implements OnClickListener {

		private int num;

		public BListener(int num) {
			this.num = num;
		}

		@Override
		public void onClick(View arg0) {
			Button b = (Button) arg0;

			if (connected) {
				try {
					Uri notification = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(
							getApplicationContext(), notification);
					r.play();
				} catch (Exception e) {
				}

				connection.send(num);
				log("Sending " + num);
				
			} else {
				try {
					Uri notification = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_ALARM);
					Ringtone r = RingtoneManager.getRingtone(
							getApplicationContext(), notification);
					r.play();
					Thread.sleep(1000);
					r.stop();
				} catch (Exception e) {
				}
			}
		}

	}

	private class ConnectionInitializer extends
			AsyncTask<Connection, Void, Exception> {

		@Override
		protected Exception doInBackground(Connection... arg0) {
			for (Connection c : arg0)
				try {
					c.init();
				} catch (Exception e) {
					return e;
				}
			return null;
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		if(item.getTitle().equals("Wireless Manager")){
			
			Intent intent = new Intent(this, WirelessActivity.class);
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
		
		switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	public void onPause(){
		
		
		try {
			File f= new File(this.getFilesDir(), CAVES);
			f.createNewFile();
			CaveManager.getCaveManager().save(new FileOutputStream(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onPause();
	}

}

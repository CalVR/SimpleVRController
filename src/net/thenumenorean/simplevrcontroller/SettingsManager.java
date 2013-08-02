package net.thenumenorean.simplevrcontroller;

import net.thenumenorean.simplevrcontroller.cave.Cave;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.simplevrcontroller.R;

public class SettingsManager {
	
	public static final int DEFAULT_MAX_SPEED = 250;
	private SeekBar speed;
	private TextView speed_text;
	private Cave cave;
	
	public SettingsManager(MainActivity ma){
		
		speed_text = (TextView) ma.findViewById(R.id.speed_text);
		speed = (SeekBar) ma.findViewById(R.id.max_speed);
		speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int prog, boolean arg2) {
				speed_text.setText("Max Speed: " + prog);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {
				cave.setMaxSpeed(bar.getProgress());
			}
			
		});
	}
	
	public void load(Cave c){
		cave = c;
		
		speed.setProgress(c.getMaxSpeed());
		speed_text.setText("Max Speed: " + c.getMaxSpeed());
	}

}

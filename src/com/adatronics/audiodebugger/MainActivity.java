package com.adatronics.audiodebugger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import com.adatronics.audiodebugger.R;

public class MainActivity extends Activity {
	private static final String TAG = "AudioDebugger";
	CheckBox Both_Radio;
	CheckBox Left_Radio;
	CheckBox Right_Radio;
	EditText Both_freq;
	EditText Both_offset;
	EditText Left_freq;
	EditText Right_freq;
	Button playorstop;
	SeekBar volume;
	private int check_seq = 0;
	private float freq = 0;
	private float offset = 0;
	private int duration = 3;
	private final int sampleRate = 44100;
	private int numSamples = duration * sampleRate;
	private byte generatedSine[] = new byte[2 * numSamples];
	private double sample[] = new double[numSamples];
	private boolean isplay = false;
	private float vol = 0.70f;
	int mPlayBufferSize = AudioTrack.getMinBufferSize(sampleRate,
			AudioFormat.CHANNEL_CONFIGURATION_STEREO,
			AudioFormat.ENCODING_PCM_16BIT);
	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
			sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
			AudioFormat.ENCODING_PCM_16BIT, mPlayBufferSize,
			AudioTrack.MODE_STREAM);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_audio_debegger);
		playorstop = (Button) findViewById(R.id.play_or_stop);
		Both_Radio = (CheckBox) findViewById(R.id.both_channel);
		Left_Radio = (CheckBox) findViewById(R.id.left_channel);
		Right_Radio = (CheckBox) findViewById(R.id.right_channel);
		Both_freq = (EditText) findViewById(R.id.both_freq);
		Both_offset = (EditText) findViewById(R.id.both_offset);
		Left_freq = (EditText) findViewById(R.id.left_freq);
		Right_freq = (EditText) findViewById(R.id.right_freq);
		volume = (SeekBar) findViewById(R.id.volume);
		volume.setMax(100);
		volume.setProgress(70);
		volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				vol = (float) (seekBar.getProgress())
						/ (float) (seekBar.getMax());
				Log.d(TAG, String.valueOf(vol));
				if (check_seq == 0) {
					audioTrack.setStereoVolume(vol, vol);
				} else if (check_seq == 1) {
					audioTrack.setStereoVolume(vol, 0);
				} else {
					audioTrack.setStereoVolume(0, vol);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				// vol = (float) (seekBar.getProgress())
				// / (float) (seekBar.getMax());
			}
		});
		Both_Radio.setChecked(true);
		Left_Radio.setChecked(false);
		Right_Radio.setChecked(false);
		Both_freq.setEnabled(true);
		Both_offset.setEnabled(true);
		Left_freq.setEnabled(false);
		Right_freq.setEnabled(false);
		Both_freq.setFocusable(true);
		Both_freq.setFocusableInTouchMode(true);
		Both_freq.requestFocus();
		Both_Radio
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							check_seq = 0;
							Left_Radio.setChecked(false);
							Right_Radio.setChecked(false);
							Both_freq.setEnabled(true);
							Both_offset.setEnabled(true);
							Left_freq.setEnabled(false);
							Right_freq.setEnabled(false);
							Both_freq.setFocusable(true);
							Both_freq.setFocusableInTouchMode(true);
							Both_freq.requestFocus();
						}
					}

				});

		Left_Radio
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							check_seq = 1;
							Both_Radio.setChecked(false);
							Right_Radio.setChecked(false);
							Both_freq.setEnabled(false);
							Both_offset.setEnabled(false);
							Left_freq.setEnabled(true);
							Right_freq.setEnabled(false);
							Left_freq.setFocusable(true);
							Left_freq.setFocusableInTouchMode(true);
							Left_freq.requestFocus();
						}
					}

				});

		Right_Radio
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							check_seq = 2;
							Both_Radio.setChecked(false);
							Left_Radio.setChecked(false);
							Both_freq.setEnabled(false);
							Both_offset.setEnabled(false);
							Left_freq.setEnabled(false);
							Right_freq.setFocusable(true);
							Right_freq.setFocusableInTouchMode(true);
							Right_freq.requestFocus();
							Right_freq.setEnabled(true);

						}
					}

				});
	}

	@SuppressLint("NewApi")
	public void Play_Or_Stop(View view) {
		if (!isplay) {
			isplay = true;
			playorstop.setText("    Stop    ");
		} else {
			isplay = false;
			playorstop.setText("    Play    ");
		}
		if (check_seq == 0) {
			if (!Both_freq.getText().toString().isEmpty()
					&& !Both_offset.getText().toString().isEmpty()) {
				freq = Float.parseFloat(Both_freq.getText().toString());
				offset = Float.parseFloat(Both_offset.getText().toString());
				genSineTone(freq, offset);
				Thread playThread = new Thread(new PlayThread(), "play");
				playThread.start();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);
				alertDialogBuilder.setTitle("Oops");
				alertDialogBuilder
						.setMessage("Please input the valid float number!")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}

		} else if (check_seq == 1) {
			if (!Left_freq.getText().toString().isEmpty()) {
				freq = Float.parseFloat(Left_freq.getText().toString());
				offset = 0;
				genSineTone(freq, offset);
				Thread playThread = new Thread(new PlayThread(), "play");
				playThread.start();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);
				alertDialogBuilder.setTitle("Oops");
				alertDialogBuilder
						.setMessage("Please input the valid float number!")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		} else {
			if (!Right_freq.getText().toString().isEmpty()) {
				freq = Float.parseFloat(Right_freq.getText().toString());
				offset = 0;
				genSineTone(freq, offset);
				Thread playThread = new Thread(new PlayThread(), "play");
				playThread.start();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);
				alertDialogBuilder.setTitle("Oops");
				alertDialogBuilder
						.setMessage("Please input the valid float number!")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}
	}

	private void genSineTone(float freqOfTone, float offset) {
		// fill out the array
		Log.d(TAG, String.valueOf(freqOfTone) + String.valueOf(offset));
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone)
					+ Math.PI * offset);
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSine[idx++] = (byte) (val & 0x00ff);
			generatedSine[idx++] = (byte) ((val & 0xff00) >>> 8);

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void playWav() {

		audioTrack.play();
		if (check_seq == 0) {

			while (isplay) {
				audioTrack.write(generatedSine, 0, generatedSine.length);
			}
		} else if (check_seq == 1) {
			while (isplay) {
				audioTrack.write(generatedSine, 0, generatedSine.length);
			}
		} else {
			while (isplay) {
				audioTrack.write(generatedSine, 0, generatedSine.length);
			}
		}
		audioTrack.stop();
		// if (audioTrack != null) {
		// audioTrack.stop();
		// audioTrack.release();
		// audioTrack = null;
		// }
	}

	private class PlayThread implements Runnable {

		@Override
		public void run() {
			playWav();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (audioTrack != null) {
			audioTrack.release();
			audioTrack = null;
		}
	}

}
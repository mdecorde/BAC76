package fr.hnit.bac76;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class NextBac extends Activity {
	TextView helloTextView;
	private Horaires horaires;
	private ListView previousList;
	private ListView currentList;
	private ListView nextList;
	private TextView previousText;
	private TextView currentText;
	private TextView nextText;
	private Spinner leftSpinner, rightSpinner;
	private File workDir;
	private File tsvFile;
	private File binFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next_bac);

		View helloView = this.findViewById(R.id.Hell);
		if (helloView != null && helloView instanceof TextView) {
			helloTextView = (TextView)helloView;
		} else {
			return;
		}

		try {
			String sdcardpath = Environment.getExternalStorageDirectory().getPath();
			//String sdcardpath = Environment.getDataDirectory().getPath();
			workDir = new File(sdcardpath, "bac76");
			workDir.mkdir();
			if (!workDir.exists()) {
				show("Working directory was not created: "+workDir);
				return;
			}

			tsvFile = new File(workDir, "horaires.tsv");
			binFile = new File(workDir, "horaires.bin");

			createHorairesBinIfNeeded();
			loadHoraires();
			if (horaires == null) {
				show("Error: no horaires created");
				return;
			} else {
				show("LEFTS: "+horaires.lefts);
				show("RIGHTS: "+horaires.rights);
			}

			rightSpinner = (Spinner) this.findViewById(R.id.rightSpinner);
			rightSpinner.setAdapter(new ArrayAdapter<String>(this, R.drawable.mylist, horaires.rights));
			rightSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					updateGridValues(horaires.rights, rightSpinner.getSelectedItemPosition());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) { }
			});

			// Spinners
			leftSpinner = (Spinner) this.findViewById(R.id.leftSpinner);
			leftSpinner.setAdapter(new ArrayAdapter<String>(this, R.drawable.mylist, horaires.lefts));
			leftSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					updateGridValues(horaires.lefts, leftSpinner.getSelectedItemPosition());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) { }
			});

			//		show(horaires.toString());
			previousList = (ListView) this.findViewById(R.id.previousList);
			currentList = (ListView) this.findViewById(R.id.currentList);
			currentList.setBackgroundColor(Color.LTGRAY);
			nextList = (ListView) this.findViewById(R.id.nextList);

			previousText = (TextView) this.findViewById(R.id.previousText);
			previousText.setGravity(Gravity.FILL);
			currentText = (TextView) this.findViewById(R.id.currentText);
			currentText.setGravity(Gravity.FILL);
			nextText = (TextView) this.findViewById(R.id.nextText);
			nextText.setGravity(Gravity.FILL);
			//containerTable = (TableLayout) this.findViewById(R.id.containerTable);

			//Object settingsAction = this.findViewById(R.menu.next_bac);

			updateGridValues(horaires.lefts, leftSpinner.getSelectedItemPosition());
		} catch(Exception e) {
			show(e.getMessage());
		}
	}

	private void loadHoraires() throws Exception {
		if (horaires == null) {
			horaires = Horaires.read(binFile);
		}
	}

	private void createHorairesBin() throws Exception {
		if (!tsvFile.exists()) {
			show("No TSV file found at "+tsvFile.getAbsolutePath()+"\n This file is mandatory see README file.");
			// use default one
			return;
		}

		BuildHoraires bh = new BuildHoraires(tsvFile, "UTF-8");
		horaires = bh.createHoraires();
		if (horaires == null) {
			show("Error: while creating horaires: null");
		} else {
			Horaires.write(binFile, horaires);
		}
	}

	private void createHorairesBinIfNeeded() throws Exception {
		if (!binFile.exists()) {
			createHorairesBin();
		}
	}

	public void reloadHoraires() throws Exception {
		if (!binFile.delete()) {
			throw new IllegalStateException("reloadHoraires: Could not delete binary horaires: "+binFile);
		}
		createHorairesBin();
	}

	public void show(String mess) {
		helloTextView.setText(mess);
		helloTextView.setGravity(Gravity.FILL);
	}

	Calendar c = Calendar.getInstance(); 
	protected void updateGridValues(ArrayList<String> rights, int id) {
		if (horaires == null) {
			show("Horaires not set");
		}

		if (id == AdapterView.INVALID_POSITION) {
			String[] nextValues = {};
			nextText.setText("-");
			previousList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nextValues));
			currentList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nextValues));
			nextList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nextValues));
			return;
		}

		// get current time in minutes
		int hours = c.get(Calendar.HOUR);
		if (hours <= 12 && c.get(Calendar.AM_PM) == Calendar.PM) hours += 12;
		int time = (hours*60) + c.get(Calendar.MINUTE);

		// get current day
		int day = c.get(Calendar.DAY_OF_WEEK) - 1;

		// test if day is free
		int daym = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		if (ferie(daym, month)) day = 8;
		if (day == 0) day = 7; // fix sunday

		if (id > 0) {
			String previousSite = rights.get(id-1);
			List<Horaire> previousValues = horaires.get(previousSite).get(day);
			goToNextBac(previousText, previousList, previousValues, previousSite, time);
		} else {
			notimes(previousText, previousList);
		}

		String site = rights.get(id);
		List<Horaire> currentValues = horaires.get(site).get(day);
		goToNextBac(currentText, currentList, currentValues, site, time);

		if (id != rights.size() - 1) {
			String nextSite = rights.get(id+1);
			List<Horaire> nextValues = horaires.get(nextSite).get(day);
			goToNextBac(nextText, nextList, nextValues, nextSite, time);
		} else {
			noNext();
		}
	}

	/**
	 * Fill listview with no times
	 * 
	 * @param textfield
	 * @param list
	 */
	private void notimes(TextView textfield, ListView list) {
		String[] tmp = {};
		textfield.setText("");
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,tmp));
	}

	/**
	 * Go to next time of the list view
	 * 
	 * @param textfield
	 * @param list
	 * @param values
	 * @param site
	 * @param time
	 */
	private void goToNextBac(TextView textfield, ListView list, List<Horaire> values, String site, int time) {
		if (values.size() == 0) {
			notimes(textfield, list);
		} else {
			int idx = getNextTimePosition(time, values);

			int nextTime = values.get(idx).time;
			textfield.setText(site + "\n"+remainingTime(nextTime, time));

			list.setAdapter(new ArrayAdapter<Horaire>(this, android.R.layout.simple_list_item_1,values));
			list.setSelection(idx);
		}
	}

	/**
	 * French free days
	 * 
	 * @param day
	 * @param month
	 * @return
	 */
	private boolean ferie(int day, int month) {
		if (day == 1 && month== 1) return true;
		else if (day == 1 && month == 5) return true;
		else if (day == 8 && month == 5) return true;
		else if (day == 14 && month == 7) return true;
		else if (day == 15 && month == 8) return true;
		else if (day == 1 && month == 11) return true;
		else if (day == 25 && month == 12) return true;
		else return false;
	}

	private void noCurrent() {
		String[] nextValues = {};
		currentText.setText("");
		currentList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nextValues));
	}

	private void noNext() {
		String[] nextValues = {};
		nextText.setText("");
		nextList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nextValues));
	}

	private void noPrevious() {
		String[] previousValues = {};
		previousText.setText("");
		previousList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,previousValues));
	}

	public String remainingTime(int current, int next) {
		int diff = Math.abs(next - current);
		int h = (diff/60);
		int m = (diff%60);
		//		if (h > 0) {
		//			return ""+h+"h et "+m+"min";
		//		} else {
		//			return ""+m+"min";
		//		}
		return ""+diff;
	}

	int getNextTimePosition(int minutes, List<Horaire> previousValues) {
		for (int i = 0 ; i < previousValues.size() ; i++) {
			if (previousValues.get(i).time > minutes) {
				return i;
			}
		}
		return 0;
	}

	//	TextView createTextView(boolean endline, boolean endcolumn) { 
	//		TextView t = new TextView(this); 
	//		int bottom = endline ? 1 : 0; 
	//		int right = endcolumn ? 1 :0; 
	//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.3f); 
	//		params.setMargins(1, 1, right, bottom); 
	//		t.setLayoutParams(params); 
	//		t.setPadding(4, 4, 10, 4); 
	//		//text.setBackgroundColor(getResources().getColor(R.color.white)); 
	//		return t; 
	//	}


	public void showSettings() {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i); 
	}

	public void showAbout() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("github or other"));
		startActivity(i); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.next_bac, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			showSettings();
			return true;
		case R.id.action_about:
			show("About BAC76...");
			return true;
		case R.id.Hell:
			helloTextView.setGravity(Gravity.NO_GRAVITY);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

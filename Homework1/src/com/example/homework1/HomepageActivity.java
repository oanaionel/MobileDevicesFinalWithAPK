package com.example.homework1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.example.homework1.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

public class HomepageActivity extends Activity {

	ImageView viewImage;
	Button save;
	private TextView emailDisplay, welcome_msg, img;

	public String picturePath, emailreplacement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);

		Parse.initialize(this, "BmbiDoWmcHnINiTcLUymM6lcP84rHYd5XrfC2gFW",
				"KY1MGCdC2HawkP4msjtNx4Y1V3qNNWNEbSlEfG82");
		getIntent();

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

		// Convert currentUser into String
		String username = currentUser.getUsername().toString();
		String email = currentUser.getEmail().toString();
		emailDisplay = (EditText) findViewById(R.id.emailview);
		welcome_msg = (TextView) findViewById(R.id.txt_welcome);
		viewImage = (ImageView) findViewById(R.id.viewImage);
		viewImage.setImageResource(R.drawable.pug);

		String next = "<font color='#33b5e5'>" + username + "</font>";
		welcome_msg.setText(Html.fromHtml(next));

		emailDisplay.setText(email);

		// Locate Button in homepage.xml
		save = (Button) findViewById(R.id.saveprofile);

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// saveData(v);
				emailreplacement = emailDisplay.getText().toString();
				if (isEmailValid(emailreplacement) == true) {
					ParseUser user = ParseUser.getCurrentUser();
					user.setEmail(emailreplacement);
					user.saveInBackground();
					Toast.makeText(getApplicationContext(),
							"The changes have been saved", Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(getApplicationContext(), "Invalid email",
							Toast.LENGTH_SHORT).show();
			}
		});
		img = (TextView) findViewById(R.id.selectphoto);
		img.setClickable(true);
		img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
	}

	/**
	 * method is used for checking valid email id format.
	 * 
	 * @param email
	 * @return boolean true for valid false for invalid
	 */
	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	private void selectImage() {

		final CharSequence[] options = { "Choose from Gallery", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				HomepageActivity.this);
		builder.setTitle("Add photo");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	// imggg
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
							bitmapOptions);
					// bitmap.recycle();
					viewImage.setImageBitmap(bitmap);

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream outFile = null;
					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");
					try {
						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				picturePath = c.getString(columnIndex);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Log.w("path of image from gallery......******************.........",
						picturePath + "");
				viewImage.setImageBitmap(thumbnail);
				// ImageView imageView = (ImageView)
				// findViewById(R.id.viewImage);
			}
		}
	}

	// /img end

	public void ChangePassword() {
		Intent i = new Intent(HomepageActivity.this,
				ChangePasswordActivity.class);
		startActivity(i);
	}

	public void showWeather() {
		Intent i = new Intent(HomepageActivity.this, WeatherActivity.class);
		startActivity(i);
	}

	public void showPet() {
		Intent i = new Intent(HomepageActivity.this, Questions.class);
		startActivity(i);
	}

	public void showLinks() {
		Intent i = new Intent(HomepageActivity.this, LinksActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_weather:
			showWeather();
			return true;
		case R.id.action_change_password:
			ChangePassword();
			return true;
		case R.id.action_links:
			showLinks();
			return true;

		case R.id.action_logout:
			ParseUser.logOut();
			finish();
			return true;
		case R.id.action_pet:
			showPet();
			return true;

		}
		return item.getItemId() == R.id.action_overflow
				|| super.onOptionsItemSelected(item);

	}

}

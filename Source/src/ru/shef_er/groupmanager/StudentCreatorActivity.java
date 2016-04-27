package ru.shef_er.groupmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class StudentCreatorActivity extends Activity {

	DB db;
	static long id;
	EditText editTextFirstname;
	EditText editTextSurname;
	EditText editTextPatronymic;
	EditText editTextDob;
	ImageView ivPhoto;
	Button buttonAddPhoto;
	String mFirstname;
	String mSurname;
	String mPatronymic;
	String mDob;
	Bitmap mPhoto;

	String img;
	byte[] byteArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_editor);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

		editTextFirstname = (EditText) findViewById(R.id.editTextFirstname);
		editTextSurname = (EditText) findViewById(R.id.editTextSurname);
		editTextPatronymic = (EditText) findViewById(R.id.editTextPatronymic);
		editTextDob = (EditText) findViewById(R.id.editTextDob);
		ivPhoto = (ImageView) findViewById(R.id.imageView);
		buttonAddPhoto = (Button) findViewById(R.id.buttonAddPhoto);

		// подключение к БД
		db = new DB(this);
		db.open();

		Intent intent = getIntent();
		id = intent.getLongExtra("id", 0);

		buttonAddPhoto.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				Bitmap photo = (Bitmap) intent.getExtras().get("data");  
                byteArray = Utilities.getBytes(photo);
    			ivPhoto.setImageBitmap(Utilities.getImage(byteArray));
			}
			break;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	// меню
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.student_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			mFirstname = editTextFirstname.getText().toString();
			mSurname = editTextSurname.getText().toString();
			mPatronymic = editTextPatronymic.getText().toString();
			mDob = editTextDob.getText().toString();
			if (mFirstname.equals("")||mSurname.equals("")) {
				Toast.makeText(getApplicationContext(), R.string.student_incorrect_input_toast, Toast.LENGTH_LONG).show();
			} else {
				db.addStudent(mFirstname, mSurname, mPatronymic, mDob, String.valueOf(id), byteArray);
				finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

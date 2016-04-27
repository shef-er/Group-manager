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

public class StudentEditorActivity extends Activity {

	static long id;
	String[] mStudentData;
	DB db;
	EditText editTextFirstname;
	EditText editTextSurname;
	EditText editTextPatronymic;
	EditText editTextDob;
	Button buttonAddPhoto;
	ImageView ivPhoto;
	
	long mID;
	String mFirstname;
	String mSurname;
	String mPatronymic;
	String mDob;
	String mGroup;
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
		buttonAddPhoto = (Button) findViewById(R.id.buttonAddPhoto);
		ivPhoto = (ImageView) findViewById(R.id.imageView);

		// подключение к БД
		db = new DB(this);
		db.open();

		Intent intent = getIntent();
		id = intent.getLongExtra("id", 0);
		
		// извлечение данных
		mStudentData = db.getStudentRecordData(id);
		mID = Long.parseLong(mStudentData[0]);
		mGroup = mStudentData[5];
		
		editTextFirstname.setText(mStudentData[1]);
		editTextSurname.setText(mStudentData[2]);
		editTextPatronymic.setText(mStudentData[3]);
		editTextDob.setText(mStudentData[4]);

		buttonAddPhoto.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
			}
		});

		
		if(db.getStudentRecordPhoto(id)!=null) {
			byteArray = db.getStudentRecordPhoto(id);
			mPhoto = Utilities.getImage(db.getStudentRecordPhoto(id));
			ivPhoto.setImageBitmap(mPhoto);
		}
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
				db.updateStudent(mID, mFirstname, mSurname, mPatronymic, mDob, mGroup, byteArray);
				finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

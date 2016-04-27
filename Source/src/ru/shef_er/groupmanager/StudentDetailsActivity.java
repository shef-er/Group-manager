package ru.shef_er.groupmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class StudentDetailsActivity extends Activity {

	static long id;
	static long groupID;
	String[] mStudentData;
	DB db;
	TextView tvName;
	TextView tvDob;
	TextView tvGroup;
	ImageView ivPhoto;

	String mFirstname;
	String mSurname;
	String mPatronymic;
	String mDob;
	String mGroup;
	Bitmap mPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_details);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

		tvName = (TextView) findViewById(R.id.tvName);
		tvDob = (TextView) findViewById(R.id.tvDob);
		tvGroup = (TextView) findViewById(R.id.tvGroup);
		ivPhoto = (ImageView) findViewById(R.id.imageView);

		// подключение к БД
		db = new DB(this);
		db.open();

		Intent intent = getIntent();
		id = intent.getLongExtra("id", 0);

		// извлекает данные по записи
		mStudentData = db.getStudentRecordData(id);
		mFirstname = mStudentData[1];
		mSurname = mStudentData[2];
		mPatronymic = mStudentData[3];
		mDob = mStudentData[4];
		mGroup = mStudentData[6];

		if (db.getStudentRecordPhoto(id) != null) {
			mPhoto = Utilities.getImage(db.getStudentRecordPhoto(id));
			ivPhoto.setImageBitmap(mPhoto);
		}

		tvName.setText(mFirstname + " " + mSurname + " " + mPatronymic);
		tvDob.setText(mDob);
		tvGroup.setText(getResources().getString(R.string.student_details_group) + " " + mGroup);

		db.close();
	}

	protected void onDestroy() {
		super.onDestroy();
	}
}

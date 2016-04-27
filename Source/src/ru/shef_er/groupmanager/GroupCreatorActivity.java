package ru.shef_er.groupmanager;

import ru.shef_er.groupmanager.R;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class GroupCreatorActivity extends Activity {

	DB db;
	EditText editTextGroupNumber;
	EditText editTextGroupFaculty;
	String mGroupNumber;
	String mGroupFaculty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_editor);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

		editTextGroupNumber = (EditText) findViewById(R.id.editTextGroupName);
		editTextGroupFaculty = (EditText) findViewById(R.id.editTextGroupFaculty);

		// подключение к БД
		db = new DB(this);
		db.open();
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	// меню
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			mGroupNumber = editTextGroupNumber.getText().toString();
			mGroupFaculty = editTextGroupFaculty.getText().toString();
			if (mGroupNumber.equals("")) {
				Toast.makeText(getApplicationContext(), R.string.group_incorrect_input_toast, Toast.LENGTH_LONG).show();
			} else {
				db.addGroup(mGroupNumber, mGroupFaculty);
				finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

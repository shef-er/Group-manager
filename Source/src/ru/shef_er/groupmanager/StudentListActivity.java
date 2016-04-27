package ru.shef_er.groupmanager;

import java.util.concurrent.TimeUnit;

import android.app.ActionBar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class StudentListActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	ListView listView;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	static long currentGroupID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_list);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

		Intent intent = getIntent();
		currentGroupID = intent.getLongExtra("groupID", 0);


		// подключение к БД
		db = new DB(this);
		db.open();

		String[] mGroupData = db.getGroupRecordData(currentGroupID);
		
		setTitle(getResources().getString(R.string.title_activity_student_list)+" ("+mGroupData[1]+")");

		// сопоставление данных и View
		String[] from = { DB.STUDENT_COLUMN_SURNAME, DB.STUDENT_COLUMN_FIRSTNAME };
		int[] to = { android.R.id.text1, android.R.id.text2 };

		scAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, from, to, 0);

		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(scAdapter);

		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Intent intent = new Intent(StudentListActivity.this,
						StudentDetailsActivity.class);
				intent.putExtra("id", id);
				startActivity(intent);
			}
		});

		// создаем лоадер для чтения данных
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().getLoader(0).forceLoad();
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new StudentCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	static class StudentCursorLoader extends CursorLoader {

		DB db;

		public StudentCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = db.getStudentData(currentGroupID);
			try {
				TimeUnit.SECONDS.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return cursor;
		}

	}

	// контекстное меню
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.action_edit:
			Intent intent = new Intent(StudentListActivity.this,
					StudentEditorActivity.class);
			intent.putExtra("id", info.id);
			startActivity(intent);
			return true;
		case R.id.action_delete:
			db.deleteStudent(info.id);
			getLoaderManager().getLoader(0).forceLoad();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	// меню и ActionBar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.student_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(StudentListActivity.this,
					StudentCreatorActivity.class);
			intent.putExtra("id", currentGroupID);
			startActivity(intent);
			return true;
/*		case R.id.action_sort_a-z:
			scAdapter.sort(new Comparator<String>() {
		        @Override 
		        public int compare(String arg1, String arg0) {
		            return arg1.compareTo(arg0);
		        }
		    });
			return true;
*/		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

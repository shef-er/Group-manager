package ru.shef_er.groupmanager;

import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	ListView listView;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// подключение к БД
		db = new DB(this);
		db.open();

		// сопоставление данных и View
		String[] from = { DB.GROUP_COLUMN_NUMBER, DB.GROUP_COLUMN_FACULTY };
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
				Intent intent = new Intent(MainActivity.this,
						StudentListActivity.class);
				intent.putExtra("groupID", id);
				startActivity(intent);
			}
		});

		// создание лоадера для чтения данных
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
		return new GroupCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	static class GroupCursorLoader extends CursorLoader {

		DB db;

		public GroupCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = db.getGroupData();
			try {
				TimeUnit.SECONDS.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return cursor;
		}

	}

	// подтверждение выхода
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setCancelable(false)
				.setMessage(R.string.exit_confirm)
				.setPositiveButton(R.string.exit_confirm_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MainActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.exit_confirm_negative, null).show();
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
			Intent intent = new Intent(MainActivity.this,
					GroupEditorActivity.class);
			intent.putExtra("groupID", info.id);
			startActivity(intent);
			return true;

		case R.id.action_delete:
			if (db.isEmptyGroup(info.id))
				db.deleteGroup(info.id);
			else
				Toast.makeText(getApplicationContext(),
						R.string.group_protection_toast, Toast.LENGTH_SHORT)
						.show();
			getLoaderManager().getLoader(0).forceLoad();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	// меню и ActionBar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(MainActivity.this,
					GroupCreatorActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

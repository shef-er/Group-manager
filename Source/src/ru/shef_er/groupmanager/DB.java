package ru.shef_er.groupmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

	private static final String DB_NAME = "database";
	private static final int DB_VERSION = 1;

	// имя таблицы групп, поля и запрос создания
	private static final String GROUP_TABLE = "group_table";
	public static final String GROUP_COLUMN_ID = "_id";
	public static final String GROUP_COLUMN_NUMBER = "number";
	public static final String GROUP_COLUMN_FACULTY = "faculty";
	private static final String GROUP_TABLE_CREATE = 
			"create table "	+ GROUP_TABLE + "(" 
			+ GROUP_COLUMN_ID + " integer primary key autoincrement, "
			+ GROUP_COLUMN_NUMBER + " text, "
			+ GROUP_COLUMN_FACULTY + " text" 
			+ ");";

	// имя таблицы студентов, поля и запрос создания
	private static final String STUDENT_TABLE = "student_table";
	public static final String STUDENT_COLUMN_ID = "_id";
	public static final String STUDENT_COLUMN_FIRSTNAME = "firstname";
	public static final String STUDENT_COLUMN_SURNAME = "surname";
	public static final String STUDENT_COLUMN_PATRONYMIC = "patronymic";
	public static final String STUDENT_COLUMN_DOB = "dob";
	public static final String STUDENT_COLUMN_GROUP = "group_table";
	public static final String STUDENT_COLUMN_PHOTO = "photo";
	private static final String STUDENT_TABLE_CREATE = 
			"create table "	+ STUDENT_TABLE + "(" 
			+ STUDENT_COLUMN_ID	+ " integer primary key autoincrement, " 
			+ STUDENT_COLUMN_FIRSTNAME + " text, "
			+ STUDENT_COLUMN_SURNAME + " text, "
			+ STUDENT_COLUMN_PATRONYMIC + " text, "
			+ STUDENT_COLUMN_DOB + " text, "
			+ STUDENT_COLUMN_GROUP + " integer, "
			+ STUDENT_COLUMN_PHOTO + " blob"
			+ ");";

	private final Context mCtx;

	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;

	public DB(Context ctx) {
		mCtx = ctx;
	}

	// открывает подключение
	public void open() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	}

	// закрывет подключение
	public void close() {
		if (mDBHelper != null)
			mDBHelper.close();
	}	

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// создание таблиц
			db.execSQL(GROUP_TABLE_CREATE);
			db.execSQL(STUDENT_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	
/********************
 * Для работы с таблицей групп
 * 
 */

	// данные по всем группам
	public Cursor getGroupData() {
		return mDB.query(GROUP_TABLE, null, null, null, null, null, null);
	}
	
	// добавляет новую запись группы
	public void addGroup(String number, String faculty) {
	    ContentValues cv = new ContentValues();
	    cv.put(GROUP_COLUMN_NUMBER, number);
	    cv.put(GROUP_COLUMN_FACULTY, faculty);
	    mDB.insert(GROUP_TABLE, null, cv);
	  }
	
	// обновляет запись группы
	public void updateGroup(long id, String number, String faculty) {
	    String groupID = String.valueOf(id);
	    ContentValues cv = new ContentValues();
		cv.put(GROUP_COLUMN_ID, id);
	    cv.put(GROUP_COLUMN_NUMBER, number);
	    cv.put(GROUP_COLUMN_FACULTY, faculty);
	    mDB.update(GROUP_TABLE, cv,  "_id = ?", new String[] { groupID });
	  }
	
	// удаляет запись группы
	public void deleteGroup(long id){
		mDB.delete(GROUP_TABLE, GROUP_COLUMN_ID + " = " + id, null);
	}

	// получает данные из записи группы
	public String[] getGroupRecordData(long id) {
		Cursor cursor = mDB.query(GROUP_TABLE, null, GROUP_COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		int cols = 3;
	    String[] values = new String[cols];
		for (int i = 0; i < cols; i++) {
		    values[i] = cursor.getString(i);
		}
	    cursor.close();
	    return values;
	}

	// проверка заполнености группы
	public boolean isEmptyGroup(long id) {
		boolean empty = true;
	    Cursor cursor = mDB.query(STUDENT_TABLE, null, STUDENT_COLUMN_GROUP + " = " + id, null, null, null, null);
	    if(cursor.getCount() == 0)
	    	empty=true;
        if(cursor.getCount() > 0)
            empty=false;
	    cursor.close();
		return empty;
	}	
	
/********************
 * Для работы с таблицей студентов
 * 
 */
	// данные по всем студентам конкретной группы
	public Cursor getStudentData(long id) {
		return mDB.query(STUDENT_TABLE, null, STUDENT_COLUMN_GROUP + " = " + id, null, null, null, STUDENT_COLUMN_SURNAME + " ASC");
	}
	
	// добавляет новую запись студента
	public void addStudent(String firstname, String surname, String patronymic, String dob, String group, byte[] image) {
	    ContentValues cv = new ContentValues();
	    cv.put(STUDENT_COLUMN_FIRSTNAME, firstname);
	    cv.put(STUDENT_COLUMN_SURNAME, surname);
	    cv.put(STUDENT_COLUMN_PATRONYMIC, patronymic);
	    cv.put(STUDENT_COLUMN_DOB, dob);
	    cv.put(STUDENT_COLUMN_GROUP, group);
	    cv.put(STUDENT_COLUMN_PHOTO, image);
	    mDB.insert(STUDENT_TABLE, null, cv);
	  }
	
	// обновляет запись студента
	public void updateStudent(long id, String firstname, String surname, String patronymic, String dob, String group, byte[] image) {
	    String studentID = String.valueOf(id);
	    ContentValues cv = new ContentValues();
	    cv.put(STUDENT_COLUMN_FIRSTNAME, firstname);
	    cv.put(STUDENT_COLUMN_SURNAME, surname);
	    cv.put(STUDENT_COLUMN_PATRONYMIC, patronymic);
	    cv.put(STUDENT_COLUMN_DOB, dob);
	    cv.put(STUDENT_COLUMN_GROUP, group);
	    cv.put(STUDENT_COLUMN_PHOTO, image);
	    mDB.update(STUDENT_TABLE, cv,  "_id = ?", new String[] { studentID });
	  }
	
	// удаляет запись студента
	public void deleteStudent(long id){
		mDB.delete(STUDENT_TABLE, STUDENT_COLUMN_ID + " = " + id, null);
	}

	// получает данные из записи студента
	public String[] getStudentRecordData(long id) {
		Cursor cursor = mDB.query(STUDENT_TABLE, null, STUDENT_COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
	    int cols = 6;
	    String[] values = new String[7];
		for (int i = 0; i < cols; i++) {
		    values[i] = cursor.getString(i);
		}
	    cursor.close();
	    Log.d("db", values[5]);
	    Cursor cursor1 = mDB.query(GROUP_TABLE, null, GROUP_COLUMN_ID + " = " + values[5], null, null, null, null);
		cursor1.moveToFirst();
	    values[6] = cursor1.getString(1);
	    cursor1.close();
	    return values;
	}

	// получает фото из записи студента
	public byte[] getStudentRecordPhoto(long id) {
		Cursor cursor = mDB.query(STUDENT_TABLE, null, STUDENT_COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
	    byte[] image = cursor.getBlob(6);
	    cursor.close();
	    return image;
	}

}
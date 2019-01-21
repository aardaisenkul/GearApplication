package com.example.sertac.gearapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "sqllite_database";//database adı

	private static final String TABLE_NAME = "scoreTable";
	private static String ID = "id";
	private static String NAME = "habitName";


    public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
    	// Databesi olu�turuyoruz.Bu methodu biz �a��rm�yoruz. Databese de obje olu�turdu�umuzda otamatik �a��r�l�yor.
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ NAME + " TEXT )";
		db.execSQL(CREATE_TABLE);
	}


	

	public void deleteScore(int id){ //id si belli olan row u silmek için

		 SQLiteDatabase db = this.getWritableDatabase();
		 db.delete(TABLE_NAME, ID + " = ?",
		            new String[] { String.valueOf(id) });
		 db.close();
	}

	public void addScore(String habitName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, habitName);

		db.insert(TABLE_NAME, null, values);
		db.close(); //Database Bağlantısını Kapattık*/
	}

		public ArrayList<habitFeatures> scores(){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT habitName  FROM " + TABLE_NAME+" ORDER BY habitName";

		Cursor cursor = db.rawQuery(selectQuery, null);
	    ArrayList<habitFeatures> scoreList = new ArrayList<>();
	    int i=0;
	    if (cursor.moveToFirst()) {
	        do {
	            scoreList.add(new habitFeatures(""+cursor.getString(0)));

	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return scoreList;
	}

	public void resetTables(){ 
		//Bunuda uygulamada kullanm�yoruz. T�m verileri siler. tabloyu resetler.
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		//db.execSQL("DROP TABLE IF EXISTS " + ID);
		//onCreate(db);
	}

}

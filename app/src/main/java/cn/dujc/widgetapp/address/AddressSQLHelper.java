package cn.dujc.widgetapp.address;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author du
 * date: 2019/2/6 3:15 PM
 */
public class AddressSQLHelper extends SQLiteOpenHelper {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(2);

    private static final String TABLE_PROVINCE = "province", TABLE_CITY = "city", TABLE_DISTRICT = "district";
    private static final String KEY_ID = "id", KEY_NAME = "name", KEY_PID = "pid", KEY_EXTRA = "extra";

    public AddressSQLHelper(@Nullable Context context, int version) {
        super(context, "address.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        createIfNotExists(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        dropIfExists(db);
        createIfNotExists(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void dropIfExists(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROVINCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRICT);
    }

    private void createIfNotExists(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROVINCE + " (" + KEY_ID + " varchar PRIMARY KEY, " + KEY_NAME + " varchar, " + KEY_PID + " varchar, " + KEY_EXTRA + " varchar)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CITY + " (" + KEY_ID + " varchar PRIMARY KEY, " + KEY_NAME + " varchar, " + KEY_PID + " varchar, " + KEY_EXTRA + " varchar)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DISTRICT + " (" + KEY_ID + " varchar PRIMARY KEY, " + KEY_NAME + " varchar, " + KEY_PID + " varchar, " + KEY_EXTRA + " varchar)");
    }

    public void resetData(final List<IAddress> provinces
            , final List<IAddress> cities, final List<IAddress> districts, final OnParseDone onParseDone) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                db.beginTransaction();
                dropIfExists(db);
                createIfNotExists(db);
                if (provinces != null && provinces.size() > 0) {
                    for (IAddress province : provinces) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_ID, province.getId());
                        cv.put(KEY_NAME, province.getName());
                        cv.put(KEY_PID, province.getPid());
                        cv.put(KEY_EXTRA, province.getExtra());
                        db.insert(TABLE_PROVINCE, null, cv);
                    }
                }
                if (cities != null && cities.size() > 0) {
                    for (IAddress city : cities) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_ID, city.getId());
                        cv.put(KEY_NAME, city.getName());
                        cv.put(KEY_PID, city.getPid());
                        cv.put(KEY_EXTRA, city.getExtra());
                        db.insert(TABLE_CITY, null, cv);
                    }
                }
                if (districts != null && districts.size() > 0) {
                    for (IAddress district : districts) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_ID, district.getId());
                        cv.put(KEY_NAME, district.getName());
                        cv.put(KEY_PID, district.getPid());
                        cv.put(KEY_EXTRA, district.getExtra());
                        db.insert(TABLE_DISTRICT, null, cv);
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                if (onParseDone != null) HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        onParseDone.onParseDone(AddressSQLHelper.this);
                    }
                });
            }
        });
    }

    public List<IAddress> getProvinces() {
        List<IAddress> provinces = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROVINCE, new String[]{KEY_ID, KEY_NAME}, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            IAddress province = new IAddress.Impl(id, name, null);
            provinces.add(province);
        }
        cursor.close();
        db.close();
        return provinces;
    }

    public List<IAddress> getCities(String pid) {
        List<IAddress> cities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITY, new String[]{KEY_ID, KEY_NAME}, KEY_PID + " = ?", new String[]{pid}, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            IAddress city = new IAddress.Impl(id, name, pid);
            cities.add(city);
        }
        cursor.close();
        db.close();
        return cities;
    }

    public List<IAddress> getDistricts(String cid) {
        List<IAddress> districts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_DISTRICT, new String[]{KEY_ID, KEY_NAME}, KEY_PID + " = ?", new String[]{cid}, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            IAddress district = new IAddress.Impl(id, name, cid);
            districts.add(district);
        }
        cursor.close();
        db.close();
        return districts;
    }

}

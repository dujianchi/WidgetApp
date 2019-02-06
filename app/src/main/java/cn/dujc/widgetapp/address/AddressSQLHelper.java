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

    static final Handler HANDLER = new Handler(Looper.getMainLooper());
    static final Executor EXECUTOR = Executors.newFixedThreadPool(2);

    private static final int VERSION = 1;
    private static final String TABLE_PROVINCE = "province", TABLE_CITY = "city", TABLE_DISTRICT = "district";
    private static final String KEY_ID = "id", KEY_NAME = "name", KEY_PID = "pid", KEY_EXTRA = "extra";
    private static final String VERSION_TABLE = "_version", VERSION_NAME = "name";

    public AddressSQLHelper(@Nullable Context context) {
        super(context, "address.db", null, VERSION);
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }

    private synchronized void dropIfExists(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROVINCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRICT);

        db.execSQL("DROP TABLE IF EXISTS " + VERSION_TABLE);
    }

    private synchronized void createIfNotExists(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROVINCE + " (" + KEY_ID + " varchar PRIMARY KEY, " + KEY_NAME + " varchar, " + KEY_PID + " varchar, " + KEY_EXTRA + " varchar)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CITY + " (" + KEY_ID + " varchar PRIMARY KEY, " + KEY_NAME + " varchar, " + KEY_PID + " varchar, " + KEY_EXTRA + " varchar)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DISTRICT + " (" + KEY_ID + " varchar PRIMARY KEY, " + KEY_NAME + " varchar, " + KEY_PID + " varchar, " + KEY_EXTRA + " varchar)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + VERSION_TABLE + " (" + KEY_ID + " integer PRIMARY KEY AUTOINCREMENT, " + VERSION_NAME + " varchar)");
    }

    public synchronized void resetData(final List<IAddress> provinces
            , final List<IAddress> cities, final List<IAddress> districts, final OnParseDone onParseDone) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                db.beginTransaction();
                //dropIfExists(db);
                //createIfNotExists(db);
                if (provinces != null && provinces.size() > 0) {
                    for (IAddress province : provinces) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_ID, province.getId());
                        cv.put(KEY_NAME, province.getName());
                        cv.put(KEY_PID, province.getPid());
                        cv.put(KEY_EXTRA, province.getExtra());
                        db.replace(TABLE_PROVINCE, null, cv);
                    }
                }
                if (cities != null && cities.size() > 0) {
                    for (IAddress city : cities) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_ID, city.getId());
                        cv.put(KEY_NAME, city.getName());
                        cv.put(KEY_PID, city.getPid());
                        cv.put(KEY_EXTRA, city.getExtra());
                        db.replace(TABLE_CITY, null, cv);
                    }
                }
                if (districts != null && districts.size() > 0) {
                    for (IAddress district : districts) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_ID, district.getId());
                        cv.put(KEY_NAME, district.getName());
                        cv.put(KEY_PID, district.getPid());
                        cv.put(KEY_EXTRA, district.getExtra());
                        db.replace(TABLE_DISTRICT, null, cv);
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                if (onParseDone != null) HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        onParseDone.onParseDone(
                                (provinces != null && provinces.size() > 0)
                                        || (cities != null && cities.size() > 0)
                                        || (districts != null && districts.size() > 0)
                        );
                    }
                });
            }
        });
    }

    public synchronized List<IAddress> getProvinces() {
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

    public synchronized List<IAddress> getCities(String pid) {
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

    public synchronized List<IAddress> getDistricts(String cid) {
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

    public synchronized String getVersionName() {
        String version = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(VERSION_TABLE, new String[]{VERSION_NAME}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            version = cursor.getString(cursor.getColumnIndex(VERSION_NAME));
        }
        cursor.close();
        db.close();
        return version;
    }

    public synchronized void updateVersion(String version) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, 1);
        cv.put(VERSION_NAME, version);
        db.replace(VERSION_TABLE, null, cv);
        db.close();
    }
}

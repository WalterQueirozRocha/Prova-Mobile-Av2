package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TrilhasDB extends SQLiteOpenHelper {

    private static final String DATABASE = "trilhas.db";
    private static final int VERSION = 1;

    private static final String TABLE_WAYPOINTS = "waypoints";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_AVG_SPEED = "avg_speed";
    private static final String COLUMN_TOTAL_DISTANCE = "total_distance";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_WAYPOINT_IDS = "waypoint_ids";

    public TrilhasDB(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_WAYPOINTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL,"+
                COLUMN_START_DATE + " TEXT," +
                COLUMN_AVG_SPEED + " REAL," +
                COLUMN_TOTAL_DISTANCE + " REAL," +
                COLUMN_WAYPOINT_IDS + " TEXT," +
                COLUMN_DURATION + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYPOINTS);
        onCreate(db);
    }

    public void addWayPoint(WayPoint wayPoint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, wayPoint.getLatitude());
        values.put(COLUMN_LONGITUDE, wayPoint.getLongitude());
        db.insert(TABLE_WAYPOINTS, null, values);
        db.close();
        Log.d("TrilhasDB", "Waypoint adicionado: Lat=" + wayPoint.getLatitude() + ", Lon=" + wayPoint.getLongitude());
    }


    public List<WayPoint> getAllWayPoints() {
        List<WayPoint> wayPoints = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WAYPOINTS+ " WHERE " + COLUMN_LATITUDE + " IS NOT NULL", null);
        if (cursor.moveToFirst()) {
            do {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
                wayPoints.add(new WayPoint(latitude, longitude));
                Log.d("TrilhasDB", "Waypoint recuperado: Lat=" + latitude + ", Lon=" + longitude);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wayPoints;
    }

    public void addTrilhaSummary(String startDate, double avgSpeed, double totalDistance, long duration, List<Integer> waypointIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_AVG_SPEED, avgSpeed);
        values.put(COLUMN_TOTAL_DISTANCE, totalDistance);
        values.put(COLUMN_DURATION, duration);
        String waypointIdsString = TextUtils.join(",", waypointIds);
        values.put(COLUMN_WAYPOINT_IDS, waypointIdsString);
        db.insert(TABLE_WAYPOINTS, null, values);
        db.close();
        Log.d("TrilhasDB", "Resumo da trilha adicionado: Data de Início=" + startDate);
    }

    public List<WayPoint> getAllSummaries() {
        List<WayPoint> summaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WAYPOINTS + " WHERE " + COLUMN_START_DATE + " IS NOT NULL", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                double avgSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AVG_SPEED));
                double totalDistance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_DISTANCE));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                summaries.add(new WayPoint(id, startDate, avgSpeed, totalDistance, duration));
                Log.d("TrilhasDB", "Resumo da trilha recuperado: ID=" + id + ", Data de Início=" + startDate);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return summaries;
    }

    public int getLastId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_WAYPOINTS, null);
        int lastId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return lastId;
    }

    // Método para apagar trilha por ID
    public void apagarTrilha(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WAYPOINTS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String waypointIdsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAYPOINT_IDS));
            List<Integer> waypointIds = new ArrayList<>();
            String[] waypointIdsArray = waypointIdsString.split(",");
            for (String idString : waypointIdsArray) {
                waypointIds.add(Integer.parseInt(idString));
            }
            // Remove todos os waypoints relacionados à trilha
            for (int waypointId : waypointIds) {
                db.delete(TABLE_WAYPOINTS, COLUMN_ID + " = ?", new String[]{String.valueOf(waypointId)});
            }
        }
        cursor.close();
        // Remove o resumo da trilha
        db.delete(TABLE_WAYPOINTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        Log.d("TrilhasDB", "Trilha apagada com sucesso: ID=" + id);
    }
}

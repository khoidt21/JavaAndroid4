package dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import model.ModelMap;

public class MapDbhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mapManager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "maps";
    private static final String KEY_ID = "id";
    private static final String KEY_END_ADDRESS = "endAddress";
    private static final String KEY_START_ADDRESS = "startAddress";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DURATION = "duration";

    public MapDbhelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_alarms_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s TEXT,%s TEXT,%s TEXT)",
                TABLE_NAME,KEY_ID,KEY_END_ADDRESS,KEY_START_ADDRESS,KEY_DISTANCE,KEY_DURATION
        );
        db.execSQL(create_alarms_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_alarms_table = String.format("DROP TABLE IF EXISTS %s",TABLE_NAME);
        db.execSQL(drop_alarms_table);
        onCreate(db);
    }
    // ham add map
    public void addMap(ModelMap map){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_END_ADDRESS,map.getEndAddress());
        values.put(KEY_START_ADDRESS,map.getStartAddress());
        values.put(KEY_DISTANCE,map.getDistance());
        values.put(KEY_DURATION,map.getDuration());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    // ham get All search address
    public ArrayList<ModelMap> getAlarms(){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[] { KEY_ID,KEY_END_ADDRESS, KEY_START_ADDRESS,KEY_DISTANCE,KEY_DURATION };

        Cursor cursor = db.query(TABLE_NAME, columns, null,
                null, null, null, KEY_ID
                        + " DESC",null);

        ArrayList<ModelMap> mapList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                ModelMap map = new ModelMap();
                int id = cursor.getInt(0);
                String endaddress = cursor.getString(1);
                String startaddress = cursor.getString(2);
                String distance = cursor.getString(3);
                String duration = cursor.getString(4 );
                map.setEndAddress(endaddress);
                map.setStartAddress(startaddress);
                map.setDistance(distance);
                map.setDuration(duration);
                mapList.add(map);
            }while (cursor.moveToNext());
        }
        return mapList;
    }

}


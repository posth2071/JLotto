package com.example.maptest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public final class DBOpenHelper extends SQLiteOpenHelper {
    private Context context;

    public static final String DATABASE_NAME = "NumTest.db";   //DB이름 상수 설정
    public static final String TABLE_NAME = "lotto_no";   //Table이름 설정
    public static final int DB_VERSION = 1;              //Version 설정

    //public static final String _ID = "_id";        //로또번호 기록 1
    public static final String COLUMN_TURN = "turn";            //회차번호 기록
    public static final String COLUMN_NUMBERSET = "numberset";
    public static final String COLUMN_HALLPAIR = "hallpair";
    public static final String COLUMN_RESULT = "result";
    //public static final String NUM_1 = "num1";        //로또번호 기록 1
    //public static final String NUM_2 = "num2";        //로또번호 기록 2
    //public static final String NUM_3 = "num3";        //로또번호 기록 3
    //public static final String NUM_4 = "num4";        //로또번호 기록 4
    //public static final String NUM_5 = "num5";        //로또번호 기록 5
    //public static final String NUM_6 = "num6";        //로또번호 기록 6
    //public static final String NUM_7 = "num7";        //로또번호 기록 Bonus
    //public static final String ODDEVEN = "oddeven";
    //public static final String _CREATE0 = "create table if not exists "+ TABLE_NAME + "(" +"_id integer primary key autoincrement," +TURNNUM+" text not null," +NUMBER+" text not null);")

    public DBOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
        Log.d("데이터베이스", "DB생성");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        String query = String.format("CREATE TABLE %s ("      // 테이블명 -
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "%s INTEGER NOT NULL, "       // 컬럼 - turn
                + "%s TEXT NOT NULL, "          // 컬럼 - numberset
                + "%s TEXT, "          // 컬럼 - hallpair
                + "%s TEXT "                    // 컬럼 - result
                + ");", TABLE_NAME, COLUMN_TURN, COLUMN_NUMBERSET, COLUMN_HALLPAIR, COLUMN_RESULT);
         */
        String query = "create table " + TABLE_NAME + " (" +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT," +
                "turn INTEGER not null," +
                "numberset TEXT not null," +
                "hallpair TEXT not null," +
                "result TEXT);";

        db.execSQL(query);
        Log.d("데이터베이스", "DB테이블 Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
        Log.d("데이터베이스", "DB Updated");
    }

    public int insertDB(int turn, String[] str) {
        String test1 = "DB";
        //String query = String.format("INSERT INTO %s VALUES ('%s', %d, '%s', '%s', '%s');", null, TABLE_NAME, turn, str, test, test1);
        //db.execSQL(query);

        ArrayList<DBinfo> dbCheck = selectDB(turn);
        Iterator<DBinfo> it = dbCheck.iterator();
        while(it.hasNext()){
            String strset = it.next().getNumberset();
            if(str[0].compareTo(strset)==0) {
                Toast.makeText(context.getApplicationContext(), "DB 중복", Toast.LENGTH_SHORT).show();
                Log.d("테스트", str[0] +" - " +strset);
                Log.d("테스트", "DB중복");
                return 0;
            }
        }
        Log.d("테스트","iterater 나옴");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TURN, turn);
        cv.put(COLUMN_NUMBERSET, str[0]);
        cv.put(COLUMN_HALLPAIR, str[1]);
        cv.put(COLUMN_RESULT, test1);
        SQLiteDatabase insertdb = getWritableDatabase();
        try {
            Log.d("테스트","DB Insert실행");
            long rowId = insertdb.insert(TABLE_NAME, null, cv);
            Log.d("테스트","id - " + String.valueOf(rowId));
            if (rowId < 0) {
                Log.d("테스트","DB Insert실패");
                throw new SQLException("Fail To Insert");
            } else {
                Log.d("테스트", "DB insert 성공 - " + turn+", "+str[0]+", "+str[1]);
                Log.d("데이터베이스", "DB 테이블 Insert " + turn + ", " + str[0] + ", " + str[1]);
            }
        } catch (Exception e) {
            Log.d("데이터베이스", e.toString());
        }
        insertdb.close();

        return 1;
    }

    public ArrayList<DBinfo> selectDB(int turn) {
        SQLiteDatabase selectdb = getReadableDatabase();
        String sql = String.format("select * from %s where turn='%s'", TABLE_NAME, String.valueOf(turn));
        Cursor cursor = selectdb.rawQuery(sql, null);

        ArrayList<DBinfo> listSelect = new ArrayList<DBinfo>();
        while (cursor.moveToNext()) {
            DBinfo dbinfo = new DBinfo();
            dbinfo.setTurn(cursor.getInt(cursor.getColumnIndex(COLUMN_TURN)));
            dbinfo.setNumberset(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBERSET)));
            dbinfo.setHallfair(cursor.getString(cursor.getColumnIndex(COLUMN_HALLPAIR)));
            dbinfo.setResult(cursor.getString(cursor.getColumnIndex(COLUMN_RESULT)));
            listSelect.add(dbinfo);
            //teststr = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBERSET));
        }
        selectdb.close();                      //db닫기
        logTest("Select", listSelect);   // 로그출력
        return listSelect;
    }

    //DB전체 호출
    public ArrayList<DBinfo> selectAllDB(){
        SQLiteDatabase selectdb = getReadableDatabase();
        String sql = String.format("select * from %s", TABLE_NAME);
        Cursor cursor = selectdb.rawQuery(sql, null);

        ArrayList<DBinfo> listAll = new ArrayList<DBinfo>();
        while (cursor.moveToNext()) {
            DBinfo dbinfo = new DBinfo();
            dbinfo.setTurn(cursor.getInt(cursor.getColumnIndex(COLUMN_TURN)));
            dbinfo.setNumberset(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBERSET)));
            dbinfo.setHallfair(cursor.getString(cursor.getColumnIndex(COLUMN_HALLPAIR)));
            dbinfo.setResult(cursor.getString(cursor.getColumnIndex(COLUMN_RESULT)));
            listAll.add(dbinfo);
        }
        selectdb.close();                       //db닫기
        logTest("ALL", listAll);          // 로그출력
        return listAll;
    }

    //DB내용 삭제
    public ArrayList<DBinfo> deleteDB(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("DELETE FROM %s;", TABLE_NAME);
        db.execSQL(sql);
        db.close();
        Log.d("데이터베이스", "DB delete");

        return selectAllDB();   //DB전체 반환
    }

    public void logTest(String type, ArrayList<DBinfo> dbList){
        int listCount = dbList.size(); //검색결과 갯수

        String logText = "\nDBList Count => "+listCount+"개\n";
        for(int i=0; i<listCount; i++)
            logText += "\t\t"+type +"["+i+"] => " +dbList.get(i).getInfo()+"\n";

        Log.d("데이터베이스", "\n " + logText);
    }

}

    /*
    public List<NumberData> selectAll(){
        List<NumberData> dataResultList = new ArrayList<NumberData>();
        String sql = "select * from " + TABLE_NAME +" ORDER BY "+ TURNNUM + "DESC;";
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        while(cursor.moveToNext()){
            NumberData numData = new NumberData();

        }
    }

}


     */
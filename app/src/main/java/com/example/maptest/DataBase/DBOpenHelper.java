package com.example.maptest.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.maptest.Activity.FragMent_Two.FragTwo;
import com.example.maptest.Activity.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;

public final class DBOpenHelper extends SQLiteOpenHelper {
    private Context context;

    public static final String DATABASE_NAME = "Lotto.db";   //DB이름 상수 설정
    public static final String TABLE_NAME = "lotto_no";   //Table이름 설정
    public static final int DB_VERSION = 1;              //Version 설정

    //public static final String _ID = "_id";        //로또번호 기록 1
    public static final String COLUMN_TURN = "turn";            //회차번호 기록
    public static final String COLUMN_NUMSET = "numset";

    //public static final String COLUMN_HALLPAIR = "hallpair";
    //public static final String COLUMN_RESULT = "result";

    public DBOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
        Log.d("데이터베이스", "DB생성 - "+ DATABASE_NAME);
    }

    //처음 DB생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + " (" +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT," +
                "turn INTEGER not null," +
                "numset TEXT not null);";
        db.execSQL(query);
        Log.d("데이터베이스", "DB테이블 Created" + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
        Log.d("데이터베이스", "DB Updated");
    }

    public int insertDB(int turn, String numset) {
        Log.d("데이터베이스", "DB InsertDB 진입");

        //입력 회차와 동일 db전부 검색
        ArrayList<DBinfo> dbCheck = selectDB(turn);
        Iterator<DBinfo> it = dbCheck.iterator();

        while(it.hasNext()){
            String strset = MainActivity.numsetSort(it.next().getNumset());       //번호세트 가져와서 오름차순 정렬
            String checkset = MainActivity.numsetSort(numset);
            if(checkset.compareTo(strset)==0) {
                Toast.makeText(context.getApplicationContext(), String.format("%d회차 중복", turn), Toast.LENGTH_SHORT).show();
                Log.d("데이터베이스", String.format("DB Insert 실패 (중복)\n\t\t%d회차, %s", turn, numset));
                return 0;
            }
        }

        ContentValues cv = new ContentValues();         //DB저장위해
        cv.put(COLUMN_TURN, turn);
        cv.put(COLUMN_NUMSET, numset);
        SQLiteDatabase insertdb = getWritableDatabase();        //읽기전용
        try {
            Log.d("데이터베이스","DB Insert실행");
            long rowId = insertdb.insert(TABLE_NAME, null, cv);
            //Log.d("데이터베이스","id - " + String.valueOf(rowId));
            if (rowId < 0) {
                Log.d("데이터베이스","DB Insert실패");
                throw new SQLException("Fail To Insert");
            } else {
                Log.d("데이터베이스", String.format("DB insert 성공\n\t\t%d회, %s ", turn, numset));
                //Toast.makeText(context,"저장성공",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("데이터베이스", e.toString());
        }
        insertdb.close();
        selectAllDB();
        return 1;
    }

    public ArrayList<DBinfo> selectDB(int turn) {
        Log.d("데이터베이스", "DB selectDB 진입, turn - "+turn);
        SQLiteDatabase selectdb = getReadableDatabase();
        String sql = String.format("select * from %s where turn='%s'", TABLE_NAME, String.valueOf(turn));
        Cursor cursor = selectdb.rawQuery(sql, null);

        ArrayList<DBinfo> listSelect = new ArrayList<DBinfo>();
        while (cursor.moveToNext()) {
            DBinfo dbinfo = new DBinfo();
            dbinfo.setTurn(cursor.getInt(cursor.getColumnIndex(COLUMN_TURN)));
            dbinfo.setNumset(cursor.getString(cursor.getColumnIndex(COLUMN_NUMSET)));
            /*
            dbinfo.setNumberset(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBERSET)));
            dbinfo.setHallfair(cursor.getString(cursor.getColumnIndex(COLUMN_HALLPAIR)));
            dbinfo.setResult(cursor.getString(cursor.getColumnIndex(COLUMN_RESULT)));
             */
            listSelect.add(dbinfo);
        }
        selectdb.close();                      //db닫기

        logTest("Select", listSelect, turn);   // 로그출력
        return listSelect;
    }

    //DB전체 호출
    public ArrayList<DBinfo> selectAllDB(){
        Log.d("데이터베이스", "DB selectAllDB 진입");
        SQLiteDatabase selectdb = getReadableDatabase();
        String sql = String.format("select * from %s", TABLE_NAME);
        Cursor cursor = selectdb.rawQuery(sql, null);

        ArrayList<DBinfo> listAll = new ArrayList<DBinfo>();
        while (cursor.moveToNext()) {
            DBinfo dbinfo = new DBinfo();
            dbinfo.setTurn(cursor.getInt(cursor.getColumnIndex(COLUMN_TURN)));
            dbinfo.setNumset(cursor.getString(cursor.getColumnIndex(COLUMN_NUMSET)));
            /*
            dbinfo.setNumberset(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBERSET)));
            dbinfo.setHallfair(cursor.getString(cursor.getColumnIndex(COLUMN_HALLPAIR)));
            dbinfo.setResult(cursor.getString(cursor.getColumnIndex(COLUMN_RESULT)));
             */
            listAll.add(dbinfo);
        }
        selectdb.close();                       //db닫기
        logTest("ALL", listAll, 0);          // 로그출력
        return listAll;
    }

    //DB리스트 설정 회차별 반환함수
    public ArrayList<ArrayList<DBinfo>> selectListAllDB(){
        Log.d("데이터베이스", "DB selectListAllDB 진입");
        SQLiteDatabase selectdb = getReadableDatabase();
        //turn(회차리스트 얻기),  DISTINCT - 중복제거, ORDER BY turn DESC - turn내림차순 정렬
        String sql = String.format("select distinct turn from %s order by turn desc", TABLE_NAME);
        Cursor cursor = selectdb.rawQuery(sql, null);

        ArrayList<Integer> turnlist = new ArrayList<>();
        while (cursor.moveToNext()){
            turnlist.add(cursor.getInt(cursor.getColumnIndex(COLUMN_TURN)));
        }

        // 회차종류 만큼 반복 -> dbAllList에 최신회차 순서부터 넣기
        ArrayList<ArrayList<DBinfo>> dbAllList = new ArrayList<>();
        for (int i=0; i<turnlist.size(); i++){
            dbAllList.add(selectDB(turnlist.get(i)));
            logTest("List",dbAllList.get(i),0);
        }

        return dbAllList;
    }

    //DB내용 삭제
    public ArrayList<ArrayList<DBinfo>> deleteDB(String numberset){
        Log.d("데이터베이스", "DB delete - "+ numberset);

        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'", TABLE_NAME, COLUMN_NUMSET, numberset);
        db.execSQL(sql);
        db.close();

        FragTwo.dialog.dltwo_listitem.clear();
        FragTwo.dialog.dltwo_listitem.addAll(selectListAllDB());
        for (int i=0; i<FragTwo.dialog.dltwo_listitem.size(); i++){
            StringBuilder log_text = new StringBuilder(" \n\tdltwo_listItem");
            Iterator<DBinfo> it = FragTwo.dialog.dltwo_listitem.get(i).iterator();
            while (it.hasNext()){
                log_text.append(" \n\t");
                log_text.append(it.next().getInfo());
                }
            Log.d("데이터베이스", log_text.toString());
        }

        FragTwo.dialog.dltwo_adapter.notifyDataSetChanged();
        return FragTwo.dialog.dltwo_listitem;   //DB전체 반환
}

    public void deletAllDB(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("DELETE FROM %s", TABLE_NAME);
        db.execSQL(sql);
        db.close();
    }

    public void logTest(String type, ArrayList<DBinfo> dbList, int turn) {
        int listCount = dbList.size(); //검색결과 갯수

        String logText = new String();
        if (type.compareTo("Select") == 0) {
            logText= "\nDBList ["+turn+"] Count => " + listCount + "개\n";
        }else {
            logText= "\nDBList [ALL] Count => " + listCount + "개\n";
        }
        
        for (int i = 0; i < listCount; i++) {
            logText += "\t\t" + type + "[" + i + "] => " + dbList.get(i).getInfo() + "\n";
        }
        Log.d("데이터베이스", "\nDBinsert 성공\n"+ logText);
    }
}

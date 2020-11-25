package pl.edu.uwr.runningapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "treningi_aplikacja_modyfikacja2";
    private static String DB_PATH = null;
    private static final int DB_VERSION = 1;
    public static final String TABLE_Twszystkie = "treningiwszystkie";


    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.mContext = context;

        this.DB_PATH = "/data/data/"+context.getPackageName()+"/"+"databases";

    }
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if (dbExist){

        }
        else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            }
            catch (IOException e){
                throw new Error("Error copying database");
            }
        }
    }
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String mPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(mPath,null,SQLiteDatabase.OPEN_READWRITE);
        }
        catch (SQLException e){

        }
        if (checkDB!=null){
            checkDB.close();
        }
        return checkDB != null ? true:false;

    }
    private void copyDataBase() throws IOException{
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while((length=mInput.read(buffer))>0){
            mOutput.write(buffer, 0 ,length);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
        Log.i("dziala","baza skopiowana");
    }
    public void openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath,null,SQLiteDatabase.OPEN_READWRITE);
        Log.i("dziala","baza otwarta");
    }

    @Override
    public synchronized void close(){
        if (mDataBase != null){
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (newVersion>oldVersion){
            try{
                copyDataBase();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    //public void dodajTreningBiegowyW(Integer nr_treningu,String data_treningu, String rodzaj,  Float dystans,String czas_trwania, String komentarz, String poszczegolne_odcinki, String srednie_tempo) {
    //    SQLiteDatabase db = this.getWritableDatabase();
    //    ContentValues contentValues = new ContentValues();
    //    contentValues.put("nr", nr_treningu);
    //    contentValues.put("Data", data_treningu);
    //    contentValues.put("Rodzaj", rodzaj);
    //    contentValues.put("Dystans", dystans);
    //    contentValues.put("Czas", czas_trwania);
    //    contentValues.put("Komentarz", komentarz);
    //    contentValues.put("PoszczegolneOdcinki", poszczegolne_odcinki);
    //    contentValues.put("SrednieTempo", srednie_tempo);
    //    mDataBase.insert("treningiwszystkie", null, contentValues);
    //}
    public void dodajTreningBiegowyWM(Integer nr_treningu,String data_treningu, String rodzaj,  Float dystans,String czas_trwania, String komentarz, String poszczegolne_odcinki, String srednie_tempo, String trasa_szerokosci, String trasa_dlugosci, String wysokoscUp, String wysokoscDown) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nr", nr_treningu);
        contentValues.put("Data", data_treningu);
        contentValues.put("Rodzaj", rodzaj);
        contentValues.put("Dystans", dystans);
        contentValues.put("Czas", czas_trwania);
        contentValues.put("Komentarz", komentarz);
        contentValues.put("PoszczegolneOdcinki", poszczegolne_odcinki);
        contentValues.put("SrednieTempo", srednie_tempo);
        contentValues.put("TrasaSzer", trasa_szerokosci);
        contentValues.put("TrasaDlug", trasa_dlugosci);
        contentValues.put("WysokoscUp", wysokoscUp);
        contentValues.put("WysokoscDown", wysokoscDown);
        mDataBase.insert("treningiwszystkie", null, contentValues);
    }
    public void dodajTreningSprawnosciowyW(Integer nr_treningu,String data_treningu, String rodzaj, String czas_trwania, String tresc_treningu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nr", nr_treningu);
        contentValues.put("Data", data_treningu);
        contentValues.put("Rodzaj", rodzaj);
        contentValues.put("Czas", czas_trwania);
        contentValues.put("TrescTreningu", tresc_treningu);
        contentValues.put("Komentarz", "");
        mDataBase.insert("treningiwszystkie", null, contentValues);
        //Log.i("zapis","zapisane");
    }
    public void dodajTreningSilowyW(Integer nr_treningu,String data_treningu, String rodzaj, String czas_trwania, Integer obciazenie_laczne, String tresc_treningu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nr", nr_treningu);
        contentValues.put("Data", data_treningu);
        contentValues.put("Rodzaj", rodzaj);
        contentValues.put("Czas", czas_trwania);
        contentValues.put("ObciazenieLaczne", obciazenie_laczne);
        contentValues.put("TrescTreningu", tresc_treningu);
        contentValues.put("Komentarz", "");
        mDataBase.insert("treningiwszystkie", null, contentValues);
    }
    public void dodajTreningSilaBiegowaW(Integer nr_treningu,String data_treningu, String rodzaj, String czas_trwania, Float dystans, String tresc_treningu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nr", nr_treningu);
        contentValues.put("Data", data_treningu);
        contentValues.put("Rodzaj", rodzaj);
        contentValues.put("Czas", czas_trwania);
        contentValues.put("Dystans", dystans);
        contentValues.put("TrescTreningu", tresc_treningu);
        contentValues.put("Komentarz", "");
        mDataBase.insert("treningiwszystkie", null, contentValues);
    }
    public void dodajTreningElementySzybkosciW(Integer nr_treningu, String data_treningu, String rodzaj, String czas_trwania, Float dystans, String tresc_treningu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nr", nr_treningu);
        contentValues.put("Data", data_treningu);
        contentValues.put("Rodzaj", rodzaj);
        contentValues.put("Czas", czas_trwania);
        contentValues.put("Dystans", dystans);
        contentValues.put("TrescTreningu", tresc_treningu);
        contentValues.put("Komentarz", "");
        mDataBase.insert("treningiwszystkie", null, contentValues);
    }

    public Integer countWszystkieTreningi(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        Cursor c = mDataBase.query("treningiwszystkie",null,null,null,null,null,null);
        int liczba_treningow = c.getCount();

        return liczba_treningow;
    }
    public Cursor queryWszystkieTreningii(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        return mDataBase.query("treningiwszystkie",null,null,null,null,null,null);
    }
    public Cursor queryWszystkieTreningi2(){
        String query="SELECT * FROM treningiwszystkie ORDER BY DATE(Data) DESC";
        return mDataBase.rawQuery(query, null);
    }
    public Cursor queryWszystkieTreningiPosortowane(){
        String query="SELECT * FROM treningiwszystkie ORDER BY date(Data) DESC";
        return mDataBase.rawQuery(query, null);
    }
    public Cursor queryPodgladTreningu(String nr_treningu){
        String query="SELECT * FROM treningiwszystkie WHERE nr = ?";
        String[] selectionArgs = {nr_treningu};
        return mDataBase.rawQuery(query, selectionArgs);
    }
    public boolean updateKomentarz(String komentarz, Integer id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Komentarz",komentarz);
        mDataBase.update("treningiwszystkie", cv, "nr="+id, null);
        return true;

    }
    public boolean deleteTrening(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        mDataBase.delete("treningiwszystkie","nr=?",new String[]{id});
        return true;
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.setVersion(oldVersion);
    }

}

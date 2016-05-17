package myapplication.mynewsapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ttslso on 2016/5/9.
 */
public class WebContentDBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME="WebContent.db";
    private static final int VERSION = 1;

    public WebContentDBHelper (Context context){
        super(context,DB_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table WebContent ("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "newsId INTEGER," +
                "json VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

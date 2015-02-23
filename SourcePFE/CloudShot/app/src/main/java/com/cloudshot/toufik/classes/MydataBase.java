package com.cloudshot.toufik.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toufik on 26/01/2015.
 */
public class MydataBase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    public static final String TABLE_DB="User_TB";

    public static final String KEY_ID=" _id";
    public static final String KEY_IDENTIFIANT="identifiant";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_CONTAINER="container";
    public static final String DATABASE_NAME = "User.db";

    public static final String CREATE_REQUEST="create table "+TABLE_DB+" ( " +
            KEY_ID+" integer, " +
            KEY_IDENTIFIANT+" TEXT, " +
            KEY_PASSWORD+" TEXT, "+
            KEY_CONTAINER+" TEXT )";

    public static final String DELETE_REQUEST="drop table if exists "+TABLE_DB;
    public static final String VIDER_TABLE="DELETE FROM "+TABLE_DB;



    public MydataBase(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public MydataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MydataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                      DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DELETE_REQUEST);
        onCreate(db);
    }

    public long insertUser(User newUser)
    {
        //Ouverture de la base en lecture
        SQLiteDatabase db = this.getWritableDatabase();

        //Creation d'un mappage de valeurs ou les noms des colonnes sont les clées
        ContentValues values = new ContentValues();

        values.put(KEY_ID,newUser.getId());
        values.put(KEY_IDENTIFIANT,newUser.getIdentifiant());
        values.put(KEY_PASSWORD,newUser.getPassword());
        values.put(KEY_CONTAINER,newUser.getContainer());

        //Insertion
        return db.insert(TABLE_DB,
                null,//Colonne technique qui pourra indiquer que values est null
                values);
    }


    public boolean isPresent(User user)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        //Specification de la liste des colonnes à lire
        String[] projection = {
                KEY_ID
        } ;

        String clauseWhere = KEY_ID+" = ?";
        String [] whereArgs = {String.valueOf(user.getId())};

        //Ordre des données
        String sortOrder = KEY_ID+" DESC";
        Cursor c = db.query(
                TABLE_DB,
                projection,
                clauseWhere,
                whereArgs,
                null,
                null,
                sortOrder
        );

        if(c.moveToNext())
            return true;
        else
            return false;

    }

    public User listeUser(){
        SQLiteDatabase db = this.getReadableDatabase();

        String requetSelect = "SELECT * FROM "+TABLE_DB;


        Cursor c = db.rawQuery(requetSelect, null);

        User user = new User();

        //while(c.moveToNext())
        if(c.moveToNext())
        {

            user.setId(Long.getLong(c.getString(0)));
            user.setIdentifiant(c.getString(1));
            user.setPassword(c.getString(2));
            user.setContainer(c.getString(3));


        }

        return user;
    }

    public void viderMaTable()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(VIDER_TABLE);
    }
}

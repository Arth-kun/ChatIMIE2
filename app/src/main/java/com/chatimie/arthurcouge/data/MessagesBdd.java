package com.chatimie.arthurcouge.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chatimie.arthurcouge.chatimie.Message;

import java.util.ArrayList;

public class MessagesBdd {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "tchatcheur.db";

    private static final String TABLE_MESSAGES = "table_messages";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_PSEUDO = "pseudo";
    private static final int NUM_COL_PSEUDO = 1;
    private static final String COL_MESSAGE = "message";
    private static final int NUM_COL_MESSAGE = 2;
    private static final String COL_HOUR = "datetime";
    private static final int NUM_COL_HOUR = 3;

    private SQLiteDatabase bdd;

    private MaBaseSQLite maBaseSQLite;

    public MessagesBdd(Context context){
        //On crée la BDD et sa table
        maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertMessage(Message msg){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_MESSAGE, msg.getMessage());
        values.put(COL_PSEUDO, msg.getPseudo());
        values.put(COL_HOUR, msg.getHour());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_MESSAGES, null, values);
    }

    public int updateMessage(int id, Message msg){
        //La mise à jour d'un message dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel message on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_MESSAGE, msg.getMessage());
        values.put(COL_PSEUDO, msg.getPseudo());
        values.put(COL_HOUR, msg.getHour());
        return bdd.update(TABLE_MESSAGES, values, COL_ID + " = " +id, null);
    }

    public int removeMessageWithID(int id){
        //Suppression d'un message de la BDD grâce à l'ID
        return bdd.delete(TABLE_MESSAGES, COL_ID + " = " +id, null);
    }

    //Cette méthode permet de convertir un cursor en un message
    private Message cursorToMessage(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //On créé un message
        Message msg = new Message(c.getString(NUM_COL_MESSAGE), c.getString(NUM_COL_PSEUDO), c.getString(NUM_COL_HOUR));

        //On retourne le message
        return msg;
    }

    public ArrayList<Message> getAll(){
        open();
        ArrayList<Message> messages = new ArrayList<>();
        Cursor c = bdd.query(TABLE_MESSAGES, null, null, null, null, null, null);
        if (c.moveToFirst()){
            while (c.isAfterLast() == false) {
                messages.add(cursorToMessage(c));
                c.moveToNext();
            }
        }
        close();
        return messages;
    }
}

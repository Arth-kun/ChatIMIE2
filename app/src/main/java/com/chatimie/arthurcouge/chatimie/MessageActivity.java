package com.chatimie.arthurcouge.chatimie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chatimie.arthurcouge.data.MessagesBdd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.ListItemClicklistener {

    private TextView textViewId;
    private TextView textViewBody;
    private TextView textViewTitle;
    private EditText messageAEnvoye;
    private String pseudo;

    private MessageAdapter mAdapter;
    private RecyclerView mGroupeMessageList;

    private static MessagesBdd messagesBdd;

    public static MessagesBdd getMessagesBdd() { return messagesBdd; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //Création d'une instance de ma classe MessagesDBB
        messagesBdd = new MessagesBdd(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pseudo = getIntent().getExtras().get("pseudo").toString();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageAEnvoye = (EditText) findViewById(R.id.plainTextMessage);
                Message msgEnvoye = new Message(pseudo, messageAEnvoye.getText().toString(), DateFormat.format("dd-MM hh:mm a", new Date()).toString());
                mAdapter.addToList(msgEnvoye);
                messageAEnvoye.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                messageAEnvoye.clearFocus();
            }
        });
        mGroupeMessageList = (RecyclerView) findViewById(R.id.recyclerViewMessageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mGroupeMessageList.setLayoutManager(linearLayoutManager);

        mGroupeMessageList.setHasFixedSize(false);
        mAdapter = new MessageAdapter(this);
        mGroupeMessageList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.KEY_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Pseudo", "");
            editor.apply();
            Intent changeActivity = new Intent(MessageActivity.this, MainActivity.class);
            startActivity(changeActivity);
            finish();
            return true;
        }

        if (id == R.id.clear_message) {
            messagesBdd.open();
            messagesBdd.removeAllMessages();
            messagesBdd.close();
            mAdapter.clearList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void getAndShowPost() {
        Uri constructionURI = Uri.parse("https://jsonplaceholder.typicode.com").buildUpon()
                .appendPath("posts")
                .appendPath("1")
                //.appendQueryParameter("id",String.valueOf(1))
                .build();
        try {
            URL urlFinal = new URL(constructionURI.toString());
            Toast.makeText(this.getBaseContext(), urlFinal.toString(), Toast.LENGTH_LONG).show();
            new AsyncTask<URL, Integer, String>() {
                @Override
                protected String doInBackground(URL... urls) {
                    if (urls.length > 0) {
                        try {
                            return getResponseFromHttpUrl(urls[0]);
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String result) {
                    if (result != null && !result.equals("")) {
                        Toast.makeText(MessageActivity.this, result, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject postOne = new JSONObject(result);
                            Iterator<?> keys = postOne.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                Log.i("MainActivity_KV", key + " : " + postOne.get(key));
                                switch (key) {
                                    case "userId":
                                        textViewId.setText(postOne.get(key).toString());
                                        break;
                                    case "body":
                                        textViewBody.setText(postOne.get(key).toString());
                                        break;
                                    case "title":
                                        textViewTitle.setText(postOne.get(key).toString());
                                        break;
                                }

                            }
                        } catch (JSONException jsonError) {
                            jsonError.printStackTrace();
                        }
                    }
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                }
            }.execute(urlFinal);


        } catch (IOException error) {
            error.printStackTrace();
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(String pseudo) {
/*        Intent i = new Intent();
        pseudo = "JimmyFonteneau";
        i.putExtra(Intent.EXTRA_TEXT, pseudo);
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://twitter.com/"+pseudo));
        startActivity(i);
        Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();*/
    }

/*    private Cursor getAllPosts(){
        return mDb.query(
                PostsBDDContract.PostsEntry.TABLE_NAME,null,null,null,null,null,
                PostsBDDContract.PostsEntry.COLUMN_DATE_NAME
        );
    }*/

    private void addPost(String date,String message,String pseudo){
/*        ContentValues cv = new ContentValues();
        cv.put(PostsBDDContract.PostsEntry.COLUMN_DATE_NAME,date);
        cv.put(PostsBDDContract.PostsEntry.COLUMN_MESSAGE_NAME,message);
        cv.put(PostsBDDContract.PostsEntry.COLUMN_PSEUDO_NAME,date);
        mDb.insert(PostsBDDContract.PostsEntry.TABLE_NAME,null,cv);*/
    }

    private void removePost(long id){
/*        mDb.delete(PostsBDDContract.PostsEntry.TABLE_NAME,
                PostsBDDContract.PostsEntry._ID + "=" + id,null);*/
    }

}

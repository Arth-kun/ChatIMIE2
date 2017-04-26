package com.chatimie.arthurcouge.chatimie;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView textViewId;
    TextView textViewBody;
    TextView textViewTitle;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final String KEY_PREFERENCES= "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Sans cette ligne l'application crash au démarrage
        SharedPreferences sharedPreferences = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        String pseudoSp = sharedPreferences.getString("Pseudo", null);
        if (!pseudoSp.equals("")) {
            Toast.makeText(this, pseudoSp, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("pseudo", pseudoSp);
            startActivity(intent);
        }
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

       /* textViewId = (TextView) findViewById(R.id.textViewId);
        textViewBody = (TextView) findViewById(R.id.textViewBody);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        getAndShowPost();*/

        Button buttonEnter = (Button) findViewById(R.id.buttonEnter);
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText plainTextBody = (EditText) findViewById(R.id.plainTextBody);
                String pseudo = plainTextBody.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Pseudo", pseudo);
                editor.apply();
                doINeedPermission(pseudo);
                Intent intent = new Intent(view.getContext(), MessageActivity.class);
                intent.putExtra("pseudo", pseudo);
                startActivity(intent);
            }
        });
        //ContentResolver resolver = getContentResolver();
        //URI uri = monProvider.CONTENT_URI;
        //Cursor cursor = resolver.query(uri,null,null,null,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                EditText plainTextBody = (EditText) findViewById(R.id.plainTextBody);
                String pseudo = plainTextBody.getText().toString();
                doINeedPermission(pseudo);
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void doINeedPermission(String pseudo) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            getTheNumber(pseudo);
        }
    }

    private void getTheNumber(String pseudo){
        //Get ses infos
        //get Content Resolver
        ContentResolver contentResolver = getContentResolver();
        //CRUD
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME LIKE '%" + pseudo + "%'", null, null);
        if (cursor.moveToFirst()) {
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //  Get all phone numbers.
            //
            Cursor phones = contentResolver.query(Phone.CONTENT_URI, null,
                    Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                Toast.makeText(getApplicationContext(),number,Toast.LENGTH_SHORT).show();
            }
            phones.close();
        }
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
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

    @Nullable
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
}
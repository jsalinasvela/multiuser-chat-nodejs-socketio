package com.ciberchat.ciberchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ciberchat.ciberchat.database.MyDbHelper;
import com.ciberchat.ciberchat.database.model.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by inmobitec on 7/1/18.
 */

public class LoginActivity extends AppCompatActivity {

    EditText cibertecid_input;
    Button login_btn;
    JSONArray data_result;
    String received_cibertecid, user_firstname, user_lastname;

    private JSONArray table;
    //private Db db;
    private MyDbHelper db;

    ProgressDialog dialog;
    User self_user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing database
        db = new MyDbHelper(getApplicationContext());
        table = db.init();

        cibertecid_input = (EditText) findViewById(R.id.cibertecid_input);
        login_btn = (Button)findViewById(R.id.login_btn);

        load_events();

    }

    public void load_events(){

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entered_id = String.valueOf(cibertecid_input.getText());

                //TODO:  DO SOME VALIDATION ON THE ENTERED_ID

                //retrieve user data from jsonserver to check if user is logged in or not, if not logged in,
                //the enter him in local database as the logged in user
                //then go to chat Activity

                if(entered_id!=""){
                    Log.v("LOGGING IN:", "TRUE");
                    Log.v("cibertecid:", entered_id);
                    Log.v("ESPACIO:", "");

                    String strURL= "https://my-json-server.typicode.com/jsalinasvela/sample-jsonserver-members/members?cibertec_id="+entered_id;

                    wsAsyncTask ws = new wsAsyncTask();
                    ws.execute(strURL);

                }else{
                    Log.v("cibertec_id:", "Empty");
                }
            }
        });
    }



    public class wsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] strUrl) {
            return requestWebService(strUrl[0]);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Logging in ...");
            dialog.setMessage("Obtaining information about authorized users...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result){

            Log.v("RESULTS: ", result);

            try {
                data_result = new JSONArray(result);
                if (data_result.length()>0){
                    received_cibertecid = data_result.getJSONObject(0).getString("cibertec_id");
                    user_firstname = data_result.getJSONObject(0).getString("first_name");
                    user_lastname = data_result.getJSONObject(0).getString("last_name");

                    //iNSERTAR USUARIO COMO LOGGEADO A LA BASE DE DATOS
                    self_user = new User(1,received_cibertecid, user_firstname, user_lastname);

                    JSONArray users = db.createUser(self_user);

                    Log.v("USERS:", String.valueOf(users));

                    //LLAMAR A MAIN ACTIVITY
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);

                    Bundle b = new Bundle();
                    b.putString("cibertec_id", received_cibertecid);
                    b.putString("first_name", user_firstname);
                    b.putString("last_name", user_lastname);
                    i.putExtras(b);
                    startActivity(i);
                    finish();

                }else{
                    Log.v("USER:", "DOES NOT EXIST");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*
            try {
                data_array = new JSONArray(data_result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fillOutResults();
            */

            dialog.dismiss();

        }

        public String requestWebService(String serviceURL){
            HttpURLConnection urlConnection = null;

            try{
                URL urlToRequest = new URL(serviceURL);
                urlConnection = (HttpURLConnection)urlToRequest.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                //Get JSON data
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Scanner scanner = new Scanner(in);
                String strJSON = scanner.useDelimiter("\\A").next();
                scanner.close();
                return strJSON;
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (SocketTimeoutException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally{
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }

            return null;
        }

    }
}

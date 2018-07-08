package com.snipthink.restapi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private  String TAG=MainActivity.class.getSimpleName();
private ProgressDialog progressDialog;
private ListView lv;
private static  String url="http://192.168.43.152:5000/api/v1/resources/books/all";
ArrayList<HashMap<String,String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList=new ArrayList<>();
        lv=(ListView)findViewById(R.id.list);

        new GetContacts().execute();

    }
    @SuppressLint("StaticFieldLeak")
    private class GetContacts extends AsyncTask<Void, Void, Void>{
        @Override

        protected void onPreExecute(){
            super.onPreExecute();
            //Showing progress dialog

            progressDialog =new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }


        @Override
        protected Void doInBackground(Void... voids) {
           HttpHandler sh =new HttpHandler();
           //Making a request to url and getting response
            String jsonStr=sh.makeServiceCall(url);

            Log.e(TAG,"Response from url: "+jsonStr);

            if (jsonStr!=null){
                try {
                    JSONObject jsonObj=new JSONObject(jsonStr);
                    //getting Json array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    //looping through ALL Contacts

                    for (int i=0;i< contacts.length();i++){
                        JSONObject c =contacts.getJSONObject(i);

                        String id =c.getString("id");
                        String name=c.getString("name");
                        String email=c.getString("email");
                        String address=c.getString("address");
                        String gender=c.getString("gender");

                        JSONObject phone=c.getJSONObject("phone");
                        String mobile=phone.getString("mobile");
                        String home=phone.getString("home");
                        String office=phone.getString("office");

                        HashMap<String,String> contact=new HashMap<>();
                        contact.put("id",id);
                        contact.put("name",name);
                        contact.put("email",email);
                        contact.put("mobile",mobile);
                        contact.put("office",office);
                        contact.put("home",home);

                        //adding contact to contact list

                        contactList.add(contact);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG,"Json parsing error: "+e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"JSON Parsing error:",Toast.LENGTH_SHORT).show();
                        }
                    });
               //     e.printStackTrace();
                }

            }else
            {
                Log.e(TAG,"Couldn't get json from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get json from server",Toast.LENGTH_SHORT).show();

                    }
                });
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            //Dismiss the progress dialog
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            ListAdapter adapter=new SimpleAdapter(
                    MainActivity.this,contactList,
                    R.layout.list_item,new String[]{"name","email","mobile","office","home"},new int[]{R.id.name,R.id.email,R.id.mobile,R.id.office,R.id.home}
            );

            lv.setAdapter(adapter);
        }




    }
}

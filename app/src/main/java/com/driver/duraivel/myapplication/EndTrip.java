package com.driver.duraivel.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class EndTrip extends AppCompatActivity {
    String customer, numb, pick, drop, skm="", bid;
    TextView cname, cnum, pickloca, droploca, bookid;
    String dista;
    EditText c1;
RequestQueue mQueue;
Button etp;
AlertDialog alert;
    String p="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip);
        final Intent intent = getIntent();
        mQueue = Volley.newRequestQueue(this);
        Bundle extras = intent.getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cname = (TextView) findViewById(R.id.cusnamea);
        etp=(Button)findViewById(R.id.stta);
        cnum = (TextView) findViewById(R.id.cusnuma);
        pickloca = (TextView) findViewById(R.id.frma);
        droploca = (TextView) findViewById(R.id.toa);
        bookid = (TextView) findViewById(R.id.boida);
        c1=(EditText)findViewById(R.id.kma);
        if (extras != null) {
            skm=extras.getString("skm");
            customer = extras.getString("cusname");
            cname.setText(customer);
            numb = extras.getString("cusnum");
            cnum.setText(numb);
            pick = extras.getString("pickloc");
            pickloca.setText(pick);
            drop = extras.getString("droploc");
            droploca.setText(drop);
            bid = extras.getString("bid");
            bookid.setText(bid);
          getDistanceInfo(pick,drop);
            etp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getApplicationContext(),pick+" "+drop,Toast.LENGTH_LONG).show();
                    Intent i =new Intent(EndTrip.this,GetAmount.class);
                    Bundle extras = new Bundle();
                    extras.putString("from",pick);
                    extras.putString("to",drop);
                    extras.putString("ckm", c1.getText().toString());
                    extras.putString("bid",bid);
                    i.putExtras(extras);
                    startActivity(i);
                }
            });

//int dip =Integer.parseInt(dista)+Integer.parseInt(skm);
       }

    }
    private void getDistanceInfo(String picking,String droping) {
        StringBuilder stringBuilder = new StringBuilder();
        final String[] ss = {"o"};
        String newpick=picking.replaceAll(" ","%20");
        String newdrop=droping.replaceAll(" ","%20");
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+newpick+"&destinations="+newdrop+"&key=AIzaSyAmkb3OfGp_AYsdEVLnSvy_3ivO1aNC8Ew";
        JsonObjectRequest request =new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("rows"); //To get the inventory as an array
                    JSONObject routes = jsonArray.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("elements");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    Log.i("Distance", distance.toString());
                    dista =distance.getString("text").replaceAll("km","").trim();
                    float pdis=Float.valueOf(dista);
                    float curdis=Float.valueOf(skm);
                   int totdis= (int) (pdis+curdis);
                    c1.setText(String.valueOf(totdis));
                    Toast.makeText(getApplicationContext(),dista,Toast.LENGTH_SHORT).show();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),String.valueOf(e.getMessage()),Toast.LENGTH_SHORT).show();

                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

            }
        });

        mQueue.add(request);


    }




}
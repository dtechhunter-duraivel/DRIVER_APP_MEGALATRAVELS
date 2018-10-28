package com.driver.duraivel.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Billing extends AppCompatActivity {
    String customer,numb,pick,drop,skm,bid;
    TextView cname,cnum,pickloca,droploca,bookid;
    AlertDialog alert;
    TextView sk,ek,dis;
    Button starttrip;
    SharedPreferences sharedpreferences;
    RequestQueue SQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        sharedpreferences = getApplicationContext().getSharedPreferences("sma", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        sk=(TextView)findViewById(R.id.km);
        ek=(TextView)findViewById(R.id.ekm);
        starttrip = (Button)findViewById(R.id.stt);
        cname=(TextView)findViewById(R.id.cusname);
        cnum=(TextView)findViewById(R.id.cusnum);
        pickloca =(TextView)findViewById(R.id.frm);
        droploca=(TextView)findViewById(R.id.to);
        dis=(TextView)findViewById(R.id.dis);
        bookid=(TextView)findViewById(R.id.boid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SQueue = Volley.newRequestQueue(Billing.this);
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null)
        {
            customer = extras.getString("cusname");
            cname.setText(customer);
            numb = extras.getString("cusnum");
            cnum.setText(numb);
            pick = extras.getString("pickloc");
            pickloca.setText(pick.substring(5).trim());
            drop = extras.getString("droploc");
            droploca.setText(drop.substring(3).trim());
            bid=extras.getString("bid");
            bookid.setText(bid);

            if(sharedpreferences.contains("authto"))
            {
                final String authtok = sharedpreferences.getString("authto", null).toString();
                getKm(authtok,bid);

                starttrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   Intent i = new Intent(Billing.this, EndTrip.class);
                        Bundle extras = new Bundle();
                        extras.putString("bid",bid);
                        extras.putString("pickloc", pick.substring(5));
                        extras.putString("droploc", drop.substring(3));
                        extras.putString("cusname", customer);
                        extras.putString("cusnum", numb);
                        Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
                        startJourney(authtok,bid,sk.getText().toString());
                        extras.putString("skm",sk.getText().toString());

                        i.putExtras(extras);
                        startActivity(i);
                    }
                });
            }


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
                    String dista =distance.getString("text").replaceAll("km","").trim();
                    float pdis=Float.valueOf(dista);
                    dis.setText(String.valueOf(pdis)+"KM");
                    ek.setText(String.valueOf(pdis+Integer.parseInt(sk.getText().toString())));
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

        SQueue.add(request);


    }




    void getKm(final String ato, final String bookid)
    {
        String url = "http://104.211.211.0/Mobile/api/Driver/GetStartingKM/"+bookid;
        final StringRequest strReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        sk.setText(response);
                        getDistanceInfo(pick,drop);

                    }
                },
                new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                })


        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + ato);
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse (NetworkResponse response)
            {
                int mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        SQueue.add(strReq);
    }
    void startJourney(final String ato, final String bookid, final String stkm)
    {
        String url = "http://104.211.211.0/Mobile/api/Driver/Pickup";
        final StringRequest strReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                       Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body=null;
                        if(error instanceof  ServerError)
                        {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Toast.makeText(getApplicationContext(),body,Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                })


        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + ato);
                return headers;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                Map<String,String> map = new HashMap<String, String>();
                map.put("Booking_ID", bookid);
                map.put("Vehicle_ID", "1");
                map.put("Driver_ID", "295e32f3-8990-4d41-a81a-a35f0b3885ea");
                map.put("Location_From", pick);
                map.put("Starting_KM", stkm);
                map.put("Narration", "new");
                map.put("Location_To", drop);
                map.put("Pickup_DateTime", dateFormat.format(date));
                return map;
            }
            @Override
            protected Response<String> parseNetworkResponse (NetworkResponse response)
            {
                int mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        SQueue.add(strReq);
    }




}

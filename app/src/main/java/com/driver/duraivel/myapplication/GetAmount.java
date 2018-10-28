package com.driver.duraivel.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GetAmount extends AppCompatActivity {

String nodays,closekm,nohours,ackm,hillkm,haltdays,tollcharge,permitcharge,othercharge,bid,parkingcharge,facility,pvisted,pic,dro;
EditText nod,cokm,noh,ack,hkm,hdays,tolc,perch,otherc,parkc,plv;
SharedPreferences sharedpreferences;
Spinner fac;
ProgressDialog pd;
AlertDialog alert;
Button gb;
RequestQueue SQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_amount);
        SQueue= Volley.newRequestQueue(this);
        sharedpreferences = getApplicationContext().getSharedPreferences("sma", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        final Intent intent = getIntent();
        nod=(EditText)findViewById(R.id.e1);
        noh=(EditText)findViewById(R.id.e2);
        ack=(EditText)findViewById(R.id.e3);
        fac =(Spinner)findViewById(R.id.e4);
        hkm=(EditText)findViewById(R.id.pass);
        hdays=(EditText)findViewById(R.id.confirmpass);
        tolc=(EditText)findViewById(R.id.toll);
        perch=(EditText)findViewById(R.id.permit);
        parkc =(EditText)findViewById(R.id.address);
        otherc=(EditText)findViewById(R.id.oc);
        plv=(EditText)findViewById(R.id.pv);
        gb=(Button)findViewById(R.id.gtbill);
        String arraySpinner[]=new String[]{"A/C","Non-A/C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fac.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       Bundle extras = intent.getExtras();
        if (extras != null) {
            bid = extras.getString("bid");
            closekm = extras.getString("ckm");
            pic = extras.getString("from");
            dro = extras.getString("to");
            //  Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();

            if (sharedpreferences.contains("authto")) {
                final String authtok = sharedpreferences.getString("authto", null).toString();
                gb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd = new ProgressDialog(GetAmount.this);
                        pd.setTitle("Proccessing");
                        pd.setMessage("Please Wait..");
                        pd.show();
                        nodays = nod.getText().toString();
                        nohours = noh.getText().toString();
                        ackm = ack.getText().toString();
                        if (fac.getSelectedItem().toString().equals("A/C")) {
                            facility = "1";
                        } else {
                            facility = "0";
                        }
                        haltdays = hdays.getText().toString().trim();
                        hillkm = hkm.getText().toString().trim();
                        tollcharge = tolc.getText().toString().trim();
                        permitcharge = perch.getText().toString().trim();
                        parkingcharge = parkc.getText().toString().trim();
                        othercharge = otherc.getText().toString().trim();
                        pvisted = plv.getText().toString().trim();
                         //   getKm(authtok,bid);
                        //  getNextPickUp(authtok);

                        gp(authtok, bid, haltdays, nodays ,hillkm, tollcharge, permitcharge, parkingcharge,ackm, othercharge, pvisted, nohours, closekm,facility);


                    }

                });
            }
        }
    }





    void gp(final String atok, final String bookid, final String haldays, final String noDays, final String hillkilom, final String tolCharge, final String permCharge, final String parkCharge, final String acKm, final String otherCharge, final String placesVisted, final String noHours, final String closeKm, final String fac)
    {
        String url = "http://104.211.211.0/Mobile/api/Driver/Drop";
        StringRequest strReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.i("HFGDHHHHHHHHHHHH", response);
                        Toast.makeText(getApplicationContext(),response +"ll",Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body=null;
                        try {
                            body=new String(error.networkResponse.data, "UTF-8");
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(GetAmount.this);
                        builder.setMessage(body).setTitle("Info")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        alert.dismiss();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                        Toast.makeText(getApplicationContext(),body,Toast.LENGTH_LONG).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("BookingID", bookid);
                map.put("ClosingKM",closeKm);
                map.put("Facility",fac);
                map.put("NoOfDays",noDays);
                map.put("NoOfHours",noHours);
                map.put("ACKM",acKm);
                map.put("HillKM",hillkilom);
                map.put("HaltDays",haldays);
                map.put("TollCharge",tolCharge);
                map.put("PermitCharge",permCharge);
                map.put("ParkingCharge",parkCharge);
                map.put("OtherCharge",otherCharge);
                map.put("PlacesVisited",placesVisted);
                Log.i("MAP:",map.toString());
                Log.i("hello:", "Durai");
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+atok);
                return headers;
            }



        };
        SQueue.add(strReq);
      strReq.setRetryPolicy(new DefaultRetryPolicy(
              (int) TimeUnit.SECONDS.toMillis(20),
              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}

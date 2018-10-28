package com.driver.duraivel.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main7Activity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "sma" ;
    private RequestQueue mQueue,SQueue;
    ProgressDialog pd;
    AlertDialog alert;
    SharedPreferences pref;
    ArrayAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<ModelProduct> productList;
    String[] bookid;
    String[] cusname;
    String[] cusphone;
    String[] pick ;
    String[] drop;
    String[] drivername;
    String[] drivephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);bookid=new String[50];
       cusname=new String[50];
        cusphone=new String[50];
        pick=new String[50];
        drop=new String[50];
      drivername=new String[50];
        drivephone=new String[50];

     SQueue = Volley.newRequestQueue(Main7Activity.this);
        pref = getApplicationContext().getSharedPreferences("sma", Context.MODE_MULTI_PROCESS); // 0 - for private mode
getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(pref.contains("authto"))
{
    String authtok = pref.getString("authto", null).toString();

    getNextPickUp(authtok);
}
        recyclerView = findViewById(R.id.rv);
        productList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvLayoutManager = layoutManager;
        recyclerView.setLayoutManager(rvLayoutManager);



    }
    void adapt(String cname,String cnum,String bid,String ploc,String dloc )
    {

        productList.add(new ModelProduct(cname,cnum,bid,"From: "+ploc,"To: "+dloc));
            //productList.add(new ModelProduct(R.drawable.indica,"Indica","140 kMph","Tata","Min. Rs.100/4 kms","Additional Charges 15 Rs/km","A/C"));
    }
    void setList()
    {
        ProductAdapter adapter =new ProductAdapter(this,productList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position)
            {
               // Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
                String id = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.desc1)).getText().toString();
                String picloc = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.cst)).getText().toString();
                String droploc= ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.amount)).getText().toString();
                String cusnum= ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.desc)).getText().toString();
                String cusname = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.head)).getText().toString();
                Intent i = new Intent(Main7Activity.this, Billing.class);
                Bundle extras = new Bundle();
                extras.putString("bid",id);
                extras.putString("pickloc", picloc);
                extras.putString("droploc", droploc);
                extras.putString("cusname", cusname);
                extras.putString("cusnum", cusnum);
                i.putExtras(extras);
                startActivity(i);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
              //  Toast.makeText(getApplicationContext(),"Clicked"+id,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void OnBookClick(int position) {
              //  Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
            }
        });

    }
    void getNextPickUp(final String tsk) {

        String url = "http://104.211.211.0/Mobile/api/Driver/GetNextPickup";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONArray jsonArray = response.getJSONArray("PickupData"); //To get the inventory as an array
                            int i;
                            for (i = 0; i < jsonArray.length(); i++) {

                                JSONObject book = jsonArray.getJSONObject(i);
                                bookid[i] = book.getString("Booking_ID");
                              cusname[i] = book.getString("CustomerName");
                                cusphone[i] = book.getString("CustomerPhoneNumber");
                                pick[i] = book.getString("PickupAddress");
                               drop[i] = book.getString("DropAddress");

                                drivername[i] = book.getString("DriverName");

                               drivephone[i] = book.getString("DriverPhoneNumber");

                                adapt(cusname[i], cusphone[i], bookid[i],pick[i],drop[i]);
if(i==jsonArray.length()-1)
                                {
                                    setList();
                                }
                            }
                            Toast.makeText(getApplicationContext(),String.valueOf(i)+String.valueOf(jsonArray.length()-1),Toast.LENGTH_SHORT).show();

                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
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
                    message = "Connection TimeOut! This may failed to update the tarrif details";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Main7Activity.this);
                builder.setMessage(message).setTitle("Info")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.show();

               // Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }

        })
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+tsk);
                return headers;
            }
        };


        SQueue.add(request);
    }

}

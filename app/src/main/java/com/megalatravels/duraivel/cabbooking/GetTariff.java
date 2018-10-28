package com.megalatravels.duraivel.cabbooking;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GetTariff {

    RequestQueue SQueue;

  public String[] fetchtarif(final String token, final Context context)
    {
        SQueue = Volley.newRequestQueue(context);
String tdetails[]={"durai","d"};


        StringRequest request = new StringRequest(Request.Method.GET, "http://mobile/api/Account/UserInfo", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject udetails =new JSONObject(response);
                    String name=udetails.getString("UserName");
                    String num=udetails.getString("Email");
                    //    userid.setText(name);
                    //  mob.setText(num);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = null;
                String body = null;
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    Toast.makeText(context,   "Error:" + body, Toast.LENGTH_SHORT).show();

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(context, "Unsupported Encoding", Toast.LENGTH_SHORT).show();
                }

                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "User details does not meets minimum the requirements of Megala Travels";
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
        })
        {





        };
        SQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return tdetails ;

    }
}

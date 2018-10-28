package com.driver.duraivel.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {
RequestQueue SQueue;
EditText num,pass;
Button reset;
AlertDialog alert;
ProgressDialog pd;
String numb,passw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        num=(EditText)findViewById(R.id.number);
        pass=(EditText)findViewById(R.id.password);
        reset=(Button)findViewById(R.id.reset);
        pd=new ProgressDialog(this);
        pd.setTitle("Verifying");
        pd.setMessage("Checking Your Credentials");
        numb=num.getText().toString();
        passw=pass.getText().toString();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                resetPassword(numb,passw);
            }
        });
    }
    void resetPassword(final String number,final  String password)
    {
        //  Toast.makeText(getApplicationContext(),tok,Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://mobile.megalatravels.com/api/Booking/BookMyTrip", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                //  Toast.makeText(getApplicationContext(),response+"Your Car ha",Toast.LENGTH_LONG).show()
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                builder.setMessage("Your password has been reset successfully").setTitle("Info")
                        .setCancelable(false)
                        .setPositiveButton("GO TO LOGIN", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                String message = null;
                String body = null;
                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    Toast.makeText(getApplicationContext(), statusCode + ":" + body, Toast.LENGTH_SHORT).show();

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(getApplicationContext(), "Unsupported Encoding", Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                builder.setMessage(message).setTitle("Info")
                        .setCancelable(false)
                        .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.show();
            }
        })
        {



            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> map = new HashMap<String, String>();
                map.put("PhoneNumber", number);
                map.put("Password", password);
                map.put("ConfirmPassword", password);
                return map;
            }

        };
        SQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}

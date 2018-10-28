package com.driver.duraivel.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Bill extends AppCompatActivity {
TextView startpoint,endpoint,amo;
String sp,ep,am;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        startpoint=(TextView)findViewById(R.id.spoint);
        endpoint=(TextView)findViewById(R.id.epoint);
        amo=(TextView)findViewById(R.id.t2);
        Intent intent=getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            sp=extras.getString("stpoint");
            startpoint.setText(sp);
            ep=extras.getString("eppoint");
            endpoint.setText(ep);
            am=extras.getString("amount");
            amo.setText(am);
        }
    }

}

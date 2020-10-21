package com.example.cityparcelproject.cityparcel.track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cityparcelproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShippingPackageActivity extends AppCompatActivity {

    private static String URL = "http://thecityparcel.com/ShippingPackageList.php";
    private ShippingPackageAdapter shippingPackageAdapter;
    private RecyclerView recyclerView;
    private String memEmail, getTitle, getDestination, getPrice, getDeliveryman;
    private int getIndex;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_package);
        Intent intent = getIntent();
        memEmail = intent.getStringExtra("memEmail");
        init();
    }
    private void init() {
        recyclerView = findViewById(R.id.shipping_package_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        shippingPackageAdapter = new ShippingPackageAdapter(ShippingPackageActivity.this);
        shippingPackageListPHP();
    }

    private void shippingPackageListPHP() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonobject = new JSONObject(response);
                    JSONArray jsonarray = jsonobject.getJSONArray("response");
                    for (int i = 0; i < jsonarray.length(); ++i) {
                        JSONObject jData = jsonarray.getJSONObject(i);
                        getTitle = jData.getString("parcel_title");
                        getDestination = jData.getString("parcel_destination");
                        getPrice = jData.getString("parcel_price");
                        getIndex = jData.getInt("parcel_idx");
                        getDeliveryman = jData.getString("parcel_deliveryman");
                        shippingPackageAdapter.addItem(new ShippingPackageNode(getTitle, getDestination, getPrice, getIndex, getDeliveryman));
                    }
                    recyclerView.setAdapter(shippingPackageAdapter); //show
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data [" + e.getMessage()+"]");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShippingPackageActivity.this, "DB CONNECTION FAIL", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("memEmail", memEmail);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

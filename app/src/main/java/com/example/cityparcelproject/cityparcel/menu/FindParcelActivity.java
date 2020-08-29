package com.example.cityparcelproject.cityparcel.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class FindParcelActivity extends AppCompatActivity {

    private FindParcelAdapter findParcelAdapter;
    private static String URL = "http://thecityparcel.com/ParcelList.php";
    private String startingPoint, getTitle, getDestination, getPrice, getMember, memEmail, name, uid;
    private TextView startingPointTextView;
    private RecyclerView recyclerView;
    private int index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_parcel);

        Intent intent = getIntent();
        startingPoint = intent.getStringExtra("startingPoint");
        memEmail = intent.getStringExtra("memEmail");
        uid = intent.getStringExtra("uid");
        name = intent.getStringExtra("name");
        startingPointTextView = (TextView) findViewById(R.id.textView_find_parcel_startPoint);
        startingPointTextView.setText(startingPoint);

        init();

        findParcelAdapter.setOnItemClickListener(new FindParcelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(FindParcelActivity.this, FindParcelInfoActivity.class);
                intent.putExtra("parcelIdx", findParcelAdapter.getList().get(position).getIndex());
                intent.putExtra("memEmail", memEmail);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                FindParcelActivity.this.startActivity(intent);
            }
        });
    }

    private void init() {
        recyclerView = findViewById(R.id.find_parcel_recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        findParcelAdapter = new FindParcelAdapter();
        getParcelListPHP();

    }

    private void getParcelListPHP() {
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
                        index = jData.getInt("parcel_idx");
                        getMember = jData.getString("parcel_member");
                        findParcelAdapter.addItem(new FindParcelNode(index, getTitle, getDestination, getPrice, getMember));
                    }
                    recyclerView.setAdapter(findParcelAdapter); //show
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data [" + e.getMessage()+"]");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FindParcelActivity.this, "DB CONNECTION FAIL", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("location", startingPoint);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

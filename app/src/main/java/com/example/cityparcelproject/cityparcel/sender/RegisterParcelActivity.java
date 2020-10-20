package com.example.cityparcelproject.cityparcel.sender;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cityparcelproject.R;
import com.example.cityparcelproject.cityparcel.webView.DaumWebViewActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterParcelActivity extends AppCompatActivity {

    private static final int DaumWebViewActivityForResult = 10001;
    private String parcelTitle, parcelPrice, parcelLocation, parcelDestination, parcelForm,  parcelState, parcelMember, name, uid, parcelWeight, parcelDistance;
    private TextView myLocationTextView;
    private EditText titleEditText, priceEditText, destinationEditText, fromEditText, weightEditText, distanceEditText;
    private Button registerDoneBtn;
    private ImageButton findLocationImageBtn, calculatorImageBtn;

    private static String URL = "http://thecityparcel.com/RegisterParcel.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_parcel);

        Intent intent = getIntent();
        parcelMember = intent.getStringExtra("memEmail");
        parcelLocation = intent.getStringExtra("myLocation");
        name = intent.getStringExtra("name");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        registerDoneBtn = (Button) findViewById(R.id.button_registerParcel_done);
        titleEditText = (EditText) findViewById(R.id.editText_registerParcel_title);
        priceEditText = (EditText) findViewById(R.id.editText_registerParcel_price);
        myLocationTextView = (TextView) findViewById(R.id.textView_registerParcel_myLocation);
        destinationEditText =  (EditText) findViewById(R.id.editText_registerParcel_destination);
        findLocationImageBtn = (ImageButton) findViewById(R.id.imageButton_registerParcel_findAddress);
        calculatorImageBtn = findViewById(R.id.imageButton_registerParcel_calculator);
        fromEditText = (EditText) findViewById(R.id.editText_registerParcel_form);
        weightEditText = findViewById(R.id.editText_registerParcel_weight);
        distanceEditText = findViewById(R.id.editText_registerParcel_distance);


        myLocationTextView.setText(parcelLocation);

        calculatorImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(weightEditText.getText().toString().length() > 0 && distanceEditText.getText().toString().length() > 0) {
                    double weightPrice = Double.parseDouble(weightEditText.getText().toString());
                    double distancePrice = Double.parseDouble(distanceEditText.getText().toString());
                    double result = (weightPrice * 500) + (distancePrice * 1000);
                    priceEditText.setText(Integer.toString((int)result));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "무게(kg)와 거리(km)를 작성해 주세요!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }
        });

        findLocationImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent daumWebViewIntent = new Intent(RegisterParcelActivity.this, DaumWebViewActivity.class);
                RegisterParcelActivity.this.startActivityForResult(daumWebViewIntent, DaumWebViewActivityForResult);
            }
        });

        registerDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parcelTitle = titleEditText.getText().toString();
                parcelPrice = priceEditText.getText().toString();
                parcelDestination = destinationEditText.getText().toString();
                parcelForm = fromEditText.getText().toString();
                parcelState = "scheduled";
                parcelWeight = weightEditText.getText().toString();
                parcelDistance = distanceEditText.getText().toString();

                if(parcelTitle.length() < 3) {
                    Toast.makeText(getApplicationContext(),"3글자 이상 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                    titleEditText.requestFocus();
                    return;
                }
                if(parcelPrice.length() < 1) {
                    Toast.makeText(getApplicationContext(),"희망 운송 비용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    priceEditText.requestFocus();
                    return;
                }

                if(parcelDestination.length() == 1) {
                    Toast.makeText(getApplicationContext(),"목적지 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                    destinationEditText.requestFocus();
                    return;
                }

                if(parcelForm.length() < 10) {
                    Toast.makeText(getApplicationContext(),"운송 물품에 관한 정보를 10글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                    fromEditText.requestFocus();
                    return;
                }

                if(parcelWeight.length() < 1) {
                    Toast.makeText(getApplicationContext(),"운송 물품의 무게를 입력해주세요", Toast.LENGTH_SHORT).show();
                    fromEditText.requestFocus();
                    return;
                }

                if(parcelDistance.length() < 1) {
                    Toast.makeText(getApplicationContext(),"예상 거리를 입력해주세요", Toast.LENGTH_SHORT).show();
                    fromEditText.requestFocus();
                    return;
                }
                    doRegisterParcelPHP();
                    finish();

            }
        });
    }

    private void doRegisterParcelPHP() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "운송정보가 등록됬어요", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterParcelActivity.this, "Error!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterParcelActivity.this, "Error!" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("parcelTitle", parcelTitle);
                params.put("parcelPrice", parcelPrice);
                params.put("parcelLocation", parcelLocation);
                params.put("parcelDestination", parcelDestination);
                params.put("parcelForm", parcelForm);
                params.put("parcelMember", parcelMember);
                params.put("parcelState", parcelState);
                params.put("senderName", name);
                params.put("uid", uid);
                params.put("parcelWeight", parcelWeight);
                params.put("parcelDistance", parcelDistance);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {

            case DaumWebViewActivityForResult:

                if (resultCode == RESULT_OK) {
                    String address = intent.getExtras().getString("address");
                    if (address != null)
                        destinationEditText.setText(address);

                }
                break;

        }
    }

    public void btnBackIconClicked(View view) {
        onBackPressed();
    }
}

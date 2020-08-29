package com.example.cityparcelproject.cityparcel.track;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cityparcelproject.R;

public class TrackMyParcelActivity extends AppCompatActivity {

    Button scheduledBtn, ShippingBtn, completedBtn;

    private String memEmail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_my_parcel);

        Intent intent = getIntent();
        memEmail = intent.getStringExtra("memEmail");

        scheduledBtn = (Button) findViewById(R.id.button_trackMyParcel_scheduled);
        ShippingBtn = (Button) findViewById(R.id.button_trackMyParcel_shipping);
        completedBtn = (Button) findViewById(R.id.button_trackMyParcel_completed);

        scheduledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackMyParcelActivity.this, ScheduledPackageActivity.class);
                intent.putExtra("memEmail", memEmail);
                TrackMyParcelActivity.this.startActivity(intent);
            }
        });

        ShippingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackMyParcelActivity.this, ShippingPackageActivity.class);
                intent.putExtra("memEmail", memEmail);
                TrackMyParcelActivity.this.startActivity(intent);
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackMyParcelActivity.this, CompletedPackageActivity.class);
                intent.putExtra("memEmail", memEmail);
                TrackMyParcelActivity.this.startActivity(intent);
            }
        });
    }
}

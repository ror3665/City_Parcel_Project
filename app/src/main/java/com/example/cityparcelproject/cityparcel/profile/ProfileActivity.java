package com.example.cityparcelproject.cityparcel.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cityparcelproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView email_textView, username_textView;
    private Button redeliveryRateButton, mannerButton;
    private String myUid;
    private  VoteModel voteModel;

    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email_textView = findViewById(R.id.profile_user_email);
        username_textView = findViewById(R.id.profile_user_display_name);
        redeliveryRateButton = findViewById(R.id.profile_user_redelivery_rate_button);
        mannerButton = findViewById(R.id.profile_user_manner_button);
        voteModel = new VoteModel();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        basicInfo();
        getVotes();

        redeliveryRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = voteModel.redeliveryRate;
                int value = Integer.parseInt(str) + 1;
                String result = Integer.toString(value);
                FirebaseDatabase.getInstance().getReference().child("profile").child(myUid).child("redeliveryRate").setValue(result);
            }
        });

        mannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = voteModel.manner;
                int value = Integer.parseInt(str) + 1;
                String result = Integer.toString(value);
                FirebaseDatabase.getInstance().getReference().child("profile").child(myUid).child("manner").setValue(result);
            }
        });

    }

    private void basicInfo() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        email_textView.setText(email);
        username_textView.setText(username);
    }

    void getVotes() {
        FirebaseDatabase.getInstance().getReference().child("profile").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()) {
                    voteModel = snapshot.getValue(VoteModel.class);
                }
                redeliveryRateButton.setText("재거래희망 +" + voteModel.redeliveryRate);
                mannerButton.setText("좋아요 +" + voteModel.manner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void btnBackIconClicked(View view) {
        onBackPressed();
    }
}

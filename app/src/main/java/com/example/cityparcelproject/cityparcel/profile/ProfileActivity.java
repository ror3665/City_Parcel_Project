package com.example.cityparcelproject.cityparcel.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityparcelproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView email_textView, username_textView;
    private EditText editTextComment;
    private Button redeliveryRateButton, mannerButton;
    private ImageButton senderButton;
    private String profileUID;
    private VoteModel voteModel;
    private RecyclerView recyclerView;
    private ProfileCommentAdapter profileCommentAdapter;

    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        profileUID = intent.getStringExtra("uid");
        email_textView = findViewById(R.id.profile_user_email);
        username_textView = findViewById(R.id.profile_user_display_name);
        editTextComment = findViewById(R.id.editText_profile_comment);
        redeliveryRateButton = findViewById(R.id.profile_user_redelivery_rate_button);
        mannerButton = findViewById(R.id.profile_user_manner_button);
        senderButton = findViewById(R.id.button_profile_comment_send);
        recyclerView = findViewById(R.id.parcel_profile_recycleView);
        voteModel = new VoteModel();

        basicInfo();
        getVotes();
        init();

        redeliveryRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = voteModel.redeliveryRate;
                int value = Integer.parseInt(str) + 1;
                String result = Integer.toString(value);
                FirebaseDatabase.getInstance().getReference().child("profile").child(profileUID).child("redeliveryRate").setValue(result);
            }
        });

        mannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = voteModel.manner;
                int value = Integer.parseInt(str) + 1;
                String result = Integer.toString(value);
                FirebaseDatabase.getInstance().getReference().child("profile").child(profileUID).child("manner").setValue(result);
            }
        });

        senderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String message = editTextComment.getText().toString();
                Object timestamp = ServerValue.TIMESTAMP;
                String uid = profileUID;

                CommentModel commentModel = new CommentModel(username, message, timestamp, uid);
                FirebaseDatabase.getInstance().getReference().child("profile").child(profileUID).child("comment").push().setValue(commentModel);
                editTextComment.setText(""); // init
                Toast toast = Toast.makeText(getApplicationContext(), "댓글이 등록되었어요", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                //disable keyboard
                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
            }
        });
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        profileCommentAdapter = new ProfileCommentAdapter();
        getComment();
    }

    private void basicInfo() {
        FirebaseDatabase.getInstance().getReference().child("users").child(profileUID).child("userEmail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                email_textView.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(profileUID).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                username_textView.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getVotes() {

        FirebaseDatabase.getInstance().getReference().child("profile").child(profileUID).addValueEventListener(new ValueEventListener() {
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
                throw error.toException();
            }
        });
    }

    void getComment() {

        FirebaseDatabase.getInstance().getReference().child("profile").child(profileUID).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileCommentAdapter.deleteAllItem();
                for(DataSnapshot item : snapshot.getChildren()) {
                    CommentModel commentModel =  item.getValue(CommentModel.class);
                    profileCommentAdapter.addItem(commentModel);
                }
                recyclerView.setAdapter(profileCommentAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    public void btnBackIconClicked(View view) {
        onBackPressed();
    }
}

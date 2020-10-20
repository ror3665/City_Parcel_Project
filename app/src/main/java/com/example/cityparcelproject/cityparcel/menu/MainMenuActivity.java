package com.example.cityparcelproject.cityparcel.menu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cityparcelproject.R;
import com.example.cityparcelproject.cityparcel.authentication.MainActivity;
import com.example.cityparcelproject.cityparcel.deliveryman.DeliveryManActivity;
import com.example.cityparcelproject.cityparcel.gps.GpsTracker;
import com.example.cityparcelproject.cityparcel.message.UserModel;
import com.example.cityparcelproject.cityparcel.profile.ProfileActivity;
import com.example.cityparcelproject.cityparcel.profile.VoteModel;
import com.example.cityparcelproject.cityparcel.sender.RegisterParcelActivity;
import com.example.cityparcelproject.cityparcel.track.TrackMyParcelActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity  implements View.OnClickListener {

    //album
    private static final int PICK_FROM_ALBUM = 10;

    //GPS
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //auth
    private IdpResponse mIdpResponse;

    //etc
    Button findCityButton, registerParcelButton, findParcelButton, myProfileButton;
    Button trackMyParcelBtn, deliverymanBtn, signoutbtn, deleteuser;
    private TextView emailtxt, usernametxt, citytxt;
    String memEmail, name;
    private ImageView imageViewProfile;
    private Uri imageUri;
    private String profileUid;
    private boolean isAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());

        setContentView(R.layout.activity_main_menu);
        populateProfile();

        citytxt = (TextView) findViewById(R.id.textView_serviceMain_userCity);
        findCityButton = (Button) findViewById(R.id.button_serviceMain_findCity);
        registerParcelButton = (Button) findViewById(R.id.button_serviceMain_registerParcel);
        findParcelButton = (Button) findViewById(R.id.button_serviceMain_findParcel);
        trackMyParcelBtn = (Button) findViewById(R.id.button_serviceMain_trackMyParcel);
        deliverymanBtn = (Button) findViewById(R.id.button_serviceMain_deliveryman);
        signoutbtn = (Button) findViewById(R.id.sign_out);
        deleteuser = (Button) findViewById(R.id.delete_account);
        imageViewProfile = findViewById(R.id.profile_imageView_profile);

        myProfileButton = (Button) findViewById(R.id.button_serviceMain_my_profile);

        signoutbtn.setOnClickListener(this);
        deleteuser.setOnClickListener(this);
        findCityButton.setOnClickListener(this);
        registerParcelButton.setOnClickListener(this);
        findParcelButton.setOnClickListener(this);
        trackMyParcelBtn.setOnClickListener(this);
        deliverymanBtn.setOnClickListener(this);
        //profile.setOnClickListener(this);
        myProfileButton.setOnClickListener(this);
        checkProfile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_serviceMain_findCity:
                findCity();
                break;
            case R.id.button_serviceMain_registerParcel:
                registerParcel();
                break;
            case R.id.button_serviceMain_findParcel:
                findParcel();
                break;
            case R.id.button_serviceMain_trackMyParcel:
                trackMyParcel();
                break;
            case R.id.button_serviceMain_deliveryman:
                deliveryman();
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.delete_account:
                deleteAccountClicked();
                break;
//            case R.id.mainMenuActivity_imageView_profile:
//                setImageProfile();
//                break;
            case R.id.button_serviceMain_my_profile:
                myProfileButtonClicked();
                break;
            default:
                break;
        }
    }

    private void myProfileButtonClicked() {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        VoteModel voteModel = new VoteModel();
        voteModel.redeliveryRate = "0";
        voteModel.manner = "0";
        voteModel.uid = uid;
        checkProfile();
        if(isAvailable) {
            FirebaseDatabase.getInstance().getReference().child("profile").child(uid).setValue(voteModel);
            checkProfile();
        }
        checkProfile();
        Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
        intent.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        MainMenuActivity.this.startActivity(intent);
    }

    void checkProfile() {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("profile").child(uid).orderByChild(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isAvailable = false;
                } else {
                    isAvailable = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    private void setImageProfile() {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                startActivityForResult(intent, PICK_FROM_ALBUM);
//                saveImageProfile();
//    }

    private void findCity() {
        gpsTracker = new GpsTracker(MainMenuActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        String address = getCurrentAddress(latitude, longitude);
        citytxt.setText(address);
        registerParcelButton.setEnabled(true);
        registerParcelButton.setTextColor(Color.parseColor("#00BFFF"));
        findParcelButton.setEnabled(true);
        findParcelButton.setTextColor(Color.parseColor("#00BFFF"));
        //Toast.makeText(MainMenu.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
    }

    private void registerParcel() {
        Intent intent = new Intent(MainMenuActivity.this, RegisterParcelActivity.class);
        intent.putExtra("memEmail", memEmail);
        intent.putExtra("myLocation", citytxt.getText().toString());
        intent.putExtra("name", name);
        MainMenuActivity.this.startActivity(intent);
    }

    private void findParcel() {
        String startingPoint = citytxt.getText().toString();
        Intent intent = new Intent(MainMenuActivity.this, FindParcelActivity.class);
        intent.putExtra("startingPoint", startingPoint);
        intent.putExtra("name", name);
        MainMenuActivity.this.startActivity(intent);
    }
    private void trackMyParcel() {
        Intent intent = new Intent(MainMenuActivity.this, TrackMyParcelActivity.class);
        intent.putExtra("memEmail", memEmail);
        MainMenuActivity.this.startActivity(intent);
    }
    private void deliveryman() {
        Intent intent = new Intent(MainMenuActivity.this, DeliveryManActivity.class);
        intent.putExtra("memEmail", memEmail);
        MainMenuActivity.this.startActivity(intent);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                            intent.putExtra("memEmail", memEmail);
                            intent.putExtra("myLocation", citytxt.getText().toString());
                            intent.putExtra("name", name);
                            MainMenuActivity.this.startActivity(intent);
                        } else {
                        }
                    }
                });
    }

    private void deleteAccountClicked() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .create();

        dialog.show();
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                        } else {
                        }
                    }
                });
    }

    private void populateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        emailtxt = (TextView) findViewById(R.id.user_email);
        emailtxt.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        memEmail = emailtxt.getText().toString();
        usernametxt = (TextView) findViewById(R.id.user_display_name);
        usernametxt.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());
        name = usernametxt.getText().toString();
        StringBuilder providerList = new StringBuilder(100);

        providerList.append("Providers used: ");

        if (user.getProviderData() == null || user.getProviderData().isEmpty()) {
            providerList.append("none");
        } else {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                if (GoogleAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Google");
                } else {
                    providerList.append(providerId);
                }
            }
        }

        //데이터베이스에 저장
        final String uid = user.getUid();
        Uri uri = Uri.parse("android.resource://com.example.cityparcelproject/drawable/circle_member_profile");
        FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                while(!imageUrl.isComplete());
                UserModel userModel = new UserModel();
               // userModel.profileImageUrl = imageUrl.getResult().toString();
                userModel.userEmail = memEmail;
                userModel.userName = name;

                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);

                //token 넣기
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Map<String, Object> map = new HashMap<>();
                        map.put("pushToken", token);
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
                    }
                });
            }
        });

    }

//
//    public void saveImageProfile() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        final String uid = user.getUid();
//        FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
//                while(!imageUrl.isComplete());
//                UserModel userModel = new UserModel();
//                userModel.profileImageUrl = imageUrl.getResult().toString();
//                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
//            }
//        });
//    }

    //Geocoder GPS -> 주소
    public String getCurrentAddress(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.KOREA); //한국 서비스 전용
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        String fullAddress = address.getAddressLine(0).toString();
        String[] trimAddress = fullAddress.split(" ");
        String result = trimAddress[1] + " " + trimAddress[2] + " " + trimAddress[3];
        return result;
        //return address.getAddressLine(0).toString()+"\n";
    }

    /* GPS 권한 관련*/
    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainMenuActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainMenuActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainMenuActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainMenuActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainMenuActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainMenuActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainMenuActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainMenuActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainMenuActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            imageViewProfile.setImageURI(data.getData());
            imageUri = data.getData();
        }

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;

        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




}

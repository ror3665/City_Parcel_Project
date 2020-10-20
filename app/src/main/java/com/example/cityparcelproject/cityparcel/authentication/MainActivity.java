package com.example.cityparcelproject.cityparcel.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cityparcelproject.R;
import com.example.cityparcelproject.cityparcel.menu.MainMenuActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_SIGN_IN  = 1000;
    private TextView textViewFindEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button firebaseuiauthbtn = (Button)findViewById(R.id.sign_in_with_google_button);
        textViewFindEmail = findViewById(R.id.textView_linkify);
        firebaseuiauthbtn.setOnClickListener(this);

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern1 = Pattern.compile("이메일");
        Linkify.addLinks(textViewFindEmail, pattern1, "https://accounts.google.com/signin/v2/recoveryidentifier?hl=ko&flowName=GlifWebSignIn&flowEntry=ServiceLogin");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK)
            {
                // Successfully signed in
                Intent i = new Intent(this, MainMenuActivity.class);
                i.putExtras(data);
                startActivity(i);

            }
            else
            {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.sign_in_with_google_button:
                signin();
                break;
            default:
                break;
        }
    }

    private void signin()
    {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())                 // Theme 설정
                        .setLogo(getSelectedLogo())                  // 로고 설정
                        .setAvailableProviders(getSelectedProviders())// Providers 설정
                        .setTosAndPrivacyPolicyUrls("https://naver.com",
                                "https://google.com")
                        .setIsSmartLockEnabled(true)              //SmartLock 설정
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * FirebaseUI에 표시할 테마 정보
     * @return 테마 정보
     */
    private int getSelectedTheme()
    {
        return AuthUI.getDefaultTheme();
    }

    /**
     * Firebase UI에 표시할 로고 이미지
     * @return 로고 이미지
     */
    private int getSelectedLogo()
    {
        return AuthUI.NO_LOGO;
    }

    private List<AuthUI.IdpConfig> getSelectedProviders()
    {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
            selectedProviders.add(new AuthUI.IdpConfig.GoogleBuilder().build());
        return selectedProviders;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            finish();
        }
    }
}
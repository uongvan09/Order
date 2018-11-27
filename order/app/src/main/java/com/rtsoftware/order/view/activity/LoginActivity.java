package com.rtsoftware.order.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rtsoftware.order.R;
import com.rtsoftware.order.view.Untill;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtEmail;
    EditText edtPassWord;
    TextView btnLogin;
    TextView btnFogot;
    TextView btnRegister;
    ImageView btnHideShowPassword;
    ImageView frameFintel;
    ProgressBar prgWaiting;
    LinearLayout lnlLogin;

    boolean isShowPass = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        init();
    }

    private void init() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassWord = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnFogot = findViewById(R.id.btnFogotPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnHideShowPassword = findViewById(R.id.btnIconHidePassword);
        lnlLogin= findViewById(R.id.lnlLogin);
        lnlLogin.setOnClickListener(this);
        frameFintel= findViewById(R.id.frameLogin);
        prgWaiting= findViewById(R.id.prgBarLogin);
        prgWaiting.setVisibility(View.GONE);
        frameFintel.setVisibility( View.GONE);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnFogot.setOnClickListener(this);
        btnHideShowPassword.setOnClickListener(this);

        edtPassWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_DONE:
                        login();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnFogotPassword:
                break;
            case R.id.btnRegister:
                break;
            case R.id.btnIconHidePassword:
                if (isShowPass) {
                    isShowPass = false;
                    edtPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnHideShowPassword.setBackgroundResource(R.drawable.icon_hide_password);
                } else {
                    isShowPass = true;
                    edtPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);btnHideShowPassword.setBackgroundResource(R.drawable.icon_show_password);
                }
                break;
            default:
                Untill.hideKeyboard(this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private  void  login(){
        String email = edtEmail.getText().toString().trim();
        String password = edtPassWord.getText().toString().trim();
        prgWaiting.setVisibility(View.VISIBLE);
        frameFintel.setVisibility( View.VISIBLE);
        enable(false);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        prgWaiting.setVisibility(View.GONE);
                        frameFintel.setVisibility( View.GONE);
                        enable(true);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Email hoặ mật khẩu không đúng. Xin vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private  void  enable(boolean isDisable){
        edtEmail.setEnabled(isDisable);
        edtEmail.setClickable(isDisable);
        edtPassWord.setEnabled(isDisable);
        edtPassWord.setClickable(isDisable);
        btnLogin.setEnabled(isDisable);
        btnLogin.setClickable(isDisable);
        btnHideShowPassword.setEnabled(isDisable);
        btnHideShowPassword.setClickable(isDisable);
        btnFogot.setEnabled(isDisable);
        btnFogot.setClickable(isDisable);
        btnRegister.setEnabled(isDisable);
        btnRegister.setClickable(isDisable);
    }
}

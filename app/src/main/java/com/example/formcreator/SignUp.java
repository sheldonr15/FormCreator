package com.example.formcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {

    TextInputEditText mTextFullName;
    TextInputEditText mEmail;
    TextInputEditText mUsername;
    TextInputEditText mPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;
    DatabaseHelper db;

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = new DatabaseHelper(this);
        mTextFullName = (TextInputEditText)findViewById(R.id.fullname);
        mEmail = (TextInputEditText)findViewById(R.id.email);
        mUsername = (TextInputEditText)findViewById(R.id.username);
        mPassword = (TextInputEditText)findViewById(R.id.password);
        mButtonRegister = (Button)findViewById(R.id.buttonSignUp);
        mTextViewLogin = (TextView)findViewById(R.id.loginText);
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(SignUp.this, Login.class);
                startActivity(LoginIntent);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mUsername.getText().toString().trim();
                String pwd = mPassword.getText().toString().trim();
                String fullname = mTextFullName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();

                if(user != null && !user.isEmpty() && pwd != null && !pwd.isEmpty() && fullname != null && !fullname.isEmpty() && email != null && !email.isEmpty()){
                    long val = db.addUser(user, pwd);
                    if(val>0){
                        Toast.makeText(SignUp.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                        Intent moveToLogin = new Intent(SignUp.this, Login.class);
                        startActivity(moveToLogin);
                    }
                    else {
                        Toast.makeText(SignUp.this, "Registration Error!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SignUp.this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
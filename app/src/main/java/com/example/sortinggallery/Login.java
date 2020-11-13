package com.example.sortinggallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    TextInputEditText mTextUsername;
    TextInputEditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    DatabaseHelper db;

    // Boolean loggedInFlag;


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
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // myEdit.putBoolean("loggedInFlag", false);
        // myEdit.commit();

        //if(getIntent().getExtras()!=null){
            //loggedInFlag = getIntent().getBooleanExtra("EXTRA_SESSION_ID", false);
            // myEdit.putBoolean("loggedInFlag", loggedInFlag);
        //}
        //else{

            //loggedInFlag = false;
        //}

        // if(!loggedInFlag){
        if(!sharedPreferences.getBoolean("loggedInFlag", false)){
            Log.v("Login.java", "If condition : " + sharedPreferences.getBoolean("loggedInFlag", false));
            db = new DatabaseHelper(this);
            mTextUsername = (TextInputEditText)findViewById(R.id.username);
            mTextPassword = (TextInputEditText)findViewById(R.id.password);
            mButtonLogin = (Button)findViewById(R.id.buttonLogin);
            mTextViewRegister = (TextView)findViewById(R.id.signUpText);

            mTextViewRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent registerIntent = new Intent(Login.this, SignUp.class);
                    startActivity(registerIntent);
                }
            });

            mButtonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user = mTextUsername.getText().toString().trim();
                    String pwd = mTextPassword.getText().toString().trim();
                    Boolean res = db.checkUser(user, pwd);
                    if(res==true){
                        Toast.makeText(Login.this, "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                        // loggedInFlag = true;
                        myEdit.putBoolean("loggedInFlag", true);
                        myEdit.commit();
                        Intent logInIntent = new Intent(Login.this, MainActivity2.class);
                        //logInIntent.putExtra("EXTRA_SESSION_ID", loggedInFlag);
                        startActivity(logInIntent);
                    }
                    else {
                        Toast.makeText(Login.this, "Login Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Log.v("Login.java", "else condition : " + sharedPreferences.getBoolean("loggedInFlag", false));
            Intent logInIntent = new Intent(Login.this, MainActivity2.class);
            // logInIntent.putExtra("EXTRA_SESSION_ID", loggedInFlag);
            startActivity(logInIntent);
        }

    }
}
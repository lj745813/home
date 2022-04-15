package com.example.savedata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;
    private EditText UserEdit;
    private EditText PasswordEdit;
    private Button login;
    private CheckBox remember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        UserEdit = (EditText) findViewById(R.id.text_int);
        PasswordEdit = (EditText) findViewById(R.id.text_int2);
        remember = (CheckBox) findViewById(R.id.checkbox);
        login = (Button) findViewById(R.id.login);
        boolean isRemember = preferences.getBoolean("remember_password",false);
        if (isRemember){
            String User = preferences.getString("User","");
            String Password = preferences.getString("Password","");
            UserEdit.setText(User);
            PasswordEdit.setText(Password);
            remember.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = UserEdit.getText().toString();
                String Password = PasswordEdit.getText().toString();
                if (TextUtils.isEmpty(UserEdit.getText().toString()) || TextUtils.isEmpty(PasswordEdit.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                } else
                if(User.equals("Admin")&&Password.equals("12345678")){
                    editor = preferences.edit();
                    if(remember.isChecked()){
                        editor.putBoolean("remember_password",true);
                        editor.putString("User",User);
                        editor.putString("Password",Password);
                    }else{editor.clear();}
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("User",User);
                    intent.putExtra("Password",Password);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();

                }else {Toast.makeText(LoginActivity.this,"账号密码错误",Toast.LENGTH_SHORT).show();}
            }
        });
    }
}
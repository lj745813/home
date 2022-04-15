package com.example.test8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText C_name,C_code,C_id;//编辑框
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        final EditText c_name=findViewById(R.id.C_name);
        final EditText c_code=findViewById(R.id.C_code);
        helper = new MyDatabaseHelper(this, "BookStore.db", null, 1);//dbName数据库名
        Button addCategory = (Button) findViewById(R.id.insert_category);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =c_name.getText().toString();
                String code =c_code.getText().toString();
                db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
                ContentValues values1 = new ContentValues();
                values1.put("category_name",name);
                values1.put("category_code",code);
                db.insert("Category", null, values1);
                values1.clear();
                db.close();
                finish();
            }
        });
    }
}
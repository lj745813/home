package com.example.test8;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddBookActivity extends AppCompatActivity {
    private EditText B_name,B_author,B_price,B_pages,B_category_id,B_id;//编辑框
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        final EditText b_name=findViewById(R.id.B_name);
        final EditText b_author=findViewById(R.id.B_author);
        final EditText b_price=findViewById(R.id.B_price);
        final EditText b_pages=findViewById(R.id.B_pages);
        final EditText b_category_id=findViewById(R.id.B_category_id);
        helper = new MyDatabaseHelper(this, "BookStore.db", null, 1);//dbName数据库名
        Button addBook = (Button) findViewById(R.id.insert_Add);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =b_name.getText().toString();
                String author =b_author.getText().toString();
                String price =b_price.getText().toString();
                String pages =b_pages.getText().toString();
                String category_id =b_category_id.getText().toString();
                db = helper.getWritableDatabase();//获取到了 SQLiteDatabase 对象
                ContentValues values = new ContentValues();
                values.put("name",name);
                values.put("author",author);
                values.put("price",price);
                values.put("pages",pages);
                values.put("category_id",category_id);
                db.insert("Book", null, values);
                values.clear();
                db.close();
                finish();
            }
        });
    }
}
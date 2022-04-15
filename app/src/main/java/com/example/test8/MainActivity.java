package com.example.test8;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<Book>();
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 1);
        //初始化
        initcom();
        //添加书籍
        Button addb = (Button) findViewById(R.id.button2);
        addb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
        //添加类别
        Button addc = (Button) findViewById(R.id.button3);
        addc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });
        //初始化，插入数据
        Button init = (Button) findViewById(R.id.button);
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, initBook.class);
                startActivity(intent);
            }
        });
        //更新表单
        Button qub = (Button) findViewById(R.id.button1);
        qub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                books.clear();
                initcom();
                BookAdapter adapter = new BookAdapter(
                        MainActivity.this, R.layout.simple_list_item_1, books);
                ListView listView = (ListView) findViewById(R.id.listview);
                listView.setAdapter(adapter);
            }
        });
        BookAdapter adapter = new BookAdapter(
                MainActivity.this, R.layout.simple_list_item_1, books);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

    }

    public void initcom(){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor =db.rawQuery("select b.*,c.category_name,c.category_code from Book b left outer join Category c on b.category_id=c.category_code;",null);
        if(cursor.moveToFirst()){
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String category_name = cursor.getString(cursor.getColumnIndex("category_name"));
                @SuppressLint("Range") String author = cursor.getString(cursor.getColumnIndex("author"));
                @SuppressLint("Range") String price = String.valueOf(cursor.getDouble(cursor.getColumnIndex("price")));

                Book book =new Book();
                book.setName(name);
                book.setAuthor(author);
                book.setCategory_name(category_name);
                book.setPrice(price);
                books.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
}


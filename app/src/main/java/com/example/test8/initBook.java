package com.example.test8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class initBook extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_book);
        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 1);

        Button addData = (Button) findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", "红楼梦");
                values.put("author", "曹雪芹");
                values.put("price", 56);
                values.put("pages", 186);
                values.put("category_id", 10001);
                db.insert("Book",null,values);

                values.put("name", "水浒传");
                values.put("author", "吴承恩");
                values.put("price", 80);
                values.put("pages", 80);
                values.put("category_id", 10001);
                db.insert("Book",null,values);

                values.put("name", "三国演义");
                values.put("author", "罗贯中");
                values.put("price", 46);
                values.put("pages", 256);
                values.put("category_id", 10001);
                db.insert("Book",null,values);

                values.put("name", "格林童话");
                values.put("author", "雅可布·格林");
                values.put("price", 76);
                values.put("pages", 335);
                values.put("category_id", 10002);
                db.insert("Book",null,values);

                ContentValues values1 = new ContentValues();
                values1.put("category_name","中国名著");
                values1.put("category_code",10001);
                db.insert("Category",null,values1);

                values1.put("category_name","儿童读物");
                values1.put("category_code",10002);
                db.insert("Category",null,values1);
                db.close();
                finish();
            }
        });
    }
}
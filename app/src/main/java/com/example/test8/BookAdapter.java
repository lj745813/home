package com.example.test8;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    private int resourceId;
    public BookAdapter(Context context, int textViewResourceId, List<Book> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Book book = getItem(position); // 获取当前项的实例
        View view;
        view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView author = (TextView) view.findViewById(R.id.author);
        TextView category = (TextView) view.findViewById(R.id.category);
        TextView price = (TextView) view.findViewById(R.id.price);
        Log.d("TAG", "onCreate: "+book.getName()+book.getCategory_name()+book.getAuthor()+ book.getPrice());
        name.setText(book.getName());
        author.setText(book.getAuthor());
        category.setText(book.getCategory_name());
        price.setText(book.getPrice());
        return view;
    }
}

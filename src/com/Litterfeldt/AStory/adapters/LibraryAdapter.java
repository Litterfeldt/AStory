package com.Litterfeldt.AStory.adapters;

import android.graphics.BitmapFactory;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Typeface;
import java.util.*;


import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.models.Book;

public class LibraryAdapter extends ArrayAdapter<Book> {

    private Context context;
    private Typeface font;
    public LibraryAdapter (Context context, int layoutRef, ArrayList<Book> books, Typeface font){
        super(context, layoutRef, (List<Book>) books);
        this.context = context;
        this.font = font;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.librarylistbox, null);
        }

        Book item = getItem(position);
        if (item != null) {
            TextView header = (TextView)view.findViewById(R.id.BoxBookTitle);
            TextView author = (TextView)view.findViewById(R.id.BoxBookAuthor);
            ImageView img = (ImageView)view.findViewById(R.id.BoxBookCoverImage);
            System.out.println();
            if (header != null && item.name() != null) {
                header.setTypeface(font);
                header.setText(item.name());
            }
            if (author != null && item.author() != null) {
                author.setTypeface(font);
                author.setText(item.author());
            }
            if (img != null && item.image() != null) {
                img.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(item.image(), 0, item.image().length)));
            }
        }
        return view;
    }
}

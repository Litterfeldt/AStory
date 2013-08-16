package com.Litterfeldt.AStory.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.pagerView;
import com.Litterfeldt.AStory.services.AudioplayerService;


import java.util.ArrayList;


public class CustomListAdapterVTwo extends BaseAdapter {

    public Activity activity;
    private ArrayList<ArrayList<String>> data;
    private static LayoutInflater inflater = null;
    private Typeface font;
    private AudioplayerService core;





    public CustomListAdapterVTwo(pagerView a, ArrayList<ArrayList<String>> d){
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        font = Typeface.createFromAsset(activity.getAssets(), "font.ttf");
        core = a.apService;



    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean isEnabled(int position)
    {
        if(position==0) {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi;
        if(position==0){
            vi = inflater.inflate(R.layout.library_header_listrow,null);
            TextView head = (TextView)vi.findViewById(R.id.libraryHeader);
            head.setTypeface(font);
            vi.setEnabled(false);
            vi.setClickable(false);
        }
        else{
        vi = inflater.inflate(R.layout.list_row_two,null);
        //All the graphical components
        TextView header = (TextView)vi.findViewById(R.id.summary);
        TextView Author = (TextView)vi.findViewById(R.id.author);
        ImageView img = (ImageView)vi.findViewById(R.id.list_image);

        //-----------------------------------------------------------------------------------
        ArrayList<String> book;
        book=data.get(position-1);
        try{
            byte [] b = core.sqlConnector.getPicture(book.get(0));
            //noinspection deprecation
            img.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b,0,b.length)));
        }
        catch (Exception ignored){}
        header.setText(book.get(0));
        Author.setText(book.get(1));
        header.setTypeface(font);
        Author.setTypeface(font);
        }
        return vi;


    }

}
package com.Litterfeldt.AStory.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.pagerView;
import com.Litterfeldt.AStory.services.AudioplayerService;


import java.io.File;
import java.util.ArrayList;


public class CustomListAdapterVTwo extends BaseAdapter {

    public Activity activity;
    private ArrayList<ArrayList<String>> data;
    private static LayoutInflater inflater = null;

    private Typeface font;

    private AudioplayerService core;
    private ArrayList<ArrayList<String>> booklist;
    private HashMap<Integer, View> views;




    public CustomListAdapterVTwo(pagerView a, ArrayList<ArrayList<String>> d){
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        font = Typeface.createFromAsset(activity.getAssets(), "font.ttf");
        core = a.apService;
        core.getBookList();
        booklist = core.booklist;
        views = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return data.size()+1;
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
        vi = views.get(position);
        if(vi==null){
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
                ArrayList<String> bookPath=data.get(position-1);
                ArrayList<String> book = core.sqlConnector.getBookNameFromPath(bookPath.get(0));
                if(book.isEmpty()){
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("list",bookPath);
                    bundle.putInt("pos", position);
                    new GetBookDataTask().execute(bundle);
                }else{
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

            }
            views.put(position, vi);
        }

        return vi;


    }


    private class GetBookDataTask extends AsyncTask<Bundle , Void, Bundle> {
        @Override
        protected Bundle doInBackground(Bundle...b) {
            ArrayList<String> strings = b[0].getStringArrayList("list");
            for ( String s : strings ){
                core.sqlConnector.addChapter(s);
            }
            Bundle bundle = new Bundle();
            bundle.putString("list",strings.get(0));
            bundle.putInt("pos", b[0].getInt("pos"));

            return bundle;
        }
        @Override
        protected void onProgressUpdate(Void...v) {
        }
        @Override
        protected void onPostExecute(Bundle result) {
            View vi = views.get(result.getInt("pos"));
            TextView header = (TextView)vi.findViewById(R.id.summary);
            TextView Author = (TextView)vi.findViewById(R.id.author);
            ImageView img = (ImageView)vi.findViewById(R.id.list_image);
            ArrayList<String> book = core.sqlConnector.getBookNameFromPath(result.getString("list").toString());

            try{
                byte [] b = core.sqlConnector.getPicture(book.get(0));
                //noinspection deprecation
                img.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b,0,b.length)));
            }
            catch (Exception ignored){}
            Log.e("",result.getString("list").toString());
            header.setText(book.get(0));
            Author.setText(book.get(1));
            header.setTypeface(font);
            Author.setTypeface(font);
            super.onPostExecute(result);
        }
    }

}
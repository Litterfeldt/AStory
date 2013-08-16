package com.Litterfeldt.AStory.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.adapters.CustomListAdapter;
import com.Litterfeldt.AStory.pagerView;

public class LibraryFragment extends Fragment {
    private Thread updateThread;
    private Handler threadHandler;
    private CustomListAdapter adapter;
    private GridView list;
    private ProgressBar searchingSpinner;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library, container, false);
        list =(GridView)view.findViewById(R.id.projectList);
        ((pagerView) getActivity()).apService.getBookList();
        try{
            adapter = new CustomListAdapter(((pagerView) getActivity()) ,((pagerView) getActivity()).apService.booklist);
            list.setAdapter(adapter);
        }
        catch (NullPointerException e){
            Log.e("ERROR/Astory/LIBRARY","NullpointerException");
            Toast.makeText(getActivity(),"Your Audiobook-folder is empty, please put books in your /AudioBooks folder on your external storage drive", Toast.LENGTH_LONG);
        }



        //Buttons:
        Button refreshbutton = (Button) view.findViewById(R.id.refreshbutton);
        searchingSpinner = (ProgressBar) view.findViewById(R.id.searchSpinner);
        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchingSpinner.bringToFront();
                searchingSpinner.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
                updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ((pagerView) getActivity()).apService.sqlConnector.emptyBookList();
                        ((pagerView) getActivity()).apService.sqlConnector.allocateBooks();
                        ((pagerView) getActivity()).apService.getBookList();
                        Message msg = new Message();
                        msg.what = 1;
                        threadHandler.sendMessage(msg);
                    }
                });
                updateThread.start();
                threadHandler = new Handler(){
                    public void handleMessage(Message msg){
                        try{
                        adapter = new CustomListAdapter(((pagerView) getActivity()) ,((pagerView) getActivity()).apService.booklist);
                        list.setAdapter(adapter);
                        }
                        catch (NullPointerException e){
                            Log.e("ERROR/Astory/LIBRARY","NullpointerException");
                            Toast.makeText(getActivity(),"Your Audiobook-folder is empty, please put books in your /AudioBooks folder on your external storage drive", Toast.LENGTH_LONG);
                        }
                        searchingSpinner.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                    }};
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((pagerView) getActivity()).apService.mp.reset();
                ((pagerView) getActivity()).apService.mp.playBook(((pagerView) getActivity()).apService.booklist.get(i).get(0),0,((pagerView) getActivity()));
                ((pagerView) getActivity()).mPager.setCurrentItem(0);
                ((pagerView) getActivity()).updatePicture = true;

            }

        });




        return view;
    }
}

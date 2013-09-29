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
import com.Litterfeldt.AStory.adapters.CustomListAdapterVTwo;
import com.Litterfeldt.AStory.pagerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TestActivity extends Fragment {
    private Thread updateThread;
    private Handler threadHandler;
    private CustomListAdapterVTwo adapter;
    private PullToRefreshListView pullToRefreshView;
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
        View view = inflater.inflate(R.layout.library_two, container, false);
        pullToRefreshView = (PullToRefreshListView)view.findViewById(R.id.pull_to_refresh_listview);
        ((pagerView) getActivity()).apService.getBookList();
        /**try{
            adapter = new CustomListAdapterVTwo(((pagerView) getActivity()) ,((pagerView) getActivity()).apService.booklist);
            pullToRefreshView.setAdapter(adapter);
        }
        catch (NullPointerException e){
            Log.e("ERROR/Astory/LIBRARY","NullpointerException");
            Toast.makeText(getActivity(),"Your Audiobook-folder is empty, please put books in your /AudioBooks folder on your external storage drive", Toast.LENGTH_LONG);
        }
         **/



        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                /**updateThread = new Thread(new Runnable() {
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
                            adapter = new CustomListAdapterVTwo(((pagerView) getActivity()) ,((pagerView) getActivity()).apService.booklist);
                            pullToRefreshView.setAdapter(adapter);
                        }
                        catch (NullPointerException e){
                            Log.e("ERROR/Astory/LIBRARY","NullpointerException");
                            Toast.makeText(getActivity(),"Your Audiobook-folder is empty, please put books in your /AudioBooks folder on your external storage drive", Toast.LENGTH_LONG);
                        }
                        pullToRefreshView.onRefreshComplete();

                    }};**/
            }
        });

        pullToRefreshView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                i=i-1;
                if(i==0){
                  //Todo set back switch so that it goes back to the player
                }
                else{
                ((pagerView) getActivity()).apService.mp.reset();
                ((pagerView) getActivity()).apService.mp.playBook(((pagerView) getActivity()).apService.booklist.get(i).get(0),0,((pagerView) getActivity()));
                ((pagerView) getActivity()).mPager.setCurrentItem(0);
                ((pagerView) getActivity()).updatePicture = true;
                }
            }

        });




        return view;
    }
}

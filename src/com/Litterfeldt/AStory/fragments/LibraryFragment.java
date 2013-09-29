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
import android.os.AsyncTask;
import com.handmark.pulltorefresh.library.*;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.adapters.CustomListAdapterVTwo;
import com.Litterfeldt.AStory.pagerView;

import java.util.ArrayList;
import java.io.File;


public class LibraryFragment extends Fragment {

    private Thread updateThread;
    private Handler threadHandler;

    private CustomListAdapterVTwo adapter;
    private PullToRefreshGridView list;
    private ProgressBar searchingSpinner;
    private TextView emptyText;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.library, container, false);

        list =(PullToRefreshGridView) view.findViewById(R.id.pull_to_refresh_listview);
        emptyText = (TextView) view.findViewById(R.id.emptyText);

        ((pagerView) getActivity()).apService.getBookList();
        ArrayList<ArrayList<String>> booklist = ((pagerView) getActivity()).apService.booklist;

        if (!booklist.isEmpty()){
            adapter = new CustomListAdapterVTwo(((pagerView) getActivity()),
                    ((pagerView) getActivity()).apService.sqlConnector.allocateBookFolderHerarchy());
            list.setAdapter(adapter);
        }else{
            emptyText.setVisibility(View.VISIBLE);
        }

        list.setOnRefreshListener(new PullToRefreshGridView.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                new GetDataTask().execute();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((pagerView) getActivity()).apService.mp.reset();
                ((pagerView) getActivity()).apService.getBookList();
                ((pagerView) getActivity()).apService.mp.playBook(((pagerView) getActivity()).apService.booklist.get(i-1).get(0),0,((pagerView) getActivity()));
                ((pagerView) getActivity()).mPager.setCurrentItem(0);
                ((pagerView) getActivity()).updatePicture = true;

            }

        });
        return view;
    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<ArrayList<String>>> {
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void...v) {
            ((pagerView) getActivity()).apService.sqlConnector.emptyBookList();
            return ((pagerView) getActivity()).apService.sqlConnector.allocateBookFolderHerarchy();
        }
        @Override
        protected void onProgressUpdate(Void...v) {
        }
        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            if(!result.isEmpty()){
                adapter = new CustomListAdapterVTwo(((pagerView) getActivity()) ,result);
                list.setAdapter(adapter);
            }else {
                Log.e("##########","Nothing IN audiobook folder");
                Toast.makeText(getActivity(),"Your Audiobook-folder is empty, please put books in your /AudioBooks folder on your external storage drive", Toast.LENGTH_LONG);
            }
            list.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}

package com.Litterfeldt.AStory.fragments;


import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.customClasses.CustomMediaPlayer;
import com.Litterfeldt.AStory.pagerView;
import com.Litterfeldt.AStory.services.AudioplayerService;
import java.util.ArrayList;

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private TextView book;
    private TextView author;
    private TextView back;
    private TextView play;
    private TextView forward;
    private Typeface font;
    private TextView fback;
    private TextView fforward;

    private ProgressBar progressBar;

    private SeekBar processbar;
    private ImageView background;
    private TextView timegone;
    private TextView timeleft;
    private Runnable mUpdateTimeTask;
    private Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onPause() {
        try {
            setSaveState();
        } catch (NullPointerException n) {
            Log.e("ERROR/PLAYERFRAGMENT", "Couldn't make a save file from the playerfragment's onPause() method");
        }
        super.onPause();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player, container, false);
        font = Typeface.createFromAsset(((pagerView) getActivity()).getAssets(), "font.ttf");
        book = (TextView) view.findViewById(R.id.titleheader);
        author = (TextView) view.findViewById(R.id.subheader);
        timeleft = (TextView) view.findViewById(R.id.timeleft);
        timegone = (TextView) view.findViewById(R.id.timegone);

        back = (TextView) view.findViewById(R.id.thirtySBack);
        forward = (TextView) view.findViewById(R.id.thirtySForward);
        fback = (TextView) view.findViewById(R.id.LastChapter);
        fforward = (TextView) view.findViewById(R.id.nextchapter);
        play = (TextView) view.findViewById(R.id.playpause);
        background = (ImageView) view.findViewById(R.id.background);
        processbar = (SeekBar) view.findViewById(R.id.seekbar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        book.setTypeface(font);
        author.setTypeface(font);
        timeleft.setTypeface(font);
        timegone.setTypeface(font);

        processbar.setOnSeekBarChangeListener(this);

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushedShortForwardSkipp(30000);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushedShortBackSkipp(30000);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushedPlay();
            }
        });
        fback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findprev();
            }
        });
        fforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findnext();
            }
        });

        if (!((pagerView) getActivity()).apService.mp.isPlaying && !hasBookPlaying()) {
            playSavedState(getSaveState());
            SetBackgroundAndTitle();
            ((pagerView) getActivity()).apService.showNotification();

        } else {
            SetBackgroundAndTitle();
        }
        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {
            CustomMediaPlayer mp = ((pagerView) getActivity()).apService.mp;

            public void run() {
                if (hasBookPlaying()){

                    long totalDuration = mp.getDuration();
                    long currentDuration = mp.getCurrentPosition();

                    // Displaying time completed playing
                    timeleft.setText("" + getTimeString(totalDuration - currentDuration));
                    timegone.setText("" + getTimeString(currentDuration));
                    // Updating progress bar
                    int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                    //processbar.setProgress(progress);
                    //TODO change so that one shows chapter the other book progress
                    processbar.setMax((int) totalDuration);
                    processbar.setProgress((int) currentDuration);
                    progressBar.setMax((int) totalDuration);
                    progressBar.setProgress((int) currentDuration);

                    if (((pagerView) getActivity()).updatePicture) {
                        SetBackgroundAndTitle();
                        play.setBackgroundResource(R.drawable.pasue);
                        ((pagerView) getActivity()).updatePicture = false;
                    }
                    showPlayerControls();

                    // Running this thread after 500 milliseconds
                    mHandler.postDelayed(this, 500);
                }else{
                    book.setText("Swipe Right");
                    author.setText("to choose a book");
                    hidePlayerControls();
                    mHandler.postDelayed(this, 2000);
                }
            }
        };
        updateProgressBar();
        return view;
    }

    //--- SAVE STATE CODE ---
    private void setSaveState() {
        if (((pagerView) getActivity()).apService.mp.isPlaying) {
            try {
                ((pagerView) getActivity()).apService.sqlConnector.clearCach();
                ((pagerView) getActivity()).apService.sqlConnector.newSave(((pagerView) getActivity())
                        .apService.sqlConnector.getBookIDFromName
                                (((pagerView) getActivity()).apService.mp.currentBookname)
                        , ((pagerView) getActivity()).apService.mp.currentChapterIndex
                        , ((pagerView) getActivity()).apService.mp.getCurrentPosition());
                Log.i("SQL/SAVE", "Made save!");
            } catch (Exception e) {
                Log.i("SQL/SAVE", "Couldnt make save");
            }
        }

    }
    private int[] getSaveState() {
        try {
            return ((pagerView) getActivity()).apService.sqlConnector.getSave();
        } catch (Exception e) {
            Log.w("SQL/SAVE", "Couldnt make save");
            return null;
        }
    }
    private void playSavedState(int[] i) {
        if (i == null) {
            return;
        }
        AudioplayerService service = ((pagerView) getActivity()).apService;
        String booknm = service.sqlConnector.getBookNameFromID(i[0]);
        int currentChapter = i[1];
        int currentPos = i[2];
        service.playBook(booknm, currentChapter);
        service.mp.seekTo(currentPos);
        play.setBackgroundResource(R.drawable.pasue);

    }

    //--- PLAYER CONTROLS ---
    private void pushedPlay() {
        CustomMediaPlayer mp = ((pagerView) getActivity()).apService.mp;
        if (!mp.playerStartedPlayingABook && !mp.hasCurrentBook && getSaveState() != null ) {
            playSavedState(getSaveState());
            SetBackgroundAndTitle();
            ((pagerView) getActivity()).apService.showNotification();
        } else if (!mp.isPlaying && mp.hasCurrentBook) {
            mp.start();
            play.setBackgroundResource(R.drawable.pasue);
            ((pagerView) getActivity()).apService.showNotification();
        } else if (mp.isPlaying && mp.hasCurrentBook) {
            mp.pause();
            play.setBackgroundResource(R.drawable.play);
            ((pagerView) getActivity()).apService.showNotification();
            setSaveState();
        }
    }
    private void pushedShortBackSkipp(int skipp) {
        try {
            int currentpos = ((pagerView) getActivity()).apService.mp.getCurrentPosition();
            if (currentpos - skipp >= ((pagerView) getActivity()).apService.mp.getDuration()) {
                ((pagerView) getActivity()).apService.mp.seekTo(currentpos - skipp);
            } else {
                ((pagerView) getActivity()).apService.mp.seekTo(0);
            }
        } catch (Exception e) {
            Log.e("PLAYERBACKEXCEPTION", e.toString());
        }
    }
    private void pushedShortForwardSkipp(int skipp) {
        try {
            int currentpos = ((pagerView) getActivity()).apService.mp.getCurrentPosition();
            if (currentpos + skipp <= ((pagerView) getActivity()).apService.mp.getDuration()) {
                ((pagerView) getActivity()).apService.mp.seekTo(currentpos + skipp);
            } else {
                ((pagerView) getActivity()).apService.mp.seekTo(((pagerView) getActivity()).apService.mp.getDuration());
            }
        } catch (Exception e) {
            Log.e("PLAYERFORWARDEXCEPTION", e.toString());
        }
    }

    private void findnext() {
        if(hasBookPlaying()){
            if (currentChapterIndex() < chapterArraySize()) {
                playChapter(currentChapterIndex() + 1);
                SetBackgroundAndTitle();

            } else {
             stopMediaPlayer();
                setPlayerHasNoBook();
                play.setBackgroundResource(R.drawable.play);
            }
        }
    }
    private void findprev() {
        if (hasBookPlaying()){
            if (currentChapterIndex() > 0) {
                playChapter(currentChapterIndex() - 1);
                SetBackgroundAndTitle();
            } else {
                seekTo(0);
            }
        }
    }

    //--- UI HELPER METHODS ---
    private void hidePlayerControls(){
        processbar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        timeleft.setVisibility(View.GONE);
        timegone.setVisibility(View.GONE);
    }
    private void showPlayerControls(){
        processbar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        timeleft.setVisibility(View.VISIBLE);
        timegone.setVisibility(View.VISIBLE);
    }
    private String getTimeString(long millis) {
        StringBuilder buf = new StringBuilder();
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));
        return buf.toString();
    }
    private void SetBackgroundAndTitle() {
        if (hasBookPlaying()) {
            try {
                byte[] b = getCurrentBackgroundPicture();
                background.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length)));
                book.setText(currentBookName().trim());
                author.setText(currentAuthorName().trim());
            } catch (Exception e) {
                //Log.e("DEV","Couldnt extract image: " +e.toString());
            }
        }
    }
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) { seekTo(i); }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateProgressBar();
    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 500);
    }
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    //Service-Glue goes here
    private void seekTo(int i) {
        if (hasBookPlaying()) { ((pagerView) getActivity()).apService.mp.seekTo(i); }
    }
    private byte[] getCurrentBackgroundPicture() {
        return ((pagerView) getActivity()).apService.sqlConnector.getPicture(currentBookName());
    }
    private String currentBookName() {
        return currentBookChapterList().get(0).get(0);
    }
    private String currentAuthorName() {
        return currentBookChapterList().get(0).get(1);
    }
    private ArrayList<ArrayList<String>> currentBookChapterList() {
        return ((pagerView) getActivity()).apService.currentBookChapterList;
    }
    private boolean isEmptyBookChapterList() {
        return currentBookChapterList().isEmpty();
    }
    private boolean hasBookPlaying() {
        return ((pagerView) getActivity()).apService.mp.hasCurrentBook;
    }
    private int currentChapterIndex(){
        return ((pagerView) getActivity()).apService.mp.currentChapterIndex;
    }
    private int chapterArraySize(){
        return ((pagerView) getActivity()).apService.currentBookChapterList.size();
    }
    private void playChapter(int chapterIndex){
        if(hasBookPlaying()) { ((pagerView) getActivity()).apService.mp.playBook(currentBookName(),chapterIndex,((pagerView) getActivity())); }
    }
    private void stopMediaPlayer(){
        ((pagerView) getActivity()).apService.mp.stop();
    }
    private void setPlayerHasNoBook(){
        ((pagerView) getActivity()).apService.mp.playerStartedPlayingABook = false;
    }


}

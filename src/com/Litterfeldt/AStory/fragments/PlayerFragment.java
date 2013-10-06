package com.Litterfeldt.AStory.fragments;


import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.customClasses.CustomMediaPlayer;
import com.Litterfeldt.AStory.dbConnector.dbSave;
import com.Litterfeldt.AStory.models.Book;
import com.Litterfeldt.AStory.models.SaveState;
import com.Litterfeldt.AStory.pagerView;
import com.Litterfeldt.AStory.services.AudioplayerService;

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

        if (getMediaPlayer().hasBook()) SetBackgroundAndTitle();

        if (!getMediaPlayer().isPlaying() && !getMediaPlayer().hasBook()){
            SaveState s = getSave();
            if (s != null ){
                playSavedState(s);
                ((pagerView) getActivity()).apService.showNotification();
            }
        }
        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {
            public void run() {
                CustomMediaPlayer mp = getMediaPlayer();
                if (mp != null){
                    if (mp.hasBook()){
                        long totalDuration = mp.getDuration();
                        long currentDuration = mp.getCurrentPosition();
                        processbar.setMax((int) totalDuration);
                        processbar.setProgress((int) currentDuration);
                        progressBar.setMax((int) totalDuration);
                        progressBar.setProgress((int) currentDuration);
                        processbar.setProgress((int)currentDuration);
                        if (!isBackgroundSet()) {
                            SetBackgroundAndTitle();
                        }
                        showPlayerControls();
                        if (mp.isPlaying()){
                            timegone.setText("" + getTimeString(currentDuration));
                            timeleft.setText("" + getTimeString(totalDuration - currentDuration));

                            play.setBackgroundResource(R.drawable.pasue);
                        }else{
                            play.setBackgroundResource(R.drawable.play);
                        }
                        mHandler.postDelayed(this, 500);
                    }else{
                        backgroundIsNotSet();
                        book.setText("Swipe Right");
                        author.setText("to choose a book");
                        hidePlayerControls();
                        mHandler.postDelayed(this, 1000);
                    }
                }
            }
        };
        updateProgressBar();
        return view;
    }
    //--- PLAYER CONTROLS ---
    private void pushedPlay() {
        CustomMediaPlayer mp = getMediaPlayer();
        if (mp != null){
            if(mp.isPlaying()){
                mp.pause();
                save();
            }else if(mp.hasBook()){
                mp.start();
            }
            getService().showNotification();
        }
    }
    private void pushedShortBackSkipp(int skipp) {
        CustomMediaPlayer mp = getMediaPlayer();
        if(mp != null){
            int currentPos = mp.getCurrentPosition();
            if (currentPos - skipp >= mp.getDuration()) {
               mp.seekTo(currentPos - skipp);
            } else {
                mp.seekTo(0);
            }
        }
    }
    private void pushedShortForwardSkipp(int skipp) {
        CustomMediaPlayer mp = getMediaPlayer();
        if(mp != null){
            int currentPos = mp.getCurrentPosition();
            if (currentPos + skipp <= mp.getDuration()) {
                mp.seekTo(currentPos + skipp);
            } else {
                mp.seekTo(mp.getDuration());
            }
        }
    }

    private void findnext() {
        getService().nextChapter();
        backgroundIsNotSet();
    }
    private void findprev() {
        getService().previousChapter();
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
        CustomMediaPlayer mp = getMediaPlayer();
        byte[] b = mp.book().image();
        background.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length)));
        book.setText(mp.book().name());
        author.setText(mp.book().author());
        mp.setBackgroundToggle(true);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) { getMediaPlayer().seekTo(i);}
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
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private boolean isBackgroundSet(){
        return getMediaPlayer().getBackgroundToggle();
    }
    private void backgroundIsNotSet(){
        getMediaPlayer().setBackgroundToggle(false);
    }


    //Service-Glue goes here
    private AudioplayerService getService(){
        try{
            return ((pagerView) getActivity()).apService;
        }catch (Exception Ignored){} return null;
    }
    private CustomMediaPlayer getMediaPlayer(){
        try{
            return getService().getMediaPlayer();
        }catch (Exception Ignored){} return null;
    }

    //--- SAVE STATE CODE ---
    private void playSavedState(SaveState s) {
        AudioplayerService as = getService();
        if (s != null && as != null) {
            Book book = null;
            for (Book b : as.getBookList()){
                if (b.id() == s.bookId()){
                    book = b;
                    break;
                }
            }
            if (book != null){
                getMediaPlayer().playBook(book,s.chapterId());
                getMediaPlayer().seekTo(s.time_pos());
            }
            backgroundIsNotSet();
            play.setBackgroundResource(R.drawable.pasue);
        }
    }
    private void save(){
        if(getMediaPlayer() != null){
            if(getMediaPlayer().hasBook()){
                Book book = getMediaPlayer().book();
                SaveState s = new SaveState(book.id(),
                        book.currentChapterIndex(),
                        getMediaPlayer().getCurrentPosition());
                dbSave.setSave(this.getActivity().getApplicationContext(),s);
            }
        }
    }
    private SaveState getSave(){
        if(getMediaPlayer() != null){
            return dbSave.getSave(this.getActivity().getApplicationContext());
        }return null;
    }
}

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
    private TextView chapterCount;
    private TextView timegone;
    private TextView timeleft;
    private Runnable mUpdateTimeTask;
    private Handler mHandler;

    private AudioplayerService as;
    private CustomMediaPlayer mp;



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
        mHandler.removeCallbacks(mUpdateTimeTask);
        super.onPause();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("custom","creating fragment");
        as = getService();
        mp = getMediaPlayer();

        View view = inflater.inflate(R.layout.player, container, false);
        font = Typeface.createFromAsset(((pagerView) getActivity()).getAssets(), "font.ttf");
        book = (TextView) view.findViewById(R.id.titleheader);
        author = (TextView) view.findViewById(R.id.subheader);
        timeleft = (TextView) view.findViewById(R.id.timeleft);
        timegone = (TextView) view.findViewById(R.id.timegone);
        chapterCount = (TextView) view.findViewById(R.id.chaptercount);

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
        chapterCount.setTypeface(font);

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

        if (mp.hasBook()) SetBackgroundAndTitle();

        if (!mp.isPlaying() && !mp.hasBook()){
            SaveState s = getSave();
            if (s != null ){
                playSavedState(s);
                ((pagerView) getActivity()).apService.showNotification();
            }
        }
        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {
            public void run() {
                if (mp != null){
                    if (mp.hasBook()){
                        Book book = mp.book();
                        long totalBookDuration = book.getDuration();
                        long currentBookDuration = book.getCurrentChapterDuration() + mp.getCurrentPosition();

                        long chapterDuration = mp.getDuration();
                        long currentDuration = mp.getCurrentPosition();

                        processbar.setMax((int) chapterDuration);
                        processbar.setProgress((int) currentDuration);
                        progressBar.setMax((int) totalBookDuration);
                        progressBar.setProgress((int) currentBookDuration);

                        chapterCount.setText(book.currentChapterIndex()+1 + "/" + book.chapterCount());

                        if (!isBackgroundSet()) {
                            SetBackgroundAndTitle();
                        }
                        showPlayerControls();
                        if (mp.isPlaying()){
                            timegone.setText("" + getTimeString(currentBookDuration));
                            timeleft.setText("" + getTimeString(totalBookDuration - currentBookDuration));

                            play.setBackgroundResource(R.drawable.pasue);
                        }else{
                            play.setBackgroundResource(R.drawable.play);
                        }
                        mHandler.postDelayed(this, 1000);
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
        if (mp != null){
            if(mp.isPlaying()){
                mp.pause();
                save();
            }else if(mp.hasBook()){
                mp.start();
            }
            as.showNotification();
        }
    }
    private void pushedShortBackSkipp(int skipp) {
        if(mp != null){
            int currentPos = mp.getCurrentPosition();
            if (currentPos - skipp > skipp) {
               mp.seekTo(currentPos - skipp);
            } else {
                mp.seekTo(0);
            }
        }
    }
    private void pushedShortForwardSkipp(int skipp) {
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
        as.nextChapter();
        backgroundIsNotSet();
    }
    private void findprev() {
        as.previousChapter();
    }

    //--- UI HELPER METHODS ---
    private void hidePlayerControls(){
        processbar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        timeleft.setVisibility(View.GONE);
        timegone.setVisibility(View.GONE);
        chapterCount.setVisibility(View.GONE);
    }
    private void showPlayerControls(){
        processbar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        timeleft.setVisibility(View.VISIBLE);
        timegone.setVisibility(View.VISIBLE);
        chapterCount.setVisibility(View.VISIBLE);
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
        if(mp != null) {
            byte[] b = mp.book().image();
            background.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length)));
            book.setText(mp.book().name());
            author.setText(mp.book().author());
            mp.setBackgroundToggle(true);
        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) { mp.seekTo(i);}
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
        return mp.getBackgroundToggle();
    }
    private void backgroundIsNotSet(){
        mp.setBackgroundToggle(false);
    }


    //Service-Glue goes here
    private AudioplayerService getService(){
      return ((pagerView) getActivity()).getApService();
    }
    private CustomMediaPlayer getMediaPlayer(){
       return getService().getMediaPlayer();
    }

    //--- SAVE STATE CODE ---
    private void playSavedState(SaveState s) {
        if (s != null && as != null) {
            Book book = null;
            for (Book b : as.getBookList()){
                if (b.id() == s.bookId()){
                    book = b;
                    break;
                }
            }
            if (book != null){
                mp.playBook(book,s.chapterId());
                mp.seekTo(s.time_pos()-30);
            }
            backgroundIsNotSet();
            play.setBackgroundResource(R.drawable.pasue);
        }
    }
    private void save(){
        as.save();
    }
    private SaveState getSave(){
        return as.getSave();
    }
}

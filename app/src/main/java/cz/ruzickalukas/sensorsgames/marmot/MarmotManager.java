package cz.ruzickalukas.sensorsgames.marmot;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import cz.ruzickalukas.sensorsgames.R;

class MarmotManager extends Handler {

    private Activity activity;
    private FrameLayout gameLayout;
    private TextView timeView;
    private TextView scoreView;

    private int xMax, yMax;
    private long startTime;
    private int nextAppearence;
    private int marmotExpiration;

    private int marmotAppeared = 0;
    private int score = 0;

    private static final int ADD_NEW_MARMOT = 101;
    private static final int UPDATE_TIME = 102;
    static final int MARMOT_HIT = 103;

    MarmotManager(Activity activity, FrameLayout gameLayout, TextView scoreView, TextView timeView) {
        this.activity = activity;
        this.gameLayout = gameLayout;
        this.timeView = timeView;
        this.scoreView = scoreView;
        Display display = activity.getWindowManager().getDefaultDisplay();
        xMax = (int)(display.getWidth() -
                activity.getResources().getDimension(R.dimen.marmot_width));
        yMax = (int)(display.getHeight() -
                activity.getResources().getDimension(R.dimen.marmot_height) -
                activity.getResources().getDimension(R.dimen.score_time_height));
    }

    void startGame() {
        startTime = System.currentTimeMillis();
        timeView.setText(String.format(activity.getResources()
                .getString(R.string.time_format_seconds),60));
        sendEmptyMessage(UPDATE_TIME);

        marmotAppeared = 0;
        score = 0;
        scoreView.setText(String.format(activity.getResources().getString(R.string.score),0));
        nextAppearence = 3000;
        marmotExpiration = 2000;
        sendEmptyMessageDelayed(ADD_NEW_MARMOT,500);

        Toast.makeText(activity, activity.getResources().getString(R.string.game_start),
                Toast.LENGTH_SHORT).show();
    }

    void endGame() {
        removeMessages(UPDATE_TIME);
        removeMessages(ADD_NEW_MARMOT);

        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.game_over_title))
                .setMessage(String.format(activity.getResources()
                        .getString(R.string.game_over_score),score,marmotAppeared))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.play_again_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGame();
                            }
                        })
                .setNegativeButton(activity.getResources().getString(R.string.go_back_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                .show();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case ADD_NEW_MARMOT:
                Random rand = new Random();
                int xPos = rand.nextInt(xMax + 1);
                int yPos = rand.nextInt(yMax + 1);

                MarmotView marmot = new MarmotView(activity, this);
                gameLayout.addView(marmot);
                marmot.setPosition(xPos, yPos);
                marmot.animateIn(marmotExpiration);
                sendEmptyMessageDelayed(ADD_NEW_MARMOT, nextAppearence);

                updateTimings();
                marmotAppeared++;
                break;
            case UPDATE_TIME:
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 60000) {
                    timeView.setText(String.format(activity.getResources()
                            .getString(R.string.time_format_tenths),0.0f));
                    endGame();
                } else {
                    long remainingTime = 60000 - (currentTime - startTime);
                    if (remainingTime <= 10000) {
                        timeView.setText(String.format(activity.getResources()
                                .getString(R.string.time_format_tenths),(float)remainingTime/1000));
                        sendEmptyMessageDelayed(UPDATE_TIME, 100);
                        if (remainingTime < marmotExpiration) {
                            removeMessages(ADD_NEW_MARMOT);
                        }
                    } else {
                        timeView.setText(String.format(activity.getResources()
                                .getString(R.string.time_format_seconds),remainingTime/1000));
                        sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                    }
                }
                break;
            case MARMOT_HIT:
                score++;
                scoreView.setText(String.format(activity.getResources().getString(R.string.score),
                        score));
                updateTimings();
                break;
        }
    }

    private void updateTimings() {
        nextAppearence *= 0.977;
        marmotExpiration *= 0.989;
    }
}

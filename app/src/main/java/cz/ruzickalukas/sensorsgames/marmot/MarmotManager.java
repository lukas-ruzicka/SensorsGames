package cz.ruzickalukas.sensorsgames.marmot;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import cz.ruzickalukas.sensorsgames.R;
import cz.ruzickalukas.sensorsgames.main.GameStatus;

class MarmotManager extends Handler {

    private Activity activity;
    private FrameLayout gameLayout;
    private TextView timeView;
    private TextView scoreView;

    private boolean running;
    private int xMax, yMax;
    private long startTime;
    private int nextAppearence;
    private int marmotExpiration;
    private boolean waiting = true;
    private long lastStep;

    private int score = 0;

    private long pausedAt;

    static final int ADD_NEW_MARMOT = 101;
    static final int UPDATE_TIME = 102;
    static final int MARMOT_HIT = 103;

    MarmotManager(Activity activity, FrameLayout gameLayout, TextView scoreView,
                  TextView timeView) {
        this.activity = activity;
        this.gameLayout = gameLayout;
        this.timeView = timeView;
        this.scoreView = scoreView;

        timeView.setText(String.format(activity.getResources()
                .getString(R.string.time_format_seconds),60));
        scoreView.setText(String.format(activity.getResources()
                .getString(R.string.score),0));

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

        score = 0;
        scoreView.setText(String.format(activity.getResources().getString(R.string.score),0));
        nextAppearence = 3000;
        marmotExpiration = 2000;
        sendEmptyMessageDelayed(ADD_NEW_MARMOT,500);

        running = true;
        waiting = false;

        Toast.makeText(activity, activity.getResources().getString(R.string.game_start),
                Toast.LENGTH_SHORT).show();
    }

    private void endGame() {
        removeMessages(UPDATE_TIME);
        removeMessages(ADD_NEW_MARMOT);

        running = false;
        waiting = true;

        GameStatus.updateScore(activity, R.string.marmot, score, false);

        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.game_over_title))
                .setMessage(String.format(activity.getResources()
                        .getString(R.string.game_over_marmot), score))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.play_again_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                waiting = false;
                                timeView.setText(String.format(activity.getResources()
                                        .getString(R.string.time_format_seconds),60));
                                scoreView.setText(String.format(activity.getResources()
                                        .getString(R.string.score),0));
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

    void addStep(long stepTime) {
        score++;
        scoreView.setText(String.format(activity.getResources().getString(R.string.score),
                score));
        lastStep = stepTime;
    }

    void pauseGame() {
        removeMessages(UPDATE_TIME);
        removeMessages(ADD_NEW_MARMOT);
        pausedAt = System.currentTimeMillis();
    }

    void resumeGame() {
        startTime += System.currentTimeMillis() - pausedAt;
        sendEmptyMessage(UPDATE_TIME);
        sendEmptyMessageDelayed(ADD_NEW_MARMOT, nextAppearence);
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
                marmot.placeOnScreen(xPos, yPos, marmotExpiration);
                sendEmptyMessageDelayed(ADD_NEW_MARMOT, nextAppearence);

                updateTimings();
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
                checkTime();
                break;
        }
    }

    private void updateTimings() {
        nextAppearence *= 0.977;
        marmotExpiration *= 0.989;
    }

    private void checkTime() {
        if (System.currentTimeMillis() - lastStep > 3000) {
            Toast.makeText(activity, activity.getResources().getString(R.string.stop_during_game),
                    Toast.LENGTH_SHORT).show();
            endGame();
        }
    }

    boolean isRunning() {
        return running;
    }

    boolean isWaiting() {
        return waiting;
    }

    void notWaitingAnymore() {
        waiting = false;
    }
}

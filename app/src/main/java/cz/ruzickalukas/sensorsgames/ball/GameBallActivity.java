package cz.ruzickalukas.sensorsgames.ball;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import cz.ruzickalukas.sensorsgames.R;

public class GameBallActivity extends AppCompatActivity {

    private BallView ball;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_ball);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ball = findViewById(R.id.ball);
        ball.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ball.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ball.unregister();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onBackPressed() {
        onPause();
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.game_exit_title))
                .setMessage(getResources().getString(R.string.game_exit_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                    }
                })
                .show();
    }
}

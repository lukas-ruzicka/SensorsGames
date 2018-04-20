package cz.ruzickalukas.sensorsgames.marmot;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import cz.ruzickalukas.sensorsgames.R;

public class GameMarmotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_marmot);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}

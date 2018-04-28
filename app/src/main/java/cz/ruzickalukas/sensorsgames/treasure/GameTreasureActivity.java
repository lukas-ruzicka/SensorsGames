package cz.ruzickalukas.sensorsgames.treasure;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cz.ruzickalukas.sensorsgames.R;
import cz.ruzickalukas.sensorsgames.main.GameStatus;

public class GameTreasureActivity extends AppCompatActivity {

    private CompassView compass;
    private TextView instructions;
    private ImageView chest;
    private Navigation navigation;

    private boolean waiting = true;
    private boolean gpsPermissionGranted = false;

    private boolean finalTargetAchieved = false;

    private static final int LOCATION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_treasure);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.game_instructions_title))
                .setMessage(getResources().getString(R.string.treasure_instructions))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                waiting = false;
                                onResume();
                            }
                        })
                .show();

        compass = findViewById(R.id.compass);
        compass.init(this);

        instructions = findViewById(R.id.instructions);

        chest = findViewById(R.id.chest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!finalTargetAchieved && !waiting) {
            compass.register();
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!finalTargetAchieved) {
            compass.unregister();
            if (gpsPermissionGranted) {
                navigation.unregister();
            }
        }
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

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this, permissionType);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionType},
                    requestCode);
        } else {
            grantedGPSPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.no_permission_gps),
                        Toast.LENGTH_LONG).show();
                this.finish();
            } else {
                grantedGPSPermission();
            }
        }
    }

    private void grantedGPSPermission() {
        if (navigation == null) {
            navigation = new Navigation(instructions, this);
        }
        gpsPermissionGranted = true;
        navigation.register();
    }

    void finalTargetAchieved() {
        finalTargetAchieved = true;
        compass.unregister();

        compass.setVisibility(View.GONE);
        instructions.setVisibility(View.GONE);

        chest.setVisibility(View.VISIBLE);

        GameStatus.updateScore(this, R.string.treasure, 0);

        final Activity activity = this;
        chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting = true;
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getResources().getString(R.string.game_over_title))
                        .setMessage(activity.getResources().getString(R.string.game_over_treasure))
                        .setCancelable(false)
                        .setPositiveButton(activity.getResources()
                                        .getString(R.string.play_again_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        waiting = false;
                                        chest.setVisibility(View.GONE);
                                        compass.setVisibility(View.VISIBLE);
                                        instructions.setVisibility(View.VISIBLE);
                                        navigation = null;
                                        onResume();
                                    }
                                })
                        .setNegativeButton(activity.getResources()
                                        .getString(R.string.go_back_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        activity.finish();
                                    }
                                })
                        .show();
            }
        });
    }
}

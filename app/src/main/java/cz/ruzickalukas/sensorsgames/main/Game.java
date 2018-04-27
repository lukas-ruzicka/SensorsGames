package cz.ruzickalukas.sensorsgames.main;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import cz.ruzickalukas.sensorsgames.R;

class Game {

    // Game ID equals resource ID of its name
    private int gameId;
    private String name;
    private List<Integer> sensorsResList = new ArrayList<>();
    private String sensorsNames;
    private float score;
    private String scoreStringFormat;
    private int status;

    static final String SCORE = "highest_score";
    static final String STATUS = "game_status";

    Game(Context context, int nameRes, int... sensorsRes) {
        this.gameId = nameRes;
        this.name = context.getResources().getString(nameRes);
        sensorsNames = "";
        for (int sensorRes : sensorsRes) {
            this.sensorsResList.add(sensorRes);
            this.sensorsNames += context.getResources().getString(sensorRes) + ", ";
        }
        this.sensorsNames = sensorsNames.substring(0,sensorsNames.length() - 2);
        loadStatus(context);
        if (gameId == R.string.marmot) {
            scoreStringFormat = context.getResources().getString(R.string.points);
        } else {
            scoreStringFormat = context.getResources().getString(R.string.seconds);
        }
    }

    void loadStatus(Context context) {
        SharedPreferences pref = context.getSharedPreferences(String.valueOf(this.gameId),0);
        this.score = pref.getFloat(SCORE, 0);
        this.status = pref.getInt(STATUS, R.string.not_played);
    }

    public int getId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getSensorsResources() {
        return sensorsResList;
    }

    public String getSensorsNames() {
        return sensorsNames;
    }

    public String getScoreText() {
        return String.format(scoreStringFormat, score);
    }

    public int getStatus() {
        return status;
    }

}

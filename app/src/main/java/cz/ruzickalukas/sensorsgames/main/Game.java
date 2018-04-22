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
    private int status;

    private static final String SCORE = "highest_score";
    private static final String STATUS = "game_status";

    Game(Context context, int nameRes, int... sensorsRes) {
        this.gameId = nameRes;
        this.name = context.getResources().getString(nameRes);
        sensorsNames = "";
        for (int sensorRes : sensorsRes) {
            this.sensorsResList.add(sensorRes);
            this.sensorsNames += context.getResources().getString(sensorRes) + ", ";
        }
        this.sensorsNames = sensorsNames.substring(0,sensorsNames.length() - 2);
        SharedPreferences pref = context.getSharedPreferences(this.name,0);
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
        if (gameId == R.string.marmot) {
            return Integer.toString((int)score) + " points";
        } else {
            return Float.toString(score) + " s";
        }
    }

    public int getStatus() {
        return status;
    }

}

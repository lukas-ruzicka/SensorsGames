package cz.ruzickalukas.sensorsgames.main;

import android.content.Context;
import android.content.SharedPreferences;

import cz.ruzickalukas.sensorsgames.R;

public class GameStatus {

    static void updateStatus(Context context, int gameId) {
        SharedPreferences pref = context.getSharedPreferences(String.valueOf(gameId),0);
        if (pref.getInt(Game.STATUS, R.string.not_played) == R.string.not_played) {
            pref.edit().putInt(Game.STATUS, R.string.in_progress).apply();
        }
    }

    public static void updateScore(Context context, int gameId, float score){
        SharedPreferences pref = context.getSharedPreferences(String.valueOf(gameId),0);
        if (pref.getInt(Game.STATUS, R.string.not_played) == R.string.in_progress) {
            pref.edit().putInt(Game.STATUS, R.string.done).apply();
        }
        pref.edit().putFloat(Game.SCORE, score).apply();
    }

}

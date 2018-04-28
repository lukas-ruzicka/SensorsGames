package cz.ruzickalukas.sensorsgames.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cz.ruzickalukas.sensorsgames.R;

class GamesListAdapter extends BaseAdapter{

    private List<Game> gamesList;

    private Context context;

    GamesListAdapter(List<Game> gamesList, Context context) {
        this.gamesList = gamesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return gamesList.size();
    }

    @Override
    public Object getItem(int position) {
        return gamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.cell_game, parent, false);
            } else {
                return null;
            }
        }

        Game game = gamesList.get(position);

        convertView.setId(game.getId());

        TextView name = convertView.findViewById(R.id.name);
        name.setText(game.getName());

        TextView status = convertView.findViewById(R.id.status);
        if (game.getStatus() == R.string.done && game.getId() != R.string.treasure) {
            String scoreText = context.getResources().getString(R.string.highest_score_intro) +
                    game.getScoreText();
            status.setText(scoreText);
        } else {
            String statusText = context.getResources().getString(R.string.game_status_intro) +
                    context.getResources().getString(game.getStatus());
            status.setText(statusText);
        }

        TextView sensors = convertView.findViewById(R.id.sensors);
        sensors.setText(game.getSensorsNames());

        return convertView;
    }
}

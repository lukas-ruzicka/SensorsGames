package cz.ruzickalukas.sensorsgames.ball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cz.ruzickalukas.sensorsgames.R;

public class TrackView extends View {

    private boolean[][] barriersField = new boolean[14][25];
    private Bitmap barrier;
    private float cellWidth, cellHeight;
    private float xMax, yMax;

    static final int X_DIRECTION = 1;
    static final int Y_DIRECTION = 2;
    static final int BOTH_DIRECTIONS = 3;

    public TrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int cellSize = (int)context.getResources().getDimension(R.dimen.default_cell_size);
        barrier = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.barrier), cellSize, cellSize, false);
        initBarriers();
    }

    private void initBarriers() {
        for (int i = 2; i < 24; i++) {
            barriersField[7][i] = true;
        }
        barriersField[5][10] = true;
        barriersField[6][10] = true;
        invalidate();
    }

    void init(Activity activity, float cellWidth, float cellHeight, float xMax, float yMax) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.xMax = xMax;
        this.yMax = yMax;

        barrier = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.barrier), (int)cellWidth, (int)cellHeight, false);

        invalidate();
    }

    boolean checkBarrier(float x, float y) {
        float xEnd = x + cellWidth;
        float yEnd = y + cellHeight;

        return checkEdge(x, y) ||
                barriersField[(int)(x/cellWidth)][(int)(y/cellHeight)] ||
                barriersField[(int)(x/cellWidth)][(int)(yEnd/cellHeight)] ||
                barriersField[(int)(xEnd/cellWidth)][(int)(yEnd/cellHeight)] ||
                barriersField[(int)(xEnd/cellWidth)][(int)(y/cellHeight)];
    }

    private boolean checkEdge(float x, float y) {
        return x < 0 || (x + cellWidth) >= xMax || y < 0 || (y + cellHeight) >= yMax;
    }

    int getBarrierDirection(float x, float y, float lastX, float lastY) {
        float xEnd = x + cellWidth;
        float yEnd = y + cellHeight;

        if ((x < 0 || xEnd >= xMax) && (y < 0 || yEnd >= yMax)) {
            return BOTH_DIRECTIONS;
        } else if (x < 0 || xEnd >= xMax) {
            return X_DIRECTION;
        } else if (y < 0 || yEnd >= yMax) {
            return Y_DIRECTION;
        }

        boolean topLeft = barriersField[(int)(x/cellWidth)][(int)(y/cellHeight)];
        boolean topRight = barriersField[(int)(xEnd/cellWidth)][(int)(y/cellHeight)];
        boolean bottomLeft = barriersField[(int)(x/cellWidth)][(int)(yEnd/cellHeight)];
        boolean bottomRight = barriersField[(int)(xEnd/cellWidth)][(int)(yEnd/cellHeight)];

        boolean xDirection =
                (topLeft && x < lastX) || (bottomLeft && x < lastX) ||
                (topRight && x > lastX) || (bottomRight && x > lastX);

        boolean yDirection =
                (topLeft && y < lastY) || (topRight && y < lastY) ||
                (bottomLeft && y > lastY) || (bottomRight && y > lastY);

        if (xDirection && yDirection) {
            if ((topLeft && bottomRight) || (topRight && bottomLeft)) {
                return BOTH_DIRECTIONS;
            } else if ((topLeft && bottomLeft && !topRight && !bottomRight) ||
                    (!topLeft && !bottomLeft && topRight && bottomRight) ||
                    (Math.abs((int)(x/cellWidth) - (int)(lastX/cellWidth)) == 1)) {
                return X_DIRECTION;
            } else {
                return Y_DIRECTION;
            }
        } else if (xDirection) {
            return X_DIRECTION;
        } else if (yDirection) {
            return Y_DIRECTION;
        } else {
            return 0;
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < barriersField.length; i++) {
            for (int j = 0; j < barriersField[0].length; j++) {
                if (barriersField[i][j]) {
                    canvas.drawBitmap(barrier, i * cellWidth, j * cellHeight, null);
                }
            }
        }
    }
}

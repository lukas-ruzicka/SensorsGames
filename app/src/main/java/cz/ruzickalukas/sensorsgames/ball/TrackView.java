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
    private Bitmap hole;
    private float cellWidth, cellHeight;
    private float xMax, yMax;

    static final int X_DIRECTION = 1;
    static final int Y_DIRECTION = 2;
    static final int BOTH_DIRECTIONS = 3;

    public TrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    void init(Activity activity, float cellWidth, float cellHeight, float xMax, float yMax) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.xMax = xMax;
        this.yMax = yMax;

        barrier = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.barrier), (int)cellWidth, (int)cellHeight, false);
        initBarriers();

        hole = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.hole), (int)cellWidth, (int)cellHeight, false);

        invalidate();
    }

    boolean checkHole(float x, float y) {
        return ((int)((x + cellWidth) / cellWidth) == barriersField.length - 1)
                && ((int)((y + cellHeight) / cellWidth) == barriersField[0].length - 1);
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

        boolean topLeft = x < 0 || y < 0 || barriersField[(int)(x/cellWidth)][(int)(y/cellHeight)];
        boolean topRight = xEnd >= xMax || y < 0 ||
                barriersField[(int)(xEnd/cellWidth)][(int)(y/cellHeight)];
        boolean bottomLeft = x < 0 || yEnd >= yMax ||
                barriersField[(int)(x/cellWidth)][(int)(yEnd/cellHeight)];
        boolean bottomRight = xEnd >= xMax || yEnd >= yMax ||
                barriersField[(int)(xEnd/cellWidth)][(int)(yEnd/cellHeight)];

        switch (getTrueCount(topLeft, topRight, bottomLeft, bottomRight)) {
            case 1:
                boolean xDirection =
                        (topLeft && x < lastX) || (bottomLeft && x < lastX) ||
                                (topRight && x > lastX) || (bottomRight && x > lastX);
                boolean yDirection =
                        (topLeft && y < lastY) || (topRight && y < lastY) ||
                                (bottomLeft && y > lastY) || (bottomRight && y > lastY);

                if (xDirection && yDirection) {
                    return BOTH_DIRECTIONS;
                } else if (xDirection) {
                    return X_DIRECTION;
                } else if (yDirection) {
                    return Y_DIRECTION;
                } else {
                    return BOTH_DIRECTIONS;
                }
            case 2:
                if ((topLeft && topRight && !bottomLeft && !bottomRight) ||
                        (!topLeft && !topRight && bottomLeft && bottomRight)) {
                    return Y_DIRECTION;
                } else if ((topLeft && bottomLeft && !topRight && !bottomRight) ||
                        (!topLeft && !bottomLeft && topRight && bottomRight)) {
                    return X_DIRECTION;
                } else {
                    return BOTH_DIRECTIONS;
                }
            case 3:
            case 4:
            default:
                return BOTH_DIRECTIONS;
        }
    }

    int getTrueCount(boolean... vars) {
        int count = 0;
        for (boolean var : vars) {
            count += (var ? 1 : 0);
        }
        return count;
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
        canvas.drawBitmap(hole, (barriersField.length - 1) * cellWidth,
                (barriersField[0].length - 1) * cellHeight, null);
    }

    private void initBarriers() {
        barriersField[0][2] = true;
        barriersField[1][2] = true;
        barriersField[2][2] = true;
        barriersField[3][2] = true;
        barriersField[4][2] = true;
        barriersField[5][2] = true;
        barriersField[6][2] = true;
        barriersField[7][2] = true;
        barriersField[8][2] = true;
        barriersField[11][2] = true;
        barriersField[0][3] = true;
        barriersField[6][3] = true;
        barriersField[7][3] = true;
        barriersField[8][3] = true;
        barriersField[11][3] = true;
        barriersField[0][4] = true;
        barriersField[6][4] = true;
        barriersField[7][4] = true;
        barriersField[8][4] = true;
        barriersField[11][4] = true;
        barriersField[3][5] = true;
        barriersField[8][5] = true;
        barriersField[11][5] = true;
        barriersField[3][6] = true;
        barriersField[8][6] = true;
        barriersField[11][6] = true;
        barriersField[3][7] = true;
        barriersField[6][7] = true;
        barriersField[7][7] = true;
        barriersField[8][7] = true;
        barriersField[11][7] = true;
        barriersField[3][8] = true;
        barriersField[6][8] = true;
        barriersField[11][8] = true;
        barriersField[3][9] = true;
        barriersField[6][9] = true;
        barriersField[11][9] = true;
        barriersField[3][10] = true;
        barriersField[6][10] = true;
        barriersField[9][10] = true;
        barriersField[10][10] = true;
        barriersField[11][10] = true;
        barriersField[12][10] = true;
        barriersField[13][10] = true;
        barriersField[3][11] = true;
        barriersField[6][11] = true;
        barriersField[3][12] = true;
        barriersField[6][12] = true;
        barriersField[2][13] = true;
        barriersField[3][13] = true;
        barriersField[6][13] = true;
        barriersField[7][13] = true;
        barriersField[8][13] = true;
        barriersField[9][13] = true;
        barriersField[10][13] = true;
        barriersField[11][13] = true;
        barriersField[2][14] = true;
        barriersField[3][14] = true;
        barriersField[11][14] = true;
        barriersField[3][15] = true;
        barriersField[11][15] = true;
        barriersField[3][16] = true;
        barriersField[4][16] = true;
        barriersField[5][16] = true;
        barriersField[6][16] = true;
        barriersField[7][16] = true;
        barriersField[8][16] = true;
        barriersField[11][16] = true;
        barriersField[8][17] = true;
        barriersField[11][17] = true;
        barriersField[8][18] = true;
        barriersField[0][19] = true;
        barriersField[1][19] = true;
        barriersField[2][19] = true;
        barriersField[3][19] = true;
        barriersField[4][19] = true;
        barriersField[5][19] = true;
        barriersField[8][19] = true;
        barriersField[0][20] = true;
        barriersField[8][20] = true;
        barriersField[9][20] = true;
        barriersField[10][20] = true;
        barriersField[11][20] = true;
        barriersField[12][20] = true;
        barriersField[13][20] = true;
        barriersField[0][21] = true;
        barriersField[0][22] = true;
        barriersField[3][22] = true;
        barriersField[4][22] = true;
        barriersField[5][22] = true;
        barriersField[6][22] = true;
        barriersField[0][23] = true;
        barriersField[10][23] = true;
        barriersField[11][23] = true;
        barriersField[0][24] = true;
        barriersField[10][24] = true;
        barriersField[11][24] = true;
    }
}

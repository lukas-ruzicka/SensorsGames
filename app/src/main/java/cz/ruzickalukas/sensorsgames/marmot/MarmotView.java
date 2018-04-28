package cz.ruzickalukas.sensorsgames.marmot;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import cz.ruzickalukas.sensorsgames.R;

public class MarmotView extends AppCompatImageView {

    private StayOutHandler stayOutHandler;
    private boolean hitOrExpired = false;

    public MarmotView(Context context) {
        super(context);
    }

    MarmotView(Context context, final MarmotManager marmotManager) {
        this(context);

        setImageResource(R.drawable.marmot);
        setLayoutParams(new ViewGroup.LayoutParams(
                (int)context.getResources().getDimension(R.dimen.marmot_width),
                (int)context.getResources().getDimension(R.dimen.marmot_height)));

        stayOutHandler = new StayOutHandler(this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hitOrExpired) {
                    stayOutHandler.removeMessages(0);
                    marmotManager.sendEmptyMessage(MarmotManager.MARMOT_HIT);
                    expire();
                }
            }
        });
    }

    void placeOnScreen(int xPos, int yPos, int expiration) {
        setX(xPos);
        setY(yPos);

        stayOutHandler.sendEmptyMessageDelayed(0, expiration);
    }

    void expire() {
        hitOrExpired = true;
        ((FrameLayout)this.getParent()).removeView(this);
    }
}

class StayOutHandler extends Handler {

    private MarmotView marmotView;

    StayOutHandler(MarmotView marmotView) {
        this.marmotView = marmotView;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        marmotView.expire();
    }
}

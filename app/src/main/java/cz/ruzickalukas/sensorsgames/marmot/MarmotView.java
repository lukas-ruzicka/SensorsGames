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
                    animateOut();
                }
            }
        });
    }

    void setPosition(int xPos, int yPos) {
        setX(xPos);
        setY(yPos);
    }

    void animateIn(int expiration) {
        /*ScaleAnimation scale = new ScaleAnimation(0.1f,1f,0.1f,1f,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(300);
        TranslateAnimation translate = new TranslateAnimation(getX(),getX(),
                getY(), getY() - 20);
        translate.setDuration(300);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setStartOffset(200);
        alpha.setDuration(100);

        AnimationSet animateIn = new AnimationSet(true);
        animateIn.addAnimation(scale);
        animateIn.addAnimation(translate);
        animateIn.addAnimation(alpha);
        animateIn.setFillAfter(true);

        startAnimation(animateIn);*/

        stayOutHandler.sendEmptyMessageDelayed(0, expiration);
    }

    void animateOut() {
        /*ScaleAnimation scaleOut = new ScaleAnimation(1f,0.1f,1f,0.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleOut.setDuration(300);
        TranslateAnimation translateOut = new TranslateAnimation(getX(),getX(),
                getY(), getY() + 20);
        translateOut.setDuration(300);
        AlphaAnimation alphaOut = new AlphaAnimation(1.0f, 0.0f);
        alphaOut.setStartOffset(200);
        alphaOut.setDuration(100);

        AnimationSet animateOut = new AnimationSet(true);
        animateOut.addAnimation(scaleOut);
        animateOut.addAnimation(translateOut);
        animateOut.addAnimation(alphaOut);
        animateOut.setFillAfter(true);

        startAnimation(animateOut);*/
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        if (hitOrExpired) {
            ((FrameLayout)this.getParent()).removeView(this);
        }
    }

    void expire() {
        hitOrExpired = true;
        // To be deleted
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
        marmotView.animateOut();
        marmotView.expire();
    }
}

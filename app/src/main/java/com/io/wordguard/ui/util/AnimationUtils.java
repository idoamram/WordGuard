package com.io.wordguard.ui.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.io.wordguard.R;

public class AnimationUtils {
    public interface AnimationFinishedListener {
        void onAnimationFinished();
    }

    private static int getDuration(Context context) {
        int duration;
        duration = context.getResources().getInteger(android.R.integer.config_longAnimTime) * 2;
        return duration;
    }

    @ColorInt
    private static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    private static void registerCircularRevealAnimation(final Context context, final View view, final RevealAnimationSetting revealSettings, final int startColor, final int endColor, final AnimationFinishedListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);

                    int cx = revealSettings.getCenterX();
                    int cy = revealSettings.getCenterY();
                    int width = revealSettings.getWidth();
                    int height = revealSettings.getHeight();

                    //Simply use the diagonal of the view
                    float finalRadius = (float) Math.sqrt(width * width + height * height);
                    Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                    anim.setDuration(getDuration(context));
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            listener.onAnimationFinished();
                        }
                    });
                    anim.start();
                    startBackgroundColorAnimation(view, startColor, endColor, getDuration(context));
                }
            });
        } else {
            if (listener != null) {
                listener.onAnimationFinished();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void startCircularRevealExitAnimation(Context context, final View view, RevealAnimationSetting revealSettings, int startColor, int endColor, final AnimationFinishedListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = revealSettings.getCenterX();
            int cy = revealSettings.getCenterY();
            int width = revealSettings.getWidth();
            int height = revealSettings.getHeight();

            float initRadius = (float) Math.sqrt(width * width + height * height);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0);
            anim.setDuration(getDuration(context));
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //Important: This will prevent the view's flashing (visible between the finished animation and the Fragment remove)
                    view.setVisibility(View.GONE);
                    if (listener != null) {
                        listener.onAnimationFinished();
                    }
                }
            });
            anim.start();
            startBackgroundColorAnimation(view, startColor, endColor, getDuration(context));
        } else {
            if (listener != null) {
                listener.onAnimationFinished();
            }
        }
    }

    private static void startBackgroundColorAnimation(final View view, int startColor, int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
    }

    //Specific cases for our share link screen

    public static void registerCircularRevealAnimation(Context context, View view,
                                                       RevealAnimationSetting revealSettings,
                                                       AnimationFinishedListener listener) {
        registerCircularRevealAnimation(context, view, revealSettings,
                getColor(context, R.color.colorPrimary),
                getColor(context, R.color.quantum_white_100), listener);
    }

    public static void startCircularRevealExitAnimation(Context context, View view,
                                                        RevealAnimationSetting revealSettings,
                                                        AnimationFinishedListener listener) {
        startCircularRevealExitAnimation(context, view, revealSettings,
                getColor(context, R.color.quantum_white_100),
                getColor(context, R.color.colorPrimary), listener);
    }
}
package com.prince.flutter_steps_counter;

import android.util.Log;

import java.math.BigDecimal;


/**
 * Created by gojuukaze on 16/8/11.
 * Email: i@ikaze.uu.me
 */
public class StepDetector {
    private final int DELAY = 100;
    private float MAX_VEL = 14f;
    private float MIN_VEL = 7f;
    private float init_vel = 1.1f;

    private float VEL_THRESHOLD = init_vel;
    private float[] velocities = {init_vel, init_vel, init_vel, init_vel};
    private int pos = 0;
    private StepListener stepListener;
    private long lastStepTime = 0;
    private float lastVel = 0;

    private final long WAIT_STEPS = 4;
    private final int WAIT_MODEL = 0;
    private final int ACTIVITY_MODEL = 1;

    private final int RUN_MODEL = 2;
    private int model;

    private boolean isActivity = false;

    private long tempSteps = 0;

    private int initCount;

    private float crest=0;
    private float trough=0;

    private final int up=0;
    private final int down=1;
    private final int init=2;
    private  int nowStatus=init;
    private int lastStatus=init;

    final public String TAG = "FLUTTER_STEP_PLUGIN";


    public StepDetector(StepListener stepListener) {
        model = WAIT_MODEL;
        initCount = 0;
        this.stepListener = stepListener;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public void updateStep(float x, float y, float z) {
        long curTime = System.currentTimeMillis();
        if (lastStepTime == 0) {
            lastStepTime = curTime;
        }
        if (curTime - lastStepTime < DELAY)
            return;
        lastStepTime = curTime;

        BigDecimal b = new BigDecimal(Math.sqrt(x * x + y * y + z * z));
        float vel = b.setScale(2, BigDecimal.ROUND_DOWN).floatValue();

        if (lastVel == 0)
            lastVel = vel;

        if (vel < MIN_VEL || vel > MAX_VEL) {
            initStepDetector();
            return;
        }

        VEL_THRESHOLD = getVEL_THRESHOLD();

        if(vel>lastVel)
        {
            if (lastStatus==up || crest==0)
                crest=vel;
            if (trough==0)
                trough=lastVel;
            nowStatus=up;
        }
        else if (vel<=lastVel)
        {
            if (lastStatus==down || trough==0)
                trough=vel;
            if (crest==0)
                crest=lastVel;
            nowStatus=down;
        }

        if (nowStatus!=lastStatus&& (nowStatus!=init && lastStatus!=init))
        {
            if (crest - trough >= VEL_THRESHOLD) {
                realSteps(1);
                updateVEL_THRESHOLD(crest - trough);
            } else {
                initStepDetector();
            }
            crest=trough=0;
        }

        lastVel = vel;
        lastStatus=nowStatus;

    }

    public void realSteps(long num) {
        initCount = 0;
        if (model == ACTIVITY_MODEL || model == RUN_MODEL)
            stepListener.step(num);
        else {
            if (tempSteps >= WAIT_STEPS) {
                model = RUN_MODEL;
                stepListener.step(num + tempSteps);
                tempSteps = 0;
            } else tempSteps++;
        }
    }

    public void updateModel(boolean f) {
        if (f) {
            setModel(ACTIVITY_MODEL);
        } else {
            if (model == ACTIVITY_MODEL) {
                setModel(WAIT_MODEL);
            }
        }
    }

    public void initThreshold() {
        pos=0;
        for (int i = 0; i < 4; i++)
            velocities[i] = init_vel;

    }

    public void initStepDetector() {
        if (initCount < 2) {
            initCount++;
            return;
        }
        nowStatus=init;
        lastStatus=init;
        crest=0;
        trough=0;
        initThreshold();
        if (model == RUN_MODEL)
            model = WAIT_MODEL;
        tempSteps = 0;
        initCount = 0;

    }

    public void updateVEL_THRESHOLD(float vel) {
        velocities[pos++] = vel;
        if (pos == 4)
            pos = 0;
    }

    public float getVEL_THRESHOLD() {
        float sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += velocities[i];
        }
        sum = sum / 4;
        return sum;
    }


}


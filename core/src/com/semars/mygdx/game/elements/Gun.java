package com.semars.mygdx.game.elements;

import com.badlogic.gdx.utils.Array;

/**
 * Created by semar on 7/14/15.
 */
public class Gun {

    private float gunAngle;
    private float gunX;
    private float gunY;


    public Gun(float angle, float x, float y) {
        gunAngle = angle;
        gunX = x;
        gunY = y;
    }

    public ShotActor createShot() {
        ShotActor shot = new ShotActor(gunX, gunY, gunAngle);
        return shot;
    }

    public void setGunAngle(float gunAngle) {
        this.gunAngle = gunAngle;
    }

    public void setGunX(float gunX) {
        this.gunX = gunX;
    }

    public void setGunY(float gunY) {
        this.gunY = gunY;
    }
}

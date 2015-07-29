package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/14/15.
 */
public class Gun {

    private float gunAngle;
    private Vector2 gunPos = new Vector2();
    private float gunX;
    private float gunY;
    private ShotActor.ShotType shotType;
    private float fireRate;
    private float lastShotTime;
    private Array <ShotActor> shotsActive;
    private Sound laserSound;

    public Gun(float angle, float x, float y, ShotActor.ShotType shotType, float fireRate) {
        gunAngle = angle;
        gunX = x;
        gunY = y;
        this.shotType = shotType;
        gunPos.set(gunX, gunY);
        this.fireRate = fireRate;
        lastShotTime = 0f;
        shotsActive = new Array<ShotActor>();
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser1.ogg"));
    }

    public ShotActor shoot(float delta) {
        gunPos.set(gunX, gunY);
        lastShotTime += delta;

        if (lastShotTime > fireRate) {
            ShotActor shot = Asteroidia.actorManager.addShotActor(gunPos, ActorType.SHOT, CollisionGroup.PLAYER_SHOT, gunAngle, this.shotType);
            lastShotTime = 0f;
            shotsActive.add(shot);
            laserSound.play();
            return shot;
        }
        return null;
    }

    /*/////////////////
    Getters and Setters
    *//////////////////

    public void setGunAngle(float gunAngle) {
        this.gunAngle = gunAngle;
    }

    public Vector2 getGunPos() {
        return gunPos;
    }

    public void setGunPos(Vector2 gunPos) {
        this.gunPos = gunPos;
    }

    public void setShotType(ShotActor.ShotType shotType) {
        this.shotType = shotType;
    }

    public void setGunX(float gunX) {
        this.gunX = gunX;
    }

    public void setGunY(float gunY) {
        this.gunY = gunY;
    }

    public ShotActor.ShotType getShotType() {
        return shotType;
    }
}

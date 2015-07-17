package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.semars.mygdx.game.Asteroidia;

import java.util.Iterator;

/**
 * Created by semar on 6/28/15.
 */
public class PlayerActor extends SpaceActor {
    private Body body;
    private Fixture fixture;
    private Texture texture;
    private int playerWidth = 52;
    private int playerHeight = 40;
    private float moveSpeed = 300f;
    private float angle = 90;
    private boolean isMoving;
    private boolean isActive;
    private Vector2 currentPos = new Vector2();
    public Vector2 endPos = new Vector2();
    public Vector2 startPos = new Vector2();
    public Vector2 moveDirection = new Vector2();
    public Vector2 moveVelocity = new Vector2();
    public Vector2 moveAmount = new Vector2();
    public float moveDistance;
    private float delta;
    private Rectangle boundingRectangle = new Rectangle();
    private Sound killSound = Gdx.audio.newSound(Gdx.files.internal("explosion.mp3"));
    private Gun gun;
    private float gunX;
    private float gunY;
    private float gunAngle;
    private Array<ShotActor> shotsActive;
    private float timeSinceLastShot = 0f;


    public PlayerActor() {
        texture = new Texture(Gdx.files.internal("playerBlue2.png"));
        setBounds(Asteroidia.WIDTH / 2, Asteroidia.HEIGHT / 4, playerWidth, playerHeight);
        currentPos.set(getX(), getY());
        boundingRectangle.set(getX(), getY(), getWidth(), getHeight());
        isMoving = false;
        shotsActive = new Array<ShotActor>();

        // set gun to ship's tip
        gunAngle = angle;
        gunX = getX() + playerWidth / 2;
        gunY = getY() + playerHeight;
        gun = new Gun(gunX, gunY, gunAngle);
    }

    @Override
    public void createBody(World world, Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x/Asteroidia.CONVERSION, pos.y/Asteroidia.CONVERSION);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / Asteroidia.CONVERSION / 2, getHeight() / Asteroidia.CONVERSION / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(texture, currentPos.x, currentPos.y, playerWidth, playerHeight);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        shoot(delta);
    }

    @Override
    public void move() {
        if (isMoving) {
            // set amount to move player (Vector2)
            currentPos.add(moveAmount);

            // ensure player movement will not leave screen
            if(currentPos.x < 0) currentPos.x = 0;
            if(currentPos.x > Asteroidia.WIDTH - getWidth()) currentPos.x = Asteroidia.WIDTH - getWidth();
            if(currentPos.y < 0) currentPos.y = 0;
            if(currentPos.y > Asteroidia.HEIGHT - getHeight()) currentPos.y = Asteroidia.HEIGHT - getHeight();

            boundingRectangle.setPosition(currentPos.x, currentPos.y);
            setX(currentPos.x);
            setY(currentPos.y);
            gunX = currentPos.x + playerWidth / 2;
            gunY = currentPos.y + playerHeight;
            gun.setGunX(gunX);
            gun.setGunY(gunY);
            // if goal is reached, stop movement
            if (startPos.dst2(currentPos) >= moveDistance) {
                isMoving = false;
            }
        }
    }

    @Override
    public void destroy(World world) {
        if (isActive)
        //killSound.play();
        setVisible(false);
        isMoving = false;
        System.out.println("============DEAD=============");
    }

    public void shoot(float delta) {
        Iterator<ShotActor> iterator = shotsActive.iterator();
        while (iterator.hasNext()) {
            ShotActor shot = iterator.next();
            if (shot.isDestroyed()) {
                iterator.remove();
            }
        }
        timeSinceLastShot += delta;
        if (timeSinceLastShot > 0.25f) {
            ShotActor shot = gun.createShot();
            shot.createBody(Asteroidia.world, shot.getCurrentPos());
            shotsActive.add(shot);
            getStage().addActor(shot);
            timeSinceLastShot = 0;
        }
    }

    public float getAngle() {
        return angle;
    }

    public Vector2 getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Vector2 currentPos) {
        this.currentPos = currentPos;
    }

    public Texture getTexture() {
            return texture;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public Gun getGun() {
        return gun;
    }

    public Array<ShotActor> getShotsActive() {
        return shotsActive;
    }
}
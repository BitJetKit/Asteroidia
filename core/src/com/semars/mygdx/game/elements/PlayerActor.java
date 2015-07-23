package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    private ActorData actorData;
    private Texture texture;
    private float playerWidth;
    private float playerHeight;
    private float moveSpeed;
    private float angle;
    private float moveAmtPerTimestep;
    private float moveSpeedToUse;
    private boolean isMoving;
    private boolean isActive;
    private Vector3 moveTarget = new Vector3();
    private Vector2 worldPos = new Vector2();
    private Vector2 endPos = new Vector2();
    private Vector2 startPos = new Vector2();
    private Vector2 moveDirection = new Vector2();
    private Vector2 moveVelocity = new Vector2();
    private Vector2 moveVelocityDifference = new Vector2();
    private Vector2 moveAmount = new Vector2();
    private Vector2 moveForce = new Vector2();
    private float moveDistance;
    private float delta;
    private Sound killSound = Gdx.audio.newSound(Gdx.files.internal("explosion.mp3"));
    private Gun gun;
    private float gunX;
    private float gunY;
    private float gunAngle;
    private Array<ShotActor> shotsActive;
    private float timeSinceLastShot = 0f;

    public PlayerActor(Vector2 pos, World world, int actorIndex, int collisionGroup) {
        super(pos, world, actorIndex, collisionGroup);
        actorData = new ActorData(actorIndex, collisionGroup);
        texture = new Texture(Gdx.files.internal("shipOrange.png"));
        playerWidth = 0.94f;
        playerHeight = 0.72f;
        moveSpeed = 5f;
        angle = 0;
        isMoving = false;
        isActive = true;

        setBounds(pos.x, pos.y, playerWidth, playerHeight);
        createBody(world, pos, 0, 0, 0);
        worldPos.set(pos);
        moveTarget.set(body.getPosition().x, body.getPosition().y, 0);

        // set gun to ship's tip
        gunAngle = angle;
        gunX = getX() + playerWidth / 2;
        gunY = getY() + playerHeight;
        gun = new Gun(gunX, gunY, gunAngle);

        shotsActive = new Array<ShotActor>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, worldPos.x - playerWidth / 2, worldPos.y - playerHeight / 2, playerWidth, playerHeight);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        updateWorldPos();
        shoot(delta);
    }

    @Override
    public void move() {
        // set coordinates to Vector2(s) for calculation
        endPos.set(moveTarget.x, moveTarget.y + playerHeight);
        startPos.set(worldPos);

        // calculate distance and direction between player and touch
        moveSpeedToUse = moveSpeed;
        moveDistance = endPos.dst2(startPos);
        moveDirection.set(endPos.sub(startPos)).nor();
        moveAmtPerTimestep = moveSpeedToUse / Asteroidia.STEP;

        // scale speed to prevent passing target
        if (moveAmtPerTimestep > moveDistance) {
            moveSpeedToUse *= (moveDistance / moveAmtPerTimestep);
        }

        moveVelocity.set(moveDirection.scl(moveSpeedToUse));
        moveVelocityDifference.set(moveVelocity.sub(body.getLinearVelocity()));
        moveForce.set(moveVelocityDifference.scl(Asteroidia.STEP * body.getMass()));

        body.applyForce(moveForce, body.getWorldCenter(), true);

        updateWorldPos();

        // ensure player movement will not leave screen
        if(worldPos.x - playerWidth/2 < 0) worldPos.x = 0 + playerWidth/2;
        if(worldPos.x - playerWidth/2 > Asteroidia.WIDTH - playerWidth) worldPos.x = Asteroidia.WIDTH - playerWidth/2;
        if(worldPos.y - playerHeight/2 < 0) worldPos.y = 0 + playerHeight/2;
        if(worldPos.y - playerHeight/2 > Asteroidia.HEIGHT - playerHeight) worldPos.y = Asteroidia.HEIGHT - playerHeight/2;

        body.setTransform(worldPos, angle);

        gunX = worldPos.x;
        gunY = worldPos.y + playerHeight;
        gun.setGunX(gunX);
        gun.setGunY(gunY);
    }

    public void shoot(float delta) {
        Iterator<ShotActor> iterator = shotsActive.iterator();
        while (iterator.hasNext()) {
            ShotActor shot = iterator.next();
            if (shot.isActive() == false) {
                iterator.remove();
            }
        }
        timeSinceLastShot += delta;
        if (timeSinceLastShot > 0.25f) {
            //ShotActor shot = gun.createShot();
            //shot.createBody(Asteroidia.world, shot.getCurrentPos());
            //shotsActive.add(shot);
            //getStage().addActor(shot);
            timeSinceLastShot = 0;
        }
    }

    @Override
    public void createBody(World world, Vector2 pos, float angle, float density, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.94f * 0.5f, 0.72f * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
        System.out.println("body: " + body.getPosition() + " world: " + worldPos + " player " + getX() + " " + getY());
    }

    @Override
    public void destroy(World world) {
        if (isActive)
        //killSound.play();
        setVisible(false);
        isMoving = false;
        System.out.println("============DEAD=============");
    }

    @Override
    public void updateWorldPos() {
        worldPos.set(body.getPosition().x, body.getPosition().y);
    }

    /*
     Getters and Setters
    */

    public float getPlayerWidth() {
        return playerWidth;
    }

    public Body getBody() {
        return body;
    }

    public float getAngle() {
        return angle;
    }

    public Vector2 getWorldPos() {
        return worldPos;
    }

    public Texture getTexture() {
            return texture;
    }

    public void setMoveTarget(Vector3 moveTarget) {
        this.moveTarget = moveTarget;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getMoveDistance() {
        return moveDistance;
    }

    public Vector2 getEndPos() {
        return endPos;
    }

    public Vector2 getMoveAmount() {
        return moveAmount;
    }

    public Vector2 getMoveDirection() {
        return moveDirection;
    }

    public Vector2 getMoveVelocity() {
        return moveVelocity;
    }

    public Vector2 getStartPos() {
        return startPos;
    }

    public Vector2 getMoveVelocityDifference() {
        return moveVelocityDifference;
    }

    public Vector2 getMoveForce() {
        return moveForce;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Gun getGun() {
        return gun;
    }

    public Array<ShotActor> getShotsActive() {
        return shotsActive;
    }

    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public void setEndPos(Vector2 endPos) {
        this.endPos = endPos;
    }

    public void setMoveVelocity(Vector2 moveVelocity) {
        this.moveVelocity = moveVelocity;
    }

    public void setMoveDistance(float moveDistance) {
        this.moveDistance = moveDistance;
    }
}
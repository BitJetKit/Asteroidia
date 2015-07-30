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
import com.semars.mygdx.game.ActorManager;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 6/28/15.
 */
public class PlayerActor extends SpaceActor {
    private Body body;
    private Fixture fixture;
    private ActorData actorData;
    private Texture texture;
    private float width;
    private float height;
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
    private ShieldActor shield;
    private Array<Gun> gunsEquipped;
    private Gun gunFront;
    private Gun gunFront2;
    private Gun gunLeft;
    private Gun gunRight;
    private Vector2 gunPos = new Vector2();
    private Array<ShotActor> shotsActive;
    private float timeSinceLastShot = 0f;
    private float timeSinceLastShotA = 0f;
    private ActorManager actorManager;
    private World world;
    private int score;

    public PlayerActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup) {
        super(pos, world, actorIndex, collisionGroup);
        actorData = new ActorData(actorIndex, collisionGroup);
        texture = new Texture(Gdx.files.internal("shipOrange.png"));
        width = 0.94f;
        height = 0.72f;
        moveSpeed = 10f;
        angle = 0;
        isMoving = false;
        isActive = true;
        actorManager = Asteroidia.actorManager;
        this.world = world;

        setBounds(pos.x, pos.y, width, height);
        createBody(world, pos, angle, 0, 0, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
        worldPos.set(pos);
        moveTarget.set(body.getPosition().x, body.getPosition().y, 0);

        gunsEquipped = new Array<Gun>();
        shotsActive = new Array<ShotActor>();

        // set gun to ship's tip
        gunFront = addGun(worldPos.x + width/8, worldPos.y + height/2, angle, ActorType.PLAYER_SHOT_BULLET, 0.15f);
        //gunFront2 = addGun(worldPos.x - width/8, worldPos.y + height, angle, Gun.ShotType.LASER, 0.15f);
        //gunLeft = addGun(worldPos.x + width/4, worldPos.y + height, 45, Gun.ShotType.LASER, 0.15f);
        //gunRight = addGun(worldPos.x - width/4, worldPos.y + height, 315, Gun.ShotType.LASER, 0.15f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        updateWorldPos();
        shoot(delta, actorManager);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, worldPos.x - width / 2, worldPos.y - height / 2, width, height);
    }

    @Override
    public void move() {
        // set coordinates to Vector2(s) for calculation
        endPos.set(moveTarget.x, moveTarget.y + height * 1.5f);
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
        restrictToWorld();
        updateGunPos();
    }

    public void shoot(float delta, ActorManager actorManager) {
        for (Gun gun : gunsEquipped) {
            gun.shoot(delta);
        }
    }

    @Override
    public void createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        body.setUserData(actorData);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width * 0.5f, height * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        fixture = body.createFixture(fixtureDef);

        shape.dispose();
    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            isActive = false;
            isMoving = false;
            world.destroyBody(body);
            shield.destroy(world);
            System.out.println("============DEAD=============");
            remove();
        }
    }

    public Gun addGun(float gunX, float gunY, float angle, ActorType shotType, float fireRate) {
        Gun gun = new Gun(angle, gunX, gunY, fireRate, shotType);
        if(gunsEquipped.size > 0) {
            gun.setLastShotTime(gunsEquipped.get(0).getLastShotTime());
        }
        gunsEquipped.add(gun);
        return gun;
    }

    public void updateGunPos() {
        if (gunsEquipped.contains(gunFront, true) && gunsEquipped.contains(gunFront2, true)) {
            gunFront.setGunX(worldPos.x + width / 8f);
            gunFront.setGunY(worldPos.y + height / 2f);
        }
        else if (gunsEquipped.contains(gunFront, true)) {
            gunFront.setGunX(worldPos.x);
            gunFront.setGunY(worldPos.y + height / 2f);
        }
        if (gunsEquipped.contains(gunFront2, true)) {
            gunFront2.setGunX(worldPos.x - width / 8f);
            gunFront2.setGunY(worldPos.y + height / 2f);
        }
        if (gunsEquipped.contains(gunLeft, true)) {
            gunLeft.setGunX(worldPos.x - width / 4f);
            gunLeft.setGunY(worldPos.y);
        }
        if (gunsEquipped.contains(gunRight, true)) {
            gunRight.setGunX(worldPos.x + width / 4f);
            gunRight.setGunY(worldPos.y);
        }

    }

    public void doubleGuns() {
        // power up both guns
        if (gunsEquipped.contains(gunFront, true) && gunsEquipped.contains(gunFront2, true)) {
            gunFront.setShotType(ActorType.PLAYER_SHOT_LASER);
            gunFront2.setShotType(ActorType.PLAYER_SHOT_LASER);
        }
        // add second front gun
        else if (gunsEquipped.contains(gunFront, true)) {
            gunFront2 = addGun(worldPos.x - width/8, worldPos.y + height, angle, ActorType.PLAYER_SHOT_BULLET, 0.15f);
        }
    }

    @Override
    public void updateWorldPos() {
        worldPos.set(body.getPosition().x, body.getPosition().y);
    }

    public void restrictToWorld() {
        // ensure player movement will not leave screen
        if(worldPos.x  < 0) {
            worldPos.x = 0;
            body.setTransform(worldPos, angle);
        }
        if(worldPos.x > Asteroidia.WIDTH) {
            worldPos.x = Asteroidia.WIDTH;
            body.setTransform(worldPos, angle);
        }
        if(worldPos.y - height /2 < 0) {
            worldPos.y = 0 + height /2;
            body.setTransform(worldPos, angle);
        }
        if(worldPos.y - height /2 > Asteroidia.HEIGHT - height) {
            worldPos.y = Asteroidia.HEIGHT - height /2;
            body.setTransform(worldPos, angle);
        }
    }

    /*/////////////////
    Getters and Setters
    *//////////////////

    public void setIndex(int newIndex){
        actorData.setInfo(newIndex, actorData.getCollisionGroup());
        body.setUserData(actorData);
    }

    public float getWidth() {
        return width;
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

    public Gun getGunFront() {
        return gunFront;
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

    public ActorManager getActorManager() {
        return actorManager;
    }

    public void setActorManager(ActorManager actorManager) {
        this.actorManager = actorManager;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Array<Gun> getGunsEquipped() {
        return gunsEquipped;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ShieldActor getShield() {
        return shield;
    }

    public void setShield(ShieldActor shield) {
        this.shield = shield;
    }
}
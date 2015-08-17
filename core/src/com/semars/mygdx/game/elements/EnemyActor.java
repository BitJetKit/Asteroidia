package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/9/15.
 */
public abstract class EnemyActor extends SpaceActor {
    protected Body body;
    private Fixture fixture;
    private Texture texture;
    protected ActorData actorData;
    private ActorType actorType;
    private float radius;
    private float angle;
    private float rotationSpeed;
    private float moveSpeed;
    private Vector2 moveDirection = new Vector2();
    private Vector2 moveVelocity = new Vector2();
    private Vector2 moveAmount = new Vector2();
    private Vector2 moveForce = new Vector2();
    private boolean isMoving;
    private boolean isActive;
    private Sound killSound;
    private float damageGiven;
    private int scoreGiven;
    private float health;

    public EnemyActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup, ActorType actorType) {
        super(pos, world, actorIndex, collisionGroup);
        actorData = new ActorData(actorIndex, collisionGroup);
        this.actorType = actorType;
        moveSpeed = 0.03f;
        texture = new Texture(Gdx.files.internal("asteroidSmall1.png"));
        width = 0.28f;
        height = 0.28f;
        scoreGiven = 10;
        moveSpeed = moveSpeed * 1.2f;
        damageGiven = 5f;
        health = 1f;
        angle = 0;
        rotationSpeed = 0.5f;
        radius = width / 2f;
        setTexture(texture);
        setBounds(pos.x, pos.y, width, height);
        worldPos.set(pos.x, pos.y);
        isMoving = false;
        //createBody(world, pos, this.angle, 0, 0, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
        setVisible(false);
        setIsActive(true);
    }

    @Override
    public Body createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.angle = angle;
        setBody(world.createBody(bodyDef));
        getBody().setUserData(actorData);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        fixture = getBody().createFixture(fixtureDef);
        shape.dispose();

        return getBody();
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(texture, worldPos.x - width / 2, worldPos.y - height / 2, this.width / 2, this.height / 2, this.width, this.height,
                this.getScaleX(), this.getScaleY(), this.getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        //restrictToWorld();
        updateWorldPos();
        setPosition(worldPos.x, worldPos.y);
        //setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    public void prepareMove(float moveDirectionX, float moveDirectionY, float moveSpeed) {
        moveDirection.set(moveDirectionX - getX(), moveDirectionY - getY()).nor();
        moveVelocity.set(moveDirection.scl(moveSpeed));
        moveForce.set(moveVelocity.scl(Asteroidia.STEP));
    }

    @Override
    public void move() {
        if (isMoving) {
            getBody().applyForceToCenter(moveForce, true);
            getBody().setLinearVelocity(moveVelocity);
            getBody().setAngularVelocity(rotationSpeed);
        }
        else {
            float moveDirectionX = MathUtils.random(Asteroidia.WIDTH);
            float moveDirectionY = MathUtils.random(Asteroidia.HEIGHT);
            prepareMove(moveDirectionX, moveDirectionY, moveSpeed);
            isMoving = true;
        }
    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            isActive = false;
            isMoving = false;
            world.destroyBody(getBody());
            killSound.play();
            System.out.println("Destroyed " + actorData.actorIndex + ", Enemy");
            remove();
        }
    }

    @Override
    public void updateWorldPos() {
        worldPos.set(body.getPosition().x, body.getPosition().y);
    }


    /*/////////////////
    Getters and Setters
    *//////////////////

    public void setIndex(int newIndex){
        actorData.setInfo(newIndex, actorData.getCollisionGroup());
        body.setUserData(actorData);
    }

    public int getScoreGiven() {
        return scoreGiven;
    }

    public float getDamageGiven() {
        return damageGiven;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public ActorData getActorData() {
        return actorData;
    }

    public Vector2 getWorldPos() {
        return worldPos;
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public ActorType getActorType() {
        return actorType;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}

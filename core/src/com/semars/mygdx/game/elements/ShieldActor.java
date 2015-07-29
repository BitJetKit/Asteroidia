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

/**
 * Created by semar on 7/29/15.
 */
public class ShieldActor extends SpaceActor {
    private Body body;
    private Fixture fixture;
    private Texture texture;
    private ActorData actorData;
    private float width;
    private float height;
    private float radius;
    private float angle;
    private float moveSpeed;
    private Vector2 worldPos = new Vector2();
    private Vector2 moveTarget = new Vector2();
    private Vector2 moveDirection = new Vector2();
    private Vector2 moveVelocity = new Vector2();
    private Vector2 moveAmount = new Vector2();
    private Vector2 moveForce = new Vector2();
    private SpaceActor shieldTarget;
    private boolean isMoving;
    private boolean isActive;
    private Sound killSound = Gdx.audio.newSound(Gdx.files.internal("boom.mp3"));

    public ShieldActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup, SpaceActor shieldTarget) {
        super(pos, world, actorIndex, collisionGroup);
        actorData = new ActorData(actorIndex, collisionGroup);
        texture = new Texture(Gdx.files.internal("shield3.png"));
        width = 1.14f;
        height = 1.08f;
        angle = 0;
        radius = width / 2f;
        setBounds(pos.x, pos.y, width, height);
        worldPos.set(pos.x, pos.y);
        isMoving = false;
        setVisible(false);
        createBody(world, pos, this.angle, 0, 0, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
        setIsActive(true);
        this.shieldTarget = shieldTarget;
    }

    @Override
    public void createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        body.setUserData(actorData);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
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
        updateWorldPos();
        setPosition(worldPos.x, worldPos.y);
    }

    @Override
    public void move() {
        moveTarget.set(shieldTarget.getWorldPos());
        body.setTransform(moveTarget, this.angle);
    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            isActive = false;
            world.destroyBody(body);
            killSound.play();
            isMoving = false;
            System.out.println("Destroyed " + actorData.actorIndex + ", Shield");
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
}

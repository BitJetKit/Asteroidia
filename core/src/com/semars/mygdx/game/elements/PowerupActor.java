package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by semar on 7/24/15.
 */
public class PowerupActor extends SpaceActor {
    private Body body;
    private Fixture fixture;
    private Texture texture;
    private ActorData actorData;
    private float width;
    private float height;
    private float radius;
    private float angle;
    private float rotationSpeed;
    private Vector2 worldPos = new Vector2();
    private boolean isMoving;
    private boolean isActive;
    private Sound killSound;
    private ActorType actorType;

    public PowerupActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup, ActorType actorType) {
        super(pos, world, actorIndex, collisionGroup);
        actorData = new ActorData(actorIndex, collisionGroup);
        this.actorType = actorType;
        switch (this.actorType) {
            case POWERUP_STAR: {
                texture = new Texture(Gdx.files.internal("powerupYellow_star.png"));
                width = 0.75f;
                height = 0.75f;
                break;
            }
            case PLAYER_SHIELD: {
                texture = new Texture(Gdx.files.internal("powerupBlue_shield.png"));
                width = 0.75f;
                height = 0.75f;
                break;
            }
            case POWERUP_BOLT: {
                texture = new Texture(Gdx.files.internal("powerupRed_bolt.png"));
                width = 0.75f;
                height = 0.75f;
                break;
            }
            case POWERUP_BOMB: {
                texture = new Texture(Gdx.files.internal("pill_green.png"));
                width = 0.30f;
                height = 0.30f;
                break;
            }
        }
        //texture = new Texture(Gdx.files.internal("pill_green.png"));
        angle = 0;
        rotationSpeed = 0;
        this.radius = width / 2;
        setBounds(pos.x, pos.y, width, height);
        worldPos.set(pos.x, pos.y);
        isMoving = false;
        createBody(world, pos, this.angle, 0, 0, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
        setIsActive(true);
        killSound = Gdx.audio.newSound(Gdx.files.internal("sfx_shieldUp.ogg"));
    }

    @Override
    public void createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.angle = angle;
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);
        body.setUserData(actorData);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(texture, worldPos.x - width / 2, worldPos.y - height / 2, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        //restrictToWorld();
        updateWorldPos();
    }

    @Override
    public void move() {

    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            isActive = false;
            isMoving = false;
            world.destroyBody(body);
            killSound.play();
            remove();
        }
    }

    public void giveEffect(PlayerActor playerActor, ActorType actorType) {
        switch (actorType) {
            case POWERUP_STAR: {
                int playerScore = playerActor.getScore();
                int scoreGiven = 100;
                playerActor.setScore(playerScore += scoreGiven);
                break;
            }
            case POWERUP_SHIELD: {
                playerActor.getShield().setHealth(100f);
                break;
            }
            case POWERUP_BOLT: {
                playerActor.doubleGuns();
                break;
            }
            case POWERUP_BOMB: {

                break;
            }
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

    public ActorType getActorType() {
        return actorType;
    }
}
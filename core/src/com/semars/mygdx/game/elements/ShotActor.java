package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/13/15.
 */
public class ShotActor extends SpaceActor {
    private Body body;
    private Fixture fixture;
    private ActorData actorData;
    private Texture texture;
    private float width;
    private float height;
    private Vector2 worldPos = new Vector2();
    private float angle;
    private float moveSpeed;
    private float lifeTime;
    private float damage;
    private boolean isActive;
    private World world;
    private ShotType shotType;

    public ShotActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup, ShotType shotType) {
        super(pos, world, actorIndex, collisionGroup);
        actorData = new ActorData(actorIndex, collisionGroup);
        this.shotType = shotType;
        switch (this.shotType) {
            case BULLET: {
                texture = new Texture(Gdx.files.internal("bulletBlue.png"));
                width = 0.08f;
                height = 0.12f;
                damage = 1f;
                break;
            }
            case LASER: {
                texture = new Texture(Gdx.files.internal("shotBlue2.png"));
                width = 0.10f;
                height = 0.28f;
                damage = 2f;
                break;
            }
        }
        this.angle = angle;
        moveSpeed = 0.05f;
        lifeTime = 0;
        this.world = world;

        setBounds(pos.x, pos.y, width, height);
        createBody(world, pos, this.angle, 0, 0, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
        worldPos.set(pos);
        isActive = true;
    }

    public enum ShotType {
        BULLET, LASER
    }

    @Override
    public void createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        body.setUserData(actorData);
        body.setBullet(true);

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
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, worldPos.x, worldPos.y, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        updateWorldPos();
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        if (lifeTime > 3f) {
            Asteroidia.actorManager.deleteActor(this);
        }
        lifeTime += delta;
    }

    @Override
    public void move() {
        body.applyLinearImpulse(0, moveSpeed, body.getWorldCenter().x, body.getWorldCenter().y, true);
        //body.applyForceToCenter(0, moveSpeed, true);

        //worldPos.y += Math.sin(angle) * moveSpeed * Gdx.graphics.getDeltaTime();

        //setPosition(worldPos.x, worldPos.y);
    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            isActive = false;
            world.destroyBody(body);
            System.out.println("Destroyed " + actorData.actorIndex + ", Shot");
            remove();
        }
    }

    @Override
    public void updateWorldPos() {
        worldPos.set(body.getPosition().x, body.getPosition().y);
    }

    /*@Override
    public void wrapEdge() {
        // Top
        x = getX();
        y = getY();
        if (y > Asteroidia.HEIGHT) {
            y = y - Asteroidia.HEIGHT - height;
        }
        // Right
        if (x > Asteroidia.WIDTH) {
            x = x - Asteroidia.WIDTH - width;
        }
        // Bottom
        if (y < -1 * height) {
            y = Asteroidia.HEIGHT - y - height;
        }
        // Left
        if (x < -1 * width) {
            x = Asteroidia.WIDTH - x - width;
        }
        setPosition(x, y);
        boundingRectangle.setPosition(x, y);
    }*/

    public void setIndex(int newIndex){
        actorData.setInfo(newIndex, actorData.getCollisionGroup());
        body.setUserData(actorData);
    }

    /*/////////////////
    Getters and Setters

    *//////////////////

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

    public Body getBody() {
        return body;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}

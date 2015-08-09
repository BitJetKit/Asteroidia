package com.semars.mygdx.game.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 6/27/15.
 */
public abstract class SpaceActor extends Actor {
    protected boolean isActive;
    private Body body;
    private ActorData actorData;
    private Vector2 worldPos;
    private Texture texture;

    public SpaceActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup) {
        actorData = new ActorData(actorIndex, collisionGroup);
        //worldPos = new Vector2();
        //createBody(world, pos, 0, 1f, 1f, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public abstract void move();

    public abstract void createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits);

    public void updateWorldPos(){
        worldPos.set(body.getPosition().x, body.getPosition().y);
    }

    public void randomizePosOutside() {
        switch (MathUtils.random(3)) {
            // Top
            case 0: {
                worldPos.y = Asteroidia.HEIGHT + getHeight();
                worldPos.x = MathUtils.random(-1 * getWidth(), Asteroidia.WIDTH + getWidth());
                setPosition(worldPos.x, worldPos.y);
                break;
            }
            // Right
            case 1: {
                worldPos.y = MathUtils.random(-1 * getHeight(), Asteroidia.HEIGHT + getHeight());
                worldPos.x = Asteroidia.WIDTH + getWidth();
                setPosition(worldPos.x, worldPos.y);
                break;
            }
            // Bottom
            case 2: {
                worldPos.y = -1 * getHeight();
                worldPos.x = MathUtils.random(-1 * getWidth(), Asteroidia.WIDTH + getWidth());
                setPosition(worldPos.x, worldPos.y);
                break;
            }
            // Left
            case 3: {
                worldPos.y = MathUtils.random(-1 * getHeight(), Asteroidia.HEIGHT + getHeight());
                worldPos.x = -1 * getWidth();
                setPosition(worldPos.x, worldPos.y);
                break;
            }
        }
    }

    public void destroy(World world) {
        if (isActive) {
            world.destroyBody(body);
            isActive = false;
            remove();
        }
    }

    public void setIndex(int newIndex){
        actorData.setInfo(newIndex, actorData.getCollisionGroup());
        body.setUserData(actorData);
    }

    public ActorData getActorData() {
        return actorData;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Vector2 getWorldPos() {
        return worldPos;
    }

    public Body getBody() {
        return body;
    }

    public Texture getTexture() {
        return texture;
    }
}
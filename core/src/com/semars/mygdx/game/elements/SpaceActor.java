package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
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
import com.semars.mygdx.game.screens.GameScreen;

/**
 * Created by semar on 6/27/15.
 */
public abstract class SpaceActor extends Actor {
    protected boolean isActive;
    private Body body;
    protected float height = 0;
    protected float width = 0;
    protected ActorData actorData;
    protected Vector2 worldPos = new Vector2(0,0);
    private Texture texture;
    protected int currentQuadrant;

    public SpaceActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup) {
        actorData = new ActorData(actorIndex, collisionGroup);
    }

    public abstract void move();

    public abstract Body createBody(World world, Vector2 pos, float angle, float density, float restitution, short categoryBits, short maskBits);

    public void updateWorldPos() {
        if (body != null) {
            worldPos.set(getBody().getPosition().x, getBody().getPosition().y);
        }
    }

    public int randomizeSpawnQuadrant() {
        int spawnQuadrant;
        int playerQuadrant = GameScreen.actorManager.getPlayerActor().getQuadrant();

        do { spawnQuadrant = MathUtils.random(3);
        } while (spawnQuadrant == playerQuadrant);

        return spawnQuadrant;
    }

    public void spawnAtQuadrant(int spawnQuadrant) {
        switch (spawnQuadrant) {
            // Top-Right
            case 0: {
                worldPos.y = Asteroidia.HEIGHT + height * 2;
                worldPos.x = MathUtils.random(-1 * width * 2, Asteroidia.WIDTH + width * 2);
                break;
            }
            // Top-Left
            case 1: {
                worldPos.y = MathUtils.random(-1 * height * 2, Asteroidia.HEIGHT + height * 2);
                worldPos.x = Asteroidia.WIDTH + width * 2;
                break;
            }
            // Bottom-Left
            case 2: {
                worldPos.y = -1 * height * 2;
                worldPos.x = MathUtils.random(-1 * width * 2, Asteroidia.WIDTH + width * 2);
                break;
            }
            // Bottom-Right
            case 3: {
                worldPos.y = MathUtils.random(-1 * height * 2, Asteroidia.HEIGHT + height * 2);
                worldPos.x = -1 * width * 2;
                break;
            }
        }
        if (getBody() != null) {
            getBody().setTransform(worldPos, 0);
        }
    }

    public void wrapEdge() {
        // Top
        if (worldPos.y - height > Asteroidia.HEIGHT) {
            worldPos.y = worldPos.y - Asteroidia.HEIGHT - height;
        }
        // Right
        if (worldPos.x - height > Asteroidia.WIDTH) {
            worldPos.x = worldPos.x - Asteroidia.WIDTH - width;
        }
        // Bottom
        if (worldPos.y < -1 * height) {
            worldPos.y = Asteroidia.HEIGHT - worldPos.y - height;
        }
        // Left
        if (worldPos.x < -1 * width) {
            worldPos.x = Asteroidia.WIDTH - worldPos.x - width;
        }
        if (getBody() != null) {
            getBody().setTransform(worldPos, getBody().getAngle());
        }
    }

    // ensure movement will not leave screen
    public void restrictToWorld(Body body) {
        // left
        if(worldPos.x  < 0) {
            worldPos.x = 0;
            body.setTransform(worldPos, body.getAngle());
        }
        // right
        if(worldPos.x > Asteroidia.WIDTH) {
            worldPos.x = Asteroidia.WIDTH;
            body.setTransform(worldPos, body.getAngle());
        }
        // bottom
        if(worldPos.y - height /2 < 0) {
            worldPos.y = 0 + height /2;
            body.setTransform(worldPos, body.getAngle());
        }
        // top
        if(worldPos.y - height /2 > Asteroidia.HEIGHT - height) {
            worldPos.y = Asteroidia.HEIGHT - height /2;
            body.setTransform(worldPos, body.getAngle());
        }
    }

    public void destroy(World world) {
        if (isActive) {
            if (getBody() != null) {
                world.destroyBody(getBody());
            }
            isActive = false;
            remove();
        }
    }

    /*/////////////////
    Getters and Setters
    *//////////////////

    public int getQuadrant() {
        // left
        if (worldPos.x < Asteroidia.WIDTH/2) {
            // bottom left
            if (worldPos.y < Asteroidia.WIDTH/2) {
                currentQuadrant = 2;
            }
            // top left
            else {
                currentQuadrant = 1;
            }
        }
        // right
        else {
            // bottom right
            if (worldPos.y < Asteroidia.WIDTH/2) {
                currentQuadrant = 3;
            }
            // top right
            else {
                currentQuadrant = 0;
            }
        }
        return currentQuadrant;
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

    public void setBody(Body body) {
        this.body = body;
    }

    public Texture getTexture() {
        return texture;
    }
}
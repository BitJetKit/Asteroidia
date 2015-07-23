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
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/13/15.
 */
public class ShotActor extends SpaceActor {
    private Body body;
    private Fixture fixture;
    private Texture texture;
    private int width;
    private int height;
    private Vector2 currentPos = new Vector2();
    private float angle;
    private float moveSpeed;
    private float lifeTime;
    private boolean isActive;
    private Rectangle boundingRectangle;
    private World world;

    public ShotActor(Vector2 pos, World world, int actorIndex, int collisionGroup, float angle) {
        super(pos, world, actorIndex, collisionGroup);
        texture = new Texture(Gdx.files.internal("laserBlue.png"));
        width = 80;
        height = 80;
        this.angle = angle;
        moveSpeed = 300f;
        lifeTime = 0;
        setSize(width, height);
        setX(pos.x);
        setY(pos.y);
        currentPos.set(pos);
        setRotation(angle);
        isActive = true;
        this.world = world;
        boundingRectangle = new Rectangle(pos.x, pos.y, width, height);
    }

    @Override
    public void createBody(World world, Vector2 pos, float angle, float density, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x / Asteroidia.CONVERT_TO_METERS, pos.y/Asteroidia.CONVERT_TO_METERS);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        body.setBullet(true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() * Asteroidia.CONVERT_TO_METERS / 2, getHeight() * Asteroidia.CONVERT_TO_METERS / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        updateWorldPos();
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        //wrapEdge();
        if (lifeTime > 3f) {
            destroy(world);
        }
        lifeTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), width, height);
    }

    @Override
    public void move() {
        currentPos.x = getX();
        currentPos.y = getY();
        currentPos.y += Math.sin(angle) * moveSpeed * Gdx.graphics.getDeltaTime();

        setPosition(currentPos.x, currentPos.y);
        boundingRectangle.setPosition(currentPos.x, currentPos.y);
    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            world.destroyBody(body);
            isActive = false;
        }
    }

    public boolean checkCollision(Rectangle boundingRectangle) {
        if (boundingRectangle.getX() < this.getBoundingRectangle().x || boundingRectangle.getY() < this.getBoundingRectangle().y) {
            return Intersector.overlaps(boundingRectangle, this.boundingRectangle);
        }
        return false;
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

    // Getters and Setters

    public Vector2 getCurrentPos() {
        return currentPos;
    }

    public boolean isActive() {
        return isActive;
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public Texture getTexture() {
        return texture;
    }
}

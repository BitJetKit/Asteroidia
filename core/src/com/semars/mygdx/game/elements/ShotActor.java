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
    private float x;
    private float y;
    private Vector2 currentPos = new Vector2();
    private float angle;
    private float moveSpeed;
    private float lifeTime;
    private Rectangle boundingRectangle;

    private boolean isDestroyed;

    public ShotActor(float x, float y, float angle) {
        texture = new Texture(Gdx.files.internal("laserBlue.png"));
        width = 4;
        height = 14;
        this.angle = angle;
        moveSpeed = 300f;
        lifeTime = 0;
        setSize(width, height);
        setX(x);
        setY(y);
        currentPos.set(x, y);
        setRotation(angle);
        boundingRectangle = new Rectangle(x, y, width, height);
    }

    @Override
    public void createBody(World world, Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x/Asteroidia.CONVERSION, pos.y/Asteroidia.CONVERSION);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        body.isBullet();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / Asteroidia.CONVERSION / 2, getHeight() / Asteroidia.CONVERSION / 2);
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
        //wrapEdge();
        if (lifeTime > 3f) {
            destroy();
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
    public void destroy() {
        setVisible(false);
        isDestroyed = true;
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

    public Vector2 getCurrentPos() {
        return currentPos;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public Texture getTexture() {
        return texture;
    }
}

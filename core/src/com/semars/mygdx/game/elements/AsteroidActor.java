package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/9/15.
 */
public class AsteroidActor extends SpaceActor implements Wrappable {

    private Body body;
    private Fixture fixture;
    private Texture texture;
    private int width;
    private int height;
    private float radius;
    private float angle;
    private Rectangle boundingRectangle = new Rectangle();
    private Vector2 currentPos = new Vector2();
    private Vector2 moveDirection = new Vector2();
    private Vector2 moveVelocity = new Vector2();
    private Vector2 moveAmount = new Vector2();
    private float moveSpeed;
    private boolean isMoving;
    private boolean isActive;
    private Sound killSound = Gdx.audio.newSound(Gdx.files.internal("boom.mp3"));

    public AsteroidActor() {
        width = 42;
        height = 42;
        angle = 90;
        texture = new Texture(Gdx.files.internal("asteroidMed1.png"));
        setBounds(-1 * width, -1 * height, width, height);
        boundingRectangle.set(getX(), getY(), getWidth(), getHeight());
        currentPos.set(getX(), getY());
        moveSpeed = 15f;
        isMoving = false;
        setVisible(false);
    }

    @Override
    public void createBody(World world, Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x/Asteroidia.CONVERSION, pos.y/Asteroidia.CONVERSION);
        bodyDef.angle = angle;
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / Asteroidia.CONVERSION / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        move();
        wrapEdge();
    }

    public void prepareMove() {
        float moveDirectionX = MathUtils.random(Asteroidia.WIDTH);
        float moveDirectionY = MathUtils.random(Asteroidia.HEIGHT);
        moveDirection.set(moveDirectionX - getX(), moveDirectionY - getY()).nor();
        moveVelocity.set(moveDirection.scl(moveSpeed));
        moveAmount.set(moveVelocity.scl(Gdx.graphics.getDeltaTime()));
    }

    @Override
    public void move() {
        if (isMoving) {
            currentPos.add(moveAmount);
            setX(currentPos.x);
            setY(currentPos.y);
            boundingRectangle.setPosition(currentPos.x, currentPos.y);
        }
        else {
            prepareMove();
            isMoving = true;
        }
    }

    public boolean checkCollision(Rectangle boundingRectangle) {
        if (boundingRectangle.getX() < this.getBoundingRectangle().x || boundingRectangle.getY() < this.getBoundingRectangle().y) {
            return Intersector.overlaps(boundingRectangle, this.boundingRectangle);
        }
        return false;
    }

    @Override
    public void destroy(World world) {
        if (isActive) {
            world.destroyBody(body);
            isActive = false;
            killSound.play();
            isMoving = false;
        }
    }

    public void randomizePosOutside() {
        switch (MathUtils.random(3)) {
            // Top
            case 0: {
                currentPos.y = Asteroidia.HEIGHT + height;
                currentPos.x = MathUtils.random(-1 * width, Asteroidia.WIDTH + width);
                setPosition(currentPos.x, currentPos.y);
                boundingRectangle.setPosition(currentPos.x, currentPos.y);
                break;
            }
            // Right
            case 1: {
                currentPos.y = MathUtils.random(-1 * height, Asteroidia.HEIGHT + height);
                currentPos.x = Asteroidia.WIDTH + width;
                setPosition(currentPos.x, currentPos.y);
                boundingRectangle.setPosition(getX(), getY());
                break;
            }
            // Bottom
            case 2: {
                currentPos.y = -1 * getHeight();
                currentPos.x = MathUtils.random(-1 * width, Asteroidia.WIDTH + width);
                setPosition(currentPos.x, currentPos.y);
                boundingRectangle.setPosition(getX(), getY());
                break;
            }
            // Left
            case 3: {
                currentPos.y = MathUtils.random(-1 * height, Asteroidia.HEIGHT + height);
                currentPos.x = -1 * width;
                setPosition(currentPos.x, currentPos.y);
                boundingRectangle.setPosition(getX(), getY());
                break;
            }
        }
    }

    public void wrapEdge() {
        // Top
        if (currentPos.y > Asteroidia.HEIGHT) {
            currentPos.y = currentPos.y - Asteroidia.HEIGHT - height;
        }
        // Right
        if (currentPos.x > Asteroidia.WIDTH) {
            currentPos.x = currentPos.x - Asteroidia.WIDTH - width;
        }
        // Bottom
        if (currentPos.y < -1 * height) {
            currentPos.y = Asteroidia.HEIGHT - currentPos.y - height;
        }
        // Left
        if (currentPos.x < -1 * width) {
            currentPos.x = Asteroidia.WIDTH - currentPos.x - width;
        }
        setPosition(currentPos.x, currentPos.y);
        boundingRectangle.setPosition(getX(), getY());
    }

    public Vector2 getCurrentPos() {
        return currentPos;
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public Texture getTexture() {
        return texture;
    }
}

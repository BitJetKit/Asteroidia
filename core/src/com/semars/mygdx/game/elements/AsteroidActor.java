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
import com.semars.mygdx.game.ActorManager;
import com.semars.mygdx.game.Asteroidia;
import com.semars.mygdx.game.screens.GameScreen;

/**
 * Created by semar on 7/9/15.
 */
public class AsteroidActor extends EnemyActor implements Wrappable {

    private ActorManager actorManager;
    private Body body;
    private Fixture fixture;
    private Texture texture;
    private ActorData actorData;
    private float width;
    private float height;
    private float radius;
    private float angle;
    private float rotationSpeed;
    private float moveSpeed;
    private Vector2 worldPos = new Vector2();
    private Vector2 moveDirection = new Vector2();
    private Vector2 moveVelocity = new Vector2();
    private Vector2 moveAmount = new Vector2();
    private Vector2 moveForce = new Vector2();
    private boolean isMoving;
    private boolean isActive;
    private Sound killSound;
    private ActorType actorType;
    private float damageGiven;
    private int scoreGiven;
    private float health;

    public AsteroidActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup, ActorType actorType) {
        super(pos, world, actorIndex, collisionGroup, actorType);
        actorManager = GameScreen.actorManager;
        actorData = new ActorData(actorIndex, collisionGroup);
        this.actorType = actorType;
        moveSpeed = 0.03f;
        switch (this.actorType) {
            case ENEMY_ASTEROID_SMALL: {
                texture = new Texture(Gdx.files.internal("asteroidSmall1.png"));
                width = 0.28f;
                height = 0.28f;
                scoreGiven = 10;
                moveSpeed = moveSpeed * 1.2f;
                damageGiven = 5f;
                health = 1f;
                break;
            }
            case ENEMY_ASTEROID_MEDIUM: {
                texture = new Texture(Gdx.files.internal("asteroidMed1.png"));
                width = 0.84f;
                height = 0.84f;
                scoreGiven = 25;
                damageGiven = 10f;
                health = 3f;
                break;
            }
            case ENEMY_ASTEROID_LARGE: {
                texture = new Texture(Gdx.files.internal("asteroidLarge3.png"));
                width = 1.80f;
                height = 1.66f;
                scoreGiven = 50;
                moveSpeed = moveSpeed / 1.5f;
                damageGiven = 20f;
                health = 10f;
                break;
            }
            default: {
                texture = new Texture(Gdx.files.internal("asteroidMed1.png"));
                width = 0.84f;
                height = 0.84f;
                scoreGiven = 25;
                damageGiven = 10f;
                health = 3f;
                break;
            }
        }
        killSound = Gdx.audio.newSound(Gdx.files.internal("boom.mp3"));
        angle = 0;
        rotationSpeed = 0.5f;
        radius = width / 2f;
        setBounds(pos.x, pos.y, width, height);
        worldPos.set(pos.x, pos.y);
        isMoving = false;
        setVisible(false);
        createBody(world, pos, angle, 0, 0, collisionGroup.getCategoryBits(), collisionGroup.getMaskBits());
        setIsActive(true);
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
        //restrictToWorld();
        updateWorldPos();
        wrapEdge();
        setPosition(worldPos.x, worldPos.y);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    public void prepareMove(float moveDirectionX, float moveDirectionY, float moveSpeed) {
        moveDirection.set(moveDirectionX - getX(), moveDirectionY - getY()).nor();
        moveVelocity.set(moveDirection.scl(moveSpeed));
        moveForce.set(moveVelocity.scl(Asteroidia.STEP));
    }

    @Override
    public void move() {
        if (isMoving) {
            body.applyForceToCenter(moveForce, true);
            body.setLinearVelocity(moveVelocity);
            body.setAngularVelocity(rotationSpeed);
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
            world.destroyBody(body);
            killSound.play();
            System.out.println("Destroyed " + actorData.actorIndex + ", Asteroid");
            remove();
        }
    }

    public void wrapEdge() {
        // Top
        if (worldPos.y - height/2 > Asteroidia.HEIGHT) {
            worldPos.y = worldPos.y - Asteroidia.HEIGHT - height/2;
        }
        // Right
        if (worldPos.x - height/2 > Asteroidia.WIDTH) {
            worldPos.x = worldPos.x - Asteroidia.WIDTH - width/2;
        }
        // Bottom
        if (worldPos.y < -1 * height/2) {
            worldPos.y = Asteroidia.HEIGHT - worldPos.y - height/2;
        }
        // Left
        if (worldPos.x < -1 * width/2) {
            worldPos.x = Asteroidia.WIDTH - worldPos.x - width/2;
        }
        body.setTransform(worldPos, body.getAngle());
    }

    @Override
    public void updateWorldPos() {
        worldPos.set(body.getPosition().x, body.getPosition().y);
    }

    // TO-DO : make sure spawn does not occur near player
    public void randomizePosOutside() {
        int spawnPos = 0;
        // check player quadrant
        if (actorManager.getPlayerActor().getWorldPos().x < Asteroidia.WIDTH/2) {
            // bottom left quadrant
            if (actorManager.getPlayerActor().getWorldPos().y < Asteroidia.WIDTH/2) {
                spawnPos = 0;
            }
            // top right quadrant
            else {
                spawnPos = 2;
            }
        }
        else if (actorManager.getPlayerActor().getWorldPos().x > Asteroidia.WIDTH/2) {
            // bottom right quadrant
            if (actorManager.getPlayerActor().getWorldPos().y < Asteroidia.WIDTH/2) {
                spawnPos = 0;
            }
            // top left quadrant
            else {
                spawnPos = 1;
            }
        }
        else {
           spawnPos = MathUtils.random(2);
        }
        switch (spawnPos) {
            // Top
            case 0: {
                worldPos.y = Asteroidia.HEIGHT + height;
                worldPos.x = MathUtils.random(-1 * width, Asteroidia.WIDTH + width);
                break;
            }
            // Right
            case 1: {
                worldPos.y = MathUtils.random(-1 * height, Asteroidia.HEIGHT + height);
                worldPos.x = Asteroidia.WIDTH + width;
                break;
            }
            // Bottom
            /*case 2: {
                worldPos.y = -1 * height;
                worldPos.x = MathUtils.random(-1 * width, Asteroidia.WIDTH + width);
                break;
            }*/
            // Left
            case 2: {
                worldPos.y = MathUtils.random(-1 * height, Asteroidia.HEIGHT + height);
                worldPos.x = -1 * width;
                break;
            }
        }
        body.setTransform(worldPos, angle);
        float moveDirectionX = worldPos.x;
        float moveDirectionY = worldPos.y;
        prepareMove(moveDirectionX, moveDirectionY, moveSpeed);
        System.out.println(" ASTEROID SPAWN: " + spawnPos);
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
}

package com.semars.mygdx.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by semar on 7/9/15.
 */
public abstract class EnemyActor extends SpaceActor {

    private Texture texture;
    private int width = 42;
    private int height = 42;
    private Vector2 currentPos = new Vector2();
    public boolean isMoving;
    public Vector2 moveDirection = new Vector2();
    public Vector2 moveVelocity = new Vector2();
    public Vector2 moveAmount = new Vector2();
    public float moveDistance;

    public EnemyActor(Vector2 pos, World world, int actorIndex, CollisionGroup collisionGroup) {
        super(pos, world, actorIndex, collisionGroup);
        texture = new Texture(Gdx.files.internal("asteroidMed1.png"));
        setBounds(50, 50, width, height);
        currentPos.set(getX(), getY());
        isMoving = false;
    }

    @Override
    public void move() {

    }
}

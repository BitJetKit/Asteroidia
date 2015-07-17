package com.semars.mygdx.game.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 6/27/15.
 */
public abstract class SpaceActor extends Actor {

    public abstract void move();

    public abstract void destroy(World world);

    public abstract void createBody(World world,Vector2 pos);
}

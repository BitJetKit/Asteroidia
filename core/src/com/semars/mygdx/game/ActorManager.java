package com.semars.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.elements.ActorType;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.ShotActor;
import com.semars.mygdx.game.elements.SpaceActor;

import java.util.ArrayList;

/**
 * Created by semar on 7/16/15.
 */
public class ActorManager {

    private ArrayList<SpaceActor> actors;
    private ArrayList<SpaceActor> deleteList;
    private World world;
    private Stage stage;

    public ActorManager(Vector2 gravity){
        world = new World(gravity, true);
        actors = new ArrayList<SpaceActor>();
        deleteList = new ArrayList<SpaceActor>();
        stage = new Stage(new FitViewport(Asteroidia.WIDTH, Asteroidia.HEIGHT));
    }

    public void update(float delta) {
        // replace actor with last element to preserve order, then remove last element
        for (SpaceActor toBeDeleted : deleteList) {
            int index = toBeDeleted.getActorData().getActorIndex();
            SpaceActor replacement = deleteList.get(deleteList.size()-1);
            replacement.setIndex(index);
            actors.set(index, replacement);
            actors.remove(deleteList.size()-1);
        }
        deleteList.clear();
    }

    // Instantiate actor at given position in world, set index as last item in actors array
    public PlayerActor addPlayerActor(Vector2 pos, ActorType actorType, int collisionGroup, float angle) {
        PlayerActor playerActor = new PlayerActor(pos, world, actors.size(), collisionGroup);
        actors.add(playerActor);
        stage.addActor(playerActor);
        return playerActor;
    }

    public AsteroidActor addAsteroidActor(Vector2 pos, ActorType actorType, int collisionGroup, float angle) {
        AsteroidActor asteroidActor = new AsteroidActor(pos, world, actors.size(), collisionGroup);
        actors.add(asteroidActor);
        stage.addActor(asteroidActor);
        asteroidActor.randomizePosOutside();
        asteroidActor.setVisible(true);
        return asteroidActor;
    }

    public ShotActor addShotActor(Vector2 pos, ActorType actorType, int collisionGroup, float angle) {
        ShotActor shotActor = new ShotActor(pos, world, actors.size(), collisionGroup, angle);
        actors.add(shotActor);
        stage.addActor(shotActor);
        return shotActor;
    }

    public void deleteActor(SpaceActor actor) {
        deleteList.add(actor);
    }

    public World getWorld() {
        return world;
    }

    public Stage getStage() {
        return stage;
    }
}

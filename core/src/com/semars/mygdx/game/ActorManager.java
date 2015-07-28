package com.semars.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.elements.ActorData;
import com.semars.mygdx.game.elements.ActorType;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.CollisionGroup;
import com.semars.mygdx.game.elements.Gun;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.PowerupActor;
import com.semars.mygdx.game.elements.ShotActor;
import com.semars.mygdx.game.elements.SpaceActor;

import java.util.ArrayList;

/**
 * Created by semar on 7/16/15.
 */
public class ActorManager implements ContactListener {

    private ArrayList<SpaceActor> actors;
    private ArrayList<SpaceActor> deleteList;
    private Array<AsteroidActor> asteroidActorArray;
    private World world;
    private Stage stage;
    private PlayerActor playerActor;
    private int score;

    public ActorManager(Vector2 gravity) {
        actors = new ArrayList<SpaceActor>();
        deleteList = new ArrayList<SpaceActor>();
        asteroidActorArray = new Array<AsteroidActor>();
        world = new World(gravity, true);
        world.setContactListener(this);
        stage = new Stage(new FitViewport(Asteroidia.WIDTH, Asteroidia.HEIGHT));
    }

    public void update(float delta) {
        for (int i=0; i< actors.size(); i++) {
            System.out.print(actors.get(i).getActorData().getActorIndex() + ": " + actors.get(i).getActorData().getCollisionGroup() + ", ");
        }
        System.out.print("\n");

        // replace actor with last element to preserve order, then remove last element
        System.out.println("DeleteList: " + deleteList);
        for (SpaceActor toBeDeleted : deleteList) {
            System.out.println("TBD: " + toBeDeleted + ", " + toBeDeleted.getActorData().getActorIndex());
            if (toBeDeleted.isActive()) {
                int index = toBeDeleted.getActorData().getActorIndex();
                SpaceActor replacement = actors.get(actors.size() - 1);
                System.out.println("\n replacing: " + index + ", " + toBeDeleted + " with: " + replacement.getActorData().getActorIndex() + ", " + replacement);
                replacement.setIndex(index);
                actors.set(index, replacement);
                actors.remove(actors.size() - 1);
                toBeDeleted.destroy(world);
                System.out.println("\n deleting: " + index + " " + toBeDeleted);
            }
        }
        deleteList.clear();
    }

    public void deleteActor(SpaceActor actor) {
        System.out.println(actor.getActorData().getActorIndex() + ", " + actor + " added to delete List");
        deleteList.add(actor);
    }

    // Instantiate actor at given position in world, set index as last item in actors array
    public PlayerActor addPlayerActor(Vector2 pos, ActorType actorType, CollisionGroup collisionGroup, float angle) {
        playerActor = new PlayerActor(pos, world, actors.size(), collisionGroup);
        actors.add(playerActor);
        stage.addActor(playerActor);
        return playerActor;
    }

    public AsteroidActor addAsteroidActor(Vector2 pos, ActorType actorType, CollisionGroup collisionGroup, float angle, AsteroidActor.AsteroidType asteroidType) {
        AsteroidActor asteroidActor = new AsteroidActor(pos, world, actors.size(), collisionGroup, asteroidType);
        actors.add(asteroidActor);
        stage.addActor(asteroidActor);
        asteroidActor.randomizePosOutside();
        asteroidActor.setVisible(true);
        asteroidActorArray.add(asteroidActor);
        return asteroidActor;
    }

    public ShotActor addShotActor(Vector2 pos, ActorType actorType, CollisionGroup collisionGroup, float angle, ShotActor.ShotType shotType) {
        ShotActor shotActor = new ShotActor(pos, world, actors.size(), collisionGroup, shotType);
        actors.add(shotActor);
        stage.addActor(shotActor);
        return shotActor;
    }

    public PowerupActor addPowerupActor(Vector2 pos, ActorType actorType, CollisionGroup collisionGroup, PowerupActor.PowerupType powerupType) {
        PowerupActor powerupActor = new PowerupActor(pos, world, actors.size(), collisionGroup, powerupType);
        actors.add(powerupActor);
        stage.addActor(powerupActor);
        return powerupActor;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Body bodyA = fixtureA.getBody();
        ActorData dataA = (ActorData) bodyA.getUserData();

        Fixture fixtureB = contact.getFixtureB();
        Body bodyB = fixtureB.getBody();
        ActorData dataB = (ActorData) bodyB.getUserData();

        // process collisions based on participants
        if (dataA.getCollisionGroup() == CollisionGroup.PLAYER && dataB.getCollisionGroup() == CollisionGroup.ENEMY) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionPlayerAndEnemy(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.ENEMY && dataB.getCollisionGroup() == CollisionGroup.PLAYER) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionPlayerAndEnemy(dataB, dataA);
        }

        if (dataA.getCollisionGroup() == CollisionGroup.PLAYER_SHOT && dataB.getCollisionGroup() == CollisionGroup.ENEMY) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionPlayer_ShotAndEnemy(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.ENEMY && dataB.getCollisionGroup() == CollisionGroup.PLAYER_SHOT) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionPlayer_ShotAndEnemy(dataB, dataA);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.PLAYER && dataB.getCollisionGroup() == CollisionGroup.POWERUP) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionPlayerAndPowerup(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.POWERUP && dataB.getCollisionGroup() == CollisionGroup.PLAYER) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionPlayerAndPowerup(dataB, dataA);
        }
    }

    void handleCollisionPlayerAndEnemy(ActorData dataPlayer, ActorData dataEnemy) {
        System.out.println("Player" + dataPlayer.getActorIndex() + " and Asteroid " + dataEnemy.getActorIndex());
        PlayerActor playerActor = (PlayerActor) actors.get(dataPlayer.getActorIndex());
        AsteroidActor asteroidActor = (AsteroidActor) actors.get(dataEnemy.getActorIndex());
        if (playerActor.isActive() && asteroidActor.isActive()) {
            deleteActor(playerActor);
            deleteActor(asteroidActor);
            asteroidActorArray.removeValue(asteroidActor, true);
        }
    }

    void handleCollisionPlayer_ShotAndEnemy(ActorData dataPlayer_Shot, ActorData dataEnemy) {
        System.out.println(dataPlayer_Shot.getActorIndex() + ", Shot" + " and " + dataEnemy.getActorIndex() + ", Asteroid");
        ShotActor shotActor = (ShotActor) actors.get(dataPlayer_Shot.getActorIndex());
        AsteroidActor asteroidActor = (AsteroidActor) actors.get(dataEnemy.getActorIndex());
        if (shotActor.isActive() && asteroidActor.isActive()) {
            deleteActor(shotActor);
            deleteActor(asteroidActor);
            asteroidActorArray.removeValue(asteroidActor, true);
            score += asteroidActor.getScoreGiven();
            System.out.println("Score: " + score);
        }
    }
    void handleCollisionPlayerAndPowerup(ActorData dataPlayer, ActorData dataPowerup) {
        System.out.println(dataPlayer.getActorIndex() + ", Player" + " and " + dataPowerup.getActorIndex() + ", Powerup");
        PlayerActor playerActor = (PlayerActor) actors.get(dataPlayer.getActorIndex());
        PowerupActor powerupActor = (PowerupActor) actors.get(dataPowerup.getActorIndex());
        if (playerActor.isActive() && powerupActor.isActive()) {
            deleteActor(powerupActor);
            powerupActor.giveEffect(playerActor, powerupActor.getPowerupType());
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    /*/////////////////
    Getters and Setters
    *//////////////////

    public World getWorld() {
        return world;
    }

    public Stage getStage() {
        return stage;
    }

    public Array<AsteroidActor> getAsteroidActorArray() {
        return asteroidActorArray;
    }

    public PlayerActor getPlayerActor() {
        return playerActor;
    }
}

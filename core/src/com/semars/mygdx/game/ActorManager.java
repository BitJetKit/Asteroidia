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
import com.semars.mygdx.game.elements.EnemyActor;
import com.semars.mygdx.game.elements.EnemyShipActor;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.PowerupActor;
import com.semars.mygdx.game.elements.ShieldActor;
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
    private Array<EnemyActor> enemyActorArray;
    private World world;
    private Stage stage;
    private PlayerActor playerActor;
    private int score;

    public ActorManager(Vector2 gravity) {
        actors = new ArrayList<SpaceActor>();
        deleteList = new ArrayList<SpaceActor>();
        asteroidActorArray = new Array<AsteroidActor>();
        enemyActorArray = new Array<EnemyActor>();

        stage = new Stage(new FitViewport(Asteroidia.WIDTH, Asteroidia.HEIGHT));

        // create Box2D world and set ActorManager to listen for collisions
        world = new World(gravity, true);
        world.setContactListener(this);
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
        if (actor.getClass() == playerActor.getClass()) {

        }
    }

    // instantiate actor at given position in world, set index as last item in actors array
    public PlayerActor addPlayerActor(Vector2 pos, ActorType actorType, CollisionGroup collisionGroup, float angle) {
        playerActor = new PlayerActor(pos, world, actors.size(), collisionGroup);
        actors.add(playerActor);
        stage.addActor(playerActor);
        ShieldActor shield = addShieldActor(playerActor.getWorldPos(), CollisionGroup.SHIELD, playerActor, ActorType.PLAYER_SHIELD);
        playerActor.setShield(shield);
        return playerActor;
    }
    public AsteroidActor addAsteroidActor(Vector2 pos, CollisionGroup collisionGroup, float angle, ActorType actorType) {
        AsteroidActor asteroidActor = new AsteroidActor(pos, world, actors.size(), collisionGroup, actorType);
        actors.add(asteroidActor);
        stage.addActor(asteroidActor);
        asteroidActorArray.add(asteroidActor);
        return asteroidActor;
    }
    public EnemyShipActor addEnemyShipActor(Vector2 pos, CollisionGroup collisionGroup, ActorType actorType) {
        EnemyShipActor enemyShipActor = new EnemyShipActor(pos, world, actors.size(), collisionGroup, actorType);
        enemyShipActor.setVisible(true);
        actors.add(enemyShipActor);
        stage.addActor(enemyShipActor);;
        enemyActorArray.add(enemyShipActor);
        return enemyShipActor;
    }
    public ShotActor addShotActor(Vector2 pos, ActorType actorType, CollisionGroup collisionGroup, float angle) {
        ShotActor shotActor = new ShotActor(pos, world, actors.size(), collisionGroup, actorType, angle);
        actors.add(shotActor);
        stage.addActor(shotActor);
        return shotActor;
    }
    public ShieldActor addShieldActor(Vector2 pos, CollisionGroup collisionGroup, SpaceActor shieldTarget, ActorType actorType) {
        ShieldActor shieldActor = new ShieldActor(pos, world, actors.size(), collisionGroup, shieldTarget);
        actors.add(shieldActor);
        stage.addActor(shieldActor);
        return shieldActor;
    }
    public PowerupActor addPowerupActor(Vector2 pos, CollisionGroup collisionGroup, ActorType actorType) {
        PowerupActor powerupActor = new PowerupActor(pos, world, actors.size(), collisionGroup, actorType);
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
        if (dataA.getCollisionGroup() == CollisionGroup.SHIELD && dataB.getCollisionGroup() == CollisionGroup.ENEMY) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionShieldAndEnemy(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.ENEMY && dataB.getCollisionGroup() == CollisionGroup.SHIELD) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionShieldAndEnemy(dataB, dataA);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.PLAYER && dataB.getCollisionGroup() == CollisionGroup.POWERUP) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionPlayerAndPowerup(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.POWERUP && dataB.getCollisionGroup() == CollisionGroup.PLAYER) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionPlayerAndPowerup(dataB, dataA);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.PLAYER && dataB.getCollisionGroup() == CollisionGroup.ENEMY_SHOT) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionPlayerAndEnemy_Shot(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.ENEMY_SHOT && dataB.getCollisionGroup() == CollisionGroup.PLAYER) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionPlayerAndEnemy_Shot(dataB, dataA);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.SHIELD && dataB.getCollisionGroup() == CollisionGroup.ENEMY_SHOT) {
            System.out.println(dataA.getActorIndex() + ", " + dataA.getCollisionGroup() + " hit " + dataB.getActorIndex() + ", " + dataB.getCollisionGroup());
            handleCollisionShieldAndEnemy_Shot(dataA, dataB);
        }
        if (dataA.getCollisionGroup() == CollisionGroup.ENEMY_SHOT && dataB.getCollisionGroup() == CollisionGroup.SHIELD) {
            System.out.println(dataB.getActorIndex() + ", " + dataB.getCollisionGroup() + " hit " + dataA.getActorIndex() + ", " + dataA.getCollisionGroup());
            handleCollisionShieldAndEnemy_Shot(dataB, dataA);
        }
    }

    void handleCollisionPlayerAndEnemy(ActorData dataPlayer, ActorData dataEnemy) {
        System.out.println("Player" + dataPlayer.getActorIndex() + " and Enemy " + dataEnemy.getActorIndex());
        PlayerActor playerActor = (PlayerActor) actors.get(dataPlayer.getActorIndex());
        EnemyActor enemyActor = (EnemyActor) actors.get(dataEnemy.getActorIndex());
        if (playerActor.isActive() && enemyActor.isActive()) {
            deleteActor(playerActor);
            deleteActor(enemyActor);
            enemyActorArray.removeValue(enemyActor, true);
            setScore(score += enemyActor.getScoreGiven());
        }
    }
    void handleCollisionPlayer_ShotAndEnemy(ActorData dataPlayer_Shot, ActorData dataEnemy) {
        System.out.println(dataPlayer_Shot.getActorIndex() + ", Shot" + " and " + dataEnemy.getActorIndex() + ", Enemy");
        ShotActor shotActor = (ShotActor) actors.get(dataPlayer_Shot.getActorIndex());
        EnemyActor enemyActor = (EnemyActor) actors.get(dataEnemy.getActorIndex());
        if (shotActor.isActive() && enemyActor.isActive()) {
            enemyActor.setHealth(enemyActor.getHealth() - shotActor.getDamageGiven());
            if (shotActor.getHealth() <= 0) { deleteActor(shotActor); }
            if (enemyActor.getHealth() <= 0) {
                deleteActor(enemyActor);
                enemyActorArray.removeValue(enemyActor, true);
                if (enemyActor.getClass() == AsteroidActor.class) {
                    asteroidActorArray.removeValue((AsteroidActor) enemyActor, true);
                }
                setScore(score += enemyActor.getScoreGiven());
            }
        }
    }
    void handleCollisionPlayerAndEnemy_Shot(ActorData dataPlayer, ActorData dataEnemy_Shot) {
        System.out.println(dataEnemy_Shot.getActorIndex() + ", Shot" + " and " + dataPlayer.getActorIndex() + ", Player");
        PlayerActor playerActor = (PlayerActor) actors.get(dataPlayer.getActorIndex());
        ShotActor shotActor = (ShotActor) actors.get(dataEnemy_Shot.getActorIndex());
        if (shotActor.isActive() && playerActor.isActive()) {
            playerActor.setHealth(playerActor.getHealth() - shotActor.getDamageGiven());
            if (shotActor.getHealth() <= 0) { deleteActor(shotActor); }
            if (playerActor.getHealth() <= 0) { deleteActor(playerActor); }
        }
    }
    void handleCollisionShieldAndEnemy(ActorData dataPlayer_Shield, ActorData dataEnemy) {
        System.out.println(dataPlayer_Shield.getActorIndex() + ", Shield" + " and " + dataEnemy.getActorIndex() + ", Asteroid");
        ShieldActor shieldActor = (ShieldActor) actors.get(dataPlayer_Shield.getActorIndex());
        EnemyActor enemyActor = (EnemyActor) actors.get(dataEnemy.getActorIndex());
        if (shieldActor.isActive() && enemyActor.isActive()) {
            shieldActor.setHealth(shieldActor.getHealth() - enemyActor.getDamageGiven());
            enemyActor.prepareMove(enemyActor.getX() * -1, enemyActor.getY() * -1, enemyActor.getMoveSpeed());
            enemyActor.getBody().applyForceToCenter(new Vector2(enemyActor.getX() * -1, enemyActor.getY() * -1), true);
        }
    }
    void handleCollisionShieldAndEnemy_Shot(ActorData dataPlayer_Shield, ActorData dataEnemy_Shot) {
        System.out.println(dataPlayer_Shield.getActorIndex() + ", Shield" + " and " + dataEnemy_Shot.getActorIndex() + ", Shot");
        ShieldActor shieldActor = (ShieldActor) actors.get(dataPlayer_Shield.getActorIndex());
        ShotActor shotActor = (ShotActor) actors.get(dataEnemy_Shot.getActorIndex());
        if (shieldActor.isActive() && shotActor.isActive()) {
            shieldActor.setHealth(shieldActor.getHealth() - shotActor.getDamageGiven());
            if (shotActor.getHealth() <= 0) { deleteActor(shotActor); }
        }
    }
    void handleCollisionPlayerAndPowerup(ActorData dataPlayer, ActorData dataPowerup) {
        System.out.println(dataPlayer.getActorIndex() + ", Player" + " and " + dataPowerup.getActorIndex() + ", Powerup");
        PlayerActor playerActor = (PlayerActor) actors.get(dataPlayer.getActorIndex());
        PowerupActor powerupActor = (PowerupActor) actors.get(dataPowerup.getActorIndex());
        if (playerActor.isActive() && powerupActor.isActive()) {
            powerupActor.giveEffect(playerActor, powerupActor.getActorType());
            setScore(score += powerupActor.getScoreGiven());
            deleteActor(powerupActor);
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


    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

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

    public ArrayList<SpaceActor> getActors() {
        return actors;
    }
}

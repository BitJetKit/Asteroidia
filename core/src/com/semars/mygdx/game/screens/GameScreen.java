package com.semars.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.ActorManager;
import com.semars.mygdx.game.Asteroidia;
import com.semars.mygdx.game.elements.ActorType;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.CollisionGroup;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.PowerupActor;
import com.semars.mygdx.game.elements.SpaceActor;

/**
 * Created by semar on 7/23/15.
 */
public class GameScreen extends BaseScreen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    public static ActorManager actorManager;
    private Stage gameStage;
    private PlayerActor player;
    private OrthographicCamera gameCam;
    private SpriteBatch batch;
    private float accumulator;
    private float delta;
    private Vector2 gravity = new Vector2(0, 0);
    private float gameTime;
    private int gameTimeMinutes;
    private int gameTimeSeconds;
    private int score;
    private Table uiTable;
    private Label scoreLabel;
    private Label.LabelStyle scoreLabelStyle;
    private Label timeLabel;
    Vector3 touchPos = new Vector3();
    Vector3 lastTouch = new Vector3();
    private int maxAsteroids;
    private float asteroidSpawnFrequency;
    private float timeSinceLastAsteroid;
    private float powerupSpawnFrequency;
    private float timeSinceLastPowerup;
    private Array<AsteroidActor> asteroidActorArray;
    private boolean gamePaused;
    private ShapeRenderer shapeRenderer;

    public GameScreen(Asteroidia game) {
        super(game);
        screenName = "Game";
        create();
    }

    public void create() {
        // set up Box2d physics world
        Box2D.init();

        actorManager = new ActorManager(gravity);
        debugRenderer = new Box2DDebugRenderer();
        world = actorManager.getWorld();

        // set up Scene2d stage
        gameStage = actorManager.getStage();
        multiInputProcessor.addProcessor(gameStage);

        // set up camera and renderers
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, Asteroidia.WIDTH, Asteroidia.HEIGHT);
        batch = new SpriteBatch();

        score = 0;
        gameTime = 0f;

        // TO-DO : Use Scene2d.ui Table for alignment

        //uiTable = new Table(uiSkin);
        //uiStage.addActor(uiTable);
        //uiTable.setFillParent(true);
        //uiTable.row();

        scoreLabel = new Label(Integer.toString(actorManager.getScore()), uiSkin);
        scoreLabel.setPosition(uiStage.getWidth() * 0.01f, uiStage.getHeight() * .95f);
        uiStage.addActor(scoreLabel);

        timeLabel = new Label(gameTimeMinutes + ":" + gameTimeSeconds, uiSkin);
        timeLabel.setPosition(uiStage.getWidth() * 0.5f - timeLabel.getWidth(), uiStage.getHeight() * .95f);
        uiStage.addActor(timeLabel);
        // END TO-DO

        // instantiate actors
        player = actorManager.addPlayerActor(new Vector2(Asteroidia.WIDTH * 0.5f, Asteroidia.HEIGHT * 0.25f), ActorType.PLAYER, CollisionGroup.PLAYER, 0);
        //actorManager.addEnemyShipActor(new Vector2(Asteroidia.WIDTH * 0.70f, Asteroidia.HEIGHT * 0.80f), CollisionGroup.ENEMY, ActorType.ENEMY_SHIP_BLACK);

        asteroidActorArray = actorManager.getAsteroidActorArray();
        maxAsteroids = 15;
        asteroidSpawnFrequency = 1f;
        createAsteroids(1);
        timeSinceLastAsteroid = 0f;

        powerupSpawnFrequency = 30f;
        timeSinceLastPowerup = 0f;

        gamePaused = false;
    }

    @Override
    public void render(float delta) {
        // set background
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw UI elements
        uiCam.update();
        uiStage.getViewport().apply();
        //uiStage.setDebugAll(true);
        uiStage.act(delta);
        uiStage.draw();

        // draw game elements
        gameCam.update();
        //debugRenderer.render(world, gameCam.combined);
        batch.setProjectionMatrix(gameCam.combined);
        batch.begin();
        batch.end();

        gameStage.getViewport().apply();
        gameStage.draw();

        if (!player.isActive()) {
            pauseGame();
        }
        if (!gamePaused) {
            gameTime += delta;
            gameTimeMinutes = MathUtils.floor((gameTime - gameTimeSeconds) / 60);
            gameTimeSeconds = MathUtils.floor(gameTime % 60);
            timeLabel.setText(String.format("%02d:%02d", gameTimeMinutes, gameTimeSeconds));
            updateScore(actorManager.getScore());

            timeSinceLastAsteroid += delta;
            if (asteroidActorArray.size < maxAsteroids && timeSinceLastAsteroid > asteroidSpawnFrequency) {
                createAsteroids(1);
                timeSinceLastAsteroid = 0f;
            }

            timeSinceLastPowerup += delta;
            if (timeSinceLastPowerup > powerupSpawnFrequency) {
                createPowerups(1);
                timeSinceLastPowerup = 0f;
            }
        }

        // process management actions for active actors (e.g. deletion)
        actorManager.update(delta);

        // set up player movements
        registerMove();

        gameStage.act(delta);

        // step through Box2d physics world
        accumulator += delta;
        while (accumulator > 1f/Asteroidia.STEP) {
            world.step(1f/Asteroidia.STEP, Asteroidia.VELOCITY_ITER, Asteroidia.POSITION_ITER);
            accumulator -= 1f/Asteroidia.STEP;
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameStage.getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        super.dispose();
        gameStage.dispose();
        batch.dispose();
        world.dispose();
        for (SpaceActor spaceActor : actorManager.getActors()) {
            spaceActor.getTexture().dispose();
        }
        debugRenderer.dispose();
    }

    public void registerMove() {
        if (Gdx.input.isTouched()) {
            if (player != null) {
                // translate touch coordinates to Vector3 within camera
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                lastTouch.set(touchPos);
                gameCam.unproject(touchPos);
                player.setMoveTarget(touchPos);
                player.setIsMoving(true);
            }
        }
    }

    public void createAsteroids(int numAsteroids) {
        AsteroidActor asteroidActor;
        ActorType asteroidType = null;
        for (int i=0; i<numAsteroids; i++) {
            int range = MathUtils.random(10);
            if (range < 3) {
                asteroidType = ActorType.ENEMY_ASTEROID_SMALL;
            }
            else if (range < 7) {
                asteroidType = ActorType.ENEMY_ASTEROID_MEDIUM;
            }
            else {
                asteroidType = ActorType.ENEMY_ASTEROID_LARGE;
            }
            asteroidActor = actorManager.addAsteroidActor(new Vector2(Asteroidia.WIDTH / 2, Asteroidia.HEIGHT / 2), CollisionGroup.ENEMY, 0, asteroidType);
            asteroidActor.spawnAtQuadrant(asteroidActor.randomizeSpawnQuadrant());
            asteroidActor.setVisible(true);
        }
    }

    public void createPowerups(int numPowerups) {
        ActorType powerupType;
        PowerupActor powerupActor;
        for (int i=0; i<numPowerups; i++) {
            int range = MathUtils.random(10);
            if (range < 2) {
                powerupType = ActorType.POWERUP_SHIELD;
            }
            else if (range < 4) {
                powerupType = ActorType.POWERUP_BOLT;
            }
            else {
                powerupType = ActorType.POWERUP_STAR;
            }
            powerupActor = actorManager.addPowerupActor(new Vector2(0, 0), CollisionGroup.POWERUP, powerupType);
            powerupActor.getWorldPos().set(MathUtils.random(powerupActor.getWidth(), Asteroidia.WIDTH - powerupActor.getWidth()),
                    MathUtils.random(powerupActor.getHeight(), Asteroidia.HEIGHT - powerupActor.getHeight()));
            powerupActor.setVisible(true);
        }
    }

    public int updateScore(int score) {
        this.score = score;
        scoreLabel.setText(String.format("%04d", score));
        return score;
    }

    public void pauseGame() {
        gamePaused = true;
    }
}

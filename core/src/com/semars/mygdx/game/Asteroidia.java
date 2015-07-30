package com.semars.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.semars.mygdx.game.elements.ActorType;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.CollisionGroup;
import com.semars.mygdx.game.elements.EnemyActor;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.PowerupActor;
import com.semars.mygdx.game.elements.ShieldActor;
import com.semars.mygdx.game.elements.ShotActor;
import com.semars.mygdx.game.elements.SpaceActor;

public class Asteroidia extends Game implements ApplicationListener {
	private World world;
	private Box2DDebugRenderer debugRenderer;
	public static ActorManager actorManager;
	private Stage stage;
	private PlayerActor player;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private float accumulator;
	private float delta;
	private Vector2 gravity = new Vector2(0, 0);
	private float gameTime;
	private float gameTimeMinutes;
	private float gameTimeSeconds;
	private int score;
	private String scoreString;
	private BitmapFont gameFont;
	Vector3 touchPos = new Vector3();
	Vector3 lastTouch = new Vector3();
	private int maxAsteroids;
	private Array<EnemyActor> enemyActorArray;
	private Array<AsteroidActor> asteroidActorArray;
	private ShapeRenderer shapeRenderer;
	private Texture bgTexture;
	private Sprite bgSprite;

	public static float WIDTH = 9.0f;
	public static float HEIGHT = 16.0f;
	public static final float STEP = 60.0f;
	static final int VELOCITY_ITER = 8;
	static final int POSITION_ITER = 3;

	@Override
	public void create () {
		// set up Box2d physics world
		Box2D.init();

		actorManager = new ActorManager(gravity);
		debugRenderer = new Box2DDebugRenderer();
		world = actorManager.getWorld();

		// set up Scene2d stage
		stage = actorManager.getStage();
		Gdx.input.setInputProcessor(stage);

		// set up camera and renderers
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		batch = new SpriteBatch();

		//gameFont = new BitmapFont(Gdx.files.internal("kenvector_future_thin.tff"));
		gameTime = 0f;
		score = 0;
		scoreString = "" + score;

		// instantiate actors
		bgTexture = new Texture(Gdx.files.internal("background.png"));
		bgSprite = new Sprite(bgTexture);

		player = actorManager.addPlayerActor(new Vector2(WIDTH / 2f, HEIGHT / 8), ActorType.PLAYER, CollisionGroup.PLAYER, 0);
		actorManager.addPowerupActor(new Vector2(WIDTH / 8f, HEIGHT / 2f), CollisionGroup.POWERUP, ActorType.POWERUP_SHIELD);
		//actorManager.addPowerupActor(new Vector2(WIDTH / 3, HEIGHT / 2), CollisionGroup.POWERUP, ActorType.POWERUP_BOLT);
		//actorManager.addPowerupActor(new Vector2(WIDTH / 10, HEIGHT / 2), CollisionGroup.POWERUP, ActorType.POWERUP_BOLT);

		enemyActorArray = new Array<EnemyActor>();
		asteroidActorArray = actorManager.getAsteroidActorArray();

		createAsteroids(10);
		maxAsteroids = 10;
	}

	@Override
	public void render () {
		delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		debugRenderer.render(world, camera.combined);
		batch.setProjectionMatrix(camera.combined);
		stage.draw();

		gameTime += delta;
		gameTimeMinutes = gameTime / 60f;
		gameTimeSeconds = (gameTime - gameTimeMinutes) / 60.0f;
		actorManager.update(delta);

		// Set up player movements
		registerMove();

		stage.act(delta);

		if (asteroidActorArray.size < maxAsteroids) {
			createAsteroids(1);
		}

		// step through Box2d physics world
		accumulator += delta;
		while (accumulator > 1f/STEP){
			world.step(1f/STEP, VELOCITY_ITER, POSITION_ITER);
			accumulator -= 1f/STEP;
		}
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		world.dispose();
		debugRenderer.dispose();
		player.getTexture().dispose();
		for (AsteroidActor asteroidActor : asteroidActorArray) {
			asteroidActor.getTexture().dispose();
		}
		for (ShotActor shotActor : player.getShotsActive()) {
			shotActor.getTexture().dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public void registerMove() {
		if(Gdx.input.isTouched()) {
			// translate touch coordinates to Vector3 within camera
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			lastTouch.set(touchPos);
			camera.unproject(touchPos);
			player.setMoveTarget(touchPos);
			player.setIsMoving(true);
		}
	}

	public void createAsteroids(int asteroids) {
		ActorType asteroidType = null;
		for (int i=0; i<asteroids; i++) {
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
			actorManager.addAsteroidActor(new Vector2(WIDTH / 2, HEIGHT / 2), CollisionGroup.ENEMY, 0, asteroidType);
		}

	}
}

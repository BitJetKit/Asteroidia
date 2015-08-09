package com.semars.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.semars.mygdx.game.elements.ActorType;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.CollisionGroup;
import com.semars.mygdx.game.elements.EnemyActor;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.ShotActor;
import com.semars.mygdx.game.elements.SpaceActor;

import java.text.Format;

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
	private int gameTimeMinutes;
	private int gameTimeSeconds;
	private int score;
	private Label scoreLabel;
	private Label.LabelStyle scoreLabelStyle;
	private Label timeLabel;
	Vector3 touchPos = new Vector3();
	Vector3 lastTouch = new Vector3();
	private int maxAsteroids;
	private float asteroidSpawnFrequency;
	private float timeSinceLastAsteroid;
	private Array<AsteroidActor> asteroidActorArray;
	private ShapeRenderer shapeRenderer;

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

		score = 0;
		gameTime = 0f;

		// TO-DO : Use Scene2d.ui and skin
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/kenvector_future_thin.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = 12;
		BitmapFont gameFont = fontGenerator.generateFont(fontParameter); // font size 12 pixels
		fontGenerator.dispose();

		scoreLabelStyle = new Label.LabelStyle();
		scoreLabelStyle.font = gameFont;
		scoreLabel = new Label(Integer.toString(actorManager.getScore()), scoreLabelStyle);
		scoreLabel.setBounds(WIDTH * 0.01f, HEIGHT * 0.95f, WIDTH / 2f, 1f);
		scoreLabel.setFontScale(.04f, .04f);
		stage.addActor(scoreLabel);

		timeLabel = new Label(gameTimeMinutes + ":" + gameTimeSeconds, scoreLabelStyle);
		timeLabel.setBounds(WIDTH * 0.5f, HEIGHT * 0.95f, WIDTH, 1f);
		timeLabel.setFontScale(.04f, .04f);
		stage.addActor(timeLabel);
		// END TO-DO

		// instantiate actors
		player = actorManager.addPlayerActor(new Vector2(WIDTH * 0.5f, HEIGHT * 0.25f), ActorType.PLAYER, CollisionGroup.PLAYER, 0);
		actorManager.addPowerupActor(new Vector2(WIDTH * 0.80f, HEIGHT * 0.20f), CollisionGroup.POWERUP, ActorType.POWERUP_STAR);
		actorManager.addPowerupActor(new Vector2(WIDTH * 0.30f, HEIGHT * 0.50f), CollisionGroup.POWERUP, ActorType.POWERUP_BOLT);
		actorManager.addPowerupActor(new Vector2(WIDTH * 0.10f, HEIGHT * 0.75f), CollisionGroup.POWERUP, ActorType.POWERUP_BOLT);
		actorManager.addEnemyShipActor(new Vector2(WIDTH * 0.70f, HEIGHT * 0.80f), CollisionGroup.ENEMY, ActorType.ENEMY_SHIP_BLACK);

		asteroidActorArray = actorManager.getAsteroidActorArray();
		maxAsteroids = 10;
		asteroidSpawnFrequency = 1f;
		createAsteroids(1);
		timeSinceLastAsteroid = 0f;
	}

	@Override
	public void render () {
		delta = Gdx.graphics.getDeltaTime();
		// set background
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// draw game elements
		camera.update();
		debugRenderer.render(world, camera.combined);
		batch.setProjectionMatrix(camera.combined);
		stage.draw();
		batch.begin();
		batch.end();

		gameTime += delta;
		gameTimeMinutes = MathUtils.floor((gameTime - gameTimeSeconds) / 60);
		gameTimeSeconds = MathUtils.floor(gameTime % 60);
		timeLabel.setText(String.format("%02d:%02d", gameTimeMinutes, gameTimeSeconds));
		updateScore(actorManager.getScore());

		// process management actions for active actors (e.g. deletion)
		actorManager.update(delta);

		// set up player movements
		registerMove();

		stage.act(delta);

		timeSinceLastAsteroid += delta;
		if (asteroidActorArray.size < maxAsteroids && timeSinceLastAsteroid > asteroidSpawnFrequency) {
			createAsteroids(1);
			timeSinceLastAsteroid = 0;
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
		for (SpaceActor spaceActor : actorManager.getActors()) {
			spaceActor.getTexture().dispose();
		}
		debugRenderer.dispose();
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
		if (Gdx.input.isTouched()) {
			if (player != null) {
				// translate touch coordinates to Vector3 within camera
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				lastTouch.set(touchPos);
				camera.unproject(touchPos);
				player.setMoveTarget(touchPos);
				player.setIsMoving(true);
			}
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

	public int updateScore(int score) {
		this.score = score;
		scoreLabel.setText(Integer.toString(score));
		return score;
	}
}

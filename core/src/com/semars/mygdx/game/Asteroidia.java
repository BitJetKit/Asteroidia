package com.semars.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.semars.mygdx.game.elements.ActorType;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.EnemyActor;
import com.semars.mygdx.game.elements.PlayerActor;
import com.semars.mygdx.game.elements.ShotActor;

import java.util.Iterator;

public class Asteroidia extends Game implements ApplicationListener {
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private ActorManager actorManager;
	private Body body;
	private BodyDef bodyDef;
	private Stage stage;
	private PlayerActor player;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private float accumulator;
	private float torque = 0.0f;
	private float delta;
	private Vector2 gravity = new Vector2(0, 0);
	Vector3 touchPos = new Vector3();
	Vector3 lastTouch = new Vector3();
	private Array<AsteroidActor> asteroidActorArray;
	private Array<EnemyActor> enemyActorArray;
	private ShapeRenderer shapeRenderer;

	public static float WIDTH = 9.0f;
	public static float HEIGHT = 16.0f;
	public static float CONVERT_TO_METERS = 1/100f;
	public static float CONVERT_TO_PIXELS = 100f;
	public static final float STEP = 60.0f;
	static final int VELOCITY_ITER = 8;
	static final int POSITION_ITER = 3;

	@Override
	public void create () {
		// set up Box2d physics world
		Box2D.init();
		//world = new World(gravity, false);
		actorManager = new ActorManager(gravity);
		debugRenderer = new Box2DDebugRenderer();

		// set up Scene2d stage
		world = actorManager.getWorld();
		stage = actorManager.getStage();
		Gdx.input.setInputProcessor(stage);

		// set up camera and renderers
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		batch = new SpriteBatch();

		spawnObject(new Vector2(WIDTH / 2f, HEIGHT / 8), ActorType.PLAYER, 0, 90);

		enemyActorArray = new Array<EnemyActor>();
		asteroidActorArray = new Array<AsteroidActor>();
		for (int i=0; i<0; i++) {
			spawnObject(new Vector2(0,0), ActorType.ASTEROID, 1, 90);
		}
	}

	@Override
	public void render () {
		delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
		camera.update();
		debugRenderer.render(world, camera.combined);

		batch.setProjectionMatrix(camera.combined);

		// Set up player movements
		registerMove();

		//player.shoot(delta);
		Iterator<AsteroidActor> asteroidIterator = asteroidActorArray.iterator();
		Iterator<ShotActor> shotIterator = player.getShotsActive().iterator();

		// check for collision
		while (asteroidIterator.hasNext()) {
			// check for collision between asteroids and player
			AsteroidActor asteroid = asteroidIterator.next();
			while (shotIterator.hasNext()) {
			ShotActor shot = shotIterator.next();
				if (true) {
					asteroidIterator.remove();
					player.destroy(world);
					asteroid.destroy(world);
					break;
				}

				// check for collision between shots and asteroids
				if (Intersector.overlaps(asteroid.getBoundingRectangle(), shot.getBoundingRectangle())) {
					System.out.println("Hit!");
					asteroidIterator.remove();
					shotIterator.remove();
					asteroid.destroy(world);
					shot.destroy(world);
					break;
				}
			}
		}
		stage.act(delta);

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
		for (int i=0; i<asteroidActorArray.size; i++) {
			asteroidActorArray.get(i).getTexture().dispose();
		}
		for (int i=0; i<player.getShotsActive().size; i++) {
			player.getShotsActive().get(i).getTexture().dispose();
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
			System.out.println("touch: " + touchPos);
		}
	}

	public void spawnObject(Vector2 pos, ActorType actorType, int collisionGroup, float angle) {
		AsteroidActor asteroidActor;
		ShotActor shotActor;
		switch (actorType) {
			case PLAYER: {
				player = actorManager.addPlayerActor(pos, actorType, collisionGroup, angle);
				break;
			}
			case ASTEROID: {
				asteroidActor = actorManager.addAsteroidActor(pos, actorType, collisionGroup, angle);
				break;
			}
			case SHOT: {
				shotActor = actorManager.addShotActor(pos, actorType, collisionGroup, angle);
				break;
			}
		}
	}
}

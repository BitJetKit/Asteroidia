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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.elements.AsteroidActor;
import com.semars.mygdx.game.elements.EnemyActor;
import com.semars.mygdx.game.elements.Gun;
import com.semars.mygdx.game.elements.PlayerActor;
import com.badlogic.gdx.Input.Buttons;
import com.semars.mygdx.game.elements.ShotActor;

import java.util.Iterator;

public class Asteroidia extends Game implements ApplicationListener {
	public static World world;
	Box2DDebugRenderer debugRenderer;
	private Body body;
	private BodyDef bodyDef;
	private Stage stage;
	private PlayerActor player;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	public static int WIDTH = 480;
	public static int HEIGHT = 800;
	public static float CONVERSION = 100f;
	static final float STEP = 1/120f;
	static final int VELOCITY_ITER = 8;
	static final int POSITION_ITER = 3;
	float accumulator;
	private float torque = 0.0f;
	private boolean drawSprite = true;
	float delta;
	Vector3 touchPos = new Vector3();
	Vector3 lastTouch = new Vector3();
	private Array<AsteroidActor> asteroidActorArray;
	private Array<EnemyActor> enemyActorArray;

	private ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		// set up Box2d physics world
		Box2D.init();
		world = new World(new Vector2(0, 0), false);
		debugRenderer = new Box2DDebugRenderer();
		// set up Scene2d stage
		stage = new Stage(new FitViewport(WIDTH, HEIGHT));
		Gdx.input.setInputProcessor(stage);
		// set up camera and renderers
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		spawnPlayer();

		enemyActorArray = new Array<EnemyActor>();
		asteroidActorArray = new Array<AsteroidActor>();
		for (int i=0; i<8; i++) {
			spawnAsteroid();
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
		preparePlayerMove();

		//player.shoot(delta);
		Iterator<AsteroidActor> asteroidIterator = asteroidActorArray.iterator();
		Iterator<ShotActor> shotIterator = player.getShotsActive().iterator();

		// check for collision
		while (asteroidIterator.hasNext()) {
			// check for collision between asteroids and player
			AsteroidActor asteroid = asteroidIterator.next();
			while (shotIterator.hasNext()) {
			ShotActor shot = shotIterator.next();
				if (Intersector.overlaps(asteroid.getBoundingRectangle(), player.getBoundingRectangle())) {
					asteroidIterator.remove();
					player.destroy();
					asteroid.destroy();
					break;
				}

				// check for collision between shots and asteroids
				if (Intersector.overlaps(asteroid.getBoundingRectangle(), shot.getBoundingRectangle())) {
					System.out.println("Hit!");
					asteroidIterator.remove();
					shotIterator.remove();
					asteroid.destroy();
					shot.destroy();
					break;
				}
			}
		}
		stage.act(delta);
		accumulator += delta;
		while (accumulator > STEP){
			world.step(STEP, VELOCITY_ITER, POSITION_ITER);
			accumulator -= STEP;
		}
		//world.step(1f/45f, 6, 2);
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		world.dispose();
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

	public void preparePlayerMove() {
		if(Gdx.input.isTouched() || Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (lastTouch.x != Gdx.input.getX() || lastTouch.y != Gdx.input.getY()) {
				// translate touch coordinates to Vector3 within camera
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				lastTouch.set(touchPos);
				camera.unproject(touchPos);

				// set coordinates to Vector2(s) for calculation
				player.endPos.set(touchPos.x - player.getWidth() / 2, touchPos.y + player.getHeight());
				player.startPos.set(player.getCurrentPos());

				// calculate distance and direction between player and touch
				player.moveDistance = player.endPos.dst2(player.startPos);
				player.moveDirection = player.endPos.sub(player.startPos).nor();
				player.moveVelocity = player.moveDirection.scl(player.getMoveSpeed());
				player.moveAmount = player.moveVelocity.scl(delta);

				player.isMoving = true;
			}
		}
	}

	public void spawnPlayer() {
		// instantiate player
		player = new PlayerActor();
		player.createBody(world, player.getCurrentPos());
		stage.addActor(player);
	}

	public void spawnAsteroid() {
		AsteroidActor asteroidActor = new AsteroidActor();
		asteroidActor.randomizePosOutside();
		asteroidActor.createBody(world, asteroidActor.getCurrentPos());
		asteroidActorArray.add(asteroidActor);
		stage.addActor(asteroidActor);
		asteroidActor.setVisible(true);
	}

	public World getWorld() {
		return world;
	}
}

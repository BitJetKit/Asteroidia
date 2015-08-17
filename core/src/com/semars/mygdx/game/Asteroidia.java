package com.semars.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
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
import com.semars.mygdx.game.screens.GameScreen;
import com.semars.mygdx.game.screens.MainMenuScreen;

import java.text.Format;

public class Asteroidia extends Game implements ApplicationListener {

	public static float WIDTH = 9.0f;
	public static float HEIGHT = 16.0f;
	public static final float ASPECT_RATIO = WIDTH/HEIGHT;
	public static final float STEP = 60.0f;
	public static final int VELOCITY_ITER = 8;
	public static final int POSITION_ITER = 3;

	protected BitmapFont gameFont;

	@Override
	public void create () {
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ProFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = (int)(48);
		gameFont = fontGenerator.generateFont(fontParameter);
		fontGenerator.dispose();

		setScreen(new MainMenuScreen(this));
	}

	public BitmapFont getGameFont() {
		return gameFont;
	}
}

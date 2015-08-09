package com.semars.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/31/15.
 */
public class MainMenuScreen extends BaseScreen {

    /*private final Asteroidia game;
    private OrthographicCamera camera;
    private Skin skin;
    private SpriteBatch batch;
    private BitmapFont font;
    private Table table;*/

    private final Stage stage;

    private TextButton survivalButton;
    private TextButton quitButton;

    public static float WIDTH = 9.0f;
    public static float HEIGHT = 16.0f;

    public MainMenuScreen(Asteroidia game) {
        super(game);
        stage = super.getStage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(super.getStage());
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
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

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

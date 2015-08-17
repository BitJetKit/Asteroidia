package com.semars.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/31/15.
 */
public class BaseScreen implements Screen {

    protected final Asteroidia game;
    protected Stage uiStage;
    protected Skin uiSkin;
    protected String screenName;
    protected OrthographicCamera uiCam;
    protected InputMultiplexer multiInputProcessor;
    protected ScreenInputHandler screenInputHandler;

    public BaseScreen(Asteroidia game) {
        this.game = game;
        uiStage = new Stage(new FitViewport(game.WIDTH * 40, game.HEIGHT * 40));
        uiSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
        screenInputHandler = new ScreenInputHandler(game);
        multiInputProcessor = new InputMultiplexer();

        multiInputProcessor.addProcessor(uiStage);
        multiInputProcessor.addProcessor(screenInputHandler);

        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, game.WIDTH * 40, game.HEIGHT * 40);
    }

    @Override
    public void show() {
        Gdx.app.log("Asteroidia", "Showing screen " + screenName);
        Gdx.input.setInputProcessor(multiInputProcessor);
    }

    @Override
    public void render(float delta) {
        // set background
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiCam.update();
        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("Asteroidia", "Resizing screen " + screenName + " to " + width + "x" + height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.log("Asteroidia", "Pausing screen " + screenName);
    }

    @Override
    public void resume() {
        Gdx.app.log("Asteroidia", "Resuming screen " + screenName);
    }

    @Override
    public void hide() {
        Gdx.app.log("Asteroidia", "Hiding screen " + screenName);
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("Asteroidia", "Disposing screen " + screenName);
        uiStage.dispose();
        uiSkin.dispose();
    }
}
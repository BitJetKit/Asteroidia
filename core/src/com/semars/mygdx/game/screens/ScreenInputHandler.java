package com.semars.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.box2d.World;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 8/12/15.
 */
public class ScreenInputHandler implements InputProcessor {
    private final Asteroidia game;

    public ScreenInputHandler(Asteroidia game) {
        this.game = game;
        //Gdx.input.setCatchBackKey(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE || keycode == Input.Keys.ESCAPE) {
            Gdx.app.log("Screen Input", "BACK pressed");
            if (game.getScreen().getClass() == MainMenuScreen.class) {
                Gdx.app.exit();
            }
            else if (game.getScreen().getClass() == GameScreen.class) {
                game.setScreen(new MainMenuScreen(game));
            }
            else {
                Gdx.app.exit();
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

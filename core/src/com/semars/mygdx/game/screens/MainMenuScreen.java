package com.semars.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.semars.mygdx.game.Asteroidia;

/**
 * Created by semar on 7/31/15.
 */
public class MainMenuScreen extends BaseScreen {

    private SpriteBatch batch;
    private BitmapFont font;
    private Table table;
    private TextButton survivalButton;
    private TextButton quitButton;

    private Sound clickSound;

    public MainMenuScreen(final Asteroidia game) {
        super(game);
        screenName = "MainMenu";

        font = new BitmapFont();
        batch = new SpriteBatch();
        clickSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_twoTone.ogg"));

        // set up main Table
        table = new Table(uiSkin);
        uiStage.addActor(table);
        table.setFillParent(true);

        // set up Title
        Image titleImage = new Image(new Texture(Gdx.files.internal("title.png")));
        table.add(titleImage).spaceBottom(48f);
        table.row();

        // set up "Survival" button
        survivalButton = new TextButton("Survival", uiSkin);
        survivalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                survivalButton.setText("Clicked");
                game.setScreen(game.getGameScreen());
            }
        });
        uiStage.addActor(survivalButton);
        table.add(survivalButton).size(200f, 48f).uniform().spaceBottom(24f);
        table.row();

        // set up "Quit" button
        quitButton = new TextButton("Quit", uiSkin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                Gdx.app.exit();
            }
        });
        uiStage.addActor(quitButton);
        table.add(quitButton).size(200f, 48f).uniform().spaceBottom(24f);
        table.row();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.update();
        uiStage.act(delta);
        uiStage.draw();
        //uiStage.setDebugAll(true);
    }
}

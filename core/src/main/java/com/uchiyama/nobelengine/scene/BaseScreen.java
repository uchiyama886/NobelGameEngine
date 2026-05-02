package com.uchiyama.nobelengine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.uchiyama.nobelengine.CoreEngine;
import com.uchiyama.nobelengine.core.Config;

public abstract class BaseScreen implements Screen {

    protected final CoreEngine game;
    protected Stage stage;

    public BaseScreen(CoreEngine game) {
        this.game = game;
    }

    protected void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}

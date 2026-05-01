package com.uchiyama.nobelengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uchiyama.nobelengine.core.GlobalAssets;

public class Renderer {

    private final SpriteBatch batch;
    private final GlobalAssets assets;

    public Renderer(SpriteBatch batch, GlobalAssets assets) {
        this.batch = batch;
        this.assets = assets;
    }

    public void drawGameWorld() {
        batch.begin();

        batch.draw(assets.getBackground(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float charX = (Gdx.graphics.getWidth() - assets.getCharacter().getWidth()) / 2f;
        batch.draw(assets.getCharacter(), charX, 0);

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(assets.getWindowTexture(), 0, 0, Gdx.graphics.getWidth(), 300);
        batch.setColor(Color.WHITE);

        batch.draw(assets.getFace(), 30, 75);

        batch.end();
    }
}

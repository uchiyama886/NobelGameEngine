package com.uchiyama.nobelengine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.uchiyama.nobelengine.core.Config;
import com.uchiyama.nobelengine.core.GlobalAssets;

public class Renderer implements Disposable {

    private final SpriteBatch batch;
    private final GlobalAssets assets;

    public Renderer(SpriteBatch batch, GlobalAssets assets) {
        this.batch = batch;
        this.assets = assets;
    }

    public void drawGameWorld() {
        float w = Config.VIRTUAL_WIDTH;
        float h = Config.VIRTUAL_HEIGHT;

        batch.begin();

        // Full-screen background
        batch.draw(assets.getTexture(Config.IMG_BG_TEST), 0, 0, w, h);

        // Character image — Figma: x=386, w=509, h=720 (full height)
        float charW = w * (509f / 1280f);
        float charX = w * (386f / 1280f);
        batch.draw(assets.getTexture(Config.IMG_CHARA_TEST), charX, 0, charW, h);

        batch.end();
    }

    @Override
    public void dispose() {}
}

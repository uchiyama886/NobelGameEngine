package com.uchiyama.nobelengine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.uchiyama.nobelengine.CoreEngine;
import com.uchiyama.nobelengine.core.Config;

public class TitleScreen extends BaseScreen {

    public TitleScreen(CoreEngine game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.globalAssets.getDefaultFont(), Color.GOLD);
        Label titleLabel = new Label("NobelEngine Project", titleStyle);
        titleLabel.setFontScale(2.0f);

        Label.LabelStyle subtitleStyle = new Label.LabelStyle(
            game.globalAssets.getDefaultFont(), new Color(0.75f, 0.75f, 0.9f, 1f));
        Label subtitleLabel = new Label("Visual Novel Engine", subtitleStyle);

        // Gold divider line using the shared white pixel texture
        Image divider = new Image(
            new TextureRegionDrawable(new TextureRegion(game.globalAssets.getWhitePixelTexture())));
        divider.setColor(new Color(1f, 0.84f, 0f, 0.7f));

        VisTextButton.VisTextButtonStyle btnStyle = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
        btnStyle.font = game.globalAssets.getDefaultFont();

        VisTextButton startButton = new VisTextButton("START GAME", btnStyle);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.scenarioManager.load(Config.SCENARIO_FILE);
                game.setScreen(new GameScreen(game));
            }
        });

        VisTextButton exitButton = new VisTextButton("EXIT", btnStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.add(titleLabel).padBottom(12).row();
        table.add(subtitleLabel).padBottom(20).row();
        table.add(divider).width(200).height(2).padBottom(50).row();
        table.add(startButton).width(300).height(60).padBottom(16).row();
        table.add(exitButton).width(300).height(60).row();
        stage.addActor(table);

        // Version label pinned to bottom-right (virtual coordinates)
        Label.LabelStyle versionStyle = new Label.LabelStyle(
            game.globalAssets.getDefaultFont(), new Color(0.5f, 0.5f, 0.6f, 1f));
        Label versionLabel = new Label("v0.1.0", versionStyle);
        versionLabel.setPosition(
            Config.VIRTUAL_WIDTH - versionLabel.getPrefWidth() - 16, 12);
        stage.addActor(versionLabel);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        stage.getViewport().apply(true);
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(game.globalAssets.getTexture(Config.IMG_BG_TEST),
            0, 0, Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT);
        game.batch.setColor(0.07f, 0.07f, 0.18f, 0.75f);
        game.batch.draw(game.globalAssets.getWhitePixelTexture(),
            0, 0, Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT);
        game.batch.setColor(Color.WHITE);
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }
}

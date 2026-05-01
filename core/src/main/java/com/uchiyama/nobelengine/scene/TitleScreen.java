package com.uchiyama.nobelengine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.globalAssets.getFont(), Color.GOLD);
        Label titleLabel = new Label("NobelEngine Project", titleStyle);
        titleLabel.setFontScale(2.0f);

        VisTextButton.VisTextButtonStyle btnStyle = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
        btnStyle.font = game.globalAssets.getFont();

        VisTextButton startButton = new VisTextButton("START GAME", btnStyle);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.scenarioManager.load(Config.SCENARIO_FILE);
                game.setScreen(new GameScreen(game));
            }
        });

        table.add(titleLabel).padBottom(100).row();
        table.add(startButton).width(300).height(60).row();
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        game.batch.begin();
        game.batch.draw(game.globalAssets.getBackground(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }
}

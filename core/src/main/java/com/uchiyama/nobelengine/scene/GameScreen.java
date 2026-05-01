package com.uchiyama.nobelengine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.JsonValue;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.uchiyama.nobelengine.CoreEngine;
import com.uchiyama.nobelengine.ui.Renderer;

import java.util.List;

public class GameScreen extends BaseScreen {

    private Renderer renderer;
    private Label nameLabel;
    private TypingLabel lineLabel;
    private Table uiTable;
    private VisTextButton logOpenButton;
    private String lastLine = "";

    public GameScreen(CoreEngine game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        renderer = new Renderer(game.batch, game.globalAssets);

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.globalAssets.getFont(), Color.WHITE);

        nameLabel = new Label("", labelStyle);
        nameLabel.setPosition(220, 240);
        nameLabel.setColor(Color.YELLOW);

        lineLabel = new TypingLabel("", labelStyle);
        lineLabel.setPosition(220, 180);

        uiTable = new Table();
        uiTable.setFillParent(true);

        stage.addActor(uiTable);
        stage.addActor(nameLabel);
        stage.addActor(lineLabel);

        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
        style.font = game.globalAssets.getFont();
        logOpenButton = new VisTextButton("LOG", style);
        logOpenButton.setPosition(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 50);
        logOpenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLogWindow();
            }
        });
        stage.addActor(logOpenButton);

        forceUpdateText(game.scenarioManager.getCurrentName(), game.scenarioManager.getCurrentLine());
        updateChoices();
    }

    @Override
    public void hide() {
        super.hide();
        renderer = null;
        nameLabel = null;
        lineLabel = null;
        uiTable = null;
        logOpenButton = null;
        lastLine = "";
    }

    private void forceUpdateText(String currentName, String currentLine) {
        nameLabel.setText(currentName);
        lineLabel.setText(currentLine);
        lineLabel.restart();
        lastLine = currentLine;
    }

    private void updateChoices() {
        uiTable.clear();
        JsonValue choices = game.scenarioManager.getChoices();
        if (choices != null) {
            VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
            style.font = game.globalAssets.getFont();

            for (JsonValue choice : choices) {
                final String nextId = choice.getString("nextId");
                VisTextButton button = new VisTextButton(choice.getString("text"), style);
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.scenarioManager.jumpTo(nextId);
                        forceUpdateText(game.scenarioManager.getCurrentName(), game.scenarioManager.getCurrentLine());
                        updateChoices();
                    }
                });
                uiTable.add(button).padBottom(10).row();
            }
        }
    }

    private void showLogWindow() {
        final VisWindow logWindow = new VisWindow("BACK LOG");
        logWindow.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        logWindow.setPosition(0, 0);
        logWindow.addCloseButton();
        logWindow.setMovable(false);

        VisTable contentTable = new VisTable();
        contentTable.top().pad(40);

        VisTextButton.VisTextButtonStyle btnStyle = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
        btnStyle.font = game.globalAssets.getFont();

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.globalAssets.getFont(), Color.WHITE);

        VisTextButton backButton = new VisTextButton("BACK TO TITLE", btnStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logWindow.remove();
                game.setScreen(new TitleScreen(game));
            }
        });
        contentTable.add(backButton).padBottom(40).row();

        List<String> history = game.scenarioManager.getHistoryList();
        for (int i = 0; i < history.size(); i++) {
            final int jumpIndex = i;
            JsonValue sceneData = game.scenarioManager.getSceneData(history.get(i));

            String cleanLine = sceneData.getString("line").replaceAll("\\{.*?\\}", "");
            String logText = sceneData.getString("char_name") + " : " + cleanLine;

            VisTable rowTable = new VisTable();

            VisTextButton jumpButton = new VisTextButton("JUMP", btnStyle);
            jumpButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    logWindow.remove();
                    game.scenarioManager.rollbackTo(jumpIndex);
                    forceUpdateText(game.scenarioManager.getCurrentName(), game.scenarioManager.getCurrentLine());
                    updateChoices();
                }
            });

            Label textLabel = new Label(logText, labelStyle);
            textLabel.setWrap(true);

            rowTable.add(jumpButton).width(120).padRight(20).top();
            rowTable.add(textLabel).expandX().fillX().top();

            contentTable.add(rowTable).expandX().fillX().padBottom(30).row();
        }

        VisScrollPane scrollPane = new VisScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(true);

        logWindow.add(scrollPane).expand().fill().padTop(20);
        stage.addActor(logWindow);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        renderer.drawGameWorld();

        String currentLine = game.scenarioManager.getCurrentLine();
        if (!currentLine.equals(lastLine)) {
            forceUpdateText(game.scenarioManager.getCurrentName(), currentLine);
        }

        stage.act(delta);
        stage.draw();
    }
}

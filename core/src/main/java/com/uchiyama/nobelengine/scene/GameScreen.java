package com.uchiyama.nobelengine.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonValue;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.uchiyama.nobelengine.CoreEngine;
import com.uchiyama.nobelengine.core.Config;
import com.uchiyama.nobelengine.ui.Renderer;

import java.util.List;

public class GameScreen extends BaseScreen {

    private Renderer renderer;
    private Label nameLabel;
    private TypingLabel lineLabel;
    private VisTable uiTable;
    private VisTextButton logOpenButton;
    private String lastLine = "";
    private VisWindow logWindow;
    private VisScrollPane logScrollPane;
    private VisTable logContentTable;

    public GameScreen(CoreEngine game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        renderer = new Renderer(game.batch, game.globalAssets);

        Label.LabelStyle nameStyle = new Label.LabelStyle(game.globalAssets.getDefaultFont(), new Color(1f, 0.9f, 0.2f, 1f));
        nameLabel = new Label("", nameStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.globalAssets.getDefaultFont(), Color.WHITE);
        lineLabel = new TypingLabel("", labelStyle);
        lineLabel.setWrap(true);
        lineLabel.setTypingListener(new TypingAdapter() {
            @Override
            public void end() {
                if (uiTable != null) uiTable.setVisible(true);
            }
        });

        // Text group (vertical stack: name then dialog)
        VisTable textGroup = new VisTable();
        textGroup.top();
        textGroup.add(nameLabel).padBottom(6).left().expandX().fillX().row();
        textGroup.add(lineLabel).expandX().fillX().padRight(24).padBottom(36);

        // Face image as Scene2D actor (virtual size: 140×140 at 1280×720)
        Image faceImage = new Image(
            new TextureRegionDrawable(new TextureRegion(game.globalAssets.getTexture(Config.IMG_FACE_TEST))));

        // Message box background — white pixel tinted to match Figma overlay (#0A0A1F 88%)
        Drawable msgBgDrawable = new TextureRegionDrawable(
            new TextureRegion(game.globalAssets.getWhitePixelTexture()))
            .tint(new Color(0.04f, 0.04f, 0.12f, 0.88f));

        // Horizontal row: [faceImage] [textGroup] — consecutive add() without row()
        VisTable msgContent = new VisTable();
        msgContent.setBackground(msgBgDrawable);
        msgContent.add(faceImage).size(140, 140).pad(35, 24, 24, 16).top();
        msgContent.add(textGroup).expandX().fillX().padTop(8);

        // Full-screen container anchored to bottom
        VisTable msgTable = new VisTable();
        msgTable.setFillParent(true);
        msgTable.bottom();
        msgTable.add(msgContent).expandX().fillX();

        uiTable = new VisTable();
        uiTable.setFillParent(true);

        stage.addActor(msgTable);
        stage.addActor(uiTable);

        // LOG button — top-right corner
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
        style.font = game.globalAssets.getDefaultFont();
        logOpenButton = new VisTextButton("LOG", style);
        logOpenButton.setSize(80, 36);
        logOpenButton.setPosition(Config.VIRTUAL_WIDTH - 80 - 16, Config.VIRTUAL_HEIGHT - 36 - 16);
        logOpenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLogWindow();
            }
        });
        stage.addActor(logOpenButton);

        initLogWindow();

        // Mouse wheel: scroll up → open log, scroll down at bottom → close log
        stage.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                if (amountY < 0) {
                    if (logWindow != null && !logWindow.isVisible()) {
                        showLogWindow();
                        return true;
                    }
                } else if (amountY > 0) {
                    if (logWindow != null && logWindow.isVisible()) {
                        if (logScrollPane.getScrollPercentY() >= 0.99f || logScrollPane.getMaxY() <= 0) {
                            logWindow.setVisible(false);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

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
        logWindow = null;
        logScrollPane = null;
        logContentTable = null;
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
        uiTable.setVisible(false);
        JsonValue choices = game.scenarioManager.getChoices();
        if (choices != null) {
            VisTextButton.VisTextButtonStyle btnStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
            btnStyle.font = game.globalAssets.getDefaultFont();

            for (JsonValue choice : choices) {
                final String nextId = choice.getString("nextId");
                VisTextButton button = new VisTextButton(choice.getString("text"), btnStyle);
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.scenarioManager.jumpTo(nextId);
                        forceUpdateText(game.scenarioManager.getCurrentName(), game.scenarioManager.getCurrentLine());
                        updateChoices();
                    }
                });
                uiTable.add(button).width(400).height(52).padBottom(12).row();
            }
        }
    }

    private void initLogWindow() {
        logWindow = new VisWindow("BACK LOG");
        logWindow.setSize(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT);
        logWindow.setPosition(0, 0);
        logWindow.addCloseButton();
        logWindow.setMovable(false);
        logWindow.setVisible(false);

        logContentTable = new VisTable();
        logContentTable.top().pad(24, 40, 24, 40);

        logScrollPane = new VisScrollPane(logContentTable);
        logScrollPane.setScrollingDisabled(true, false);
        logScrollPane.setFlickScroll(true);
        logScrollPane.setFadeScrollBars(false);

        logWindow.add(logScrollPane).expand().fill().padTop(20);
        stage.addActor(logWindow);
    }

    private void showLogWindow() {
        logContentTable.clear();

        VisTextButton.VisTextButtonStyle btnStyle = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
        btnStyle.font = game.globalAssets.getDefaultFont();

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.globalAssets.getDefaultFont(), Color.WHITE);

        VisTextButton backButton = new VisTextButton("BACK TO TITLE", btnStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logWindow.setVisible(false);
                game.setScreen(new TitleScreen(game));
            }
        });
        logContentTable.add(backButton).padBottom(33).row();

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
                    logWindow.setVisible(false);
                    game.scenarioManager.rollbackTo(jumpIndex);
                    forceUpdateText(game.scenarioManager.getCurrentName(), game.scenarioManager.getCurrentLine());
                    updateChoices();
                }
            });

            Label textLabel = new Label(logText, labelStyle);
            textLabel.setWrap(true);

            rowTable.add(jumpButton).width(120).height(40).padRight(20).top();
            rowTable.add(textLabel).expandX().fillX().top();

            logContentTable.add(rowTable).expandX().fillX().padBottom(30).row();
        }

        logWindow.setVisible(true);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        stage.getViewport().apply(true);
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        renderer.drawGameWorld();

        String currentLine = game.scenarioManager.getCurrentLine();
        if (!currentLine.equals(lastLine)) {
            forceUpdateText(game.scenarioManager.getCurrentName(), currentLine);
        }

        stage.act(delta);
        stage.draw();
    }
}

package com.uchiyama.nobelengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;
import com.uchiyama.nobelengine.core.Config;
import com.uchiyama.nobelengine.core.GlobalAssets;
import com.uchiyama.nobelengine.scene.TitleScreen;
import com.uchiyama.nobelengine.system.ScenarioManager;

public class CoreEngine extends Game {

    public GlobalAssets globalAssets;
    public ScenarioManager scenarioManager;
    public SpriteBatch batch;

    @Override
    public void create() {
        Gdx.app.log("NobelEngine", "Engine initialized");

        scenarioManager = new ScenarioManager();
        scenarioManager.load(Config.SCENARIO_FILE);

        globalAssets = new GlobalAssets();
        globalAssets.loadAllAssets();
        globalAssets.manager.finishLoading();

        VisUI.load();
        batch = new SpriteBatch();

        setScreen(new TitleScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        globalAssets.dispose();
        batch.dispose();
        VisUI.dispose();
    }
}

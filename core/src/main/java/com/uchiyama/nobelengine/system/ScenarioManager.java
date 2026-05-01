package com.uchiyama.nobelengine.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenarioManager {

    private final Map<String, JsonValue> sceneMap = new HashMap<>();
    private String currentSceneId;
    private String currentName;
    private String currentLine;
    private final List<String> historyList = new ArrayList<>();

    public void load(String filepath) {
        sceneMap.clear();
        JsonValue array = new JsonReader().parse(Gdx.files.internal(filepath));
        for (JsonValue entry : array) {
            sceneMap.put(entry.getString("id"), entry);
        }
        currentSceneId = "scene_001";
        historyList.clear();
        historyList.add(currentSceneId);
        updateCurrent();
    }

    public void jumpTo(String nextId) {
        currentSceneId = nextId;
        historyList.add(nextId);
        updateCurrent();
    }

    public void rollbackTo(int index) {
        String targetId = historyList.get(index);
        historyList.subList(index + 1, historyList.size()).clear();
        currentSceneId = targetId;
        updateCurrent();
    }

    public JsonValue getChoices() {
        return sceneMap.get(currentSceneId).get("choices");
    }

    public String getCurrentName()     { return currentName; }
    public String getCurrentLine()     { return currentLine; }
    public List<String> getHistoryList()          { return historyList; }
    public JsonValue getSceneData(String id)       { return sceneMap.get(id); }

    public String getAllCharacters() {
        StringBuilder sb = new StringBuilder();
        for (JsonValue entry : sceneMap.values()) {
            sb.append(entry.getString("char_name"));
            sb.append(entry.getString("line"));
            JsonValue choices = entry.get("choices");
            if (choices != null) {
                for (JsonValue choice : choices) {
                    sb.append(choice.getString("text"));
                }
            }
        }
        return sb.toString();
    }

    private void updateCurrent() {
        JsonValue entry = sceneMap.get(currentSceneId);
        currentName = entry.getString("char_name");
        currentLine = entry.getString("line");
    }
}

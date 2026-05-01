package com.uchiyama.nobelengine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GlobalAssets {

    private final AssetManager assetManager = new AssetManager();
    private BitmapFont font;
    private Texture windowTexture;

    public void load(String fontCharacters) {
        assetManager.load(Config.BG_IMAGE, Texture.class);
        assetManager.load(Config.CHAR_IMAGE, Texture.class);
        assetManager.load(Config.FACE_IMAGE, Texture.class);
        assetManager.finishLoading();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        windowTexture = new Texture(pixmap);
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal(Config.FONT_FILE));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 36;
        param.characters = FreeTypeFontGenerator.DEFAULT_CHARS + fontCharacters;
        font = generator.generateFont(param);
        generator.dispose();
    }

    public Texture getBackground()    { return assetManager.get(Config.BG_IMAGE, Texture.class); }
    public Texture getCharacter()     { return assetManager.get(Config.CHAR_IMAGE, Texture.class); }
    public Texture getFace()          { return assetManager.get(Config.FACE_IMAGE, Texture.class); }
    public Texture getWindowTexture() { return windowTexture; }
    public BitmapFont getFont()       { return font; }

    public void dispose() {
        assetManager.dispose();
        if (font != null) font.dispose();
        if (windowTexture != null) windowTexture.dispose();
    }
}

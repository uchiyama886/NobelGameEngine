package com.uchiyama.nobelengine.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.utils.Disposable;

public class GlobalAssets implements Disposable {

    public final AssetManager manager;
    private Texture whitePixelTexture;

    public GlobalAssets() {
        manager = new AssetManager();

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void loadAllAssets() {
        manager.load(Config.IMG_BG_TEST, Texture.class);
        manager.load(Config.IMG_CHARA_TEST, Texture.class);
        manager.load(Config.IMG_FACE_TEST, Texture.class);

        FreeTypeFontLoaderParameter fontParams = new FreeTypeFontLoaderParameter();
        fontParams.fontFileName = Config.FONT_NOTO_SANS;
        fontParams.fontParameters.size = 24;
        fontParams.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS + Config.JAPANESE_CHARS;
        manager.load("default_font.ttf", BitmapFont.class, fontParams);
    }

    public Texture getTexture(String fileName) {
        return manager.get(fileName, Texture.class);
    }

    public BitmapFont getDefaultFont() {
        return manager.get("default_font.ttf", BitmapFont.class);
    }

    public Texture getWhitePixelTexture() {
        return whitePixelTexture;
    }

    @Override
    public void dispose() {
        if (manager != null) manager.dispose();
        if (whitePixelTexture != null) whitePixelTexture.dispose();
    }
}

package com.uchiyama.nobelengine.core;

/**
 * ゲーム全体で共有する設定値や定数を管理するクラスです。
 */
public class Config {
    // ディスプレイ・ウィンドウ設定
    public static final String GAME_TITLE = "どきどきコンピュータ部！";
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    // データファイルのパス
    public static final String SCENARIO_FILE = "data/scenario.json";

    // フォントのパス
    public static final String FONT_NOTO_SANS = "fonts/NotoSansJP-Regular.ttf";

    // 画像アセットのパス
    public static final String IMG_BG_TEST = "images/test_background.png";
    public static final String IMG_CHARA_TEST = "images/test_character.png";
    public static final String IMG_FACE_TEST = "images/test_face.png";

    // 日本語フォント生成用文字セット（テスト用）
    public static final String JAPANESE_CHARS =
        "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん" +
        "ぁぃぅぇぉっゃゅょゎ" +
        "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン" +
        "ァィゥェォッャュョ" +
        "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽ" +
        "ガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ" +
        "一二三四五六七八九十百千万年月日時分秒円人私今何見言行来" +
        "、。・「」『』（）【】！？…ー〜　";
}

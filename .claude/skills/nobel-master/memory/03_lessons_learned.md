# 過去のバグと解決策・教訓（成長領域）

## 2026-05-01: LibGDX Preferences の安全な管理パターン

### 教訓: Preferences は `flush()` を明示的に呼ばないと永続化されない

LibGDX の `Preferences` は `put*()` しただけではメモリ上の変更にすぎない。
**必ず `prefs.flush()` を呼んで初めてディスクに書き込まれる。**

セーフパターン：

```java
// system/PreferencesManager.java
public void setTextSpeed(float speed) {
    prefs.putFloat(Config.PREF_TEXT_SPEED, speed);
    prefs.flush(); // ← これを忘れると再起動後に設定が消える
}
```

### 教訓: PreferencesManager は dispose() 不要

`Preferences` オブジェクトは LibGDX 内部でキャッシュ管理されており、
明示的な `dispose()` メソッドを持たない。`PreferencesManager` も同様に
`CoreEngine.dispose()` への登録は不要。

### 教訓: TypingLabel の速度は {SPEED=X} トークンをテキスト先頭に付加して制御する

`TypingLabel` にはグローバル速度変更用のシンプルな API がなく、
テキスト内に `{SPEED=X}` トークンを埋め込むことで速度を制御する。
X は倍率で 1.0 がデフォルト（0.5 = 半速, 3.0 = 3倍速）。

速度がデフォルト（1.0）のときはトークンを付けなくて良いので、
`getTextSpeedToken()` が空文字を返す設計にすることで
既存のテキスト処理への影響を最小化できる：

```java
// PreferencesManager.java
public String getTextSpeedToken() {
    float speed = getTextSpeed();
    if (speed == Config.TEXT_SPEED_NORMAL) return "";
    return "{SPEED=" + speed + "}";
}

// GameScreen.java - forceUpdateText()
String speedToken = game.preferencesManager.getTextSpeedToken();
lineLabel.setText(speedToken + currentLine);
lineLabel.restart();
```

### 教訓: Preferences のキーは Config.java に一元管理する

ハードコードされた文字列キーが各所に散らばると、タイポによるバグが発生する。
必ず `Config.java` に `static final String PREF_XXX = "xxx"` として定義し、
`PreferencesManager` から参照させること。

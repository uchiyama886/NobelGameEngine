# コーディング規約と実装パターン（成長領域）

## 2026-05-01: VisWindow を拡張したオーバーレイ UI（ConfigWindow）

### パターン: VisWindow サブクラスを ui パッケージに配置する

複数の Screen（TitleScreen・GameScreen）から共通のオーバーレイウィンドウを表示したい場合、
`VisWindow` を継承したクラスを `ui` パッケージに作成し、各 Screen の `show()` 内で
`stage.addActor(new XxxWindow(...))` を呼ぶだけで表示できる。

```java
// ui/ConfigWindow.java
public class ConfigWindow extends VisWindow {
    public ConfigWindow(PreferencesManager prefs, BitmapFont font) {
        super("タイトル");
        setSize(480, 280);
        centerWindow();   // 画面中央に配置
        addCloseButton(); // ×ボタンを自動追加
        setMovable(false);
        // ... VisTable でコンテンツを構築
    }
}

// 各 Screen の show() または ボタンリスナ内で
stage.addActor(new ConfigWindow(game.preferencesManager, font));
```

### 疎結合の保ち方

- ConfigWindow のコンストラクタには `CoreEngine` を渡さず、必要な依存（PreferencesManager, BitmapFont）だけを渡す
- これにより ConfigWindow は Screen や CoreEngine に無関係に再利用可能になる

### VisSelectBox による選択肢 UI

設定値の切り替えには `VisSelectBox<String>` が最適。
ラベル文字列と実際の数値を parallel array で管理すると見通しが良い：

```java
private static final String[] SPEED_LABELS = {"遅い", "普通", "早い"};
private static final float[]  SPEED_VALUES = {0.5f, 1.0f, 3.0f};

final VisSelectBox<String> speedBox = new VisSelectBox<>();
speedBox.setItems(SPEED_LABELS);
speedBox.setSelected(labelForSpeed(currentSpeed));
```

### 保存ボタンのリスナ内で値を変換して永続化

```java
saveButton.addListener(new ChangeListener() {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        float speed = speedForLabel(speedBox.getSelected());
        prefsManager.setTextSpeed(speed);
        remove(); // VisWindow を stage から取り除く
    }
});
```

`remove()` で VisWindow を stage から削除できる（dispose 不要）。

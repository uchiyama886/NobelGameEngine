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

## 2026-05-01: TypingLabel を VisTable でレイアウトするパターン

### パターン: TypingLabel の折り返しは setWrap(true) + VisTable セル幅で実現する

`TypingLabel` を絶対座標（`setPosition`）で配置すると幅が確定せず `setWrap(true)` が機能しない。
`VisTable` のセルに `.expandX().fillX()` で追加することで幅が確定し、折り返しが正しく機能する。

```java
lineLabel = new TypingLabel("", labelStyle);
lineLabel.setWrap(true);  // ← VisTable のセル幅が確定して初めて機能する

VisTable msgTable = new VisTable();
msgTable.setFillParent(true);
msgTable.bottom();
msgTable.add(nameLabel).padLeft(200).padBottom(4).left().expandX().fillX().row();
msgTable.add(lineLabel).pad(20).padLeft(160).expandX().fillX().padBottom(30).row(); // 顔画像（約150px）を避けるため左余白を追加
stage.addActor(msgTable);
```

### 日本語フォント文字セットの設定

`FreeTypeFontLoaderParameter` に `characters` を設定しないと日本語が豆腐になる。
`Config.JAPANESE_CHARS` に文字セットを一元管理し、`GlobalAssets.loadAllAssets()` で適用する：

```java
fontParams.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS + Config.JAPANESE_CHARS;
```

## UI実装時のデザインシステム準拠ルール

### パターン: マジックナンバーを避け、design-system.md に準拠する
UIコンポーネント（余白、サイズ、色、タイポグラフィ）を実装または修正する際は、必ず `design-system.md` に定義されたトークンや制約事項を確認すること。
- 色やフォントサイズなどのハードコードを避け、既存のカラートークンや共通設定（Config等）にマッピングできないか検討する。
- 新しい画面やUIパーツを作る際は、定義済みの `pad`（余白）や `gap` のスケールを再利用して一貫性を保つ。

## 2026-05-02: 動的UI機能パターン（遅延表示・スクロール開閉）

### パターン: TypingAdapter で選択肢の遅延表示を実現する

`lineLabel.setTypingListener(new TypingAdapter() { @Override public void end() { uiTable.setVisible(true); } })` とし、`updateChoices()` の先頭で `uiTable.setVisible(false)` を設定することで、テキスト表示完了後に選択肢を出す。`TypingAdapter` は `TypingListener` の空実装アダプタであり `end()` だけオーバーライドできる（`event`, `onChar`, `replaceVariable` は不要）。

### パターン: VisWindow を使い回し setVisible で開閉する

ログウィンドウは毎回 `new VisWindow(...)` するのではなく、`show()` 内で `initLogWindow()` により一度だけ作成して `stage.addActor` しておき、`showLogWindow()` では内容テーブル（`logContentTable.clear()` → 再構築）を更新してから `logWindow.setVisible(true)` するパターンを採用。これにより `logScrollPane` をフィールドで保持でき、スクロール位置を参照できる。

### パターン: stage.addListener で全画面スクロールイベントを捕捉する

```java
stage.addListener(new InputListener() {
    @Override
    public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
        // amountY < 0: 上スクロール、amountY > 0: 下スクロール
        if (amountY < 0 && !logWindow.isVisible()) { showLogWindow(); return true; }
        if (amountY > 0 && logWindow.isVisible()
                && (logScrollPane.getScrollPercentY() >= 0.99f || logScrollPane.getMaxY() <= 0)) {
            logWindow.setVisible(false); return true;
        }
        return false;
    }
});
```

LibGDX 1.12.1 の `InputListener.scrolled` シグネチャは `(InputEvent, float x, float y, float amountX, float amountY)` の5引数版。`getMaxY() <= 0` はコンテンツがウィンドウに収まりスクロール不要な場合のエッジケース処理。

## 2026-05-02: FitViewport によるリサイズ対応パターン

### パターン: BaseScreen に FitViewport を組み込み、SpriteBatch も仮想座標系で描画する

`new Stage()` は物理ピクセル座標の `ScreenViewport` を使うため、ウィンドウリサイズ時に UI が崩れる。
`BaseScreen.show()` で `new Stage(new FitViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT))` に変更し、
`resize()` で `stage.getViewport().update(width, height, true)` を呼ぶことで全 Screen に共通適用できる。

SpriteBatch が Stage と異なる座標系で描くと内容がずれる。`render()` 内で以下を SpriteBatch 開始前に実行する：

```java
stage.getViewport().apply(true);                           // GL viewport をレターボックス領域に制限
game.batch.setProjectionMatrix(stage.getCamera().combined); // SpriteBatch を仮想座標系に合わせる
```

その後の SpriteBatch draw 呼び出しは `Config.VIRTUAL_WIDTH / VIRTUAL_HEIGHT`（1280/720）で描くと、
FitViewport のレターボックス領域に正確に収まる。
`setProjectionMatrix()` は `batch.begin()` の前に呼ぶこと（begin() 後は変更不可）。

### パターン: TextureRegionDrawable.tint() で Scene2D テーブル背景を着色する

SpriteBatch で直接描画していた半透明矩形（メッセージボックス等）を Scene2D に移植するには、
`TextureRegionDrawable(whitePixel).tint(new Color(...))` が最も簡潔。
返り値は `Drawable`（実体は `SpriteDrawable`）で、`VisTable.setBackground(Drawable)` に直接渡せる。
tint から生成した Drawable のパディング値はすべて 0 なので、テーブルのレイアウトに干渉しない。

```java
Drawable bg = new TextureRegionDrawable(new TextureRegion(assets.getWhitePixelTexture()))
    .tint(new Color(0.04f, 0.04f, 0.12f, 0.88f));
container.setBackground(bg);
```

### パターン: メッセージウィンドウの水平レイアウト（Figma Auto Layout 再現）

顔画像と台詞テキストを横並びにするには、同一 `VisTable` に `.row()` を挟まず連続 `add()` する：

```java
VisTable msgContent = new VisTable();
msgContent.setBackground(msgBgDrawable);
msgContent.add(faceImage).size(140, 140).pad(35, 24, 24, 16).top();   // left cell
msgContent.add(textGroup).expandX().fillX().padTop(8);                 // right cell — NO .row()

// textGroup は縦積み（名前→台詞）
VisTable textGroup = new VisTable();
textGroup.top();
textGroup.add(nameLabel).padBottom(6).left().expandX().fillX().row();
textGroup.add(lineLabel).expandX().fillX().padRight(24).padBottom(36);
```

`faceImage` は `Image(new TextureRegionDrawable(...))` で生成し、SpriteBatch では描かない。
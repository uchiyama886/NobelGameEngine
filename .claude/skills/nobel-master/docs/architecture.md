# NobelEngine アーキテクチャルール

## パッケージ構成

```
com.uchiyama.nobelengine
├── CoreEngine.java          # エントリポイント。Game を継承。
├── core/                    # エンジン横断の定数・グローバルリソース
│   ├── Config.java          # static final 定数のみ（パス・設定値）
│   └── GlobalAssets.java    # AssetManager ラッパー。テクスチャ・フォント管理
├── scene/                   # 画面（Screen）実装
│   ├── BaseScreen.java      # 全 Screen の基底。Stage 管理を担う
│   ├── TitleScreen.java
│   └── GameScreen.java
├── system/                  # ゲームロジック・データ管理
│   └── ScenarioManager.java # JSON 読み込み・シーン進行・履歴管理
└── ui/                      # 描画補助・VisUI ウィジェット
    └── Renderer.java        # SpriteBatch を使った低レベル描画
```

## 配置ルール

| 追加するもの | 配置先パッケージ | 備考 |
|---|---|---|
| 新しい画面 (Screen) | `scene` | `BaseScreen` を継承すること |
| ゲームロジック・データ管理 | `system` | LibGDX 依存を最小化する |
| 描画補助・VisUI ウィジェット | `ui` | `Scene2d.ui` は使わず VisUI のみ |
| グローバル定数 | `core/Config.java` | インスタンス化不可（private コンストラクタ） |
| グローバルアセット | `core/GlobalAssets.java` | `dispose()` を必ず実装 |

## BaseScreen 継承ルール

```java
public class MyScreen extends BaseScreen {
    public MyScreen(CoreEngine game) {
        super(game);          // CoreEngine への参照を親に渡す
    }

    @Override
    public void show() {
        super.show();         // Stage の初期化（必須）
        // VisUI ウィジェットを stage に追加する
    }

    @Override
    public void render(float delta) {
        clearScreen();        // GL バッファクリア（必須）
        stage.act(delta);
        stage.draw();
    }
    // hide() の override は不要（BaseScreen が Stage を dispose する）
}
```

## CoreEngine への登録ルール

新しいサブシステムを追加した場合、**必ずペアで実装**する：

```java
// create() に追加
mySystem = new MySystem();
mySystem.initialize();

// dispose() に追加
mySystem.dispose();
```

## VisUI 使用ルール

- `VisUI.load()` / `VisUI.dispose()` は `CoreEngine` のみが呼ぶ
- ウィジェット生成は `show()` 内で行い、`hide()` で `stage.dispose()` される連鎖に乗せる
- `Scene2d.ui`（`Table`, `Label` 等）と VisUI（`VisTable`, `VisLabel` 等）を混在させない

## Renderer の責務

`Renderer` は SpriteBatch を使った**低レベル描画**（背景・キャラクター・ウィンドウ）を担う。
テキスト描画・ボタン等の UI は VisUI ウィジェットに委ねること。

## GlobalAssets の責務

- `load(String characterData)` でシナリオから必要なアセットを非同期ロード
- テクスチャ / フォント / PixmapTexture（ウィンドウ背景）を一元管理
- アセットの追加は必ず `dispose()` に対応する解放処理を追加する

## 命名規約

| 種別 | 規約 | 例 |
|---|---|---|
| Screen クラス | `XxxScreen` | `GameScreen`, `SaveScreen` |
| Manager クラス | `XxxManager` | `ScenarioManager`, `AudioManager` |
| 定数 | UPPER_SNAKE_CASE | `SCENARIO_FILE`, `FONT_SIZE` |
| フィールド | lowerCamelCase | `scenarioManager`, `globalAssets` |

## ビルド・実行

```bash
./gradlew desktop:run   # デスクトップで実行
./gradlew core:build    # core モジュールのみビルド
```

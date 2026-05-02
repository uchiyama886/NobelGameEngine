# NobelEngine Design System

> **対象バージョン:** v0.1.0  
> **仮想解像度:** 1280 × 720 px (`Config.VIRTUAL_WIDTH / VIRTUAL_HEIGHT`)  
> **レンダリング基盤:** LibGDX + VisUI (Scene2D)  
> **データソース:** ソースコード実測値（`Config.java`, `Renderer.java`, `TitleScreen.java`, `GameScreen.java`）

---

## 1. カラートークン

| トークン名 | 用途 | Hex | LibGDX 値 | 不透明度 |
| :--- | :--- | :--- | :--- | :--- |
| `color-title` | タイトル文字 | `#FFD700` | `Color.GOLD` | 100% |
| `color-divider` | タイトル区切り線 | `#FFD700` | `(1f, 0.84f, 0f, 0.7f)` | 70% |
| `color-subtitle` | サブタイトル文字 | `#BFBFE5` | `(0.75f, 0.75f, 0.9f, 1f)` | 100% |
| `color-version` | バージョンラベル | `#7F7F99` | `(0.5f, 0.5f, 0.6f, 1f)` | 100% |
| `color-char-name` | キャラクター名 | `#FFE533` | `(1f, 0.9f, 0.2f, 1f)` | 100% |
| `color-dialog-text` | 台詞テキスト | `#FFFFFF` | `Color.WHITE` | 100% |
| `color-overlay-title` | タイトル背景オーバーレイ | `#12122E` | `(0.07f, 0.07f, 0.18f, 0.75f)` | 75% |
| `color-overlay-msgbox` | メッセージボックス背景 | `#0A0A1F` | `(0.04f, 0.04f, 0.12f, 0.88f)` | 88% |

---

## 2. タイポグラフィ

| トークン名 | フォント | サイズ | 用途 | 備考 |
| :--- | :--- | :--- | :--- | :--- |
| `font-default` | NotoSansJP-Regular.ttf | 24 px | 全UI共通ベース | BitmapFont (FreeType生成) |
| `font-title` | NotoSansJP-Regular.ttf | 48 px 相当 | タイトルロゴ | `setFontScale(2.0f)` で倍率適用 |
| `font-char-name` | NotoSansJP-Regular.ttf | 24 px | キャラクター名 | `color-char-name` (#FFE533) |
| `font-dialog` | NotoSansJP-Regular.ttf | 24 px | 台詞 (TypingLabel) | `setWrap(true)` で折り返し有効 |
| `font-ui` | NotoSansJP-Regular.ttf | 24 px | ボタン・ログ本文 | VisUI スキン上書き |

**文字セット:**  
`FreeTypeFontGenerator.DEFAULT_CHARS` + 平仮名・片仮名・濁音・半濁音・主要漢字・記号（`Config.JAPANESE_CHARS` 参照）。日本語混植に対応するため Bitmap 生成時に全文字を事前ラスタライズ。

---

## 3. コンポーネント仕様

| コンポーネント名 | 役割 / 状態 | 余白 (Padding / Gap) | サイズ制約 | 色 (Hex / Token) | タイポグラフィ / 特記事項 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TitleLabel** | タイトル文字 | `padBottom: 12` | — | `color-title` (#FFD700) | `font-title` (×2.0 scale), 中央揃え (Table デフォルト) |
| **SubtitleLabel** | サブタイトル | `padBottom: 20` | — | `color-subtitle` (#BFBFE5) | `font-default` 24px, 中央揃え |
| **GoldDivider** | タイトル区切り | `padBottom: 50` | w: 200px, h: 2px | `color-divider` (#FFD700, 70%) | 1×1 ホワイトピクセルを着色した Image |
| **StartButton** | ゲーム開始アクション | `padBottom: 16` | w: 300px, h: 60px | VisUI デフォルトスキン | `font-ui` 24px, 中央揃え |
| **ExitButton** | アプリ終了アクション | — | w: 300px, h: 60px | VisUI デフォルトスキン | `font-ui` 24px, 中央揃え |
| **VersionLabel** | バージョン表示 | `padRight: 16, padBottom: 12` | — | `color-version` (#7F7F99) | `font-default` 24px, 右下固定配置 |
| **MessageBox** | 台詞表示パネル (オーバーレイ) | — | 画面下端アンカー、高さ自動 | `color-overlay-msgbox` (#0A0A1F, 88%) | `TextureRegionDrawable.tint()` で VisTable background として設定（Scene2D） |
| **FaceImage** | 発話キャラクター顔 | `pad(35, 24, 24, 16)` (top, left, bottom, right) | size: 140×140 (仮想座標 px) | — | Scene2D `Image` アクターとして msgContent VisTable の左セルに配置 |
| **CharNameLabel** | キャラクター名 | `padBottom: 6` | expandX, fillX | `color-char-name` (#FFE533) | `font-char-name` 24px, 左揃え (`left()`)。textGroup VisTable の1行目 |
| **DialogLineLabel** | 台詞本文 (TypingLabel) | `padRight: 24, padBottom: 36` | expandX, fillX | `color-dialog-text` (#FFFFFF) | `font-dialog` 24px, `setWrap(true)`, 左揃え。textGroup VisTable の2行目 |
| **LogButton** | バックログ表示トリガー | `padRight: 16, padTop: 16` (画面端から) | w: 80px, h: 36px | VisUI デフォルトスキン | `font-ui` 24px, 右上角固定 |
| **ChoiceButton** | 選択肢 | `padBottom: 12` (行間) | w: 400px, h: 52px | VisUI デフォルトスキン | `font-ui` 24px, 中央揃え (Table デフォルト) |
| **LogWindow** | バックログフルスクリーンオーバーレイ | `contentPad: top=24, left/right=40, bottom=24` (VisTable) | 画面全体 (0,0)〜(w,h) | VisUI VisWindow スキン | タイトル: "BACK LOG", movable: false, 閉じるボタンあり |
| **LogRow** | バックログ 1 行 | `jumpBtnPadRight: 20, rowPadBottom: 30` | JUMP button w: 120, h: 40 / テキスト: expandX, fillX | `color-dialog-text` (#FFFFFF) | テキスト `setWrap(true)`, top揃え。形式: `char_name + " : " + line` (TypingLabel タグ除去済み) |
| **BackToTitleButton** | タイトルへ戻るアクション | `padBottom: 33` | VisUI デフォルト | VisUI デフォルトスキン | `font-ui` 24px, ログウィンドウ最上部 |
| **CharacterSprite** | キャラクター立ち絵 | — | w: 画面幅 × (509/1280), h: 画面高さ (全高) | — | charX = w × (386/1280), SpriteBatch 直描画 |
| **BackgroundImage** | フルスクリーン背景 | — | 画面全体 (0,0)〜(w,h) | — | SpriteBatch 直描画。両画面で共有 (`IMG_BG_TEST`) |

---

## 4. 構造の再定義

### 4-1. TitleScreen

タイトル画面の全体コンテナは `table.setFillParent(true)` により仮想解像度（1280×720）全体に展開され、デフォルトの中央配置（Scene2D Table は center/center）が適用されます。内部は縦方向の積み上げ（Vertical Stack）で構成され、要素間は各 `padBottom` で離隔します（12→20→50→16→0 の順序）。テキストおよびボタンはすべてテーブル列幅に対して中央揃えとなります。

背景の暗色オーバーレイ（`color-overlay-title`、75%）は `SpriteBatch` で Stage の下に描画されるため、Scene2D アクターとは完全に分離されています。バージョンラベルのみ `setPosition()` による絶対座標配置（右下：x = w - prefWidth - 16、y = 12）を使用します。

**スクロール:** 発生しません。

---

### 4-2. GameScreen — メッセージレイヤー

ゲーム画面の UI は 2 枚の VisTable（`msgTable`、`uiTable`）と 1 つの VisTextButton（`logOpenButton`）を同一 Stage に重ねています。

**msgTable 構造（Scene2D のみ / Figma Auto Layout 再現）:**

```
msgTable (setFillParent + bottom)
└── msgContent (VisTable, background = msgBgDrawable)
    ├── faceImage (Image, size=140×140, pad(35,24,24,16), top)
    └── textGroup (VisTable, expandX fillX, padTop=8, top)
        ├── nameLabel (Label, padBottom=6, left, expandX fillX)
        └── lineLabel (TypingLabel, expandX fillX, padRight=24, padBottom=36)
```

`faceImage` と `textGroup` は **同一行**（連続 `add()` で `.row()` を挟まない）。これにより Figma の水平 Auto Layout を再現。

**MessageBox 背景（Scene2D）:**  
`TextureRegionDrawable(whitePixel).tint(new Color(0.04f, 0.04f, 0.12f, 0.88f))` で作成した `Drawable` を `msgContent.setBackground()` に設定。SpriteBatch は使わない。

**uiTable（選択肢）:**  
`setFillParent(true)` で全体展開、デフォルトで中央揃え。選択肢ボタンは幅 400px で `padBottom: 10` の縦積み。

**logOpenButton:**  
`setPosition(Config.VIRTUAL_WIDTH - 80 - 16, Config.VIRTUAL_HEIGHT - 36 - 16)` による仮想座標絶対配置。

**スクロール:** ゲーム画面本体にスクロールはありません。

---

### 4-3. GameScreen — バックログウィンドウ

バックログは `VisWindow` をフルスクリーン（位置 (0,0)、サイズ = 画面全体）で Stage に追加することで実現。ウィンドウは `movable: false`。

内部は `VisScrollPane` でラップされた `VisTable`（縦方向、`top().pad(40)` ）。スクロール設定：

- 水平スクロール: **無効** (`setScrollingDisabled(true, false)`)
- 垂直スクロール: **有効**
- フリックスクロール: **有効** (`setFlickScroll(true)`)
- スクロールバー自動フェード: **無効** (`setFadeScrollBars(false)`)（常時表示）

ログ行はキャラクター名 + `" : "` + 台詞テキスト（TypingLabel のシェイク/カラータグを正規表現で除去した平文）を表示します。

---

## 5. アセット一覧

| パス | 種別 | 用途 |
| :--- | :--- | :--- |
| `images/test_background.png` | Texture | 全画面背景（タイトル・ゲーム共通） |
| `images/test_character.png` | Texture | キャラクター立ち絵（右寄せ） |
| `images/test_face.png` | Texture | メッセージボックス内フェイス画像 |
| `fonts/NotoSansJP-Regular.ttf` | FreeType | 全 UI 共通フォント（24px BitmapFont 生成） |
| `data/scenario.json` | JSON | シナリオデータ（id / char_name / line / choices） |

---

## 6. TypingLabel タグ仕様（シナリオ記法）

`scenario.json` の `line` フィールドに使用可能なインラインタグ。

| タグ | 効果 |
| :--- | :--- |
| `{SHAKE}...{ENDSHAKE}` | テキストシェイクエフェクト |
| `{WAIT=N}` | N 秒間テキスト送りを停止 |
| `{COLOR=name}...{CLEARCOLOR}` | 指定色でテキストを着色し、CLEARCOLOR で解除 |

バックログ表示時はこれらのタグを正規表現 `\{.*?\}` で除去して平文に変換します。

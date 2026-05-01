# scenario.json スキーマとマークアップ仕様

## ファイルパス

```
assets/data/scenario.json
```

## シーンノードのスキーマ

```json
{
  "id":        "string  — 一意のシーンID（必須）",
  "char_name": "string  — 話者名（必須）",
  "line":      "string  — セリフ（必須）。マークアップタグ使用可",
  "nextId":    "string  — 次シーンのID（選択肢なし直進の場合）",
  "choices":   "array   — 選択肢（nextId と排他。両方同時に定義しない）"
}
```

### choices 要素のスキーマ

```json
{
  "text":   "string — 選択肢ラベル（必須）",
  "nextId": "string — 遷移先シーンID（必須）"
}
```

## ID 命名規約

| パターン | 用途 | 例 |
|---|---|---|
| `scene_NNN` | 数値連番の汎用シーン | `scene_001`, `scene_042` |
| `scene_<場所>_<状態>` | 場所・状態を表すシーン | `scene_room_enter`, `scene_home` |
| `scene_end_<ラベル>` | エンディング | `scene_end_good`, `scene_end_bad` |

## マークアップタグ一覧

| タグ | 効果 | 例 |
|---|---|---|
| `{WAIT=秒数}` | 指定秒数のウェイト | `{WAIT=1}`, `{WAIT=0.5}` |
| `{SHAKE}…{ENDSHAKE}` | 範囲内テキストを振動表示 | `{SHAKE}どきっ{ENDSHAKE}` |
| `{COLOR=色名}…{CLEARCOLOR}` | 範囲内テキストに色付け | `{COLOR=cyan}出発しよう{CLEARCOLOR}` |

### 使用可能な色名（LibGDX Color 定数）

`red`, `green`, `blue`, `white`, `black`, `yellow`, `cyan`, `magenta`, `orange`, `pink`, `gold`, `gray`

## バリデーションチェックリスト

新しいシーンを追加・変更するたびに以下を確認する：

- [ ] `id` がファイル内で重複していない
- [ ] `nextId` または `choices` のどちらか一方のみを持つ（末端シーンはどちらも持たない）
- [ ] すべての `nextId`（直接 / choices 内）が実在する `id` を参照している
- [ ] `char_name` が空文字でない
- [ ] `{SHAKE}…{ENDSHAKE}` と `{COLOR=…}…{CLEARCOLOR}` が正しく対になっている
- [ ] `{WAIT=N}` の N が正の数値である

## 記述例

### 直進シーン（選択肢なし）

```json
{
  "id": "scene_room_enter",
  "char_name": "主人公",
  "line": "部室のドアをそっと開けると……{WAIT=1}中から物音が聞こえた。",
  "nextId": "scene_room_heroine_appear"
}
```

### 選択肢あり

```json
{
  "id": "scene_001",
  "char_name": "主人公",
  "line": "{SHAKE}どこへ行こう{ENDSHAKE}？{WAIT=0.5}{COLOR=cyan}部室か、それとも帰るか。{CLEARCOLOR}",
  "choices": [
    { "text": "部室へ行く", "nextId": "scene_room_enter" },
    { "text": "帰宅する",   "nextId": "scene_home" }
  ]
}
```

### 末端シーン（エンディング）

```json
{
  "id": "scene_end_good",
  "char_name": "ナレーター",
  "line": "こうして、ふたりの物語は始まった。{WAIT=2}{COLOR=gold}— END —{CLEARCOLOR}"
}
```

## ScenarioManager との対応

| JSON フィールド | ScenarioManager のメソッド |
|---|---|
| `id` | `sceneMap` のキー、`jumpTo(id)` / `rollbackTo(index)` |
| `char_name` + `line` | `getCurrentName()` / `getCurrentLine()` |
| `nextId` | `hasNext()` → `advance()` |
| `choices` | `getChoices()` |

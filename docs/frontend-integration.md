# フロントエンド連携

このバックエンドは別リポジトリのHTML / CSS / JavaScriptフロントエンドから呼び出されることを想定しています。

## API一覧

- `GET /api/schedules`
- `GET /api/schedules/{date}/slots`
- `POST /api/reservations`

## リクエストJSON例

予約:

```json
{
  "inquiryOnly": false,
  "reservationDate": "2026-06-06",
  "reservationTime": "11:00",
  "reservationCount": 2,
  "customerFamilyName": "山田",
  "customerGivenName": "花子",
  "customerFamilyKana": "ヤマダ",
  "customerGivenKana": "ハナコ",
  "customerEmail": "hanako@example.com",
  "customerTel": "09012345678",
  "customerMessage": "友人と参加します",
  "privacyAccepted": true
}
```

問い合わせ:

```json
{
  "inquiryOnly": true,
  "customerFamilyName": "山田",
  "customerGivenName": "花子",
  "customerFamilyKana": "ヤマダ",
  "customerGivenKana": "ハナコ",
  "customerEmail": "hanako@example.com",
  "customerTel": "09012345678",
  "customerMessage": "開催内容について質問があります",
  "privacyAccepted": true
}
```

## レスポンスJSON例

```json
[
  {
    "date": "2026-06-06",
    "open": true,
    "totalCapacity": 30,
    "reservedCount": 0,
    "remainingCount": 30,
    "fullyBooked": false
  }
]
```

```json
[
  {
    "slotId": 1,
    "startTime": "11:00",
    "endTime": "12:00",
    "capacity": 10,
    "reservedCount": 0,
    "remainingCount": 10,
    "active": true,
    "fullyBooked": false
  }
]
```

```json
{
  "type": "reservation",
  "reservationCode": "WS-20260606-0001",
  "message": "予約を受け付けました"
}
```

## フォーム項目とDTO

| フロント側項目 | バックエンドDTO |
| --- | --- |
| `inquiry_only` | `inquiryOnly` |
| `reservation_date` | `reservationDate` |
| `reservation_time` | `reservationTime` |
| `reservation_count` | `reservationCount` |
| `customer_family_name` | `customerFamilyName` |
| `customer_given_name` | `customerGivenName` |
| `customer_family_kana` | `customerFamilyKana` |
| `customer_given_kana` | `customerGivenKana` |
| `customer_email` | `customerEmail` |
| `customer_tel` | `customerTel` |
| `customer_message` | `customerMessage` |
| `privacy` | `privacyAccepted` |

## snake_case と camelCase

バックエンドDTOはcamelCaseです。主要項目は `@JsonAlias` でsnake_caseにも対応しているため、既存フロントエンドがフォーム名に近いsnake_case JSONを送っても受け取れます。新規実装ではcamelCaseで送ることを推奨します。

## CORS

開発用の許可オリジンは `application.yml` で管理します。

```yaml
app:
  cors:
    allowed-origins:
      - http://localhost:3000
      - http://localhost:5500
      - http://127.0.0.1:5500
```

本番環境では実際に利用するフロントエンドのオリジンだけに絞ってください。

## エラーレスポンス

```json
{
  "message": "予約人数は1〜10名で指定してください"
}
```

主なエラー:

- 必須項目不足
- メール形式不正
- 予約枠なし
- 満席
- 予約人数不正
- プライバシー未同意
- システム設定不足

## Spring Boot一体型へ統合する場合

```text
frontend/index.html
-> backend/src/main/resources/templates/index.html

frontend/css/
-> backend/src/main/resources/static/css/

frontend/js/
-> backend/src/main/resources/static/js/

frontend/images/
-> backend/src/main/resources/static/images/
```

HTML内の静的ファイル参照はThymeleaf形式へ変更します。

```html
<link rel="stylesheet" th:href="@{/css/style.css}">
<script th:src="@{/js/main.js}"></script>
<img th:src="@{/images/sample.jpg}">
```

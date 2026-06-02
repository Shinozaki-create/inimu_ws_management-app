# ワークショップ予約サイト バックエンド

Java / Spring Boot で実装したワークショップ予約サイトのバックエンド専用リポジトリです。公開LPなどのフロントエンドは別リポジトリで動かし、このアプリの `/api/**` を呼び出す構成を想定しています。管理画面は Thymeleaf で `/admin/**` に実装しています。

## 技術構成

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- Bean Validation
- H2 / MySQL
- Maven
- Lombok

## セットアップ

```bash
./mvnw test
./mvnw spring-boot:run
```

Windows PowerShell では以下です。

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## H2での起動

デフォルト設定はH2です。起動後、以下で確認できます。

- アプリ: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:inimuws`
- User: `sa`
- Password: 空

DBは `src/main/resources/schema.sql` と `data.sql` で初期化されます。

## MySQLでの起動

`application.yml` の `mysql` プロファイルを使います。

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

接続先は必要に応じて以下を変更してください。

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

## 管理者ログイン

- URL: `http://localhost:8080/admin/login`
- Email: `admin@example.com`
- Password: `password`

パスワードは `data.sql` でBCryptハッシュとして保存しています。

## 公開API

- `GET /api/schedules`
- `GET /api/schedules/{date}/slots`
- `POST /api/reservations`

予約登録と問い合わせ登録はどちらも `POST /api/reservations` で受けます。`inquiryOnly=true` の場合は `inquiries` に保存します。

## フロントエンド連携

JSONはcamelCaseを標準にしていますが、主要項目は `@JsonAlias` でsnake_caseも受け取れます。

| フロント側項目 | DTO |
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

詳細は `docs/frontend-integration.md` を参照してください。

## CORS設定

許可オリジンは `application.yml` で変更できます。

```yaml
app:
  cors:
    allowed-origins:
      - http://localhost:3000
      - http://localhost:5500
      - http://127.0.0.1:5500
```

## テスト

```powershell
.\mvnw.cmd test
```

現在は以下を確認しています。

- アプリケーションコンテキスト起動
- 管理者ログイン
- 管理画面主要ページの描画
- 公開APIの開催日、時間枠、予約、問い合わせ
- 予約時の残席加算
- 満席時エラー
- キャンセル時の `reserved_count` 減算

## 将来的なフロントエンド統合メモ

フロントエンドをSpring Boot側へ統合する場合は、以下のように移動します。

```text
frontend/index.html -> src/main/resources/templates/index.html
frontend/css/       -> src/main/resources/static/css/
frontend/js/        -> src/main/resources/static/js/
frontend/images/    -> src/main/resources/static/images/
```

HTML内の静的パスはThymeleaf形式へ変更してください。

```html
<link rel="stylesheet" th:href="@{/css/style.css}">
<script th:src="@{/js/main.js}"></script>
<img th:src="@{/images/sample.jpg}">
```
# inimu_ws_management-app

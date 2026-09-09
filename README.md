# BountyOS

**BountyOS** 是一个**只读**的移动端漏洞赏金运营控制台，面向漏洞赏金猎人（Bug Bounty Researcher）。

它是一个原生 Android 应用，让研究者能在手机上查看并同步自己在 HackerOne、Bugcrowd 等平台上的数据。它**不会**提交、修改、删除、评论或对平台数据进行任何写入操作——BountyOS 只是一个查看与同步客户端，不是漏洞扫描器，不是 AI 助手，也不是赏金提交平台。

## 目录

- [设计目标](#设计目标)
- [核心原则](#核心原则)
- [架构](#架构)
- [数据模型](#数据模型)
- [状态归一化](#状态归一化)
- [本地数据库 Schema](#本地数据库-schema)
- [平台集成](#平台集成)
- [同步机制](#同步机制)
- [通知机制](#通知机制)
- [凭证安全](#凭证安全)
- [界面功能](#界面功能)
- [本地化](#本地化)
- [技术栈](#技术栈)
- [目录结构](#目录结构)
- [环境要求](#环境要求)
- [构建与测试](#构建与测试)
- [已知限制](#已知限制)
- [常见问题](#常见问题)
- [授权](#授权)

## 设计目标

漏洞赏金猎人在外出、通勤或不方便打开电脑时，仍然需要查看自己报告的状态、严重程度、奖励与平台动态。现有平台没有针对移动端的只读查看体验，而网页版在手机上操作繁琐。

BountyOS 解决的核心问题：

1. **聚合查看**：在一个应用内同时查看 HackerOne 与 Bugcrowd 的数据，无需切换多个平台。
2. **离线可用**：数据缓存在本地，无网络时仍能查看历史报告。
3. **隐私与安全**：凭证加密存储、不经过任何第三方服务器，数据始终留在设备本地。
4. **注意力聚焦**：自动识别需要研究者行动的提交（待补充信息、复测中）。

## 核心原则

| 原则 | 说明 |
|---|---|
| **无后端** | 应用直接与平台官方 API 通信，不存在 BountyOS 自有的服务器、数据库或云函数 |
| **只读** | 仅暴露读取操作，架构上刻意不提供任何写入方法 |
| **本地优先** | Room/SQLite 是 UI 的唯一本地数据源，平台 API 是远程事实来源 |
| **安全** | 凭证经 Android Keystore 加密，绝不进入 Room、日志或明文持久化 |
| **反幻觉** | 只实现官方文档已确认的 API 端点，未确认的部分明确标注 `UNVERIFIED` |

## 架构

```
                     BOUNTYOS (Android)
                            |
              +-------------+--------------+
              |                            |
         Local Storage                 Provider APIs
              |                            |
          Room/SQLite              +---------+---------+
              |                     |                   |
              |                  HackerOne           Bugcrowd
              |                     API                 API
              |                     |                   |
              +---------------------+-------------------+
                                    |
                              Domain Models
                                    |
                               ViewModels
                                    |
                              Jetpack Compose
```

数据流采用严格的单向依赖：

```
Provider API → Remote DTO → Mapper → Domain → Room → Repository → ViewModel → Compose
```

### 分层职责

| 层 | 包路径 | 职责 |
|---|---|---|
| **domain** | `domain/` | 平台中立的领域模型、状态归一化、奖励聚合、只读 `BountyProvider` 抽象与仓库接口。**不依赖任何 Android 框架**，可独立单元测试。 |
| **data** | `data/` | Room 本地缓存、Keystore 凭证存储、Retrofit/OkHttp 远程客户端与 provider 适配器。 |
| **sync** | `sync/` | WorkManager 后台同步、变更检测、本地通知。 |
| **ui** | `ui/` | Jetpack Compose + Material 3，暗色安全操作台主题，五页底部导航。 |
| **di** | `di/` | Hilt 依赖注入模块，将接口绑定到具体实现。 |

### 依赖倒置

上层只依赖接口，具体实现通过 Hilt `@Binds` 注入。例如 `SubmissionRepository` 接口由 `SubmissionRepositoryImpl` 实现，ViewModel 只看到接口，不感知 Room 或网络细节。

## 数据模型

领域层定义了与平台无关的核心模型。以 `Submission`（提交）为例：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | `String` | 本地唯一标识，由 `provider + externalId` 组合（如 `HACKERONE:182931`） |
| `provider` | `Provider` | 来源平台（`HACKERONE` / `BUGCROWD`） |
| `externalId` | `String` | 平台内部报告编号 |
| `programName` | `String?` | 所属项目名称 |
| `title` | `String` | 报告标题 |
| `status` | `SubmissionStatus` | 规范化状态 |
| `providerStatus` | `String` | 平台原始状态值（如 `needs-more-info`） |
| `severity` | `Severity?` | 规范化严重程度 |
| `providerSeverity` | `String?` | 平台原始严重程度（如 `critical` / `P1`） |
| `weakness` | `Weakness?` | 漏洞类型（名称 / CWE 编号 / 描述） |
| `submittedAt` | `Instant?` | 提交时间 |
| `updatedAt` | `Instant?` | 最近更新时间 |
| `resolvedAt` | `Instant?` | 解决/关闭时间 |
| `reward` | `Reward?` | 奖励（金额 + 货币） |
| `canonicalUrl` | `String?` | 平台报告页原始 URL |
| `vulnerabilityInformation` | `String?` | 漏洞信息正文（未解析的 Markdown） |

其余模型：

- `Program`：项目（平台、外部 ID、句柄、名称）。
- `Activity`：报告活动（执行者、类型、消息、时间戳）。
- `Reward`：奖励，金额以 `String` 存储避免浮点精度丢失，货币用 ISO 4217 代码。
- `Integration`：平台连接状态（非敏感元数据）。

## 状态归一化

不同平台的状态命名与语义不同，BountyOS 维护一套平台无关的规范状态，同时始终保留原始状态值，不丢失信息。

规范状态枚举 `SubmissionStatus`：

| 规范状态 | 含义 |
|---|---|
| `OPEN` | 已提交、尚未被平台处理 |
| `TRIAGED` | 已被平台确认有效（或进入待客户审查） |
| `ACTION_REQUIRED` | 需要研究者补充信息或采取行动 |
| `RETESTING` | 正在复测中 |
| `RESOLVED` | 已解决并关闭 |
| `REJECTED` | 被平台驳回 |
| `DUPLICATE` | 与已有报告重复 |
| `INFORMATIVE` | 有效信息但无需进一步处理 |
| `UNKNOWN` | 无法映射到已知状态 |

### HackerOne 状态映射

| 平台状态 | 规范状态 |
|---|---|
| `new` | `OPEN` |
| `pending-program-review` | `TRIAGED` |
| `triaged` | `TRIAGED` |
| `needs-more-info` | `ACTION_REQUIRED` |
| `retesting` | `RETESTING` |
| `resolved` | `RESOLVED` |
| `not-applicable` | `REJECTED` |
| `informative` | `INFORMATIVE` |
| `duplicate` | `DUPLICATE` |
| `spam` | `REJECTED` |

### Bugcrowd 状态映射

| 平台状态 | 规范状态 |
|---|---|
| `new` | `OPEN` |
| `triaged` | `TRIAGED` |
| `unresolved` | `TRIAGED`（已接受、待修复） |
| `resolved` | `RESOLVED` |
| `informational` | `INFORMATIVE` |
| `out-of-scope` | `REJECTED` |
| `not-reproducible` | `REJECTED` |
| `not-applicable` | `REJECTED` |

### 严重程度映射

严重程度采用 HackerOne 的 `severity_rating` 语义：`none` / `low` / `medium` / `high` / `critical` 分别映射到 `NONE` / `LOW` / `MEDIUM` / `HIGH` / `CRITICAL`。Bugcrowd 的 P1-P5 优先级在官方文档中未提供与上述级别的对应关系，因此归一化为 `UNKNOWN` 并保留原始优先级字符串。

## 本地数据库 Schema

数据库使用 Room，共五张表：

### `submissions`

| 列 | 类型 | 说明 |
|---|---|---|
| `id` | TEXT (PK) | `provider:externalId` |
| `provider` | TEXT | 平台名 |
| `external_id` | TEXT | 平台报告编号 |
| `program_name` | TEXT | 项目名 |
| `title` | TEXT | 标题 |
| `status` | TEXT | 规范状态名 |
| `provider_status` | TEXT | 原始状态 |
| `severity` | TEXT | 规范严重程度 |
| `provider_severity` | TEXT | 原始严重程度 |
| `weakness_name` / `weakness_cwe_id` / `weakness_description` | TEXT | 弱点信息 |
| `submitted_at` / `updated_at` / `resolved_at` | INTEGER | epoch 毫秒 |
| `reward_amount` / `reward_currency` | TEXT | 奖励 |
| `canonical_url` | TEXT | 报告 URL |
| `vulnerability_information` | TEXT | 漏洞正文 |

- 唯一索引：`UNIQUE(provider, external_id)` —— 保证不同平台即使数字 ID 相同也不会冲突。

### `programs`

- `id` (PK)、`provider`、`external_id`、`handle`、`name`
- 唯一索引：`UNIQUE(provider, handle)`

### `activities`

- `id` (PK)、`provider`、`submission_external_id`、`actor`、`type`、`message`、`timestamp`

### `integrations`

- `provider` (PK)、`connected`、`last_synced_at`
- 仅存非敏感元数据，凭证不在此表。

### `sync_state`

- `provider` (PK)、`cursor`、`last_synced_at`
- 记录分页游标，用于增量同步。

## 平台集成

### HackerOne

- **鉴权**：HTTP Basic Auth，`username:token`（API Token 作为密码）。
- **Base URL**：`https://api.hackerone.com/v1/`
- **数据格式**：JSON:API（jsonapi.org）。
- **速率限制**：读操作 600 次/分钟。
- **已实现端点**：
  - `GET /hackers/me/reports` —— 拉取当前用户报告列表（分页）。
  - `GET /hackers/reports/{id}` —— 拉取单个报告详情。

凭证获取：在 HackerOne 的 Settings 中生成 API Token（`docs.hackerone.com/hackers/api-token.html`），Token 标识与值分别作为 Basic Auth 的用户名与密码。

### Bugcrowd

- **鉴权**：`Authorization: Token <token>`，请求头 `Accept: application/vnd.bugcrowd+json`。
- **Base URL**：`https://api.bugcrowd.com/`
- **数据格式**：JSON:API 风格（`data.attributes`）。
- **速率限制**：60 次/分钟/IP。
- **已实现端点**：
  - `GET /submissions/{id}` —— 拉取单个提交详情。

凭证获取：登录 Bugcrowd 后进入 `tracker.bugcrowd.com/user/api_credentials` 页面创建 API 凭证。

## 同步机制

同步由 `DefaultSyncCoordinator` 执行，通过 WorkManager 调度。

流程：

1. 从 `integrations` 表读取所有 `connected = true` 的平台。
2. 对每个平台，调用 `fetchSubmissions(cursor)` 分页拉取全部提交。
3. 经 mapper 将领域模型转换为 Room 实体。
4. 以 `REPLACE` 策略批量写入 `submissions` 表。
5. 更新 `sync_state` 游标与 `integrations.last_synced_at`。

同步周期为 6 小时（`SyncScheduler`），避免频繁轮询以尊重平台速率限制。连接平台成功后会立即触发一次同步，让用户尽快看到数据。

## 通知机制

BountyOS 无后端，无法提供真正的实时推送。通知由后台同步在检测到数据变化后生成：

1. 同步前对提交做快照（`id → (status, rewardAmount)`）。
2. 执行同步。
3. 同步后比较快照，识别三类变化：
   - **新增提交**（`NEW_SUBMISSION`）
   - **状态变化**（`STATUS_CHANGED`）
   - **收到奖励**（`REWARD_RECEIVED`）
4. 通过 `NotificationHelper` 发出本地通知。

通知文案本地化，且不包含任何凭证信息。

## 凭证安全

- 平台凭证（HackerOne 的 `username:token`、Bugcrowd 的 `Token`）经 **Android Keystore** 使用 AES/GCM 加密。
- 加密密钥由 Keystore 硬件保护，不可导出；仅密文（IV + 密文，Base64）存入私有 `SharedPreferences`。
- 凭证**不进入** Room 数据库、崩溃日志、调试日志或任何明文持久化。
- 鉴权头由 OkHttp 拦截器在每次请求时动态读取并注入，不缓存到字段。
- Release 构建禁用 HTTP 日志；debug 构建的日志级别为 `BASIC`（仅方法 + URL，不含 header/body）。
- 「断开连接」会清除本地凭证，但不会尝试撤销平台凭证（除非官方 API 明确支持且 `rules.md` 允许）。

## 界面功能

采用 Material 3 暗色主题，视觉方向为「克制的安全操作台」：近黑 charcoal 背景、单一 terminal green 强调色、语义化状态色，无渐变、无霓虹、无 AI 风格堆砌。

底部导航共五个目的地：

| 目的地 | 功能 |
|---|---|
| **Dashboard** | 赏金总额（按货币分组，不隐式换算）、提交统计、HackerOne/Bugcrowd 计数、注意力项、最近动态。未连接平台时显示 onboarding 空状态。 |
| **Reports** | 跨平台提交列表，支持本地搜索（标题 / 编号 / 项目名）与 provider 筛选。 |
| **Triage** | Attention Inbox，只读筛选出需关注的提交（`ACTION_REQUIRED` / `RETESTING`）。 |
| **Activity** | 统一活动时间线，标注平台来源与报告编号。 |
| **More** | 次级入口（Settings 等）。 |

报告详情页展示：标题、状态（含原始平台状态）、严重程度、弱点/CWE、奖励、漏洞信息（纯文本展示，不执行 Markdown/HTML）、活动时间线，以及「在平台中打开」按钮（跳转官方报告页）。

触觉反馈：导航选择、连接等关键交互使用系统支持的 Haptic API，尊重系统设置。

## 本地化

支持三套语言，用户可见文本一律通过字符串资源引用，不硬编码：

| 语言 | 目录 | 区域 |
|---|---|---|
| English (UK) | `values/` | en-GB |
| Deutsch | `values-de/` | de |
| 简体中文 | `values-zh-rCN/` | zh-CN |

覆盖导航、状态、空状态、设置、连接提示、通知等全部用户可见文案。

## 技术栈

| 类别 | 技术 | 版本 |
|---|---|---|
| 语言 | Kotlin | 2.0.21 |
| UI | Jetpack Compose + Material 3 | BOM 2024.10.01 |
| 架构 | MVVM + 单向数据流 | — |
| 依赖注入 | Hilt | 2.52 |
| 本地存储 | Room | 2.6.1 |
| 网络 | Retrofit / OkHttp / kotlinx-serialization | 2.11.0 / 4.12.0 / 1.7.3 |
| 后台任务 | WorkManager | 2.9.1 |
| 导航 | Navigation Compose | 2.8.3 |
| 协程 | kotlinx-coroutines | 1.9.0 |
| 构建 | Gradle + AGP | 8.11.1 + 8.7.3 |

## 目录结构

```
BountyOS/
├── app/
│   ├── src/main/java/com/bountyos/
│   │   ├── domain/                 # 领域层（不依赖 Android 框架）
│   │   │   ├── model/              # Provider/Submission/SubmissionStatus/Severity/Weakness/Reward/Activity/Program/Integration/ProviderPage
│   │   │   ├── normalization/      # StatusNormalizer（状态映射）、AttentionCalculator（注意力计算）
│   │   │   ├── aggregation/        # RewardAggregator（奖励聚合）、DashboardStatsCalculator
│   │   │   ├── provider/           # BountyProvider（只读抽象接口）
│   │   │   └── repository/         # SubmissionRepository/IntegrationRepository/SyncCoordinator 接口
│   │   ├── data/
│   │   │   ├── local/              # Room：entity/dao/database + EntityMappers
│   │   │   ├── remote/             # Retrofit：hackerone/、bugcrowd/ 的 DTO、Api、Mapper、Provider、认证拦截器
│   │   │   ├── security/           # CredentialStore 接口 + KeystoreCredentialStore
│   │   │   └── repository/         # SubmissionRepositoryImpl/IntegrationRepositoryImpl/DefaultSyncCoordinator
│   │   ├── di/                     # DataModule、RepositoryModule、ProviderQualifiers
│   │   ├── sync/                   # SyncWorker、SyncScheduler、NotificationHelper、SubmissionChange
│   │   └── ui/                     # theme/、navigation/、components/、dashboard/、reports/、detail/、triage/、activity/、settings/
│   ├── src/main/res/               # 资源：values（en-GB）/values-de/values-zh-rCN 的 strings、colors、theme、图标
│   ├── src/test/                   # domain 层单元测试
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/libs.versions.toml       # Gradle 版本目录
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md                         # 主实现提示（未纳入版本控制）
└── rules.md                        # 工程规范（未纳入版本控制）
```

## 环境要求

- **JDK 17**（AGP 8.7 要求）
- **Android SDK**：compileSdk 34，需要 `android-34` 平台与 `build-tools 34.0.0`
- minSdk 26（Android 8.0），targetSdk 34

> 若系统默认 `JAVA_HOME` 不是 JDK 17，可在 `~/.gradle/gradle.properties` 中指定（仅影响 Gradle，不改 shell）：
>
> ```properties
> org.gradle.java.home=/path/to/jdk-17/Contents/Home
> ```

## 构建与测试

```bash
# 编译调试版 APK
./gradlew assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk

# 运行单元测试
./gradlew testDebugUnitTest

# 运行 Lint 静态检查
./gradlew lintDebug

# 编译（快速检查 Kotlin 编译）
./gradlew :app:compileDebugKotlin
```

单元测试覆盖 domain 层纯函数：`StatusNormalizerTest`、`AttentionCalculatorTest`、`RewardAggregatorTest`、`DashboardStatsCalculatorTest`。

## 已知限制

以下端点或映射在官方文档中尚未完全确认，当前返回空并标注 `UNVERIFIED`，待确认后补全：

- HackerOne 的 `activities` / `programs` / `rewards` 端点。
- Bugcrowd 的 submissions 列表分页、`activities` / `programs` / `rewards` 端点。
- Bugcrowd 严重程度 P1-P5 到 `low/medium/high/critical` 的映射。

## 常见问题

**Q：BountyOS 会替我提交报告或回复评论吗？**

不会。BountyOS 是严格只读的，架构上不提供任何写入方法。「在平台中打开」只是跳转到官方报告页，后续操作发生在平台侧，与 BountyOS 无关。

**Q：我的凭证会发送到哪？**

凭证只用于向平台官方 API 发起请求，保存在设备本地并经 Android Keystore 加密。不存在 BountyOS 服务器，数据不会上传到任何第三方。

**Q：为什么不同货币的奖励没有合并成一个总数？**

因为 BountyOS 不维护汇率信息，隐式换算会引入错误。不同货币分别展示，绝不编造汇率。

**Q：无网络时能用吗？**

能。已同步的数据缓存在本地 Room，离线时仍可查看，并显示离线/陈旧提示。

## 授权

本项目仅用于合法授权的漏洞赏金活动。使用前请确保你遵守各平台的条款与服务协议。

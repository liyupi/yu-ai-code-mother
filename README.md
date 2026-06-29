# AI 应用代码生成平台

一个面向 AI 应用开发场景的全栈项目：用户用自然语言描述需求，系统通过 AI 生成网站代码，并提供实时预览、对话式迭代、源码下载和应用部署能力。

> 本仓库用于展示罗任的全栈学习、工程实践与持续维护过程。项目来源及个人工作边界见文末“来源与声明”。

## 核心能力

- **AI 代码生成**：支持根据需求选择生成策略，以 SSE 流式返回生成过程。
- **对话式迭代**：保存应用对话历史，支持继续描述需求并更新生成结果。
- **在线预览与编辑**：前端展示生成页面，并提供应用信息编辑入口。
- **部署与下载**：可生成部署标识、访问静态结果，并下载完整源码。
- **管理能力**：包含用户、应用和对话历史管理，支持精选应用。
- **可观测性**：单体后端集成 Actuator 和 Prometheus 指标出口，仓库包含 Prometheus/Grafana 相关资源。

## 系统架构

仓库同时保留两种后端形态：

1. **单体版（默认）**：根目录 Spring Boot 应用，默认端口 `8123`，适合本地开发和功能验证。
2. **微服务版**：`yu-ai-code-mother-microservice/` 下的 Maven 多模块工程，按用户、应用、AI 和截图等职责拆分，使用 Nacos 与 Dubbo 协作。

详细说明见 [系统架构](docs/ARCHITECTURE.md)。

## 技术栈

| 领域 | 主要技术 |
| --- | --- |
| 前端 | Vue 3、TypeScript、Vite、Ant Design Vue、Pinia、Vue Router |
| 后端 | Java 21、Spring Boot 3.5、Spring MVC、Spring Session |
| AI | LangChain4j、LangGraph4j、OpenAI 兼容模型接口、DashScope |
| 数据 | MySQL、MyBatis-Flex、Redis、Redisson、Caffeine |
| 微服务 | Spring Cloud Alibaba、Nacos、Dubbo |
| 工程能力 | Selenium、腾讯云 COS、Knife4j、Actuator、Prometheus、Grafana |

## 快速开始

### 1. 环境准备

- JDK 21
- MySQL 8.x
- Redis 6.x 或更高版本
- Node.js 22 与 npm
- 至少一个可用的 OpenAI 兼容模型 API

如需运行微服务版，还需准备 Nacos，并根据需要启动对应服务模块。

### 2. 初始化数据库

在 MySQL 中执行 `sql/create_table.sql`。该脚本会创建 `yu_ai_code_mother` 数据库及项目所需表。

### 3. 配置本地环境

复制 `src/main/resources/application-prod-sample.yml` 中需要的配置项到被 `.gitignore` 排除的 `application-local.yml`，然后填写本地数据库、Redis、AI 服务及可选外部服务配置。

> 不要将 API Key、数据库密码、COS 密钥等真实凭据提交到仓库。

配置项清单见 [配置说明](docs/CONFIGURATION.md)。

### 4. 启动单体后端

```powershell
.\mvnw.cmd spring-boot:run
```

默认 API 根路径为 `http://localhost:8123/api`。

### 5. 启动前端

```powershell
cd yu-ai-code-mother-frontend
npm install
npm run dev
```

Vite 开发服务器会将 `/api` 请求代理到 `http://localhost:8123`。

## 项目结构

```text
.
├── src/                              # 单体版后端源码与测试
├── sql/                              # MySQL 初始化脚本
├── yu-ai-code-mother-frontend/       # Vue 3 前端
├── yu-ai-code-mother-microservice/   # 微服务多模块工程
├── grafana/                          # Grafana 配置资源
├── docs/                             # 架构、配置、部署与贡献说明
├── prometheus.yml                    # Prometheus 采集配置
└── pom.xml                           # 单体版 Maven 工程
```

## 文档

- [系统架构](docs/ARCHITECTURE.md)
- [配置说明](docs/CONFIGURATION.md)
- [部署指南](docs/DEPLOYMENT.md)
- [个人工作与贡献边界](docs/PERSONAL_CONTRIBUTIONS.md)
- [参与贡献](.github/CONTRIBUTING.md)

## 开发者

- **维护者**：罗任
- **GitHub**：[@lucky-Luor](https://github.com/lucky-Luor)

## 许可说明

当前仓库未包含可核验的开源许可证文件，因此不对代码的再分发或商业使用做额外授权声明。如需复用，请先核对原项目的授权条款。

## 来源与声明

本项目基于程序员鱼皮的 `yu-ai-code-mother` 教学项目进行学习、整理与个人化维护。原始功能设计与主要课程代码归原作者及相关权利人所有；本仓库的个人工作边界可通过 Git 历史与 [个人工作说明](docs/PERSONAL_CONTRIBUTIONS.md) 核对。

# 配置说明

## 配置策略

项目使用 Spring Profile 区分环境。仓库已忽略 `application-local.yml` 和 `application-prod.yml`，建议将真实凭据只放在这些未跟踪文件或外部密钥管理系统中。

`src/main/resources/application-prod-sample.yml` 可作为字段参考，但其中的占位值不能直接用于生产环境。

## 必需基础设施

| 配置组 | 作用 | 本地默认 |
| --- | --- | --- |
| `spring.datasource.*` | MySQL 连接 | `localhost:3306/yu_ai_code_mother` |
| `spring.data.redis.*` | Session、缓存与 AI 记忆 | `localhost:6379` |
| `server.port` | 单体后端 HTTP 端口 | `8123` |
| `server.servlet.context-path` | API 前缀 | `/api` |

请为 MySQL、Redis 设置独立强密码，生产环境不应继续使用示例账号或端口暴露策略。

## AI 模型

`langchain4j.open-ai` 下定义了多个模型用途：

- `chat-model`：普通对话生成。
- `streaming-chat-model`：流式生成。
- `reasoning-streaming-chat-model`：需要推理的流式任务。
- `routing-chat-model`：生成策略路由。

每组配置需要按实际供应商填写 `base-url`、`api-key` 和 `model-name`。模型必须兼容项目使用的 OpenAI 请求格式。

## 可选外部服务

| 配置组 | 用途 |
| --- | --- |
| `cos.client.*` | 对象存储域名、地域、存储桶和访问凭据 |
| `dashscope.*` | 阿里云图像生成服务 |
| `pexels.api-key` | Pexels 图片搜索 |
| `code.deploy-host` | 已部署应用的对外访问基地址 |

未使用的可选功能可不配置；启用对应功能前，需确认账号权限和费用限额。

## 微服务额外配置

微服务版还使用：

- `dubbo.registry.address`：Nacos 注册中心地址。
- `dubbo.protocol.*`：Dubbo Triple 协议和端口。
- `dubbo.consumer.*` / `dubbo.provider.*`：服务调用超时等策略。

仓库中的 Nacos 账号仅是本地开发默认值，生产环境必须替换并限制网络访问。

## 前端配置

前端使用 `VITE_API_BASE_URL` 指定 API 基础地址：

```dotenv
VITE_API_BASE_URL=https://example.com/api
```

本地开发未设置时，代码默认使用 `http://localhost:8123/api`。

## 提交前安全检查

- 不提交真实 API Key、Secret ID、Secret Key、Cookie 或 Token。
- 不提交生产数据库、Redis 或 Nacos 凭据。
- 不在前端 `VITE_*` 变量中保存秘密，这些值会被打包到浏览器代码。
- 不将本地生成的 `tmp/`、`target/` 或前端构建产物当作源码提交。

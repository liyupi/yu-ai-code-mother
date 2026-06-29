# 部署指南

## 部署边界

本文档提供与当前仓库一致的通用检查清单。仓库没有提供 Docker Compose、Kubernetes 或一键发布脚本，具体进程托管、网关和密钥管理方式需由部署环境决定。

## 部署前准备

- JDK 21 和可用的 Maven Wrapper 环境。
- Node.js 22 与 npm。
- 已初始化表结构的 MySQL。
- 启用认证且仅限可信网络访问的 Redis。
- 已申请且设置限额的 AI 与可选外部服务凭据。
- 生产域名、HTTPS 证书与反向代理。
- 用于保存生成代码和部署结果的持久化磁盘空间。

## 单体后端

### 构建

```powershell
.\mvnw.cmd clean package
```

构建会执行测试。部分集成测试依赖数据库、Redis、AI 或浏览器环境，发布前需在目标环境单独确认。

### 运行要点

- 通过外部 `application-prod.yml` 或环境配置注入生产参数。
- 启用 `prod` Profile，不要依赖仓库中的本地默认密码。
- 将 `/api` 代理到后端 `8123` 端口，不直接暴露应用端口。
- 为 SSE 接口关闭代理缓冲并配置足够的长连接超时。
- 仅对监控系统开放 Actuator/Prometheus 端点。

## 前端

```powershell
cd yu-ai-code-mother-frontend
npm install
npm run type-check
npm run build
```

将 `dist/` 作为静态站点发布。构建前通过 `VITE_API_BASE_URL` 设置生产 API 基础地址。

前端使用 History 路由，静态服务器需将未匹配的页面路径回退到 `index.html`。

## 微服务版

```powershell
cd yu-ai-code-mother-microservice
..\mvnw.cmd clean package
```

微服务版需要额外完成：

1. 部署并加固 Nacos，替换示例账号。
2. 为各可运行模块配置独立的数据库、Redis、日志和服务端口。
3. 限制 Dubbo 端口只能被服务网络访问。
4. 通过 API 网关或反向代理对外提供统一入口。
5. 按服务依赖顺序启动，并通过 Nacos 确认实例注册状态。

## 发布验收

- 注册、登录、退出与会话保持正常。
- 应用创建、SSE 对话、预览、下载和部署链路正常。
- 管理员页面与权限校验正常。
- 静态资源、History 路由和跨域/反向代理配置正常。
- 日志不输出密钥或完整用户敏感数据。
- MySQL、Redis、Nacos、Dubbo 和监控端口未公开暴露。

## 回滚

- 保留上一个可运行后端包和前端静态产物。
- 发布前备份数据库，本次仓库未集成迁移工具，手工 SQL 变更必须单独制定回滚方案。
- 应用发布失败时先回切到上一版本，再核对日志、外部服务可用性和配置差异。

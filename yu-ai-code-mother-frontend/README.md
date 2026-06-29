# AI 应用代码生成平台·前端

本目录是 AI 应用代码生成平台的 Web 前端，基于 Vue 3、TypeScript 和 Ant Design Vue 实现。

## 功能页面

- `/`：应用创建、我的应用与精选应用。
- `/user/login`、`/user/register`：用户登录与注册。
- `/app/chat/:id`：与 AI 对话，接收流式生成结果并预览应用。
- `/app/edit/:id`：查看与编辑应用信息。
- `/admin/userManage`：用户管理。
- `/admin/appManage`：应用管理与精选设置。
- `/admin/chatManage`：对话历史管理。

## 技术栈

- Vue 3.5 与 TypeScript 5.8
- Vite 7
- Ant Design Vue 4
- Vue Router 4 与 Pinia 3
- Axios、Markdown-It 与 Highlight.js
- ESLint 与 Prettier

## 环境要求

- Node.js 22
- npm
- 默认运行在 `http://localhost:8123` 的单体后端

## 本地开发

```powershell
npm install
npm run dev
```

开发环境下，`vite.config.ts` 会将 `/api` 代理到 `http://localhost:8123`。

## 后端联调

默认 API 基础地址为 `http://localhost:8123/api`。可在前端环境文件中设置：

```dotenv
VITE_API_BASE_URL=http://localhost:8123/api
```

使用非本地后端时，请将该值替换为完整 API 地址。环境文件中不应保存可公开访问的私密凭据。

## 常用命令

```powershell
npm run type-check
npm run build
npm run preview
```

`npm run lint` 会使用自动修复模式，`npm run format` 会直接重写 `src/` 中的格式；运行前请先确认工作区状态。

## 目录结构

```text
src/
├── api/          # OpenAPI 生成的接口与类型
├── components/   # 公共组件
├── layouts/      # 页面布局
├── pages/        # 业务页面
├── router/       # 路由定义
├── stores/       # Pinia 状态
└── utils/        # 通用工具
```

## 构建

```powershell
npm install
npm run build
```

构建产物输出到 `dist/`。完整部署步骤见 [部署指南](../docs/DEPLOYMENT.md)。

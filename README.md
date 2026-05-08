# 智能招聘系统

一个基于 Spring Boot 3.x + Vue 3 的智能招聘系统，集成通义千问大模型，为求职者和招聘者提供智能化的招聘服务。

## 技术栈

### 后端
- Spring Boot 3.5.13
- Java 21
- MyBatis-Plus 3.5.5
- MySQL 8.x
- JWT (jjwt 0.12.5)
- Lombok
- Hutool 5.8.26
- FastJSON 2.0.47
- langchain4j 1.0.1-beta6 (AI服务集成)
- Spring WebFlux (流式输出支持)

### 前端
- Vue 3.4.21
- Vite 5.2.0
- Element Plus 2.6.3
- Vue Router 4.3.0
- Pinia 2.1.7
- Axios 1.6.8
- ECharts 5.5.0
- Sass 1.72.0

### AI服务
- 通义千问 (Qwen) 大模型
- 支持流式输出 (SSE)

## 项目结构

```
intelligent-recruitment/
├── backend/                    # 后端项目
│   ├── src/main/java/com/xu/
│   │   ├── aiservice/         # AI服务接口
│   │   ├── annotation/        # 自定义注解
│   │   ├── common/            # 通用类（Result、异常等）
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── exception/         # 异常处理
│   │   ├── mapper/            # Mapper接口
│   │   ├── service/           # 服务层
│   │   ├── util/              # 工具类
│   │   └── vo/                # 视图对象
│   └── src/main/resources/
│       └── application.yml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── styles/            # 样式文件
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面组件
│   │       ├── admin/         # 管理员页面
│   │       ├── company/       # 招聘者页面
│   │       ├── user/          # 求职者页面
│   │       ├── home/          # 首页
│   │       ├── jobs/          # 职位相关
│   │       ├── login/         # 登录
│   │       └── register/      # 注册
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
└── 系统实现.md                  # 系统实现文档
```

## 数据库设计

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_job | 职位表 |
| sys_company | 企业表 |
| sys_resume | 简历表 |
| sys_application | 投递记录表 |
| sys_interview | 面试表 |
| sys_favorite | 收藏表 |
| sys_setting | 系统设置表 |
| sys_operation_log | 操作日志表 |

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库配置

```sql
# 创建数据库
CREATE DATABASE intelligent_recruitment2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据表结构（根据实体类自动创建或手动创建）
```

修改后端配置文件 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/intelligent_recruitment2?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 2. AI服务配置（可选）

在 `application.yml` 中配置通义千问 API：

```yaml
qwen:
  base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
  api-key: your-api-key
  model: qwen-plus

langchain4j:
  open-ai:
    chat-model:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: your-api-key
      model-name: qwen-plus
```

**注意**：如果不配置AI服务，系统会自动降级到规则匹配模式。

### 3. 启动后端

```bash
cd backend

# 方式一：使用Maven
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

后端服务将在 http://localhost:8080 启动

### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 系统管理员 |
| 招聘者 | hr_alibaba | 123456 | 阿里巴巴HR |
| 招聘者 | hr_tencent | 123456 | 腾讯HR |
| 求职者 | jobseeker1 | 123456 | 求职者张三 |
| 求职者 | jobseeker2 | 123456 | 求职者李四 |

## 功能模块

### 求职者功能 (userType=1)
- **注册与登录**：支持验证码注册、找回密码
- **首页**：平台数据展示、热门职位推荐
- **职位搜索**：按名称、地点、薪资、分类筛选
- **AI智能推荐**：基于简历智能匹配职位
- **我的投递**：查看投递状态和进度
- **我的收藏**：收藏感兴趣的职位
- **我的面试**：查看面试邀请和安排
- **个人简历**：创建、编辑简历，支持AI优化（流式输出）
- **个人中心**：修改资料、密码、绑定手机邮箱

### 招聘者功能 (userType=2)
- **注册与登录**：企业入驻申请
- **企业管理**：完善企业信息、展示企业形象
- **职位管理**：发布、编辑、上下架职位
- **投递管理**：查看求职者投递、筛选简历
- **面试管理**：安排面试、发送面试邀请
- **数据统计**：职位浏览量、投递数量统计
- **AI简历筛选**：智能筛选候选人简历
- **个人中心**：修改个人资料

### 管理员功能 (userType=3)
- **首页提示**：平台运营数据总览、待办事项
- **用户管理**：查询、封禁、管理求职者
- **企业管理**：审核企业入驻、管理企业信息
- **职位审核**：审核职位发布、下架违规职位
- **数据统计**：平台数据可视化分析
- **系统设置**：平台基础信息配置
- **操作日志**：系统操作记录查询

## AI功能说明

### 1. AI简历优化
- **5维度分析**：格式规范、完整性、关键词匹配、经历描述、技能展示
- **流式输出**：支持SSE实时流式返回优化建议
- **降级方案**：API不可用时自动使用规则评分

### 2. AI职位推荐
- **智能匹配**：基于简历内容与职位库匹配
- **多维度评分**：学历、地域、技能、经验匹配度
- **个性化推荐**：为用户推荐最合适的职位

### 3. AI简历筛选
- **企业端功能**：帮助招聘者快速筛选候选人
- **批量筛选**：支持批量处理投递简历
- **智能评分**：自动评估简历与职位匹配度

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/reset-password` - 重置密码
- `POST /api/auth/logout` - 退出登录
- `GET /api/auth/check-username` - 检查用户名是否存在
- `GET /api/auth/check-phone` - 检查手机号是否存在
- `GET /api/auth/check-email` - 检查邮箱是否存在

### 公共接口
- `GET /api/public/jobs` - 获取职位列表
- `GET /api/public/jobs/{id}` - 获取职位详情
- `GET /api/public/settings` - 获取公开的系统设置

### 求职者接口
- `GET /api/resume/list` - 获取简历列表
- `POST /api/resume/create` - 创建简历
- `PUT /api/resume/update` - 更新简历
- `DELETE /api/resume/delete/{id}` - 删除简历
- `POST /api/resume/optimize/{id}` - AI优化简历
- `GET /api/resume/optimize-stream/{id}` - AI优化简历（流式）
- `GET /api/recommend/jobs` - AI职位推荐
- `GET /api/application/list` - 我的投递列表
- `POST /api/application/apply` - 投递职位
- `GET /api/favorite/list` - 我的收藏列表
- `POST /api/favorite/add` - 收藏职位
- `DELETE /api/favorite/remove/{id}` - 取消收藏
- `GET /api/interview/list` - 我的面试列表

### 招聘者接口
- `GET /api/company/info` - 获取企业信息
- `PUT /api/company/update` - 更新企业信息
- `GET /api/company/job/list` - 职位管理列表
- `POST /api/company/job/publish` - 发布职位
- `PUT /api/company/job/update` - 更新职位
- `DELETE /api/company/job/delete/{id}` - 删除职位
- `GET /api/company/application/list` - 投递管理列表
- `PUT /api/company/application/handle` - 处理投递
- `GET /api/company/interview/list` - 面试管理列表
- `POST /api/company/interview/create` - 创建面试
- `GET /api/company/statistics` - 数据统计
- `POST /api/company/ai/screen` - AI简历筛选

### 管理员接口
- `GET /api/admin/user/list` - 用户列表
- `PUT /api/admin/user/status` - 修改用户状态
- `GET /api/admin/company/list` - 企业列表
- `PUT /api/admin/company/audit` - 审核企业
- `GET /api/admin/job/list` - 职位列表
- `PUT /api/admin/job/audit` - 审核职位
- `GET /api/admin/statistics` - 数据统计
- `GET /api/admin/settings` - 系统设置列表
- `PUT /api/admin/settings` - 更新系统设置
- `GET /api/admin/log/list` - 操作日志列表

## 配置说明

### JWT 配置
```yaml
jwt:
  secret: IntelligentRecruitmentSystemSecretKey2024ForJWTTokenGenerationAndValidation
  expiration: 86400000  # 24小时
```

### 跨域配置
后端已配置允许所有来源的跨域请求，生产环境请根据需要修改 `WebConfig.java`。

### 前端代理配置
```javascript
// vite.config.js
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 开发指南

### 后端开发
1. 实体类放在 `entity` 包，使用 MyBatis-Plus 注解
2. Mapper 接口放在 `mapper` 包，继承 BaseMapper
3. Service 接口和实现分别放在 `service` 和 `service.impl` 包
4. Controller 放在 `controller` 包，使用 `@RequireRole` 注解控制权限
5. DTO 和 VO 分别放在 `dto` 和 `vo` 包
6. AI服务接口放在 `aiservice` 包，使用 langchain4j 注解

### 前端开发
1. 页面组件放在 `views` 目录，按角色分子目录
2. API 接口放在 `api` 目录
3. 状态管理放在 `stores` 目录
4. 路由配置在 `router/index.js`
5. 使用 Pinia 进行状态管理

## 构建部署

### 后端构建
```bash
cd backend
mvn clean package -DskipTests
```

### 前端构建
```bash
cd frontend
npm run build
```

构建产物在 `frontend/dist` 目录。

## 注意事项

1. 生产环境请修改 JWT 密钥
2. 生产环境请修改数据库密码
3. 生产环境请配置正确的跨域策略
4. 建议配置 HTTPS
5. AI功能需要配置通义千问 API Key，否则使用降级方案

## License

MIT

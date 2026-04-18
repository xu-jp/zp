# 智能招聘系统

一个基于 Spring Boot 3.x + Vue 3 的智能招聘系统，为求职者和招聘者提供便捷的招聘服务。

## 技术栈

### 后端
- Spring Boot 3.5.13
- MyBatis-Plus 3.5.5
- MySQL 8.x
- JWT (jjwt 0.12.5)
- Lombok
- Hutool 工具库

### 前端
- Vue 3.4
- Vite 5.2
- Element Plus 2.6
- Vue Router 4.3
- Pinia 2.1
- Axios
- Sass

## 项目结构

```
intelligent-recruitment/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/xu/
│   │   │   │   ├── common/     # 通用类（Result、异常等）
│   │   │   │   ├── config/     # 配置类
│   │   │   │   ├── controller/ # 控制器
│   │   │   │   ├── dto/        # 数据传输对象
│   │   │   │   ├── entity/     # 实体类
│   │   │   │   ├── exception/  # 异常处理
│   │   │   │   ├── mapper/     # Mapper接口
│   │   │   │   ├── service/    # 服务层
│   │   │   │   ├── util/       # 工具类
│   │   │   │   └── vo/         # 视图对象
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── styles/            # 样式文件
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面组件
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
└── database/                   # 数据库脚本
    ├── schema.sql             # 建表SQL
    └── data.sql               # 测试数据SQL
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库配置

```bash
# 1. 创建数据库并导入数据
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql

# 2. 修改后端配置文件 backend/src/main/resources/application.yml
# 更新数据库连接信息
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/intelligent_recruitment?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 2. 启动后端

```bash
cd backend

# 方式一：使用Maven
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

后端服务将在 http://localhost:8080 启动

### 3. 启动前端

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

### 求职者功能
- 注册与登录
- 职位搜索与筛选
- 我的投递管理
- 我的收藏夹
- 简历管理（含AI优化）
- 面试管理
- AI职位推荐
- 个人中心

### 招聘者功能
- 注册与登录
- 企业信息管理
- 职位发布管理
- 收到的投递处理
- 面试安排管理
- 数据统计
- 个人中心

### 管理员功能
- 用户管理
- 企业管理
- 职位审核
- 投递统计分析
- 数据可视化
- 系统设置
- 个人资料管理

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 公共接口
- `GET /api/public/jobs` - 获取职位列表
- `GET /api/public/jobs/{id}` - 获取职位详情

## 配置说明

### JWT 配置
```yaml
jwt:
  secret: IntelligentRecruitmentSystemSecretKey2024ForJWTTokenGenerationAndValidation
  expiration: 86400000  # 24小时
```

### 跨域配置
后端已配置允许所有来源的跨域请求，生产环境请根据需要修改 `WebConfig.java`。

## 开发指南

### 后端开发
1. 实体类放在 `entity` 包
2. Mapper 接口放在 `mapper` 包
3. Service 接口和实现分别放在 `service` 和 `service.impl` 包
4. Controller 放在 `controller` 包
5. DTO 和 VO 分别放在 `dto` 和 `vo` 包

### 前端开发
1. 页面组件放在 `views` 目录
2. API 接口放在 `api` 目录
3. 状态管理放在 `stores` 目录
4. 路由配置在 `router/index.js`

## 构建部署

### 后端构建
```bash
cd backend
mvn clean package -Dmaven.test.skip=true
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

## License

MIT

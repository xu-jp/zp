-- 创建数据库
CREATE DATABASE IF NOT EXISTS intelligent_recruitment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE intelligent_recruitment;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    age INT COMMENT '年龄',
    user_type TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型：1-求职者 2-招聘者 3-管理员',
    status TINYINT DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
    company_id BIGINT COMMENT '关联企业ID（招聘者）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 企业表
CREATE TABLE IF NOT EXISTS sys_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '企业名称',
    logo VARCHAR(255) COMMENT '企业Logo',
    industry VARCHAR(50) COMMENT '行业类型',
    scale VARCHAR(50) COMMENT '企业规模',
    address VARCHAR(255) COMMENT '企业地址',
    description TEXT COMMENT '企业简介',
    business_license VARCHAR(255) COMMENT '营业执照URL',
    legal_person VARCHAR(50) COMMENT '法人代表',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核 1-审核通过 2-审核拒绝',
    audit_remark VARCHAR(500) COMMENT '审核备注',
    audit_by BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_name (name),
    INDEX idx_audit_status (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';

-- 职位表
CREATE TABLE IF NOT EXISTS sys_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    title VARCHAR(100) NOT NULL COMMENT '职位名称',
    category VARCHAR(50) COMMENT '职位类别',
    description TEXT COMMENT '职位描述',
    requirement TEXT COMMENT '任职要求',
    location VARCHAR(100) COMMENT '工作地点',
    salary_min DECIMAL(10, 2) COMMENT '最低薪资（K）',
    salary_max DECIMAL(10, 2) COMMENT '最高薪资（K）',
    experience TINYINT COMMENT '经验要求：0-不限 1-1年以下 2-1-3年 3-3-5年 4-5-10年 5-10年以上',
    education VARCHAR(20) COMMENT '学历要求',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-上架',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核 1-审核通过 2-审核拒绝',
    audit_remark VARCHAR(500) COMMENT '审核备注',
    audit_by BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    application_count INT DEFAULT 0 COMMENT '投递次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_company_id (company_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_audit_status (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- 简历表
CREATE TABLE IF NOT EXISTS sys_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) COMMENT '简历标题',
    real_name VARCHAR(50) COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    birthday DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    location VARCHAR(100) COMMENT '现居住地',
    education VARCHAR(20) COMMENT '最高学历',
    school VARCHAR(100) COMMENT '毕业院校',
    major VARCHAR(100) COMMENT '专业',
    graduation_date DATE COMMENT '毕业时间',
    work_experience TEXT COMMENT '工作经历',
    project_experience TEXT COMMENT '项目经历',
    skills TEXT COMMENT '技能特长',
    self_introduction TEXT COMMENT '自我介绍',
    attachments VARCHAR(500) COMMENT '附件URL',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认简历：0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 投递记录表
CREATE TABLE IF NOT EXISTS sys_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '求职者ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待查看 1-已查看 2-待面试 3-已录用 4-已拒绝',
    remark VARCHAR(500) COMMENT '备注',
    view_time DATETIME COMMENT '查看时间',
    feedback_time DATETIME COMMENT '反馈时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';

-- 面试表
CREATE TABLE IF NOT EXISTS sys_interview (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    application_id BIGINT NOT NULL COMMENT '投递记录ID',
    user_id BIGINT NOT NULL COMMENT '求职者ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    interview_time DATETIME NOT NULL COMMENT '面试时间',
    location VARCHAR(255) COMMENT '面试地点',
    interview_type TINYINT DEFAULT 0 COMMENT '面试类型：0-线下 1-线上',
    online_link VARCHAR(255) COMMENT '线上面试链接',
    interviewer_name VARCHAR(50) COMMENT '面试官姓名',
    interviewer_phone VARCHAR(20) COMMENT '面试官电话',
    requirement VARCHAR(500) COMMENT '面试要求',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待面试 1-已面试 2-已取消 3-已通过 4-未通过',
    result VARCHAR(500) COMMENT '面试结果',
    feedback TEXT COMMENT '面试反馈',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_application_id (application_id),
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试表';

-- 收藏表
CREATE TABLE IF NOT EXISTS sys_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    remark VARCHAR(500) COMMENT '收藏备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    UNIQUE KEY uk_user_job (user_id, job_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 系统设置表
CREATE TABLE IF NOT EXISTS sys_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    setting_key VARCHAR(100) NOT NULL UNIQUE COMMENT '设置键',
    setting_value TEXT COMMENT '设置值',
    description VARCHAR(255) COMMENT '设置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    operation VARCHAR(100) COMMENT '操作类型',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    status TINYINT DEFAULT 0 COMMENT '状态：0-成功 1-失败',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 验证码表
CREATE TABLE IF NOT EXISTS sys_verification_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    target VARCHAR(100) NOT NULL COMMENT '目标（手机号/邮箱）',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type TINYINT NOT NULL COMMENT '类型：1-注册 2-找回密码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    used TINYINT DEFAULT 0 COMMENT '是否已使用：0-未使用 1-已使用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_target (target),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS sys_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    type TINYINT DEFAULT 0 COMMENT '消息类型：0-系统通知 1-职位变动 2-面试通知 3-投递反馈',
    related_id BIGINT COMMENT '关联ID（职位ID/面试ID等）',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 初始化系统设置数据
INSERT INTO sys_setting (setting_key, setting_value, description) VALUES
('platformName', '智能招聘系统', '平台名称'),
('logo', '', '平台Logo'),
('servicePhone', '400-123-4567', '客服电话'),
('serviceEmail', 'service@example.com', '客服邮箱'),
('serviceWechat', 'IntelligentRecruit', '客服微信'),
('description', '智能招聘系统是一个专业的在线招聘平台，致力于为求职者和企业提供高效、便捷的招聘服务。', '平台简介'),
('icpNumber', '京ICP备XXXXXXXX号', 'ICP备案号');

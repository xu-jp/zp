USE intelligent_recruitment;

DELETE FROM sys_user;

INSERT INTO sys_user (username, password, phone, email, real_name, user_type, status) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13800000001', 'admin@example.com', '系统管理员', 3, 0),
('recruiter', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13800000002', 'recruiter@example.com', '招聘HR', 2, 0),
('jobseeker', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13900000001', 'jobseeker@example.com', '求职者', 1, 0);

INSERT INTO sys_user (username, password, phone, email, real_name, user_type, status, company_id) VALUES
('hr_alibaba', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13800000003', 'hr_alibaba@example.com', '张HR', 2, 0, 1),
('hr_tencent', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13800000004', 'hr_tencent@example.com', '李HR', 2, 0, 2),
('hr_bytedance', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13800000005', 'hr_bytedance@example.com', '王HR', 2, 0, 3);

INSERT INTO sys_user (username, password, phone, email, real_name, user_type, status) VALUES
('jobseeker1', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13900000002', 'jobseeker1@example.com', '张三', 1, 0),
('jobseeker2', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13900000003', 'jobseeker2@example.com', '李四', 1, 0),
('jobseeker3', '$2a$10$EqKcp1WFKVQISheBxkVJceXI1MPqGkKnMU7zD9hPA0X0Uy.Jb.eey', '13900000004', 'jobseeker3@example.com', '王五', 1, 0);

DELETE FROM sys_company;

INSERT INTO sys_company (name, logo, industry, scale, address, description, business_license, legal_person, contact_phone, contact_email, audit_status) VALUES
('阿里巴巴集团', NULL, '互联网/电子商务', '10000人以上', '浙江省杭州市西湖区', '阿里巴巴集团控股有限公司（简称：阿里巴巴集团）是以曾担任英语教师的马云为首的18人于1999年在浙江省杭州市创立的公司。', NULL, '马云', '0571-85022088', 'hr@alibaba.com', 1),
('腾讯科技', NULL, '互联网/游戏', '10000人以上', '广东省深圳市南山区', '腾讯控股有限公司是一家中国互联网企业，由马化腾、张志东、许晨晔、陈一丹、曾李青于1998年创立。', NULL, '马化腾', '0755-86013388', 'hr@tencent.com', 1),
('字节跳动', NULL, '互联网/短视频', '10000人以上', '北京市海淀区', '字节跳动是一家位于北京的互联网科技公司，成立于2012年3月，是全球最早将人工智能应用于移动互联网场景的科技企业之一。', NULL, '张一鸣', '010-59959999', 'hr@bytedance.com', 1);

DELETE FROM sys_job;

INSERT INTO sys_job (company_id, title, category, description, requirement, location, salary_min, salary_max, experience, education, status, audit_status, view_count, application_count) VALUES
(1, 'Java高级开发工程师', '技术研发', '负责阿里巴巴核心业务系统的开发与维护，参与系统架构设计。', '1. 5年以上Java开发经验\n2. 熟悉Spring、MyBatis等框架\n3. 有分布式系统开发经验\n4. 良好的沟通能力', '杭州', 25.00, 45.00, 4, '本科', 1, 1, 1520, 89),
(1, '前端开发工程师', '技术研发', '负责阿里巴巴电商平台前端页面开发。', '1. 3年以上前端开发经验\n2. 精通Vue、React等框架\n3. 有大型项目经验优先', '杭州', 18.00, 35.00, 3, '本科', 1, 1, 980, 56),
(1, '产品经理', '产品设计', '负责电商平台产品规划与设计。', '1. 3年以上产品经理经验\n2. 有电商行业经验优先\n3. 良好的数据分析能力', '杭州', 20.00, 40.00, 3, '本科', 1, 1, 756, 43),
(2, '游戏开发工程师', '技术研发', '负责腾讯游戏核心玩法开发。', '1. 3年以上游戏开发经验\n2. 熟悉Unity或Unreal引擎\n3. 有上线游戏项目经验', '深圳', 22.00, 40.00, 3, '本科', 1, 1, 1230, 67),
(2, 'UI设计师', '设计', '负责腾讯产品UI设计工作。', '1. 3年以上UI设计经验\n2. 精通PS、AI等设计软件\n3. 有移动端设计经验', '深圳', 15.00, 28.00, 3, '本科', 1, 1, 890, 52),
(2, '数据分析师', '数据分析', '负责腾讯产品数据分析工作。', '1. 2年以上数据分析经验\n2. 熟悉SQL、Python\n3. 有互联网行业经验优先', '深圳', 18.00, 32.00, 2, '本科', 1, 1, 670, 38),
(3, '算法工程师', '技术研发', '负责字节跳动推荐算法研发。', '1. 硕士及以上学历\n2. 熟悉机器学习算法\n3. 有推荐系统经验优先', '北京', 30.00, 60.00, 3, '硕士', 1, 1, 1890, 102),
(3, 'iOS开发工程师', '移动开发', '负责抖音iOS客户端开发。', '1. 3年以上iOS开发经验\n2. 熟悉Swift、Objective-C\n3. 有大型App开发经验', '北京', 22.00, 42.00, 3, '本科', 1, 1, 1100, 61),
(3, 'Android开发工程师', '移动开发', '负责抖音Android客户端开发。', '1. 3年以上Android开发经验\n2. 熟悉Kotlin、Java\n3. 有大型App开发经验', '北京', 22.00, 42.00, 3, '本科', 1, 1, 1050, 58);

DELETE FROM sys_resume;

INSERT INTO sys_resume (user_id, title, real_name, gender, birthday, phone, email, location, education, school, major, graduation_date, work_experience, project_experience, skills, self_introduction, is_default) VALUES
(3, '求职者的简历', '求职者', '男', '1995-05-15', '13900000001', 'jobseeker@example.com', '北京', '本科', '北京大学', '计算机科学与技术', '2017-07-01', '2017.07-2020.06 阿里巴巴 Java开发工程师\n2020.07-至今 字节跳动 高级Java开发工程师', '1. 负责电商平台核心模块开发\n2. 参与微服务架构设计\n3. 优化系统性能，提升QPS 50%', 'Java, Spring, MySQL, Redis, Kafka', '热爱技术，追求卓越，有良好的团队协作能力。', 1),
(7, '张三的简历', '张三', '男', '1995-05-15', '13900000002', 'jobseeker1@example.com', '北京', '本科', '北京大学', '计算机科学与技术', '2017-07-01', '2017.07-2020.06 阿里巴巴 Java开发工程师\n2020.07-至今 字节跳动 高级Java开发工程师', '1. 负责电商平台核心模块开发\n2. 参与微服务架构设计\n3. 优化系统性能，提升QPS 50%', 'Java, Spring, MySQL, Redis, Kafka', '热爱技术，追求卓越，有良好的团队协作能力。', 1),
(8, '李四的简历', '李四', '女', '1996-08-20', '13900000003', 'jobseeker2@example.com', '上海', '硕士', '清华大学', '软件工程', '2020-07-01', '2020.07-至今 腾讯科技 前端开发工程师', '1. 负责腾讯视频Web端开发\n2. 参与前端架构设计\n3. 优化页面性能，提升用户体验', 'Vue, React, TypeScript, Node.js', '对前端技术有浓厚兴趣，善于学习新技术。', 1),
(9, '王五的简历', '王五', '男', '1994-03-10', '13900000004', 'jobseeker3@example.com', '深圳', '本科', '浙江大学', '数据科学', '2016-07-01', '2016.07-2019.06 网易 数据分析师\n2019.07-至今 腾讯科技 高级数据分析师', '1. 负责游戏数据分析\n2. 建立数据指标体系\n3. 为产品决策提供数据支持', 'Python, SQL, Tableau, Spark', '数据驱动决策，善于从数据中发现问题。', 1);

DELETE FROM sys_setting;

INSERT INTO sys_setting (setting_key, setting_value, description) VALUES
('site_name', '智能招聘系统', '网站名称'),
('site_logo', '/logo.png', '网站Logo'),
('customer_service_phone', '400-123-4567', '客服电话'),
('customer_service_email', 'service@example.com', '客服邮箱'),
('icp_number', '京ICP备12345678号', 'ICP备案号');

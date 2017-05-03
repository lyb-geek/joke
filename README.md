1.本系统主要基于springboot+jpa+freemarker进行开发
2.权限框架采用shiro
3.前后台消息通讯采用websocket
4.定时器定时检测系统后台文件是否存在，不存在则发送邮件告警
5.邮件支持普通文本，html格式
7.后台消息队列采用redis生产消费者模式
8.支持session共享
9.后台管理验证码采用极验证码进行校验
10.前端核心框架为h-ui.admin模板框架
11.权限树采用ztree
12.分页插件采用jquery.pagination.js
13.文件上传前台采用ajaxfileupload.js

下个版本更新计划
1.主要是基于springcloud的微服务架构
2.文件上传则用fastdfs进行文件管理
3.引入netty+protobuf、dubbox、thirft等rpc进行通讯
4.部署基于docker进行项目发布





基合Spring+SpringMVC+Mybatis框架,使用Maven工具构建本项目
=
工具
=   
-    JDK版本为7u80 64位
-   Maven版本是3.0.5 
-    mysql-server-5.7.73
-    nginx-1.6.2.tar.gz  
-    tomcat-8.5.34  
-    redis 2.8.0  
-    rabbitmq 3.6.15      
-    服务器:CentOS 7.3 64位  

目前实现的功能:
==
  * * *
 * 自定义拦截器校验用户登录信息及权限认证
 * cookie+redis实现session共享
 * 用rabbitmq处理订单消息
 * nginx均衡负载,实现tomcat集群
 * 使用redis分布式锁实现分布调度定时关单任务
 * 
 * 使用Spring security 定时关闭未支付订单,定时删除已完成订单
 * 使用Filter自动重置用户用户登录有效时间
 * 使用redis缓存商品列表
 * 等等... 
* * *
[线上地址](http://120.78.128.136/) 
# 介绍
`ReimuOA-SpringBoot`基于SpringBoot+Shiro+Mybatis+Mybatis-Plus等开发的轻量级管理系统快速开发脚手架,并此项目会进行持续更新升级，欢迎使用，若对您有帮助请点击上方的star :beers: 。
 
 由于是技术示范性脚手架,所以本项目所有依赖版本会及时更新至最新版，如果基于本脚手架开发项目，请自行锁定版本依赖。



# 使用方法（逐步完善中）
## 软件需求
    JDK 11+ (最低要求为 JDK 8,但正式开发前请自行更改测试)
    MySQL 5.7+
    Maven 3.3+
    Redis 4.0+ (3.0也可使用，但正式开发前请自行更改测试)
## 开发使用
- 克隆到本地
```git
git clone git@github.com:moons7/ReimuOA_SpringBoot.git
```
- 导入SQL
    将项目根目录下的`ReimuOA.sql`导入至数据库信息
- 修改数据库以及redis配置信息（redis 无密码则留空）
```yml
# application-dev.yml
spring:
  datasource:
    master:
      url: jdbc:mysql://localhost:3306/reimuoa?useUnicode=yes&characterEncoding=UTF8
      password: root
      username: root
      
project:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 
```
- 启动
    运行`Application.java`，默认端口为8535
    
## 正式部署须知
  须修改application-prod.yml文件, 修改数据库以及redis配置信息
```yml
# application-prod.yml
spring:
  datasource:
    master:
      url: jdbc:mysql://localhost:3306/reimuoa?useUnicode=yes&characterEncoding=UTF8
      password: root
      username: root
      
project:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 
```

- 启动
 
 使用生产环境配置有2种方式(推荐第一种，第二种须在打包前更改文件)
 
1. 命令行 java -jar xx.jar --spring.profiles.active=prod 
  
2. 修改application.yml文件
```yml
# application.yml
spring:
  profiles:
    active: prod
```
   
# 常见问题
1. 怎么没有客户端呢，那怎么使用= =？
  
   首先本项目暂时定位于`后端技术示范性`脚手架，现在暂时由于经济原因没有服务器可演示前端（前端基于 [vue-element-admin](https://github.com/PanJiaChen/vue-element-admin) 配套改造开发，对于初级后端人员上手有些难度，可能后期会有演示吧,但`前端源码`可能考虑授权付费形式放出，因为其实前端对于本脚手架并不是必选项,请大家谅解= =）

   同时本脚手架中API中并未涉及过多业务，登录与登出位于`LoginController`中，最主要权限逻辑位于shiro包中，其余业务API均可无视（待集成swagger后会注释用处）。

# 帮助文档

*  [使用前须知](doc/0.使用前须知.md)
*  [项目代码结构说明以及规范](doc/1.项目代码结构说明以及规范.md)
*  [配置文件参数说明](doc/2.配置文件参数说明.md)
*  [API返回数据结构约定](doc/3.API返回数据结构约定.md)
*  ~~[简单的登录流程]~~
*  ~~[Shiro的大概使用流程]~~


# TODO
- [ ] 文档完善
- [ ] Swagger支持
- [ ] More...

# 赞助


# 开源协议
MIT
    

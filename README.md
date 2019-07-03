# 简介 #


> 源码地址

- GitHub：[https://github.com/battcn/elasticsearch-spring-boot](https://github.com/battcn/elasticsearch-spring-boot "https://github.com/battcn/elasticsearch-spring-boot")
- 码云：[https://gitee.com/battcn/elasticsearch-spring-boot/](https://gitee.com/battcn/elasticsearch-spring-boot/ "https://gitee.com/battcn/spring-boot-starter-swagger/")

`elasticsearch-spring-boot-starter` 是一款建立在 `elasticsearch-rest-high-level-client` 基础之上的工具包

> 项目介绍

- **`elasticsearch-spring-boot-autoconfigure` ：具体代码**
- **`elasticsearch-spring-boot-starter` ： 自动装配 elasticsearch 的扩展包**


**如果该项目对您有帮助，欢迎 Fork 和 Star，有疑问可以加 `QQ：1837307557`一起交流 ，如发现项目BUG可以提交`Issue`**

# 使用 #

- 在`pom.xml`中引入依赖：

``` xml
<properties>
    <java.version>1.8</java.version>
    <!-- 指定ES版本（重要） -->
    <elasticsearch.version>7.1.1</elasticsearch.version>
</properties>
<!-- 版本说明：x.y.z 对应es的版本，最后一位代表当前包对应的小版本-->
<dependency>
    <groupId>com.battcn</groupId>
    <artifactId>elasticsearch-spring-boot-starter</artifactId>
    <version>7.1.1.1.SNAPSHOT</version>
</dependency>
```


### properties ###


```
spring.elasticsearch.rest.cluster-name=elasticsearch
spring.elasticsearch.rest.uris=http://localhost:9200
spring.elasticsearch.rest.http-client.max-total=200
spring.elasticsearch.rest.http-client.default-max-per-route=100
spring.elasticsearch.rest.http-client.connect-timeout=10000
spring.elasticsearch.rest.http-client.read-timeout=10000
spring.elasticsearch.rest.http-client.connection-request-timeout=10000
```


# 贡献者 #

Levin：1837307557@qq.com  

- 个人博文：[http://blog.battcn.com](http://blog.battcn.com "http://blog.battcn.com")


# 如何参与 #

有兴趣的可以联系本人（Pull Request），参与进来一起开发

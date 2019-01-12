# 一个简单的高性能API网关

架构：Netty,Zookeeper,mysql,redis。<br/>
一个分布式api网关解决方案。<br/>
统一解决：熔断，限流，黑白名单，rest路由（提供权重／ip_hash两种解决方案），登陆请求签名组件，token认证组件等。<br/>
支撑横向扩展。<br/>
网关抽象了前置组件，运行时组件和后置组件，非常容易动态扩展。<br/>

zip文件夹包含内容：
config.zip（配置中心的Demo zip （网关和自研的分布式配置中心configx集成））。<br/>
.sql文件：网关组件功能配置的数据库存储表。<br/>
war.zip：hystrix熔断器的面板。<br/>

目前未完成工作：<br/>

### admin管理操作后台 
精力有限，不编写了。
### rest请求转dubbo请求
rest转dubbo请求只需要调用dubbo提供的泛化调用接口即可。实现较容易

# 其他：
精力有限，目前缺少文档，仅是半成品。<br/>
日后发一篇公众号阐述一下网关的设计思路。<br/>



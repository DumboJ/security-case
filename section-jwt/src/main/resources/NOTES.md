# JWT Json Web Token 的使用
 ***自验证的无状态字符串token***
 ## 前端使用时，服务器使用自定义 jwt 过滤器拦截
    1. 验证 header 键值对是否符合规范
    2. 符合规范后，使用 Jwt 解析工具解析获取 Calims ，如果 Claims 包含认证注册或登录获取token时信息的内容，
        则把当前请求添加到Spring Security 管理的认证用户中；
        否则，则清空 Spring Security 的授权空间
    3.后续添加注册往数据库中添加用户记录，并登录时获取 访问token 逻辑
    
        
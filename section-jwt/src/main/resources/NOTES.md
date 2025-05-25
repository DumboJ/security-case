# JWT Json Web Token 的使用
 ***自验证的无状态字符串token***
 ## 前端使用时，服务器使用自定义 jwt 过滤器拦截
    1. 验证 header 键值对是否符合规范
    2. 符合规范后，使用 Jwt 解析工具解析获取 Calims ，如果 Claims 包含认证注册或登录获取token时信息的内容，
        则把当前请求添加到Spring Security 管理的认证用户中；
        否则，则清空 Spring Security 的授权空间
    3.后续添加注册往数据库中添加用户记录，并登录时获取 访问token 逻辑
- JWt使用说明：
[Jwts快速开始](https://github.com/jwtk/jjwt?tab=readme-ov-file#quickstart)
1. Header:
~~~java
    //需要始终验证一下表头中是否属于你签发的内容
    assert 
            .parser().verifyWith(key).build()
            .parseSignedClaims(jws)
            .getPayload().getSubject().equals("Joe");
~~~
2. Payload
 - 2.1 Payload的两种方式(不能同时声明使用)
    - 如果您希望有效负载是任意字节数组内容(使用双参数，方便解析)
    ~~~java
    byte[] content = "Hello World".getBytes(StandardCharsets.UTF_8);

    String jwt = Jwts.builder()

    .content(content, "text/plain") // <---

    // ... etc ...

    .build();
    ~~~
   - 如果您希望有效负载是 JSON Claims  对象(定义了标准注册 Claim 名称)
   ~~~java
   //也可以使用map一次性塞进多组
   Map<String,?> claims = getMyClaimsMap(); //implement me

    String jws = Jwts.builder()

    .claims(claims)

    // ... etc ...
   //---------
    String jws = Jwts.builder()

    .issuer("me")
    .subject("Bob")
    .audience().add("you").and()
    .expiration(expiration) //a java.util.Date
    .notBefore(notBefore) //a java.util.Date
    .issuedAt(new Date()) // for example, now
    .id(UUID.randomUUID().toString()) //just an example id

    /// ... etc ...
   ~~~
3. signed
    
        
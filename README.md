# Spring Boot OAuth2/OIDC SSO Demo

这是一个用于学习 OAuth2/OIDC 单点登录的最小可运行 demo：

- `auth-server`：认证中心，端口 `9000`
- `client-a`：业务系统 A，端口 `18081`
- `client-b`：业务系统 B，端口 `18082`

OAuth2 是授权协议；SSO 登录场景通常使用建立在 OAuth2 之上的 OpenID Connect，也就是 OIDC。

## 环境要求

- JDK 17+
- Maven 3.9+

本机如果默认 Java 是 11，可以在 PowerShell 中临时指定 JDK17：

```powershell
$env:JAVA_HOME="D:\soft\java\jdk17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

## 构建

```powershell
mvn -gs .mvn/settings.xml -s .mvn/settings.xml clean package
```

## 启动

打开 3 个 PowerShell 窗口，分别执行：

```powershell
mvn -gs .mvn/settings.xml -s .mvn/settings.xml -pl auth-server spring-boot:run
```

```powershell
mvn -gs .mvn/settings.xml -s .mvn/settings.xml -pl client-a spring-boot:run
```

```powershell
mvn -gs .mvn/settings.xml -s .mvn/settings.xml -pl client-b spring-boot:run
```

## 验证单点登录

1. 打开 `http://localhost:18081`
2. 点击“使用 SSO 登录”
3. 使用 `user / password` 或 `admin / password` 登录
4. 登录成功后打开 `http://localhost:18082`
5. 如果不再要求输入账号密码，说明 SSO 生效

## 核心配置说明

- 认证中心注册了两个客户端：
  - `client-a / secret-a`
  - `client-b / secret-b`
- 两个客户端都信任同一个 issuer：`http://localhost:9000`
- 两个客户端都使用授权码模式：`authorization_code`
- 两个客户端都请求 OIDC scope：`openid`、`profile`
- 三个服务都运行在 `localhost`，因此必须使用不同的 session cookie 名称，避免默认 `JSESSIONID` 互相覆盖。

package com.example.sso.auth;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    String index() {
        return """
                <!doctype html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <title>Auth Server</title>
                    <style>
                        body { font-family: Arial, "Microsoft YaHei", sans-serif; margin: 48px; line-height: 1.7; }
                        code { background: #f2f2f2; padding: 2px 6px; border-radius: 4px; }
                    </style>
                </head>
                <body>
                    <h1>OAuth2/OIDC 认证中心</h1>
                    <p>登录账号：<code>user / password</code> 或 <code>admin / password</code></p>
                    <p>先访问 <a href="http://localhost:18081">Client A</a>，登录后再访问
                       <a href="http://localhost:18082">Client B</a>，观察单点登录效果。</p>
                </body>
                </html>
                """;
    }
}

package com.example.sso.clientb;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

final class PageRenderer {

    private PageRenderer() {
    }

    static String render(String title, String port, OidcUser user) {
        boolean loggedIn = user != null;
        String userBlock = loggedIn
                ? "<p>当前用户：<strong>" + escape(user.getName()) + "</strong></p>"
                + "<p><a href=\"/me\">查看用户 claims JSON</a></p>"
                + "<form method=\"post\" action=\"/logout\"><button type=\"submit\">退出当前客户端</button></form>"
                : "<p>当前未登录。</p><p><a class=\"button\" href=\"/oauth2/authorization/sso\">使用 SSO 登录</a></p>";

        return """
                <!doctype html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, "Microsoft YaHei", sans-serif; margin: 48px; line-height: 1.7; }
                        a, button { font-size: 16px; }
                        .button, button { display: inline-block; padding: 8px 14px; color: #fff; background: #138a72; border: 0; border-radius: 4px; text-decoration: none; cursor: pointer; }
                        .links a { margin-right: 16px; }
                        code { background: #f2f2f2; padding: 2px 6px; border-radius: 4px; }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                    <p>业务系统端口：<code>%s</code></p>
                    %s
                    <p class="links">
                        <a href="http://localhost:18081">Client A</a>
                        <a href="http://localhost:18082">Client B</a>
                        <a href="http://localhost:9000">Auth Server</a>
                    </p>
                </body>
                </html>
                """.formatted(title, title, port, userBlock);
    }

    private static String escape(String value) {
        return value == null ? "" : value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}

package com.example.sso.clientb;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    String index(@AuthenticationPrincipal OidcUser user) {
        return PageRenderer.render("Client B", "18082", user);
    }

    @GetMapping("/me")
    Map<String, Object> me(@AuthenticationPrincipal OidcUser user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("client", "client-b");
        result.put("subject", user.getSubject());
        result.put("name", user.getFullName());
        result.put("claims", user.getClaims());
        return result;
    }
}

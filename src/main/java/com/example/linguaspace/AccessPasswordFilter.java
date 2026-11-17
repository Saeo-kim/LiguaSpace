package com.example.linguaspace;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Optional protection for the vocabulary page and API on a public URL. Set APP_ACCESS_PASSWORD to
 * turn it on; the browser then asks once for a password (any username). Off when unset.
 */
@Component
public class AccessPasswordFilter extends OncePerRequestFilter {

    private final byte[] password;

    public AccessPasswordFilter(@Value("${app.access-password:}") String password) {
        this.password = password.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return password.length == 0 || !(path.equals("/vocabulary") || path.startsWith("/api/vocabulary"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isAuthorized(request.getHeader("Authorization"))) {
            chain.doFilter(request, response);
            return;
        }
        response.setHeader("WWW-Authenticate", "Basic realm=\"Lingua Space\", charset=\"UTF-8\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isAuthorized(String header) {
        if (header == null || !header.startsWith("Basic ")) {
            return false;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(header.substring(6)), StandardCharsets.UTF_8);
            int separator = decoded.indexOf(':');
            return separator >= 0 && MessageDigest.isEqual(
                    decoded.substring(separator + 1).getBytes(StandardCharsets.UTF_8), password);
        } catch (IllegalArgumentException malformedHeader) {
            return false;
        }
    }
}

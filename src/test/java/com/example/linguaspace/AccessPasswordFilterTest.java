package com.example.linguaspace;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class AccessPasswordFilterTest {

    private static int statusFor(String password, String path, String submitted) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        if (submitted != null) {
            request.addHeader("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString(("me:" + submitted).getBytes(StandardCharsets.UTF_8)));
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        new AccessPasswordFilter(password).doFilter(request, response, new MockFilterChain());
        return response.getStatus();
    }

    @Test
    void isOffWithoutAPassword() throws Exception {
        assertThat(statusFor("", "/api/vocabulary", null)).isEqualTo(200);
    }

    @Test
    void protectsVocabularyOnly() throws Exception {
        assertThat(statusFor("secret", "/vocabulary", null)).isEqualTo(401);
        assertThat(statusFor("secret", "/api/vocabulary/import/commit", "wrong")).isEqualTo(401);
        assertThat(statusFor("secret", "/api/vocabulary", "secret")).isEqualTo(200);
        assertThat(statusFor("secret", "/healthz", null)).isEqualTo(200);
        assertThat(statusFor("secret", "/", null)).isEqualTo(200);
    }
}

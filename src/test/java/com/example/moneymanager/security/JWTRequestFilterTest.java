package com.example.moneymanager.security;

import com.example.moneymanager.util.JWTUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JWTRequestFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void continuesFilterChainWhenAuthorizationHeaderIsMissing() throws Exception {
        JWTRequestFilter filter = new JWTRequestFilter(mock(UserDetailsService.class), mock(JWTUtil.class));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/home");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, (servletRequest, servletResponse) -> chainCalled.set(true));

        assertThat(chainCalled).isTrue();
    }
}

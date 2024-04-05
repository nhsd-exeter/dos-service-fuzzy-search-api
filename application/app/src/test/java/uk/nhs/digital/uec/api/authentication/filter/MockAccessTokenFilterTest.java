package uk.nhs.digital.uec.api.authentication.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;
import uk.nhs.digital.uec.api.filter.MockAccessTokenFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {JwtUtil.class, uk.nhs.digital.uec.api.authentication.filter.MockAccessTokenFilterTest.class, MockAccessTokenFilter.class})
@ActiveProfiles("mock-auth")
public class MockAccessTokenFilterTest {

    @Autowired
    MockAccessTokenFilter accessTokenFilter;

    @BeforeEach
    public void setUp() {
      SecurityContextHolder.clearContext();
    }

    @Test
    public void testFilterChainWithOkToken() throws ServletException, IOException {
      assertNull(SecurityContextHolder.getContext().getAuthentication()); //so we know we have written it

      HttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
      FilterChain filterChain = Mockito.mock(FilterChain.class);

      when(request.getHeader("Authorization")).thenReturn("Bearer MOCK_FUZZY_API_ACCESS_TOKEN");
      accessTokenFilter.doFilterInternal(request, null, filterChain);
      verify(filterChain).doFilter(request, null);
      assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testFilterChainWithExpiredJwt() throws ServletException, IOException {
      assertNull(SecurityContextHolder.getContext().getAuthentication());

      HttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
      FilterChain filterChain = Mockito.mock(FilterChain.class);

      when(request.getHeader("Authorization")).thenReturn("Bearer SOMETHING_INVALID");
      accessTokenFilter.doFilterInternal(request, null, filterChain);
      verify(filterChain).doFilter(request, null);
      verify(request, never()).setAttribute(anyString(), anyString());
      assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}

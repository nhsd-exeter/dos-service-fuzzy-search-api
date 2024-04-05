package uk.nhs.digital.uec.api.authentication.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;
import uk.nhs.digital.uec.api.filter.AccessTokenFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {JwtUtil.class, AccessTokenFilterTest.class, AccessTokenFilter.class})
public class AccessTokenFilterTest {

  @Autowired
  AccessTokenFilter accessTokenFilter;

  @Test
  public void testFilterChainWithOkJwt() throws ServletException, IOException {
    HttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
    FilterChain filterChain = Mockito.mock(FilterChain.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer "+getJwtWithExpiryIn2124());
    accessTokenFilter.doFilterInternal(request, null, filterChain);
    verify(filterChain).doFilter(request, null);
    verify(request).setAttribute("IDENTITY_PROVIDER_ID", "testuser@servicefinder.nhs.uk");
  }

  @Test
  public void testFilterChainWithExpiredJwt() throws ServletException, IOException {
    HttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
    FilterChain filterChain = Mockito.mock(FilterChain.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer "+getJwtWithExpiryIn2020());
    accessTokenFilter.doFilterInternal(request, null, filterChain);
    verify(filterChain).doFilter(request, null);
    verify(request, never()).setAttribute(anyString(), anyString());
  }


  private String getJwtWithExpiryIn2124() {
    //created at http://jwtbuilder.jamiekurtz.com/, using dummy details
    return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3MTIyNDI1NDMsImV4cCI6NDg2NzkxNjE0MywiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdHVzZXJAc2VydmljZWZpbmRlci5uaHMudWsiLCJHaXZlbk5hbWUiOiJKb2hubnkiLCJTdXJuYW1lIjoiUm9ja2V0IiwiRW1haWwiOiJqcm9ja2V0QGV4YW1wbGUuY29tIiwiUm9sZSI6Ik1hbmFnZXIiLCJjb2duaXRvOmdyb3VwcyI6IltcIkFETUlOXCIsXCJVU0VSXCJdIn0.vawS1_oA4bY9DqdGGuphjgHRPIcZfbJtkQIvFwfacsQ";
//    return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3MTIyNDI1NDMsImV4cCI6NDg5OTQ1MjE0MywiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdHVzZXJAc2VydmljZWZpbmRlci5uaHMudWsiLCJHaXZlbk5hbWUiOiJKb2hubnkiLCJTdXJuYW1lIjoiUm9ja2V0IiwiRW1haWwiOiJqcm9ja2V0QGV4YW1wbGUuY29tIiwiUm9sZSI6WyJNYW5hZ2VyIiwiUHJvamVjdCBBZG1pbmlzdHJhdG9yIl19.LzzcABUMtv7W7mnq5KDnhj8hYZu4FH74i_Co5u7PRn0";
  }

  private String getJwtWithExpiryIn2020() {
    return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3MTIyNDI1NDMsImV4cCI6MTU4NjAxMjE0MywiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdHVzZXJAc2VydmljZWZpbmRlci5uaHMudWsiLCJHaXZlbk5hbWUiOiJKb2hubnkiLCJTdXJuYW1lIjoiUm9ja2V0IiwiRW1haWwiOiJqcm9ja2V0QGV4YW1wbGUuY29tIiwiUm9sZSI6WyJNYW5hZ2VyIiwiUHJvamVjdCBBZG1pbmlzdHJhdG9yIl19.DpN-HbAij52IkA1runi4l9miyaQmA_xkoDYHXCxce1I";
  }

}

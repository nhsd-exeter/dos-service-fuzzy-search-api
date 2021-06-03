package uk.nhs.digital.uec.api.auth.testsupport;

import java.util.Objects;
import javax.servlet.http.Cookie;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/** Hamcrest Matcher for {@link Cookie} */
public class CookieMatcher extends TypeSafeDiagnosingMatcher<Cookie> {

  private final Cookie expectedCookie;

  private CookieMatcher(Cookie expectedCookie) {
    this.expectedCookie = expectedCookie;
  }

  public static Matcher<Cookie> cookieMatching(Cookie expectedCookie) {
    return new CookieMatcher(expectedCookie);
  }

  @Override
  public void describeTo(Description description) {
    description
        .appendText("Cookie with name ")
        .appendValue(expectedCookie.getName())
        .appendText(" and value ")
        .appendValue(expectedCookie.getValue())
        .appendText(" and Domain ")
        .appendValue(expectedCookie.getDomain())
        .appendText(" and Path ")
        .appendValue(expectedCookie.getPath())
        .appendText(" and Max-Age ")
        .appendValue(expectedCookie.getMaxAge())
        .appendText(" and HttpOnly ")
        .appendValue(expectedCookie.isHttpOnly())
        .appendText(" and Secure ")
        .appendValue(expectedCookie.getSecure())
        .appendText(" and Version ")
        .appendValue(expectedCookie.getVersion())
        .appendText(" and Comment ")
        .appendValue(expectedCookie.getComment());
  }

  @Override
  protected boolean matchesSafely(Cookie actualCookie, Description mismatchDescription) {
    if (!Objects.equals(expectedCookie.getName(), actualCookie.getName())) {
      mismatchDescription.appendText("name was ").appendValue(actualCookie.getName());
      return false;
    }
    if (!Objects.equals(expectedCookie.getValue(), actualCookie.getValue())) {
      mismatchDescription.appendText("value was ").appendValue(actualCookie.getValue());
      return false;
    }
    if (!Objects.equals(expectedCookie.getDomain(), actualCookie.getDomain())) {
      mismatchDescription.appendText("Domain was ").appendValue(actualCookie.getDomain());
      return false;
    }
    if (!Objects.equals(expectedCookie.getPath(), actualCookie.getPath())) {
      mismatchDescription.appendText("Path was ").appendValue(actualCookie.getPath());
      return false;
    }
    if (!Objects.equals(expectedCookie.getMaxAge(), actualCookie.getMaxAge())) {
      mismatchDescription.appendText("Max-Age was ").appendValue(actualCookie.getMaxAge());
      return false;
    }
    if (!Objects.equals(expectedCookie.isHttpOnly(), actualCookie.isHttpOnly())) {
      mismatchDescription.appendText("HttpOnly was ").appendValue(actualCookie.isHttpOnly());
      return false;
    }
    if (!Objects.equals(expectedCookie.getSecure(), actualCookie.getSecure())) {
      mismatchDescription.appendText("Secure was ").appendValue(actualCookie.getSecure());
      return false;
    }
    if (!Objects.equals(expectedCookie.getVersion(), actualCookie.getVersion())) {
      mismatchDescription.appendText("Version was ").appendValue(actualCookie.getVersion());
      return false;
    }
    if (!Objects.equals(expectedCookie.getComment(), actualCookie.getComment())) {
      mismatchDescription.appendText("Comment was ").appendValue(actualCookie.getComment());
      return false;
    }
    return true;
  }
}

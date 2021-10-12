package uk.nhs.digital.uec.api.authentication.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private Map<String, String> headerMap;

  public CustomHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
    headerMap = new HashMap<>();
  }

  public void addHeader(String name, String value) {
    headerMap.put(name, value);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    HttpServletRequest request = (HttpServletRequest) getRequest();
    return Collections.enumeration(
        Stream.of(
                Collections.list(request.getHeaderNames()).stream().toList(),
                headerMap.keySet().stream().toList())
            .flatMap(Collection::stream)
            .toList());
  }

  @Override
  public String getHeader(String name) {
    Object value;
    if ((value = headerMap.get("" + name)) != null) return value.toString();
    else return ((HttpServletRequest) getRequest()).getHeader(name);
  }
}

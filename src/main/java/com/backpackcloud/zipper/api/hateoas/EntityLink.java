package com.backpackcloud.zipper.api.hateoas;

import com.backpackcloud.trugger.element.Element;
import com.backpackcloud.trugger.reflection.Reflection;
import com.backpackcloud.zipper.UnbelievableException;
import com.backpackcloud.zipper.api.ApiResourceModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.backpackcloud.trugger.element.Elements.element;
import static com.backpackcloud.trugger.reflection.MethodPredicates.annotatedWith;
import static com.backpackcloud.zipper.Configuration.configuration;

public class EntityLink {

  private static final String API_BASE_URL = configuration()
    .env("API_BASE_URL")
    .property("api.base.url")
    .or(() -> "http://localhost:8080");

  private static final Pattern PATH_PARAMETER_PATTERN = Pattern.compile("\\{(\\w+)}");

  private final String href;
  private final String title;

  public EntityLink(ApiResourceModel model, String title) {
    String basePath = model.controllerClass().getAnnotation(Path.class).value();
    String subPath = Reflection.reflect().methods()
      .filter(annotatedWith(GET.class))
      .filter(annotatedWith(Link.class))
      .filter(annotatedWith(Path.class))
      .from(model.controllerClass())
      .stream().findFirst()
      .map(method -> method.getAnnotation(Path.class))
      .map(Path::value)
      .orElseThrow(UnbelievableException::new);

    StringBuilder path = new StringBuilder(basePath).append(subPath);
    Matcher matcher = PATH_PARAMETER_PATTERN.matcher(path);

    while (matcher.find()) {
      StringBuilder groupName = new StringBuilder(matcher.group(0));
      groupName.deleteCharAt(0);
      groupName.deleteCharAt(groupName.length() - 1);

      String value = element(groupName.toString()).from(model)
        .map(Element::getValue)
        .map(Object::toString)
        .orElseThrow(UnbelievableException::new);

      path.replace(matcher.start(), matcher.end(), value);
    }

    this.href = API_BASE_URL + path.toString();
    this.title = title;
  }

  public EntityLink(ApiResourceModel model) {
    this(model, null);
  }

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String title() {
    return title;
  }

  @JsonProperty
  public String href() {
    return href;
  }

}

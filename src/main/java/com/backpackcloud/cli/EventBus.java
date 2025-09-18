package com.backpackcloud.cli;

import com.backpackcloud.UnbelievableException;
import com.backpackcloud.cli.annotations.Observe;
import com.backpackcloud.reflection.Context;
import com.backpackcloud.reflection.Mirror;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.backpackcloud.reflection.predicates.MethodPredicates.annotatedWith;
import static com.backpackcloud.reflection.predicates.ParameterPredicates.ofName;

public class EventBus {

  private final List<EventListener> listeners;

  public EventBus() {
    listeners = new ArrayList<>();
  }

  public void scan(Object component) {
    Mirror.reflect(component).methods().stream()
      .filter(annotatedWith(Observe.class))
      .forEach(method -> {
        String eventName = method.getAnnotation(Observe.class).value();
        listeners.add(new EventListener(eventName, component, method));
      });
  }

  public void send(String eventName, EventParam... args) {
    this.listeners.stream()
      .filter(listener -> listener.event().equals(eventName))
      .forEach(listener -> listener.notifyListener(args));
  }

  private record EventListener(String event, Object instance, Method method) {

    public void notifyListener(EventParam... params) {
      try {
        Context context = new Context();
        for (EventParam param : params) {
          context.when(ofName(param.name()), param.value());
        }
        Object[] args = context.resolve(method);
        method.invoke(instance, args);
      } catch (IllegalAccessException e) {
        throw new UnbelievableException(e);
      } catch (InvocationTargetException e) {
        throw new UnbelievableException(e.getTargetException());
      }
    }

  }

  public static EventParam param(String name, Object value) {
    return new EventParam(name, value);
  }

  public record EventParam(String name, Object value) {

  }

}

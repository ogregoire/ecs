/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Olivier Grégoire
 */
public final class Engine {

  private final ImmutableSet<EntityManager> managers;
  private final ImmutableSet<EntityProcessor> processors;
  private final Injector injector;

  private Engine(Builder builder) {
    managers = builder.managers.build();
    processors = builder.processors.build();
    injector = new Injector();
    managers.forEach(injector::inject);
    managers.forEach(EntityManager::initialize);
    processors.forEach(injector::inject);
  }

  public void process() {
    processors.forEach(EntityProcessor::doProcess);
  }

  public static class Builder {

    private final ImmutableSet.Builder<EntityManager> managers = ImmutableSet.builder();
    private final ImmutableSet.Builder<EntityProcessor> processors = ImmutableSet.builder();

    public Builder add(EntityManager manager) {
      requireNonNull(manager, "manager must not be null");
      managers.add(manager);
      return this;
    }

    public Builder add(EntityProcessor system) {
      requireNonNull(system, "processor must not be null");
      processors.add(system);
      return this;
    }

    public Engine build() {
      return new Engine(this);
    }
  }

  final class Injector {

    final ImmutableClassToInstanceMap<Object> injectables;

    Injector() {
      injectables = ImmutableClassToInstanceMap.builder()
          .putAll(toClassToInstanceMap(managers))
          .putAll(toClassToInstanceMap(processors))
          .put(Engine.class, Engine.this)
          .build();
    }
    
    private <T> Map<? extends Class<? extends Object>,? extends Object> toClassToInstanceMap(Collection<T> collection) {
      return collection.stream().collect(toMap(Object::getClass, identity()));
    }

    void inject(Object injectee) {
      Reflection.classParents(injectee.getClass())
          .flatMap(Reflection::getDeclaredFields)
          .filter(Reflection.isAnnotationPresent(Inject.class))
          .forEach(FunctionalBind.bindFirst(this::tryInject, injectee));
    }

    private void tryInject(Object injectee, Field field) {
      Object injectable = injectables.get(field.getType());
      if (injectable != null) {
        try {
          field.setAccessible(true);
          field.set(injectee, injectable);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }
}

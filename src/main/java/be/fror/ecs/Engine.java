/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs;


import static java.util.Objects.*;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Olivier Grégoire
 */
public final class Engine {

  private final ImmutableSet<EntityManager> managers;
  private final ImmutableSet<EntitySystem> systems;
  private final Injector injector;

  private Engine(Builder builder) {
    managers = builder.managers.build();
    systems = builder.systems.build();
    injector = new Injector();
    managers.forEach((manager) -> {
      injector.inject(manager);
      manager.initialize();
    });
    systems.forEach((system) -> {
      injector.inject(system);
    });
  }

  public void process() {
    systems.forEach((system) -> {
      system.doProcess();
    });
  }

  public static class Builder {

    private final ImmutableSet.Builder<EntityManager> managers = ImmutableSet.builder();
    private final ImmutableSet.Builder<EntitySystem> systems = ImmutableSet.builder();

    public Builder addManager(EntityManager manager) {
      requireNonNull(manager, "manager must not be null");
      managers.add(manager);
      return this;
    }

    public Builder addSystem(EntitySystem system) {
      requireNonNull(system, "system must not be null");
      systems.add(system);
      return this;
    }

    public Engine build() {
      return new Engine(this);
    }
  }

  final class Injector {

    final ImmutableClassToInstanceMap<Object> injectables;
    
    Injector() {
      MutableClassToInstanceMap<Object> map = MutableClassToInstanceMap.create();
      managers.forEach(o-> map.put(o.getClass(), o));
      systems.forEach(o-> map.put(o.getClass(), o));
      map.put(Engine.class, Engine.this);
      injectables = ImmutableClassToInstanceMap.copyOf(map);
    }

    void inject(Object injectee) {
      if (injectee.getClass().isAnnotationPresent(Inject.class)) {
        for (Class<?> type = injectee.getClass(); type != Object.class; type = type.getSuperclass()) {
          for (Field field : type.getDeclaredFields()) {
            tryInject(injectee, field);
          }
        }
      } else {
        for (Class<?> type = injectee.getClass(); type != Object.class; type = type.getSuperclass()) {
          for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
              tryInject(injectee, field);
            }
          }
        }
      }
    }

    private void tryInject(Object injectee, Field field) {
      Class<?> fieldType = field.getType();
      Object injectable = injectables.get(fieldType);
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

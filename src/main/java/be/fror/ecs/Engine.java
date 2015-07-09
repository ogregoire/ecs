/*
 * Copyright 2015 Olivier Grégoire.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.fror.ecs;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import be.fror.ecs.tool.FunctionalBind;
import be.fror.ecs.tool.Reflection;

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

  private final ImmutableSet<Manager> managers;
  private final ImmutableSet<Processor> processors;
  private final Injector injector;

  private Engine(Builder builder) {
    managers = builder.managers.build();
    processors = builder.processors.build();
    injector = new Injector();
    
    managers.forEach(injector::inject);
    managers.forEach(Manager::initialize);
    processors.forEach(injector::inject);
  }

  public void process() {
    processors.forEach(Processor::doProcess);
  }

  public static class Builder {

    private final ImmutableSet.Builder<Manager> managers = ImmutableSet.builder();
    private final ImmutableSet.Builder<Processor> processors = ImmutableSet.builder();

    public Builder add(Manager manager) {
      requireNonNull(manager, "manager must not be null");
      managers.add(manager);
      return this;
    }

    public Builder add(Processor system) {
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

    private <T> Map<? extends Class<? extends Object>, ? extends Object> toClassToInstanceMap(Collection<T> collection) {
      return collection.stream().collect(toMap(Object::getClass, identity()));
    }

    void inject(Object injectee) {
      Reflection.lineage(injectee.getClass())
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
      } else {
        throw new RuntimeException("No injectable element defined for field " + field);
      }
    }
  }
}

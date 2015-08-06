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

import be.fror.ecs.internal.Reflection;

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Olivier Grégoire
 */
public final class EngineBuilder {

  final Set<Class<? extends Component>> componentTypes = new LinkedHashSet<>();
  final List<Processor> processors = new ArrayList<>();
  private final Injector injector = new Injector();

  public EngineBuilder bind(Processor processor) {
    checkNotNull(processor, "processor must not be null");
    doBind(processor.getClass(), processor);
    processors.add(processor);
    return this;
  }

  public <P extends Processor> EngineBuilder bind(Class<P> key, P processor) {
    checkNotNull(key, "key must not be null");
    checkNotNull(processor, "processor must not be null");
    doBind(key, processor);
    processors.add(processor);
    return this;
  }

  public EngineBuilder bind(Object injectable) {
    checkNotNull(injectable, "injectable must not be null");
    doBind(injectable.getClass(), injectable);
    return this;
  }

  public <T> EngineBuilder bind(Class<T> key, T injectable) {
    checkNotNull(key, "key must not be null");
    checkNotNull(injectable, "injectable must not be null");
    doBind(key, injectable);
    return this;
  }

  private void doBind(Class<?> key, Object injectable) {
    checkArgument(key != ComponentMapper.class && key != Engine.class, "%s may not be bound", key);
    List<Class<? extends Component>> ct = collectComponentTypes(injectable).collect(Collectors.toList());
    injector.register(key, injectable);
    componentTypes.addAll(ct);
  }

  static Stream<Class<? extends Component>> collectComponentTypes(Object o) {
    return Reflection.lineage(o.getClass())
        .flatMap(Reflection::getDeclaredFields)
        .map(Reflection::extractComponentType)
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  private ImmutableMap<Type, ComponentMapper<?>> buildComponentMappers(Engine engine) {
    ImmutableMap.Builder<Type, ComponentMapper<?>> componentMappersBuilder = ImmutableMap.builder();
    int i = 0;
    for (Class<? extends Component> c : componentTypes) {
      componentMappersBuilder.put(c, new ComponentMapper(engine, i));
      i++;
    }
    return componentMappersBuilder.build();
  }

  public Engine build() {
    Engine engine = new Engine(this);
    injector.register(Engine.class, engine);
    injector.componentMappers = buildComponentMappers(engine);
    injector.inject();
    return engine;
  }

}

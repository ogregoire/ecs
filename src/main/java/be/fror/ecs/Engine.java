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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Olivier Grégoire
 */
public final class Engine {

  final Component[][] components;
  final Processor[] processors;
  final ImmutableMap<Class<? extends Component>, ComponentMapper<?>> componentMappers;

  Engine(Builder builder) {
    components = new Component[builder.componentTypes.size()][32];
    processors = builder.processors.toArray(new Processor[0]);
    ImmutableMap.Builder<Class<? extends Component>, ComponentMapper<?>> componentMappersBuilder = ImmutableMap.builder();
    int i = 0;
    for (Class<? extends Component> c : builder.componentTypes) {
      componentMappersBuilder.put(c, new ComponentMapper(this, i));
      i++;
    }
    this.componentMappers = componentMappersBuilder.build();
  }

  public void process() {
    for (int i = 0; i < processors.length; i++) {
      processors[i].doProcess();
    }
  }

  void setComponent(int componentId, int entityId, Component component) {
    Component[] comps = components[componentId];
    if (comps.length <= entityId) {
      components[componentId] = comps = Arrays.copyOf(comps, Integer.highestOneBit(entityId) << 1);
    }
    comps[entityId] = component;
  }

  public static class Builder {

    private final Set<Class<? extends Component>> componentTypes = new LinkedHashSet<>();
    private final List<Processor> processors = new ArrayList<>();
    private final Injector injector = new Injector();

    public Builder add(Processor processor) {
      checkNotNull(processor, "processor must not be null");
      collectComponentTypes(processor).forEach(componentTypes::add);
      processors.add(processor);
      injector.register(processor);

      return this;
    }

    public Builder add(Object injectable) {
      checkNotNull(injectable, "injectable must not be null");
      collectComponentTypes(injectable).forEach(componentTypes::add);
      injector.register(injectable);

      return this;
    }

    static Stream<Class<? extends Component>> collectComponentTypes(Object o) {
      return Reflection.lineage(o.getClass())
          .flatMap(Reflection::getDeclaredFields)
          .map(Reflection::extractComponentType)
          .filter(Optional::isPresent)
          .map(Optional::get);
    }

    public Engine build() {
      Engine engine = new Engine(this);
      injector.register(engine);
      injector.inject();
      return engine;
    }
  }
}

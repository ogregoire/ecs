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


import com.google.common.collect.ImmutableMap;

import java.util.Arrays;

/**
 *
 * @author Olivier Grégoire
 */
public final class Engine {

  final Component[][] components;
  final Processor[] processors;
  final ImmutableMap<Class<?>, ComponentMapper<?>> componentMappers;

  Engine(EngineBuilder builder) {
    components = new Component[builder.componentTypes.size()][32];
    processors = builder.processors.toArray(new Processor[0]);
    this.componentMappers = builder.buildComponentMappers(this);
  }

  public void process() {
    for (int i = 0; i < processors.length; i++) {
      processors[i].doProcess();
    }
  }

  void setComponent(int componentId, int entityId, Component component) {
    Component[] c = components[componentId];
    if (c.length <= entityId) {
      components[componentId] = c = Arrays.copyOf(c, Integer.highestOneBit(entityId) << 1);
    }
    c[entityId] = component;
  }

  
}

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

import static com.google.common.base.Preconditions.checkNotNull;

import be.fror.ecs.tool.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Olivier Grégoire
 */
public final class Engine {

  final Component[][] components;

  Engine(Builder builder) {
    components = new Component[10][32];
  }

  public void process() {
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
    
    public Builder addProcessor(Processor processor) {
      checkNotNull(processor, "processor must not be null");
      collectComponents(processor);
      
      return this;
    }

    public Builder addTool(Object injectable) {
      checkNotNull(injectable, "injectable must not be null");
      collectComponents(injectable);
      
      return this;
    }

    void collectComponents(Object o) {
      Reflection.lineage(o.getClass())
          .forEach((c) -> {
            for (Field f: c.getDeclaredFields()) {
              Type type = f.getGenericType();
              if (!(type instanceof ParameterizedType)) {
                continue;
              }
              ParameterizedType pType = (ParameterizedType)type;
              if (pType.getRawType() != ComponentMapper.class) {
                continue;
              }
              // length is 1.
              Type argType = pType.getActualTypeArguments()[0];
              if (!(argType instanceof Class)) {
                continue;
              }
              Class<? extends Component> argClass = (Class<? extends Component>)argType;
              componentTypes.add(argClass);
            }
          });
    }
    
    public Engine build() {
      return new Engine(this);
    }
  }
}

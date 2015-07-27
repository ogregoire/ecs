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

import be.fror.ecs.tool.Reflection;

import com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Olivier Grégoire
 */
public class EngineBuilder {
  final Set<Class<? extends Component>> componentTypes = new LinkedHashSet<>();

  public EngineBuilder addProcessor(Processor processor) {
    Preconditions.checkNotNull(processor, "processor must not be null");
    findDeclaredComponents(processor).forEach(componentTypes::add);
    return this;
  }

  public EngineBuilder addTool(Object injectable) {
    Preconditions.checkNotNull(injectable, "injectable must not be null");
    findDeclaredComponents(injectable).forEach(componentTypes::add);
    return this;
  }

  Stream<Class<? extends Component>> findDeclaredComponents(Object o) {
    return Reflection.lineage(o.getClass())
        .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
        .map(f -> f.getGenericType())
        .filter(t -> t instanceof ParameterizedType)
        .map(t -> (ParameterizedType)t)
        .filter(t -> t.getRawType() == ComponentMapper.class)
        .map(t -> t.getActualTypeArguments()[0])
        .filter(t -> t instanceof Class)
        .map(t -> (Class<? extends Component>)t);
  }
  
  public Engine build() {
    return new Engine(this);
  }
  
}

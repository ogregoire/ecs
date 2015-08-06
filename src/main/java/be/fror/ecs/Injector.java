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

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Olivier Grégoire
 */
class Injector {

  private final Map<Class<?>, Object> bindings = new HashMap<>();
  ImmutableMap<Class<?>, ComponentMapper<?>> componentMappers;

  void inject() {
    for (Object object : bindings.values()) {
      collectFields(object).forEach((field) -> {
        Type type = field.getType();
        Object value = null;
        if (bindings.containsKey(type)) {
          value = bindings.get(type);
        } else if (type == ComponentMapper.class && field.getGenericType() instanceof ParameterizedType) {
          ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
          Type t = parameterizedType.getActualTypeArguments()[0];
          value = componentMappers.get(t);
        }
        if (value != null) {
          try {
            field.setAccessible(true);
            field.set(object, value);
          } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("Cannot write field " + field, ex);
          }
        }
      });
    }
  }

  private static Stream<Field> collectFields(Object o) {
    return Reflection.lineage(o.getClass())
        .flatMap(Reflection::getDeclaredFields)
        .filter(Reflection::isNonFinalInstanceField);
  }

  void register(Class<?> key, Object injectable) {
    if (bindings.containsKey(key)) {
      throw new RuntimeException(String.format("key (%s) is already registered", key.getName()));
    }
    bindings.put(key, injectable);
  }

}

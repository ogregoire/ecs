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
package be.fror.ecs._internal;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import be.fror.ecs.Component;
import be.fror.ecs.ComponentMapper;

import com.google.common.collect.AbstractIterator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Olivier Grégoire
 */
public class Reflection {

  private Reflection() {
  }

  public static <T> Stream<Class<? super T>> lineage(Class<T> type) {
    return stream(spliteratorUnknownSize(new LineageIterator<>(type), 0), false);
  }

  private static class LineageIterator<T> extends AbstractIterator<Class<? super T>> {

    private Class<? super T> type;

    private LineageIterator(Class<T> type) {
      this.type = type;
    }

    @Override
    protected Class<? super T> computeNext() {
      if (type == null) {
        return endOfData();
      }
      Class<? super T> next = type;
      type = type.getSuperclass();
      return next;
    }
  }

  public static <T extends AnnotatedElement> Predicate<T> isAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return a -> a.isAnnotationPresent(annotationClass);
  }

  public static Stream<Field> getDeclaredFields(Class<?> type) {
    return Arrays.stream(type.getDeclaredFields());
  }

  public static boolean isNonFinalInstanceField(Field field) {
    int modifiers = field.getModifiers();
    return !Modifier.isFinal(modifiers)
        && !Modifier.isStatic(modifiers);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Class<? extends Component>> extractComponentType(Field field) {
    if (field.getType() != ComponentMapper.class) {
      return Optional.empty();
    }
    Type fieldGenericType = field.getGenericType();
    if (!(fieldGenericType instanceof ParameterizedType)) {
      return Optional.empty();
    }
    Type fieldArgumentType = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
    if (!(fieldArgumentType instanceof Class)) {
      return Optional.empty();
    }
    return Optional.of((Class<? extends Component>) fieldArgumentType);
  }
}

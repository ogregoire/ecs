/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import com.google.common.collect.AbstractIterator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Olivier Grégoire
 */
class Reflection {

  private Reflection() {
  }

  static <T> Stream<Class<? super T>> classParents(Class<T> type) {
    return stream(spliteratorUnknownSize(new HierarchyIterator<>(type), 0), false);
  }

  private static class HierarchyIterator<T> extends AbstractIterator<Class<? super T>> {

    private Class<? super T> type;

    private HierarchyIterator(Class<T> type) {
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

  static boolean isInjectAnnotationPresent(AnnotatedElement e) {
    return e.isAnnotationPresent(Inject.class);
  }
  
  static <T extends AnnotatedElement> Predicate<T> isAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return (a) -> a.isAnnotationPresent(annotationClass);
  }
  
  static Stream<Field> getDeclaredFields(Class<?> type) {
    return Arrays.stream(type.getDeclaredFields());
  }
}

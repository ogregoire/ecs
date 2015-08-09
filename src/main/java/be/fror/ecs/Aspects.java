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

import be.fror.ecs._internal.Reflection;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 *
 * @author Olivier Grégoire
 */
class Aspects {

  static Predicate<Entity> asEntityPredicate(Object object) {
    Set<Class<? extends Component>> all = new LinkedHashSet<>();
    Set<Class<? extends Component>> any = new LinkedHashSet<>();
    Set<Class<? extends Component>> none = new LinkedHashSet<>();
    AtomicBoolean empty = new AtomicBoolean(true);
    Reflection.lineage(object.getClass())
        .filter(type -> type.isAnnotationPresent(Aspect.class))
        .map(type -> type.getAnnotation(Aspect.class))
        .forEach(aspect -> {
          if (all.addAll(Arrays.asList(aspect.allOf())) | any.addAll(Arrays.asList(aspect.anyOf())) | none.addAll(Arrays.asList(aspect.noneOf()))) {
            empty.set(false);
          }
        });
    if (empty.get()) {
      return (e) -> true;
    }
    if (all.removeAll(any) || all.removeAll(none) || any.removeAll(none)) {
      throw new IllegalArgumentException("@Aspect's all, any and none overlap");
    }
    // TODO finish
    return null;
  }
}

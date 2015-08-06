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

import static com.google.common.base.Preconditions.checkArgument;

import be.fror.ecs.internal.Reflection;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Olivier Grégoire
 */
class ConcreteAspect {

  static ConcreteAspect createFrom(Object object) {
    Set<Class<? extends Component>> all = new LinkedHashSet<>();
    Set<Class<? extends Component>> any = new LinkedHashSet<>();
    Set<Class<? extends Component>> none = new LinkedHashSet<>();
    Reflection.lineage(object.getClass())
        .filter(type -> type.isAnnotationPresent(Aspect.class))
        .map(type -> type.getAnnotation(Aspect.class))
        .forEach(aspect -> {
          all.addAll(Arrays.asList(aspect.allOf()));
          any.addAll(Arrays.asList(aspect.anyOf()));
          none.addAll(Arrays.asList(aspect.noneOf()));
        });
    checkArgument(!all.removeAll(any) && !all.removeAll(none) && !any.removeAll(none), "@Aspect's all, any and none overlap");
    return new ConcreteAspect(all, any, none);
  }

  final ImmutableSet<Class<? extends Component>> all;
  final ImmutableSet<Class<? extends Component>> any;
  final ImmutableSet<Class<? extends Component>> none;

  ConcreteAspect(
      Set<Class<? extends Component>> all,
      Set<Class<? extends Component>> any,
      Set<Class<? extends Component>> none) {
    this.all = ImmutableSet.copyOf(all);
    this.any = ImmutableSet.copyOf(any);
    this.none = ImmutableSet.copyOf(none);
  }
}

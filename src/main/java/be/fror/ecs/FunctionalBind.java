/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Olivier Grégoire
 */
class FunctionalBind {

  private FunctionalBind() {
  }

  static <A, B> Consumer<A> bindLast(BiConsumer<A, B> fn, B b) {
    return a -> fn.accept(a, b);
  }

  static <A, B> Consumer<B> bindFirst(BiConsumer<A, B> fn, A a) {
    return b -> fn.accept(a, b);
  }
}

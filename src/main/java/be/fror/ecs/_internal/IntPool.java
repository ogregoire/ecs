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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 *
 * @author Olivier Grégoire
 */
public final class IntPool {

  public static IntPool incrementing() {
    return incrementingStartingAt(0);
  }

  public static IntPool incrementingStartingAt(final int startingValue) {
    return new IntPool(new AtomicInteger(startingValue)::getAndIncrement);
  }

  public static IntPool ofValues(final IntSupplier values) {
    if (values == null) {
      throw new NullPointerException("values must not be null");
    }
    return new IntPool(values);
  }

  private static final int INITIAL_POOL_SIZE = 32;

  private final IntSupplier nextValues;
  private int[] pool = new int[INITIAL_POOL_SIZE];
  private int poolSize = 0;

  private IntPool(final IntSupplier values) {
    nextValues = values;
  }

  public int lease() {
    if (poolSize == 0) {
      return nextValues.getAsInt();
    } else {
      return pool[--poolSize];
    }
  }

  public void release(final int value) {
    if (poolSize == pool.length) {
      // TODO probably multiplying by 2 is a bad idea. Use phi instead?
      pool = Arrays.copyOf(pool, poolSize * 2);
    }
    pool[poolSize++] = value;
  }

  public void lease(IntConsumer consumer) {
    int value = lease();
    try {
      consumer.accept(value);
    } finally {
      release(value);
    }
  }
  
}

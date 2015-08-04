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
package be.fror.ecs.internal;

import be.fror.ecs.internal.IntPool;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Olivier Grégoire
 */
public class IntPoolTest {

  public IntPoolTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of ofIncrementingValues method, of class IntPool.
   */
  @Test
  public void testOfIncrementingValues_0args() {
    IntPool pool = IntPool.ofIncrementingValues();
    assertThat(pool.lease(), is(0));
    assertThat(pool.lease(), is(1));
    assertThat(pool.lease(), is(2));
  }

  /**
   * Test of ofIncrementingValues method, of class IntPool.
   */
  @Test
  public void testOfIncrementingValues_int() {
    IntPool pool = IntPool.ofIncrementingValues(5);
    assertThat(pool.lease(), is(5));
    assertThat(pool.lease(), is(6));
    assertThat(pool.lease(), is(7));
  }

  /**
   * Test of ofValues method, of class IntPool.
   */
  @Test
  public void testOfValues() {
  }

  /**
   * Test of lease method, of class IntPool.
   */
  @Test
  public void testLease() {
  }

  /**
   * Test of release method, of class IntPool.
   */
  @Test
  public void testRelease() {
  }

}

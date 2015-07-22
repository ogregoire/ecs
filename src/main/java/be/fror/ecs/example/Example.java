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
package be.fror.ecs.example;

import be.fror.ecs.Engine;
import be.fror.ecs.Processor;

/**
 *
 * @author Olivier Grégoire
 */
public class Example {
  public static void main(String[] args) {
    new Engine.Builder()
        .addProcessor(new ExampleProcessor())
        .build()
        .process();
  }
  
  static class ExampleProcessor extends Processor {
    @Override
    protected void process() {
      System.out.println(getEngine());
    }
  }
}
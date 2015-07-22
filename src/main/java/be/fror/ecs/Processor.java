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

/**
 *
 * @author Olivier Grégoire
 */
public abstract class Processor {

  protected final Engine getEngine() {
    return null;
  }

  final void doProcess() {
    beforeProcess();
    process();
    afterProcess();
  }

  protected void beforeProcess() {
  }

  protected abstract void process();

  protected void afterProcess() {
  }
}
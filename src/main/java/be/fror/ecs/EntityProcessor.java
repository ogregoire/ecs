/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs;

/**
 *
 * @author Olivier Grégoire
 */
public abstract class EntityProcessor {

  @Inject
  private Engine engine;

  protected final Engine getEngine() {
    return engine;
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

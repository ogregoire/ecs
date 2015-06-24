/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Olivier Grégoire
 */
public final class Entity {

  private final Map<Class<? extends Component>, Component> components = new HashMap<>();

  public Entity set(Component component) {
    components.put(component.getClass(), component);
    return this;
  }

  public boolean has(Class<? extends Component> type) {
    return components.containsKey(type);
  }

  @SuppressWarnings("unchecked")
  public <T extends Component> T get(Class<T> type) {
    return (T) components.get(type);
  }
}

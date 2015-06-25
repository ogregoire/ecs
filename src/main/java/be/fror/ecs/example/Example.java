/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.ecs.example;

import be.fror.ecs.Engine;
import be.fror.ecs.EntityProcessor;

/**
 *
 * @author Olivier Grégoire
 */
public class Example {
  public static void main(String[] args) {
    new Engine.Builder()
        .add(new ExampleProcessor())
        .build()
        .process();
  }
  
  static class ExampleProcessor extends EntityProcessor {
    @Override
    protected void process() {
      System.out.println(getEngine());
    }
  }
}

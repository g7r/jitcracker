package com.mailru.allodsteam.jitcracker.main;

import com.mailru.allodsteam.jitcracker.patcher.Patcher;

/**
 * @author szagurskiy
 */
public final class Main {

  public static void main(String[] args) throws NoSuchMethodException {
    Patcher.patchMethod(Math.class, "sin", Helper.class, "sinDonor");

    try {
      System.out.println(Math.sin(1.0));
//      Helper.foo();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}

package com.mailru.allodsteam.jitcracker.main;

/**
 * @author szagurskiy
 */
public final class Helper {

  public static void foo() {

  }

  public static void fooDonor() {
    throw new RuntimeException();
  }

  public static double sinDonor(double x) {
    throw new RuntimeException();
  }

}

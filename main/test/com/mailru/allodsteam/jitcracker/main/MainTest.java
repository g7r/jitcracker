package com.mailru.allodsteam.jitcracker.main;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author szagurskiy
 */
public final class MainTest {

  private static final String MARKER_EXCEPTION_MESSAGE = "EXCEPTION_MARKER";

  private static final Object[] MARKER_ARRAY = new Object[123];

  @Rule
  public final PatchRule patcher = new PatchRule();

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  private static void foo() {
  }

  private static void fooDonor() {
    throw new RuntimeException(MARKER_EXCEPTION_MESSAGE);
  }

  private static double cosDonor(double x) {
    throw new RuntimeException(MARKER_EXCEPTION_MESSAGE);
  }

  private static long nanoTimeDonor() {
    throw new RuntimeException(MARKER_EXCEPTION_MESSAGE);
  }

  private static int numberOfLeadingZerosDonor(int x) {
    throw new RuntimeException(MARKER_EXCEPTION_MESSAGE);
  }

  private void expectMarkerException() {
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage(MARKER_EXCEPTION_MESSAGE);
  }

  @Test
  public void testPatchedFoo() throws Exception {
    patcher.patchMethod(MainTest.class, "foo", MainTest.class, "fooDonor");

    expectMarkerException();
    foo();
  }

  @Test
  public void testPatchedCos() throws Exception {
    patcher.patchMethod(Math.class, "cos", MainTest.class, "cosDonor");

    expectMarkerException();
    Math.cos(0.0);
  }

  @Test
  public void testPatchedNumberOfLeadingZeros() throws Exception {
    patcher.patchMethod(
        Integer.class, "numberOfLeadingZeros",
        MainTest.class, "numberOfLeadingZerosDonor"
    );

    expectMarkerException();
    Integer.numberOfLeadingZeros(0);
  }

  @Test
  public void testPatchedNumberOfLeadingZerosWithJit() throws Exception {
    patcher.patchMethod(
        Integer.class, "numberOfLeadingZeros",
        MainTest.class, "numberOfLeadingZerosDonor"
    );

    for (int i = 0; i < 100000; ++i) {
      try {
        callIntegerNumberOfLeadingZeros();
        Assert.fail("Shouldn't reach this line: i=" + i);
      } catch (RuntimeException e) {
        Assert.assertEquals(
            "Exception message should be \"" + MARKER_EXCEPTION_MESSAGE + "\": i=" + i,
            MARKER_EXCEPTION_MESSAGE,
            e.getMessage()
        );
      }
    }
  }

  private int callIntegerNumberOfLeadingZeros() {
    return Integer.numberOfLeadingZeros(0);
  }
}

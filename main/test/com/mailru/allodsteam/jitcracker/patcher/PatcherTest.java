package com.mailru.allodsteam.jitcracker.patcher;

import org.junit.Test;

/**
 * @author szagurskiy
 */
public final class PatcherTest {

  private static void foo() {
  }

  private static void fooDonor() {
    throw new CustomException();
  }

  @Test(expected = CustomException.class)
  public void testPatch() throws Exception {
    Patcher.patchMethod(PatcherTest.class, "foo", "fooDonor");

    try {
      foo();
    } finally {
      Patcher.resetClass(PatcherTest.class);
    }
  }

  @Test
  public void testUnpatch() throws Exception {
    Patcher.patchMethod(PatcherTest.class, "foo", "fooDonor");
    Patcher.resetClass(PatcherTest.class);

    foo();
  }

  private static final class CustomException extends RuntimeException {
    public CustomException() {
    }
  }
}

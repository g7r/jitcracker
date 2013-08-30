package com.mailru.allodsteam.jitcracker.patcher.impl;

import java.lang.instrument.Instrumentation;

/**
 * @author szagurskiy
 */
public final class InstrumentationProvider {

  private static Instrumentation instrumentation;

  public static void init(Instrumentation instrumentation) {
    InstrumentationProvider.instrumentation = instrumentation;
  }

  public static Instrumentation getInstrumentation() {
    return instrumentation;
  }
}

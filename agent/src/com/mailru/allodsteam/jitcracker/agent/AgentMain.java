package com.mailru.allodsteam.jitcracker.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author szagurskiy
 */
public final class AgentMain {

  public static void premain(String args, Instrumentation instrumentation) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final Class<?> patcherClass = Class.forName("com.mailru.allodsteam.jitcracker.patcher.impl.InstrumentationProvider");
    final Method initMethod = patcherClass.getMethod("init", Instrumentation.class);
    initMethod.invoke(null, instrumentation);
  }
}

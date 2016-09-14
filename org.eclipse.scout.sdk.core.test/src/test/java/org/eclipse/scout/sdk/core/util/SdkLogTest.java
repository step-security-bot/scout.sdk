/*******************************************************************************
 * Copyright (c) 2016 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.core.util;

import java.util.Arrays;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.scout.sdk.core.util.SdkConsole.ISdkConsoleSpi;
import org.junit.Assert;
import org.junit.Test;

/**
 * <h3>{@link SdkLogTest}</h3>
 *
 * @author Matthias Villiger
 * @since 5.2.0
 */
public class SdkLogTest {
  @Test
  public void testHandlePlaceholders() {
    assertPlaceholders("a {} b", "a {} b");
    assertPlaceholders("a null b", "a {} b", new Object[]{null});
    assertPlaceholders("a 1 b", "a {} b", 1);
    assertPlaceholders("a 1 b", "a {} b", 1, "notused");
    assertPlaceholders("a testle b", "a {} b", "testle");
    assertPlaceholders("a [] b", "a {} b", new Object[]{new Object[]{}});
    assertPlaceholders("a [1, 2, 3] b", "a {} b", new Object[]{new Object[]{1, 2, 3}});
    assertPlaceholders("a [1, 2, 3] b", "a {} b", new Object[]{new Integer[]{1, 2, 3}});
    assertPlaceholders("a testle b {} c", "a {} b {} c", "testle");
    assertPlaceholders("a [true, false, false] b", "a {} b", new Object[]{new boolean[]{true, false, false}});
    assertPlaceholders("a [40, 41, 2] b", "a {} b", new Object[]{new byte[]{40, 41, 2}});
    assertPlaceholders("a [a, l, d] b", "a {} b", new Object[]{new char[]{'a', 'l', 'd'}});
    assertPlaceholders("a [40, 41, 2] b", "a {} b", new Object[]{new short[]{40, 41, 2}});
    assertPlaceholders("a [40, 41, 2] b", "a {} b", new Object[]{new int[]{40, 41, 2}});
    assertPlaceholders("a [40, 41, 2000000000000000000] b", "a {} b", new Object[]{new long[]{40L, 41L, 2000000000000000000L}});
    assertPlaceholders("a [1.1, 1.002, 300.0] b", "a {} b", new Object[]{new float[]{1.1f, 1.002f, 300f}});
    assertPlaceholders("a [11.3, 12.004, 100.0] b", "a {} b", new Object[]{new double[]{11.3, 12.004, 100}});

    Object[] longArgs = new Object[1001];
    for (int i = 0; i < longArgs.length; i++) {
      longArgs[i] = Integer.valueOf(i + 1);
    }
    StringBuilder expected = new StringBuilder("1");
    for (int i = 2; i <= 1000; i++) {
      expected.append(", ");
      expected.append(Integer.toString(i));
    }

    assertPlaceholders("a [" + expected.toString() + ",...] b", "a {} b", new Object[]{longArgs});
  }

  private void assertPlaceholders(String expected, String input, Object... args) {
    StringBuilder sb = new StringBuilder(input);
    int retIndex = SdkLog.handlePlaceholders(sb, args);
    Assert.assertEquals(expected, sb.toString());

    int expectedNextIndex = 0;
    if (args != null) {
      expectedNextIndex = Math.min(StringUtils.countMatches(input, SdkLog.ARG_REPLACE_PATTERN), args.length);
    }
    Assert.assertEquals(expectedNextIndex, retIndex);
  }

  @Test
  public void testExtractThrowables() {
    Assert.assertNull(SdkLog.extractThrowables(0));
    Assert.assertNull(SdkLog.extractThrowables(0, (Object[]) null));
    Assert.assertNull(SdkLog.extractThrowables(0, "a", "b", null));
    Assert.assertNull(SdkLog.extractThrowables(4, "a", "b", null));
    Assert.assertEquals(1, SdkLog.extractThrowables(0, "a", "b", new Exception()).length);
    Assert.assertNull(SdkLog.extractThrowables(3, "a", "b", new Exception()));
    Assert.assertEquals(1, SdkLog.extractThrowables(2, "a", "b", new Exception()).length);
    Assert.assertEquals(2, SdkLog.extractThrowables(2, "a", "b", new Object[]{new Exception(), new Exception()}).length);
    Assert.assertEquals(3, SdkLog.extractThrowables(2, "a", "b", new Object[]{new Exception(), new Object[]{new Exception(), new Exception()}}).length);
    Assert.assertEquals(3, SdkLog.extractThrowables(2, "a", "b", new Object[]{Arrays.asList(new Exception()), Arrays.asList(new Exception(), new Exception())}).length);
  }

  @Test
  public void testParseLevel() {
    Assert.assertEquals(SdkLog.DEFAULT_LOG_LEVEL, SdkLog.parseLevel(null));
    Assert.assertEquals(SdkLog.DEFAULT_LOG_LEVEL, SdkLog.parseLevel(""));
    Assert.assertEquals(SdkLog.DEFAULT_LOG_LEVEL, SdkLog.parseLevel(" "));
    Assert.assertEquals(SdkLog.DEFAULT_LOG_LEVEL, SdkLog.parseLevel("\t"));
    Assert.assertEquals(Level.SEVERE, SdkLog.parseLevel(Level.SEVERE.getName()));
    Assert.assertEquals(SdkLog.DEFAULT_LOG_LEVEL, SdkLog.parseLevel("aa"));
  }

  @Test
  public void testLogOfObjectWithToStringThrowingException() throws Exception {
    runWithPrivateLogger(new ILogTestRunner() {
      @Override
      public void run(StringBuilder logContent) throws Exception {
        SdkLog.error("Msg: {}", new ClassWithToStringThrowingNpeFixture());
        Assert.assertEquals("[SEVERE]: Msg: [FAILED toString() of class " + SdkLogTest.class.getName() + '$' + ClassWithToStringThrowingNpeFixture.class.getSimpleName() + ']', logContent.toString());
        SdkConsole.clear();
      }
    });
  }

  @Test
  public void testLog() throws Exception {
    runWithPrivateLogger(new ILogTestRunner() {
      @Override
      public void run(StringBuilder logContent) {
        SdkLog.warning("hello");
        Assert.assertEquals("[WARNING]: hello", logContent.toString());
        Assert.assertTrue(SdkLog.isWarningEnabled());
        SdkConsole.clear();

        Exception exception = new Exception();
        SdkLog.warning("hello {} there", "test", exception);
        Assert.assertEquals("[WARNING]: hello test there" + CoreUtils.getThrowableAsString(exception), logContent.toString());
        SdkConsole.clear();

        SdkLog.error(exception);
        Assert.assertEquals("[SEVERE]: " + CoreUtils.getThrowableAsString(exception), logContent.toString());
        Assert.assertTrue(SdkLog.isErrorEnabled());
        SdkConsole.clear();

        SdkLog.warning(null, (Object[]) null);
        Assert.assertEquals("[WARNING]: ", logContent.toString());
        SdkConsole.clear();

        SdkLog.info("hello");
        Assert.assertEquals("", logContent.toString());
        Assert.assertFalse(SdkLog.isInfoEnabled());
        SdkConsole.clear();

        SdkLog.log(Level.OFF, "hello");
        Assert.assertEquals("", logContent.toString());
        Assert.assertFalse(SdkLog.isDebugEnabled());
        SdkConsole.clear();

        Assert.assertFalse(SdkLog.isLevelEnabled(null));
        SdkLog.log(null, "hello");
        Assert.assertEquals("[WARNING]: hello", logContent.toString());
        SdkConsole.clear();
      }
    });
  }

  private static final class ClassWithToStringThrowingNpeFixture {
    @Override
    public String toString() {
      throw new NullPointerException("NPE of test " + SdkLogTest.class);
    }
  }

  private interface ILogTestRunner {
    void run(StringBuilder logContent) throws Exception;
  }

  private static void runWithPrivateLogger(ILogTestRunner runnable) throws Exception {
    // lock on console to ensure no other thread writes to the console while we are testing (in case tests are running in parallel)
    synchronized (SdkConsole.class) {
      ISdkConsoleSpi backup = SdkConsole.getConsoleSpi();
      try {
        final StringBuilder logContent = new StringBuilder();
        SdkConsole.setConsoleSpi(new ISdkConsoleSpi() {

          @Override
          public void println(Level level, String s, Throwable... exceptions) {
            logContent.append(s);
            if (exceptions == null) {
              return;
            }
            for (Throwable t : exceptions) {
              if (t != null) {
                logContent.append(CoreUtils.getThrowableAsString(t));
              }
            }
          }

          @Override
          public void clear() {
            logContent.delete(0, logContent.length());
          }
        });

        runnable.run(logContent);

      }
      finally {
        SdkConsole.setConsoleSpi(backup);
      }
    }
  }
}

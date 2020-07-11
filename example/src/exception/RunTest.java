package exception;

import java.lang.reflect.*;

// Program to process marker annotations and annotations with array parameter (Page 185)
public class RunTest {
  public static void main(String[] args) throws Exception {
    int tests = 0;
    int passed = 0;
    // 通过反射获取类的相关信息
    Class<?> testClass = Class.forName(args[0]);
    // 获取每个方法
    for (Method m : testClass.getDeclaredMethods()) {
      // 判断是否添加了相应的注解，需要现在的这个代码块需要运行哪些方法
      if (m.isAnnotationPresent(Test.class)) {
        tests++;
        try {
          // 通过Method.invoke()反射的调用注解之下的方法
          m.invoke(null);
          passed++;
        } catch (InvocationTargetException wrappedExc) {
          Throwable exc = wrappedExc.getCause();
          System.out.println(m + " failed: " + exc);
        } catch (Exception exc) {
          System.out.println("Invalid @Test: " + m);
        }
      }

      // Code to process annotations with array parameter (Page 185)
      if (m.isAnnotationPresent(ExceptionTest.class)) {
        tests++;
        try {
          m.invoke(null);
          System.out.printf("Test %s failed: no exception%n", m);
        } catch (Throwable wrappedExc) {
          Throwable exc = wrappedExc.getCause();
          int oldPassed = passed;
          Class<? extends Throwable>[] excTypes =
              m.getAnnotation(ExceptionTest.class).value();
          for (Class<? extends Throwable> excType : excTypes) {
            if (excType.isInstance(exc)) {
              passed++;
              break;
            }
          }
          if (passed == oldPassed)
            System.out.printf("Test %s failed: %s %n", m, exc);
        }
      }
    }
    System.out.printf("Passed: %d, Failed: %d%n",
        passed, tests - passed);
  }
}
package test.aop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(public * test..*(..)) && !execution(* test.aop..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("\n[AOP LOG] → Вызван метод: " + joinPoint.getSignature().getName());
        if (joinPoint.getArgs().length > 0) {
            System.out.println("  Параметры: " + java.util.Arrays.toString(joinPoint.getArgs()));
        }
    }
    @AfterReturning(pointcut = "execution(public * test..*(..)) && !execution(* test.aop..*(..))", 
                    returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[AOP LOG] ← Метод завершен: " + joinPoint.getSignature().getName());
        if (result != null) {
            System.out.println("  Результат: " + result);
        }
    }
    @AfterThrowing(pointcut = "execution(public * test..*(..)) && !execution(* test.aop..*(..))", 
                   throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        System.out.println("[AOP ERROR] ✗ Ошибка в методе: " + joinPoint.getSignature().getName());
        System.out.println("  Тип ошибки: " + exception.getClass().getSimpleName());
        System.out.println("  Сообщение: " + exception.getMessage());
    }
    @Around("execution(public * test.builder.Test.run(..)) || execution(public * test.factory..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        System.out.println("\n[AOP TIMER] ⏱ Начало выполнения: " + joinPoint.getSignature().getName());
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[AOP TIMER] ⏱ Метод прерван с ошибкой. Время: " + duration + "ms");
            throw throwable;
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[AOP TIMER] ⏱ Время выполнения: " + duration + "ms");
        return result;
    }
}


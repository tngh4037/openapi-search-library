package com.library.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

// Spring에서 @Async를 사용할 때 사용할 비동기 실행기(Executor)를 커스터마이징 ( AsyncConfigurer : @Async가 사용할 Executor 설정을 커스터마이징하기 위한 인터페이스 )
// 즉, @Async 메서드들이 어떤 쓰레드 풀을 사용하는지 직접 설정할 수 있게 해주는 역할
//
// 참고) @Async는 내부적으로 TaskExecutor를 사용해서 비동기 작업을 실행한다.
//  - 만일 별도의 설정이 없으면 기본(SimpleAsyncTaskExecutor)를 사용하는데, 이건 실제 프로덕션 환경에 적합하지 않다. (쓰레드 재사용 없음, 성능 문제 등).
//  - 그래서 아래처럼 직접 설정해서 사용한다.
@Slf4j
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean("lsExecutor")
    @Override
    public Executor getAsyncExecutor() { // getAsyncExecutor()에서 적절한 쓰레드 풀을 만들어서 반환
        // 참고) corePoolsize, maxPoolsize, queueCapacity 등은 상황에 따라 알맞게 설정해야 한다.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cpuCoreCount = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(cpuCoreCount); // 기본 쓰레드 수
        executor.setMaxPoolSize(cpuCoreCount * 2); // 최대 쓰레드 수
        executor.setQueueCapacity(10); // 큐에 쌓일 수 있는 작업 수 ( 작업 큐의 크기, core 수 만큼 작업이 실행중일 때, 이후 작업은 큐에 참 )
        executor.setKeepAliveSeconds(60); // 최대 스레드 수를 초과하는 여분의 스레드가 살아있을 수 있는 시간 ( 설정한 시간이 지나면 초과스레드는 사라짐 )
        executor.setWaitForTasksToCompleteOnShutdown(true); // 애플리케이션이 종료될 때, 스레드 풀에 작업이 남아있다면 그 작업을 완료할 때 까지 기다릴지 말지 설정 ( true: 작업이 다 처리될때 까지 기다리겠다. )
        executor.setAwaitTerminationSeconds(60); // 얼마동안 기다릴지 정의
        executor.setThreadNamePrefix("LS-");
        executor.initialize(); // 내가 설정한대로 초기화되도록
        // executor.setRejectedExecutionHandler(..); // 참고) 더이상 작업을 받을 수 없는 상태가 되어, 이후 작업에 대해 reject 를 처리하는 핸들러 ( reject 시에, 어떻게 처리할지 정책을 정의 ) | 참고) 아래 CustomRejectHandler
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() { // getAsyncUncaughtExceptionHandler() 로, 비동기 메서드를 실행할 때 발생한 예외를 처리를 할 수 있다. => @Async 이 적용된 메서드에서 발생한 예외 처리
        return new CustomAsyncExceptionHandler();
    }

    private static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("Failed to execute {}", ex.getMessage());
            Arrays.asList(params)
                    .forEach(param -> log.error("parameter value = {}", param));
        }
    }

/*
    private static class CustomRejectHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    }
*/

}

// 참고) 필요에 따라, 다양한 서비스마다 다른 Executor를 쓰고 싶으면
// @Async("beanName") 방식으로 Bean을 여러 개 정의할 수도 있다.
package ru.lb.tinkoff.scala.exam.stack.ex1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class HandlerImpl implements Handler {

    private final ExecutorService executorService;
    private static final int TIMEOUT = 4;

    private final Client client;

    public HandlerImpl(Client client) {
        this.client = client;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    //условно считаем, что я знаю, что логировать нужно log, а spring boot позволяет инжектить бины и много чего еще.
    @Override
    public ApplicationStatusResponse performOperation(String id) {
        //что такое 15 сек таймаут не очень ясно, что он должен отработать за 15 сек, принял, что имеется ввиду, что дольше
        //15 секунд не мучаем животное, и отдаем FAIL если за 15 сек не получили ответа
        AtomicInteger retriesCount = new AtomicInteger(0);
        AtomicReference<Duration> lastRequestTime = new AtomicReference<>();
        CompletableFuture<Response> futureOne = CompletableFuture.supplyAsync(
                () -> callWithRetry(() -> client.getApplicationStatus1(id), retriesCount, lastRequestTime), executorService);
        CompletableFuture<Response> futureTwo = CompletableFuture.supplyAsync(
                () -> callWithRetry(() -> client.getApplicationStatus2(id), retriesCount, lastRequestTime), executorService);
        try {
            /*второй тред продолжает работать, даже когда 1ый окончился. Решение? Возможно, если управлять тредами более
            низкоуровнево, но при этом мы можем остановить только ретраи, т.к. там предусмотрена реакция на прерывание.
            или гасить пул
             */
            var response = (Response.Success) CompletableFuture.anyOf(futureOne, futureTwo).get(TIMEOUT, TimeUnit.SECONDS);
            return new ApplicationStatusResponse.Success(id, response.applicationStatus());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Was interrupted");
        } catch (ExecutionException | TimeoutException e) {
            return new ApplicationStatusResponse.Failure(lastRequestTime.get(), retriesCount.get());
        }
    }

    //пробуем получить ответ, пока не появится положительный результат
    public Response.Success callWithRetry(Supplier<Response> supplier, AtomicInteger retriesCount, AtomicReference<Duration> lastRequestTime) {
        Response response;
        do {
            LocalDateTime startTime = LocalDateTime.now();
            response = supplier.get();
            lastRequestTime.set(Duration.between(startTime, LocalDateTime.now()));
            retriesCount.incrementAndGet();
            if (response instanceof Response.RetryAfter) {
                try {
                    Thread.sleep(((Response.RetryAfter) response).delay());
                } catch (InterruptedException e) {
                    System.out.println("Interrupted while retry");
                    throw new RuntimeException("Was interrupted");
                }
            }
        } while (response instanceof Response.RetryAfter || response instanceof Response.Failure);

        return (Response.Success) response;
    }

    public void stopPull() {
        executorService.shutdownNow();
    }
}




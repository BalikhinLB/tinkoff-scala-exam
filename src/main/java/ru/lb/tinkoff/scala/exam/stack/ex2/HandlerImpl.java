package ru.lb.tinkoff.scala.exam.stack.ex2;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class HandlerImpl implements Handler {
    private final Client client;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); //тут может быть нужно что-то другое
    //например пул, на основе количества ядер, вероятно в Java 21 можно использовать newVirtualThreadPerTaskExecutor

    @Override
    public Duration timeout() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }

    @Override
    public void performOperation() {
        Event event;
        do {
            event = client.readData();
            if (event != null) {
                List<Address> recipients = event.recipients();
                Payload payload = event.payload();

                for (var address : recipients) {
                    executorService.submit(() -> sendDataWithRetry(address, payload));
                    //данное решение подходит только в случае, если не важен порядок получения payload адресатами.
                    //если порядок важен, то можно использовать, например CountDownLatch
                    //так же можно не пользоваться пулом, если известно количество адресов (ну что их не оч много), а создавать
                    //просто треды и ждать, что они закончат свою работу
                }
            }
        } while (event != null);
    }

    private void sendDataWithRetry(Address address, Payload payload) {
        Result result;
        do {
            result = client.sendData(address, payload);
            if (Result.REJECTED.equals(result)) {
                try {
                    Thread.sleep(timeout().toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Was interrupted");
                }
            }
        } while (Result.REJECTED.equals(result));
    }
}

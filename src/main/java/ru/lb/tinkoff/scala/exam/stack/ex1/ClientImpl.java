package ru.lb.tinkoff.scala.exam.stack.ex1;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ClientImpl implements Client{
    @Override
    public Response getApplicationStatus1(String id) {
        System.out.println("getApplicationStatus1 started");
        try {
            Thread.sleep(Duration.of(12, ChronoUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("status 1 Interrupted");
            throw new RuntimeException("Was interrupted");
        }
        System.out.println("getApplicationStatus1 ended");
        return new Response.Success("STATUS_1", id);
    }

    @Override
    public Response getApplicationStatus2(String id) {
        try {
            Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("status 1 Interrupted");
            throw new RuntimeException("Was interrupted");
        }
        return new Response.Success("STATUS_2", id);
    }
}

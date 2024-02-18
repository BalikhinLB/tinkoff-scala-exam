package ru.lb.tinkoff.scala.exam.stack.ex2;

import java.time.Duration;

public interface Handler {
    Duration timeout();

    void performOperation();
}


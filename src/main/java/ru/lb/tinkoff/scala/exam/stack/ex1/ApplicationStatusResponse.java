package ru.lb.tinkoff.scala.exam.stack.ex1;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public interface ApplicationStatusResponse {
    record Failure(@Nullable Duration lastRequestTime, int retriesCount) implements ApplicationStatusResponse {
        @Override
        public String toString() {
            return "Failure{" +
                    "lastRequestTime=" + lastRequestTime +
                    ", retriesCount=" + retriesCount +
                    '}';
        }
    }
    record Success(String id, String status) implements ApplicationStatusResponse {
        @Override
        public String toString() {
            return "Success{" +
                    "id='" + id + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}

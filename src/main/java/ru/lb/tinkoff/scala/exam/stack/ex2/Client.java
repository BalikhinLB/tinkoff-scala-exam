package ru.lb.tinkoff.scala.exam.stack.ex2;

public interface Client {
    //блокирующий метод для чтения данных
    Event readData();
    //блокирующий метод отправки данных
    Result sendData(Address dest, Payload payload);
}

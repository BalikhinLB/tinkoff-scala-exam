package ru.lb.tinkoff.scala.exam.stack.ex1;

public class CheckMain {
    public static void main(String[] args) {
        var service = new HandlerImpl(new ClientImpl());
        var result = service.performOperation("12");
        System.out.println(result);
        service.stopPull();
    }
}

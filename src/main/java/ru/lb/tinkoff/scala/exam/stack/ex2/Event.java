package ru.lb.tinkoff.scala.exam.stack.ex2;

import java.util.List;

public record Event(List<Address> recipients, Payload payload) {}


package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

public class FourthBean {
    @Inject
    private FirstBean firstBean;

    @PostConstruct
    void doSomething() {
        System.out.println("Hi, I'm fourth bean");
    }

    void doSomethingWithFirst() {
        throw new RuntimeException("PostConstruct failed.");
    }
}

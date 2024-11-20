package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "thirdBean", scope = BeanScope.PROTOTYPE)
public class ThirdBean {

    public FirstBean firstBean;
    @Inject
    public OtherBean secondBean;

    @PostConstruct
    void doSomething() {
        System.out.println("Hi, I'm third bean");
    }

    void doSomethingWithFirst() {
        throw new RuntimeException("PostConstruct failed.");
    }
}

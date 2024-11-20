package by.bsu.dependency.context;

import by.bsu.dependency.example.*;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HardCodedSingletonApplicationContextTest {

    private ApplicationContext applicationContext;
    private AutoScanApplicationContext autoScanApplicationContext;

    @BeforeEach
    void init() {
        applicationContext = new HardCodedSingletonApplicationContext(FirstBean.class,
                OtherBean.class, ThirdBean.class, FourthBean.class);
        autoScanApplicationContext = new AutoScanApplicationContext(Main.class.getPackageName());
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        assertThat(autoScanApplicationContext.isRunning()).isFalse();
        applicationContext.start();
        autoScanApplicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
        assertThat(autoScanApplicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> autoScanApplicationContext.containsBean("fifthBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();
        autoScanApplicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(autoScanApplicationContext.containsBean("otherBean")).isTrue();
        assertThat(autoScanApplicationContext.containsBean("smth")).isFalse();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
        assertThat(applicationContext.getBean("thirdBean")).isNotNull().isInstanceOf(ThirdBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();
        autoScanApplicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> autoScanApplicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
        assertThat(applicationContext.isSingleton("thirdBean")).isFalse();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
        assertThat(autoScanApplicationContext.isPrototype("thirdBean")).isTrue();

    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testThirdApplicationContextgetBean() {
        applicationContext.start();
        assertThat(applicationContext.getBean(ThirdBean.class).secondBean == null).isFalse();
        assertThat(applicationContext.getBean(ThirdBean.class).firstBean == null).isTrue();
    }
}

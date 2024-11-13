package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractApplicationContext implements ApplicationContext {

    private final Map<String, Class<?>> beans = new HashMap<>();
    private final Map<Class<?>, BeanScope> scopes = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();

    private ContextStatus state = ContextStatus.NOT_STARTED;

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected void addBean(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Bean.class)) {
            String name = clazz.getAnnotation(Bean.class).name();
            BeanScope scope = clazz.getAnnotation(Bean.class).scope();

            if (Objects.equals(name, "")) {
                name = clazz.getName();
                String firstLetter = name.substring(0, 1);
                String restOfName = name.substring(1);
                firstLetter = firstLetter.toLowerCase();
                name = firstLetter + restOfName;
            }
            beans.put(name, clazz);
            scopes.put(clazz, scope);
        }
    }

    /**
     * Стартует контекст. При вызове этого метода происходит создание всех необходимых объектов, объявленных
     * {@code SINGLETON} бинами.
     * <br/>
     * При невозможности запустить контекст (например, отсутствие требуемых зависимостей) должен бросать исключение.
     */
    @Override
    public void start() {
        if (state == ContextStatus.STARTED) {
            return;
        }
        state = ContextStatus.STARTED;
        scopes.forEach(
                (beanClass, beanScope) -> {
                    singletons.put(beanClass, instantiateBean(beanClass));
                });

        singletons.forEach(
                (beanClass, obj) -> {
                    Arrays.stream(beanClass.getDeclaredFields())
                            .forEach(field -> {
                                if (field.isAnnotationPresent(Inject.class)) {
                                    try {
                                        field.setAccessible(true);
                                        field.set(obj, getBean(field.getType()));
                                    } catch (IllegalAccessException e) {
                                        throw new RuntimeException("You have no rights...");
                                    }
                                }
                            });
                }
        );
    }

    @Override
    public boolean isRunning() {
        return (state == ContextStatus.STARTED);
    }

    @Override
    public boolean containsBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return beans.containsKey(name);
    }

    /**
     * Возвращает инстанс бина по имени (идентификатору). Для {@code SINGLETON} бинов каждый вызов должен возвращать
     * один и тот же объект, а для {@code PROTOTYPE} - каждый раз новый объект.
     *
     * @param name имя бина
     * @throws NoSuchBeanDefinitionException если бин с таким именем не был объявлен.
     * @throws ApplicationContextNotStartedException если контекст еще не запущен
     * @return соответствующий инстанс бина
     */
    @Override
    public Object getBean(String name) {
        return getBean(beans.get(name));
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        String nm = clazz.getAnnotation(Bean.class).name();
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        if (!containsBean(nm)) {
            throw new NoSuchBeanDefinitionException();
        }

        if (scopes.get(clazz) == BeanScope.SINGLETON) {
            return (T) singletons.get(clazz);
        } else {
            return instantiateBean(clazz);
        }
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return (scopes.get(beans.get(name)) == BeanScope.SINGLETON);
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return (scopes.get(beans.get(name)) == BeanScope.PROTOTYPE);
    }

    private <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}

package by.MatsveiZorka.quizer.generators.math;

import by.MatsveiZorka.quizer.TaskGenerator;
import by.MatsveiZorka.quizer.tasks.math.MathTask;

public interface MathTaskGenerator<T extends MathTask> extends TaskGenerator<T> {
    int getMinNumber(); // получить минимальное число
    int getMaxNumber(); // получить максимальное число

    default int getDiffNumber() {
        return getMaxNumber() - getMinNumber();
    }
}

package pl.agh.kis.seirsimulation.model.data;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ChangesValidator {
    public static int[] randomlyValidateDailyChanges(int[]changes){
        var sum= IntStream.of(changes).sum();
        while (sum>0){
            changes[ThreadLocalRandom.current().nextInt(0, changes.length-3)]-=1;
            sum= IntStream.of(changes).sum();
        }
        while (sum<0){
            changes[ThreadLocalRandom.current().nextInt(1, changes.length)]+=1;
            sum= IntStream.of(changes).sum();
        }
        return changes;
    }
}

package pl.agh.kis.seirsimulation.model.data;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Slf4j
public class DataValidator {
    public static int[] randomlyValidateDailyChanges(int[] changes) {
        if (changes[0] != 0 && changes[1] == 0 && changes[2] == 0 && changes[3] != 0) {
            changes[2] = -(changes[3] + changes[4]);
            changes[0] = 0;
        }
        var sum = 0;
        for (int i:changes) sum+=i;
        while (sum > 0) {
            changes[ThreadLocalRandom.current().nextInt(0, changes.length - 2)] -= 1;
            sum = IntStream.of(changes).sum();
        }
        while (sum < 0) {
            changes[ThreadLocalRandom.current().nextInt(1, changes.length)] += 1;
            sum = IntStream.of(changes).sum();
        }
        return changes;
    }

    public static List<Integer> validateAppliedChanges(int[] changes, List<Integer> scm, List<Integer> immigrantChangesSum) {
        List<Integer> newScm = new ArrayList<>(Collections.nCopies(5, 0));
        for (int i = 0; i < scm.size(); i++) {
            if (scm.get(i) + (changes[i] - immigrantChangesSum.get(i)) < 0) {
                changes[0] = changes[0] + (changes[i] - immigrantChangesSum.get(i));
            }
        }
        for (int i = 0; i < scm.size(); i++) {
            newScm.set(i, Math.max(0, scm.get(i) + (changes[i] - immigrantChangesSum.get(i))));
        }
        return newScm;
    }

}

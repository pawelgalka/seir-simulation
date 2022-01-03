package pl.agh.kis.seirsimulation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class Cell {
    public static final IntPredicate isSick = index -> 2 >= index && index >= 1;
    private List<Integer> stateCountMap;
    private Integer peopleLimit;
    private Map<Pair<Integer, Integer>, List<Integer>> immigrants;

    public Cell(Integer initCount, double vacPerc) {
        stateCountMap = IntStream.range(0, 5).mapToObj(x -> 0).collect(Collectors.toList());
        int vaccinated = (int) (vacPerc * initCount / 100);
        int notVaccinated = initCount - vaccinated;
        stateCountMap.set(0, notVaccinated);
        stateCountMap.set(3, vaccinated);
        peopleLimit = 2 * initCount;
        immigrants = new HashMap<>();
    }

    public int[] getImmigrantsSummed() {
        int[] sum = { 0, 0, 0, 0, 0 };
        immigrants.forEach((index, list) -> {
            for (int i = 0; i < sum.length - 1; i++) {
                sum[i] += list.get(i);
            }
        });
        return sum;
    }

    public List<Integer> getMigratedStateCountMap() {
        int[] immigrated = getImmigrantsSummed();
        List<Integer> MigratedSCM = new ArrayList<>();
        for (int i = 0; i < getStateCountMap().size(); i++) {
            MigratedSCM.add(getStateCountMap().get(i) + immigrated[i]);
        }

        return MigratedSCM;
    }

}

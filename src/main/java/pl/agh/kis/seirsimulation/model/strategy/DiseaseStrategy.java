package pl.agh.kis.seirsimulation.model.strategy;

import java.util.List;

import pl.agh.kis.seirsimulation.model.Cell;

public interface DiseaseStrategy {
    int calculateSusceptibleChange(List<Integer> stateCount, double stateCountSum, boolean lessThanHundredInfected);

    int calculateExposedChange(List<Integer> stateCount, double stateCountSum, boolean lessThanHundredInfected);

    int calculateInfectedChange(List<Integer> stateCount, boolean lessThanHundredInfected);

    int calculateRecoveredChange(List<Integer> stateCount, boolean lessThanHundredInfected);

    int calculateDiseaseDeaths(List<Integer> stateCount, boolean lessThanHundredInfected);

    int[] getDailyChanges(Cell cell);
}

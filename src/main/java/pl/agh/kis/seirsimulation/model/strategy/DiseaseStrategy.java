package pl.agh.kis.seirsimulation.model.strategy;

import pl.agh.kis.seirsimulation.model.Cell;

import java.util.List;

public interface DiseaseStrategy {
    int calculateSusceptibleChange(List<Integer> stateCount,double stateCountSum,boolean lessThanHundredInfected);
    int calculateExposedChange(List<Integer> stateCount,double stateCountSum,boolean lessThanHundredInfected);
    int calculateInfectedChange(List<Integer> stateCount,boolean lessThanHundredInfected);
    int calculateRecoveredChange(List<Integer> stateCount,boolean lessThanHundredInfected);
    int calculateDiseaseDeaths(List<Integer> stateCount,boolean lessThanHundredInfected);
    int[] getDailyChanges(Cell cell);
}

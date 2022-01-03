package pl.agh.kis.seirsimulation.model.strategy;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.CONTACT_RATE_NO_VITAL;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.DISEASE_CONFIG;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.floorOrCeil;
import static pl.agh.kis.seirsimulation.model.data.DataValidator.randomlyValidateDailyChanges;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import pl.agh.kis.seirsimulation.model.Cell;

public class SeirNoVitalsStrategy implements DiseaseStrategy {

    @Override
    public int calculateSusceptibleChange(List<Integer> stateCount, double stateCountSum,
            boolean lessThanHundredInfected) {
        if (lessThanHundredInfected) {
            return (int) floorOrCeil(-
                    ((CONTACT_RATE_NO_VITAL() * (double) stateCount.get(2) * (double) stateCount.get(0))
                            / stateCountSum));
        } else {
            return (int) Math.round(-
                    ((CONTACT_RATE_NO_VITAL() * (double) stateCount.get(2) * (double) stateCount.get(0))
                            / stateCountSum));
        }
    }

    @Override
    public int calculateExposedChange(List<Integer> stateCount, double stateCountSum, boolean lessThanHundredInfected) {
        if (lessThanHundredInfected) {
            return (int) floorOrCeil((
                    (CONTACT_RATE_NO_VITAL() * stateCount.get(2) * stateCount.get(0)) / stateCountSum) - (
                    (1. / DISEASE_CONFIG
                            .getIncubation()) * stateCount.get(1)));
        } else {
            return (int) Math.round((
                    (CONTACT_RATE_NO_VITAL() * stateCount.get(2) * stateCount.get(0)) / stateCountSum) - (
                    (1. / DISEASE_CONFIG
                            .getIncubation()) * stateCount.get(1)));
        }
    }

    @Override
    public int calculateInfectedChange(List<Integer> stateCount, boolean lessThanHundredInfected) {
        if (lessThanHundredInfected) {
            return (int) floorOrCeil(
                    ((1. / DISEASE_CONFIG.getIncubation()) * stateCount.get(1)) - (((1. / DISEASE_CONFIG
                            .getInfection())) * stateCount.get(2)));
        } else {
            return (int) Math.round(((1. / DISEASE_CONFIG.getIncubation()) * stateCount.get(1)) - (((1. / DISEASE_CONFIG
                    .getInfection())) * stateCount.get(2)));
        }
    }

    @Override
    public int calculateRecoveredChange(List<Integer> stateCount, boolean lessThanHundredInfected) {
        if (lessThanHundredInfected) {
            return (int) floorOrCeil((1. / DISEASE_CONFIG.getInfection()) * stateCount.get(2));
        } else {
            return (int) Math.round((1. / DISEASE_CONFIG.getInfection()) * stateCount.get(2));
        }
    }

    @Override
    public int calculateDiseaseDeaths(List<Integer> stateCount, boolean lessThanHundredInfected) {
        return (int) Math.round(
                calculateRecoveredChange(stateCount, lessThanHundredInfected) * DISEASE_CONFIG.getMortality());
    }

    @Override
    public int[] getDailyChanges(Cell cell) {
        List<Integer> stateCount = new ArrayList<>(cell.getStateCountMap());
        Map<Pair<Integer, Integer>, List<Integer>> immigrants = cell.getImmigrants();
        for (Pair<Integer, Integer> key : immigrants.keySet()) {
            List<Integer> immigrantsFrom = new ArrayList<>(immigrants.get(key));
            for (int j = 0; j < immigrantsFrom.size(); j++) {
                stateCount.set(j, stateCount.get(j) + immigrantsFrom.get(j));
            }
        }
        boolean lessThanHundredInfected = false;
        int stateCountSum = (int) stateCount.stream().mapToInt(Integer::intValue).sum();
        return randomlyValidateDailyChanges(
                new int[] { calculateSusceptibleChange(stateCount, stateCountSum, lessThanHundredInfected),
                        calculateExposedChange(stateCount, stateCountSum, lessThanHundredInfected),
                        calculateInfectedChange(stateCount, lessThanHundredInfected),
                        calculateRecoveredChange(stateCount, lessThanHundredInfected) - calculateDiseaseDeaths(
                                stateCount, lessThanHundredInfected),
                        calculateDiseaseDeaths(stateCount, lessThanHundredInfected) });
    }
}

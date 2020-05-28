package pl.agh.kis.seirsimulation.model;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.data.DataValidator;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

import java.util.*;

import static java.lang.Math.floor;
import static java.lang.Math.round;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.ceilOrFloor;
import static pl.agh.kis.seirsimulation.model.data.DataValidator.validateAppliedChanges;

@Slf4j
@Component
public class DiseaseProcess {

    @Autowired
    DiseaseStrategy diseaseStrategy;

    public void simulateDayAtSingleCell(Cell cell) {
        int[] changes = diseaseStrategy
                .getDailyChanges(cell);
        Map<Pair<Integer, Integer>, List<Integer>> immigrants = cell.getImmigrants();
        List<Integer> immigrantChangesSum = new ArrayList<>(Collections.nCopies(4, 0));
        var cellPeopleSum = cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        for (var key : immigrants.keySet()) {
            List<Integer> immigrantsFrom = new ArrayList<>(immigrants.get(key));
            var immigrantsSum=immigrantsFrom.stream().mapToInt(Integer::intValue).sum();
            int[] immigrantChanges={0,0,0,0,0};
            for (int j = 0; j < immigrantChanges.length-1; j++) {
                immigrantChanges[j]=(int) ceilOrFloor(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum));
                /*immigrantsFrom.set(j, immigrantsFrom.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum)));
                immigrantChangesSum.set(j, immigrantChangesSum.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum)));*/
            }
            immigrantChanges=DataValidator.randomlyValidateDailyChanges(immigrantChanges);
            for(int k=0;k<immigrantChanges.length-1;k++){
                immigrantsFrom.set(k,immigrantsFrom.get(k)+immigrantChanges[k]);
                immigrantChangesSum.set(k,immigrantChangesSum.get(k)+immigrantChanges[k]);
            }

            immigrants.put(key, immigrantsFrom);
        }
        cell.setImmigrants(immigrants);
        cell.setD(cell.getD() + changes[changes.length-1]);
        cell.setStateCountMap(validateAppliedChanges(changes,cell.getStateCountMap(),immigrantChangesSum));
    }

    public void simulateDayAtSingleCellLog(Cell cell) {
        int[] changes = diseaseStrategy
                .getDailyChanges(cell);
        log.debug("cellSCM "+cell.getStateCountMap().toString());
        log.debug("changesBasic "+ Arrays.toString(changes));
        Map<Pair<Integer, Integer>, List<Integer>> immigrants = cell.getImmigrants();
        List<Integer> immigrantChangesSum = new ArrayList<>(Collections.nCopies(4, 0));
        var cellPeopleSum = cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        for (var key : immigrants.keySet()) {
            List<Integer> immigrantsFrom = new ArrayList<>(immigrants.get(key));
            var immigrantsSum=immigrantsFrom.stream().mapToInt(Integer::intValue).sum();
            int[] immigrantChanges={0,0,0,0,0};
            for (int j = 0; j < immigrantChanges.length-1; j++) {
                immigrantChanges[j]=(int) ceilOrFloor(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum));
                /*immigrantsFrom.set(j, immigrantsFrom.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum)));
                immigrantChangesSum.set(j, immigrantChangesSum.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum)));*/
            }
            immigrantChanges=DataValidator.randomlyValidateDailyChanges(immigrantChanges);
            for(int k=0;k<immigrantChanges.length-1;k++){
                immigrantsFrom.set(k,immigrantsFrom.get(k)+immigrantChanges[k]);
                immigrantChangesSum.set(k,immigrantChangesSum.get(k)+immigrantChanges[k]);
            }
            immigrants.put(key, immigrantsFrom);
        }
        log.debug("imchangesSum "+Arrays.toString(immigrantChangesSum.toArray()));
        cell.setImmigrants(immigrants);
        cell.setD(cell.getD() + changes[changes.length-1]);
        cell.setStateCountMap(validateAppliedChanges(changes,cell.getStateCountMap(),immigrantChangesSum));
        log.debug("changed home "+cell.getStateCountMap().toString());
    }

}

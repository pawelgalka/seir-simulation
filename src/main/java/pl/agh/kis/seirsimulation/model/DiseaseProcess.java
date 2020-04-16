package pl.agh.kis.seirsimulation.model;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.data.MapData;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.round;
import static pl.agh.kis.seirsimulation.model.Cell.isSick;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MOVING_PPL_PERC;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MOVING_PPL_SICK;
import static pl.agh.kis.seirsimulation.model.data.MapData.getCellAtIndex;
import static pl.agh.kis.seirsimulation.model.data.MapData.getNeighboursOfCell;

@Component
public class DiseaseProcess {

    @Autowired DiseaseStrategy diseaseStrategy;

    public void test() {
        Cell cell = new Cell(28000);
        cell.getStateCountMap().set(1, 10);

        Cell cell1 = new Cell(25000);
        cell1.getStateCountMap().set(1, 10);

        Cell cell2 = new Cell(26000);
        cell2.getStateCountMap().set(1, 10);

        Cell cell3 = new Cell(27000);
        cell3.getStateCountMap().set(1, 10);

        Cell cell4 = new Cell(22000);
        cell4.getStateCountMap().set(1, 10000);

        Cell cell5 = new Cell(29000);
        cell5.getStateCountMap().set(1, 10);

        Cell cell6 = new Cell(38000);
        cell6.getStateCountMap().set(1, 10);

        Cell cell7 = new Cell(18000);
        cell7.getStateCountMap().set(1, 10);
        cell7.getStateCountMap().set(2, 70);

        Cell cell8 = new Cell(15000);

        Cell cell9 = new Cell(18000);
        cell9.getStateCountMap().set(1, 10);

        Cell cell10 = new Cell(18000);
        cell10.getStateCountMap().set(1, 10);

        Cell cell11 = new Cell(18000);
        cell11.getStateCountMap().set(1, 10);

        MapData.setGridMap(Arrays.asList(new ArrayList<>(Arrays.asList(cell, cell3, cell6, cell9)),
                new ArrayList<>(Arrays.asList(cell1, cell4, cell7, cell10)),
                new ArrayList<>(Arrays.asList(cell2, cell5, cell8, cell11))));

        System.out.println("cell4 " + cell4.getStateCountMap());
        System.out.println("cell7" + cell7.getStateCountMap());
        makeMove();

        System.out.println("cell4 " + cell4);
        System.out.println("cell7" + cell7);
        simulateDayAtSingleCell(cell7);
        System.out.println("cell4 " + cell4);
        System.out.println("cell7" + cell7);
        makeMoveBack(new Pair<>(2, 1));
        System.out.println(cell7.getImmigrants());
        System.out.println("cell4 " + cell4);
        System.out.println("cell7" + cell7);
    }

    public void simulateDayAtSingleCell(Cell cell) {
        System.out.println("scm" + cell.getMigratedStateCountMap());
        int[] changes = diseaseStrategy
                .getDailyChanges(cell);
        System.out.println("changes" + Arrays.toString(changes));
        Map<Pair<Integer, Integer>, List<Integer>> immigrants = cell.getImmigrants();
        List<Integer> immigrantChangesSum = new ArrayList<>(Collections.nCopies(4, 0));
        int cellPeopleSum = cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        for (var key : immigrants.keySet()) {
            List<Integer> immigrantsFrom = new ArrayList<>(immigrants.get(key));
            for (int j = 0; j < immigrantsFrom.size(); j++) {
                immigrantsFrom.set(j, immigrantsFrom.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / cellPeopleSum));
                immigrantChangesSum.set(j, immigrantChangesSum.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / cellPeopleSum));
                System.out.println((int) round((double) (immigrantsFrom.get(j) * changes[j]) / cellPeopleSum));
            }
            immigrants.put(key, immigrantsFrom);
        }
        System.out.println("immigrants" + immigrants);
        cell.setImmigrants(immigrants);
        System.out.println(immigrantChangesSum);
        //TODO manage deaths
        cell.setD(cell.getD() + diseaseStrategy.calculateDiseaseDeaths(cell.getStateCountMap()));
        for (int j = 0; j < cell.getStateCountMap().size(); j++) {
            cell.getStateCountMap().set(j, cell.getStateCountMap().get(j) + changes[j] - immigrantChangesSum.get(j));
        }
        //        cell.setStateCountMap(stateCountMap);
        System.out.println(cell);
    }

    private void makeMove() {
        for (int y = 1; y <= 1; y++) { //TODO CHANGE FOR REAL LOOP
            for (int x = 1; x <= 1; x++) {
                var neighbours = getNeighboursOfCell(new Pair<>(x, y));
                long notEmptyNeighbours = neighbours.stream()
                        .filter(e -> getCellAtIndex(e).getStateCountMap().stream().mapToInt(Integer::intValue).sum()
                                > 0).count();
                List<Integer> moving = getMovingPeople(getCellAtIndex(new Pair<>(x, y)), ((int) notEmptyNeighbours));
                for (Pair<Integer, Integer> immigrantKey : neighbours) {
                    List<Integer> immigrants = new ArrayList<>();
                    for (int j = 0; j < getCellAtIndex(new Pair<>(x, y)).getStateCountMap().size(); j++) {
                        getCellAtIndex(new Pair<>(x, y)).getStateCountMap().set(j,
                                getCellAtIndex(new Pair<>(x, y)).getStateCountMap().get(j)
                                        - moving.get(j));
                        immigrants.add(j, moving.get(j));
                    }
                    getCellAtIndex(immigrantKey).getImmigrants()
                            .put(new Pair<>(x, y), immigrants);
                }
            }
        }
    }

    private List<Integer> getMovingPeople(Cell c, int notEmptyNeighbours) {
        return IntStream.range(0, c.getStateCountMap().size()).map(index -> (int) round(
                c.getStateCountMap().get(index) * (isSick.test(index) ? MOVING_PPL_SICK : MOVING_PPL_PERC)
                        / notEmptyNeighbours)).boxed().collect(Collectors.toList());
    }

    public void makeMoveBack(Pair<Integer, Integer> source) {
        Cell sourceCell = getCellAtIndex(source);
        List<Integer> sourceSCM = sourceCell.getStateCountMap();
        List<Integer> immigrantsFrom;
        var neighbours = getNeighboursOfCell(source);
        for (var immigrantKey : sourceCell.getImmigrants().keySet()) {
            immigrantsFrom = sourceCell.getImmigrants().getOrDefault(immigrantKey, Collections.emptyList());
            List<Integer> neighbourSCM = getCellAtIndex(immigrantKey).getStateCountMap();
            for (int j = 0; j < sourceCell.getImmigrants().get(immigrantKey).size(); j++) {
                neighbourSCM.set(j, neighbourSCM.get(j) + immigrantsFrom.get(j));
            }
        }
        sourceCell.getImmigrants().clear();

    }
}

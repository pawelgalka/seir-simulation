package pl.agh.kis.seirsimulation.model.data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import pl.agh.kis.seirsimulation.model.Cell;
import pl.agh.kis.seirsimulation.model.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
@Builder
@Getter
@Setter
public class MapData {
    static List<List<Cell>> gridMap;
    static public List<Pair<Integer, Integer>> cellsForRandomIllness = new ArrayList<>();

    public static Integer getMaxValue() {
        return maxValue;
    }

    public static void setMaxValue(Integer maxValue) {
        MapData.maxValue = maxValue;
    }

    static Integer maxValue;

    public static List<List<Cell>> getGridMap() {
        return gridMap;
    }

    public static void setGridMap(List<List<Cell>> collect) {
        gridMap = collect;
    }

    public static void addRandomIndex(Pair<Integer, Integer> coordinates) {
        cellsForRandomIllness.add(coordinates);
    }

    public static Pair<Integer, Integer> getRandomIndex(int index) {
        return cellsForRandomIllness.get(index);
    }

    public static Cell getCellAtIndex(Pair<Integer, Integer> index) {
        // index = (x,y)
        return gridMap.get(index.getValue1()).get(index.getValue0());
    }

    public static int getRandomSize() {
        return cellsForRandomIllness.size();
    }

    public static List<Pair<Integer, Integer>> getNeighboursOfCell(Pair<Integer, Integer> indexOfCell /*x,y*/) {
        boolean possibleUp = false, possibleDown = false, possibleLeft = false, possibleRight = false;
        List<Pair<Integer, Integer>> neighbours = new ArrayList<>();
        if (indexOfCell.getValue1() /*y*/ >= 1) {
            possibleUp = true;
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() - 1))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() - 1));
            }
        }
        if (indexOfCell.getValue1() < gridMap.size() - 1) {
            possibleDown = true;
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() + 1))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() + 1));
            }
        }
        if (indexOfCell.getValue0() /*x*/ >= 1) {
            possibleLeft = true;
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1()))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1()));
            }
        }
        if (indexOfCell.getValue0() < gridMap.get(0).size() - 1) {
            possibleRight = true;
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1()))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1()));
            }
        }

        if (possibleUp && possibleLeft) {
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() - 1))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() - 1));
            }
        }
        if (possibleUp && possibleRight) {
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() - 1))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() - 1));
            }
        }
        if (possibleDown && possibleLeft) {
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() + 1))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() + 1));
            }
        }
        if (possibleDown && possibleRight) {
            if (MapData.getCellAtIndex(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() + 1))
                    .getStateCountMap().stream().mapToInt(Integer::intValue).sum() > 0) {
                neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() + 1));
            }
        }
        return neighbours;
    }

    public static Pair<Integer, Integer> getRandomCellCoords(List<Pair<Integer, Integer>> neighbours, Pair<Integer, Integer> sourceCell) {
        neighbours.add(sourceCell);
        Pair<Integer, Integer> randomCoords = new Pair<>(sourceCell.getValue0(), sourceCell.getValue1());
        while (neighbours.contains(randomCoords)) {
            randomCoords = getRandomIndex(ThreadLocalRandom.current().nextInt(0, getRandomSize()));
        }
        return randomCoords;
    }

    public static int getMaxStateLevel(State state) {
        return Collections.max(gridMap.stream()
                .map(cellsRow -> Collections
                        .max(cellsRow.stream().map(cell -> cell.getStateCountMap().get(state.getState()))
                                .collect(Collectors.toList()))).collect(Collectors.toList()));

    }

    public static void addIllnessToCell(Pair<Integer, Integer> index) {
        Cell cell = getCellAtIndex(index);
        cell.getStateCountMap().set(State.I.getState(), cell.getStateCountMap().get(State.I.getState()) + 50);
        cell.getStateCountMap().set(State.S.getState(), cell.getStateCountMap().get(State.S.getState()) - 50);
    }

    public static void addExposedToCell(Pair<Integer, Integer> index) {
        Cell cell = getCellAtIndex(index);
        cell.getStateCountMap().set(State.E.getState(), cell.getStateCountMap().get(State.I.getState()) + 20);
        cell.getStateCountMap().set(State.S.getState(), cell.getStateCountMap().get(State.S.getState()) - 20);
    }

    public static int getNumberOfStateSummary(State state) {
        return gridMap.stream()
                .map(cellRow -> cellRow.stream().map(cell -> cell.getStateCountMap().get(state.getState()))
                        .collect(Collectors.toList()).stream().reduce(0, Integer::sum)).reduce(0, Integer::sum);
    }

    public static int getDeathNum() {
        return gridMap.stream()
                .map(cellRow -> cellRow.stream().map(Cell::getD).collect(Collectors.toList()).stream()
                        .mapToInt(Integer::intValue).sum()).mapToInt(Integer::intValue).sum();
    }

    public static void addIllnessToCellTest(Pair<Integer, Integer> index) {
        Cell cell = getCellAtIndex(index);
        cell.getStateCountMap().set(State.I.getState(),
                (int) ((cell.getStateCountMap().get(State.I.getState()) + 10000) * 5));
        cell.getStateCountMap().set(State.S.getState(),
                cell.getStateCountMap().get(State.S.getState()) - cell.getStateCountMap().get(State.I.getState()));
    }
}

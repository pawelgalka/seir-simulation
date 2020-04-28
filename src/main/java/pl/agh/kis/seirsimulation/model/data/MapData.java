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
import java.util.stream.Collectors;

@Data
@Builder
@Getter
@Setter
public class MapData {
    static List<List<Cell>> gridMap;
    static List<Pair<Integer, Integer>> cellsForRandomIllness = new ArrayList<>();

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
        return gridMap.get(index.getValue1()).get(index.getValue0());
    }

    public static int getRandomSize() {
        return cellsForRandomIllness.size();
    }

    public static List<Pair<Integer,Integer>> getNeighboursOfCell(Pair<Integer, Integer> indexOfCell /*x,y*/) {
        boolean possibleUp = false, possibleDown = false, possibleLeft = false, possibleRight = false;
        List<Pair<Integer,Integer>> neighbours = new ArrayList<>();
        if (indexOfCell.getValue1() /*y*/ >= 1) {
            possibleUp = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() - 1));
        }
        if (indexOfCell.getValue1() < gridMap.size() - 1) {
            possibleDown = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() + 1));
        }
        if (indexOfCell.getValue0() /*x*/ >= 1) {
            possibleLeft = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1()));
        }
        if (indexOfCell.getValue0() < gridMap.get(0).size() - 1) {
            possibleRight = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1()));
        }
        if (possibleUp && possibleLeft) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() - 1));
        }
        if (possibleUp && possibleRight) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() - 1));
        }
        if (possibleDown && possibleLeft) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() + 1));
        }
        if (possibleDown && possibleRight) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() + 1));
        }
        return neighbours;
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

    public static int getNumberOfStateSummary(State state) {
        return gridMap.stream()
                .map(cellRow -> cellRow.stream().map(cell -> cell.getStateCountMap().get(state.getState()))
                        .collect(Collectors.toList()).stream().reduce(0, Integer::sum)).reduce(0, Integer::sum);
    }

    public static void addIllnessToCellTest(Pair<Integer, Integer> index) {
        Cell cell = getCellAtIndex(index);
        cell.getStateCountMap().set(State.I.getState(),
                (int) ((cell.getStateCountMap().get(State.I.getState()) + 10000)*5));
        cell.getStateCountMap().set(State.S.getState(), cell.getStateCountMap().get(State.S.getState()) - cell.getStateCountMap().get(State.I.getState()));
    }
}

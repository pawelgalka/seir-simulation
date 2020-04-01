package pl.agh.kis.seirsimulation.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.kis.seirsimulation.model.Cell;

import java.util.List;

@Data
@Builder
public class MapData {
    static List<List<Cell>> gridMap;

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
}

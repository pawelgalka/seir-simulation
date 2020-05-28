package pl.agh.kis.seirsimulation.model.data;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.Cell;
import pl.agh.kis.seirsimulation.model.State;
import pl.agh.kis.seirsimulation.model.configuration.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.ceil;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.setMaxHospital;

@Slf4j
public class DataLoader {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    GuiContext guiContext;

    public void mapData(String country) throws IOException {
        Path file = Paths.get(loadMap(country).getURI());
        setMaxHospital(country);

        Stream<String> lines = Files.lines(file);
        List<List<Integer>> linesParsed = lines
                .filter(line -> Character.isDigit(line.charAt(0))) //remove non-data lines
                .map(dataLine -> Arrays.stream(dataLine.split(" ")) // split on " " to get single cell number
                        .map(cellData -> (int) ceil(Double.parseDouble(cellData)))
                        .collect(Collectors.toList()))
                .filter(x -> x.stream().anyMatch(cell -> cell > 0)).collect(
                        Collectors.toList());

        List<Integer> emptyColumns = new ArrayList<>();
        for (var j = 0; j < linesParsed.get(0).size(); j++) {
            if (isColumnZero(linesParsed, j)) {
                emptyColumns.add(j);
            }
        }
        emptyColumns.sort(Comparator.reverseOrder());
        linesParsed.forEach(line -> emptyColumns.forEach(x -> line.remove((int) x)));

        var y = (int) ceil((double) linesParsed.size() / guiContext.getNRows());
        var x = (int) ceil((double) linesParsed.get(0).size() / guiContext.getNCols());

        int max = 0;

        List<List<Cell>> cells = new ArrayList<>();
        for (var i = 0; i < guiContext.getNRows(); i++) {
            cells.add(new ArrayList<>());
            for (var j = 0; j < guiContext.getNCols(); j++) {
                int sum = 0;
                for (var k = y * i; k < (i + 1) * y && k < linesParsed.size(); k++) {
                    for (var l = x * j; l < (j + 1) * x && l < linesParsed.get(0).size(); l++) {
                        sum += linesParsed.get(k).get(l);

                        max = Math.max(sum, max);
                    }
                }
                if(sum > Configuration.MIN_RANDOM_CELL){
                    MapData.addRandomIndex(new Pair<>(i,j));
                }
                cells.get(i).add(new Cell(sum));
            }
        }
        MapData.setGridMap(cells);
        MapData.setMaxValue(max);
        log.debug(String.valueOf(MapData.getMaxStateLevel(State.S)));
    }

    private Resource loadMap(String countryName) {
        return resourceLoader.getResource(
                "classpath:COUNTRY.asc".replace("COUNTRY", countryName));
    }

    private boolean isColumnZero(List<List<Integer>> listOfLists, int index) {
        return listOfLists.stream().map(list -> list.get(index)).allMatch(x -> x == 0);
    }
}

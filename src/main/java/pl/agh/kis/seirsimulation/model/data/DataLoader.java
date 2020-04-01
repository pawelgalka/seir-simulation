package pl.agh.kis.seirsimulation.model.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import pl.agh.kis.seirsimulation.model.Cell;

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

@Slf4j
public class DataLoader {

    @Autowired
    ResourceLoader resourceLoader;

    private Resource loadMap(String countryName) {
        return resourceLoader.getResource(
                "classpath:COUNTRY.asc".replace("COUNTRY", countryName));
    }

    private boolean isColumnZero(List<List<Integer>> listOfLists, int index) {
        return listOfLists.stream().map(list -> list.get(index)).allMatch(x -> x == 0);
    }

    public void mapData() throws IOException {
        Path file = Paths.get(loadMap("polp00ag").getURI());
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
        log.error(emptyColumns.toString());
        linesParsed.forEach(line -> emptyColumns.forEach(x -> line.remove((int) x)));

        var y = (int) ceil((double) linesParsed.size() / 50);
        var x = (int) ceil((double) linesParsed.get(0).size() / 50);
        log.error(String.valueOf(linesParsed.get(0).size()));

        int max = 0;

        List<List<Cell>> cells = new ArrayList<>();
        for (var i = 0; i < 50; i++) {
            cells.add(new ArrayList<>());
            for (var j = 0; j < 50; j++) {
                int sum = 0;
                for (var k = y * i; k < (i + 1) * y && k < linesParsed.size(); k++) {
                    for (var l = x * j; l < (j + 1) * x && l < linesParsed.get(0).size(); l++) {
                        //                        log.error(l + " " + k);
                        sum += linesParsed.get(k).get(l);
                        max = Math.max(sum, max);
                    }
                }
                cells.get(i).add(new Cell(sum));
            }
        }
        MapData.setGridMap(cells);
        MapData.setMaxValue(max);
    }
}

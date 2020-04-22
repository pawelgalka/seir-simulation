package pl.agh.kis.seirsimulation.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.Simulation;
import pl.agh.kis.seirsimulation.model.State;
import pl.agh.kis.seirsimulation.model.configuration.Configuration;
import pl.agh.kis.seirsimulation.model.data.DataLoader;
import pl.agh.kis.seirsimulation.model.data.MapData;
import pl.agh.kis.seirsimulation.view.GuiUpdater;
import pl.agh.kis.seirsimulation.view.GuiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
public class GuiController implements Initializable {

    @Autowired
    Simulation simulation;

    @Autowired
    GuiContext guiContext;

    @Autowired
    DataLoader dataLoader;

    @Autowired
    GuiUpdater guiUpdater;

    @FXML
    StackPane mapPane;

    @FXML
    GridPane grid;

    @FXML
    ImageView map;

    @FXML
    Button run;

    @FXML
    Button pause;

    @FXML
    Button step;

    @FXML
    Button reload;

    @FXML
    Button distribute;

    @FXML
    Button load;

    @FXML
    ChoiceBox<String> choice;

    @FXML
    TableView<TableData> numbers;

    @FXML
    Button show;

    @FXML
    LineChart<String, Number> history;

    @FXML
    CategoryAxis xAxis;

    @FXML
    NumberAxis yAxis;

    private final ObservableList<TableData> data =
            FXCollections.observableArrayList(
                    new TableData("Day", "0"),
                    new TableData("Total population", ""),
                    new TableData("Exposed", "0"),
                    new TableData("Infectious", "0"),
                    new TableData("Recovered", "0")
            );

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        guiContext.setNRows(36);
        guiContext.setNCols(36);
        step.setDisable(true);
        run.setOnMouseClicked(mouseEvent -> {
            simulation.run();
            step.setDisable(true);
            guiContext.setSimRunning(true);
            guiUpdater.run();
        });
        pause.setOnMouseClicked(mouseEvent -> {
            simulation.pause();
            step.setDisable(false);
            guiContext.setSimRunning(false);
        });
        step.setOnMouseClicked(mouseEvent -> {
            simulation.step();
            guiUpdater.updateTest();
        });
        distribute.setOnMouseClicked(mouseEvent -> {
            distributeRandomIll();
        });

        addCountryChoiceListener();
        loadGraphicalMap();
        guiContext.setTableView(numbers);
        guiContext.setData(data);
        guiContext.setGridPane(grid);
        guiContext.setHistory(history);
        guiContext.setXAxis(xAxis);
        guiContext.setYAxis(yAxis);
    }

    private void distributeRandomIll() {
        int split = Math.floorDiv(500, Configuration.MIN_RANDOM_CELL);
        List<Integer> splitPoints =
                IntStream.rangeClosed(1, 500)
                        .boxed().collect(Collectors.toList());
        Collections.shuffle(splitPoints);
        splitPoints.subList(split, splitPoints.size()).clear();
        Collections.sort(splitPoints);

        List<Pair<Integer, Integer>> indexes = IntStream.rangeClosed(1, split)
                .map(in -> new Random().nextInt(MapData.getRandomSize())).mapToObj(MapData::getRandomIndex)
                .collect(Collectors.toList());
        IntStream.range(0, Math.min(splitPoints.size(), indexes.size()))
                .mapToObj(i -> new Pair<>(splitPoints.get(i), indexes.get(i))).forEach(pair -> {
            int S = MapData.getCellAtIndex(pair.getValue1()).getStateCountMap().get(0);
            S -= pair.getValue0();
            MapData.getCellAtIndex(pair.getValue1()).getStateCountMap().set(0, S);
            MapData.getCellAtIndex(pair.getValue1()).getStateCountMap().set(1, pair.getValue0());
            guiUpdater.reloadLabelAtIndex(pair.getValue1(), State.S, MapData.getCellAtIndex(pair.getValue1()), grid);
        });
    }



    private void loadGraphicalMap() {
        load.setOnMouseClicked(mouseEvent -> ofNullable(guiContext.getCountry()).ifPresentOrElse(country -> {
            log.debug("Loading map of {}", country);
            try {
                dataLoader.mapData(country.toLowerCase());
            } catch (IOException e) {
                log.error("Error while parsing data", e);
            }
            Image image = new Image(country.toLowerCase() + ".png");
            map.setImage(GuiUtils.scale(image, 600, 600, true));
            guiContext.setMapHeight(map.getImage().getHeight());
            guiContext.setMapWidth(map.getImage().getWidth());
            loadGrid();
            guiUpdater.prepareChart();
            numbers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
            numbers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("value"));
            numbers.setItems(data);
            numbers.setFixedCellSize((numbers.getHeight() - 40) / data.size());
            numbers.prefHeightProperty()
                    .bind(Bindings.size(numbers.getItems()).multiply(numbers.getFixedCellSize()).add(30));

        }, () -> log.error("Could not load map")));
    }

    private void loadGrid() {
        guiContext.setDayOfSim(0);
        grid.setGridLinesVisible(true);
        grid.setMaxSize(guiContext.getMapWidth(), guiContext.getMapHeight());
        for (int i = 0; i < guiContext.getNCols(); i++) {
            //            grid.add
            for (int j = 0; j < guiContext.getNRows(); j++) {
                CellLabel label = new CellLabel(" ", j, i);
                label.setMinSize(Math.floor(guiContext.getMapWidth() / guiContext.getNCols()),
                        Math.floor(guiContext.getMapHeight() / guiContext.getNRows()));
                GridPane.setColumnIndex(label, i);
                GridPane.setRowIndex(label, j);
                grid.getChildren().add(label);
                guiUpdater.updateDataTable();
                label.setOnMouseClicked(mouseEvent -> {
                    MapData.addIllnessToCell(new Pair<>(label.getRow(), label.getCol()));
                    guiUpdater.updateLabels(State.I.getState(),grid);
                    guiUpdater.updateDataTable();
                });
            }
        }
        log.debug(String.valueOf(grid.getColumnCount()));
    }


    private void addCountryChoiceListener() {
        choice.setItems(FXCollections.observableArrayList("Polska"));
        choice.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldChoice, newChoice) -> guiContext.setCountry(newChoice));
    }

}

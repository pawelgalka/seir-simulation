package pl.agh.kis.seirsimulation.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.Simulation;
import pl.agh.kis.seirsimulation.model.State;
import pl.agh.kis.seirsimulation.model.configuration.Configuration;
import pl.agh.kis.seirsimulation.model.configuration.disease.DiseaseConfig;
import pl.agh.kis.seirsimulation.model.data.DataLoader;
import pl.agh.kis.seirsimulation.model.data.MapData;
import pl.agh.kis.seirsimulation.output.writer.CSVWriter;
import pl.agh.kis.seirsimulation.view.GuiUpdater;
import pl.agh.kis.seirsimulation.view.GuiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MIN_RANDOM_CELL;

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

    @Autowired
    CSVWriter csvWriter;

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
    ComboBox<String> choice;

    @FXML
    ComboBox<Double> vaxChoice;

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

    @FXML
    ComboBox<String> diseaseChooser;

    @FXML
    TableView<TableData> diseaseInfo;

    @FXML
    Button export;

    @FXML
    ComboBox socialDistancingLevelChoice;

    @FXML
    CheckBox raportPdf;

    private final ObservableList<TableData> simulationData =
            FXCollections.observableArrayList(
                    new TableData("Day", "0"),
                    new TableData("Total population", ""),
                    new TableData("Exposed", "0"),
                    new TableData("Infectious", "0"),
                    new TableData("Recovered", "0"),
                    new TableData("Deaths", "0")

            );

    private final ObservableList<TableData> diseaseParamsData =
            FXCollections.observableArrayList(
                    new TableData("Incubated period", ""),
                    new TableData("Infectious period", ""),
                    new TableData("Mortality indicator", ""),
                    new TableData("Reproduction index", ""),
                    new TableData("Reproduction index /w Distancing1", ""),
                    new TableData("Reproduction index /w Distancing2", ""),
                    new TableData("Birth Rate", ""),
                    new TableData("Death Rate", ""),
                    new TableData("Moving healthy rate", ""),
                    new TableData("Moving ill rate", "")
            );

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        guiContext.setNRows(36);
        guiContext.setNCols(36);
        step.setDisable(true);
        run.setOnMouseClicked(mouseEvent -> {
            step.setDisable(true);
            simulation.step();
            guiUpdater.run();
        });
        pause.setOnMouseClicked(mouseEvent -> {
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
        reload.setOnMouseClicked(mouseEvent -> {
            guiUpdater.cleanSimData();
        });
        export.setOnMouseClicked(mouseEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export result");

            try {
                String file = csvWriter.createCSVFile(guiContext.getHistoryData(),
                        guiContext.getDiseaseConfig().name(), raportPdf.isSelected());
                alert.setHeaderText("Export successful");
                alert.setContentText("Exported to " + file);
            } catch (IOException e) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Export failed");
                alert.setContentText(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
                alert.setHeaderText("Export failed and/or pdf export error");
            } finally {
                alert.show();
            }
        });

        addCountryChoiceListener();
        addVaxChoiceListener();
        fillDiseaseChoice();
        fillSocialDistancingChooser();
        loadGraphicalMap();
        guiContext.setTableView(numbers);
        guiContext.setSimulationData(simulationData);
        guiContext.setParamsTable(diseaseInfo);
        guiContext.setGridPane(grid);
        guiContext.setChartData(history);
        guiContext.setXAxis(xAxis);
        guiContext.setYAxis(yAxis);
    }

    private void distributeRandomIll() {
        int split = Math.floorDiv(500, MIN_RANDOM_CELL);
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
            MapData.getCellAtIndex(pair.getValue1()).getStateCountMap().set(2, pair.getValue0());
            guiUpdater.reloadLabelAtIndex(pair.getValue1(), State.S, MapData.getCellAtIndex(pair.getValue1()), grid);
        });
    }

    private void loadGraphicalMap() {
        load.setOnMouseClicked(mouseEvent -> ofNullable(guiContext.getCountry()).ifPresentOrElse(country -> {
            log.debug("Loading map of {}", country);
            try {
                dataLoader.mapData(country.toLowerCase(), guiContext.getVaccinationRate());
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
            numbers.setItems(simulationData);
            numbers.setFixedCellSize((numbers.getHeight() - 40) / simulationData.size());
            numbers.prefHeightProperty()
                    .bind(Bindings.size(numbers.getItems()).multiply(numbers.getFixedCellSize()).add(30));
            log.debug(String.valueOf(Bindings.size(numbers.getItems()).multiply(numbers.getFixedCellSize()).add(30)));
        }, () -> log.error("Could not load map")));
    }
    private void loadGrid() {
        guiContext.setDayOfSim(0);
        grid.setGridLinesVisible(true);
        grid.setMaxSize(guiContext.getMapWidth(), guiContext.getMapHeight());
        for (int i = 0; i < guiContext.getNCols(); i++) { // i - x
            //            grid.add
            for (int j = 0; j < guiContext.getNRows(); j++) { // j - y
                CellLabel label = new CellLabel(" ", j,i);
                label.setMinSize(Math.floor(guiContext.getMapWidth() / guiContext.getNCols()),
                        Math.floor(guiContext.getMapHeight() / guiContext.getNRows()));
                GridPane.setColumnIndex(label, i);
                GridPane.setRowIndex(label, j);
                grid.getChildren().add(label);
                guiUpdater.updateDataTable();
                guiUpdater.updateCountryInfo();
/*                if(MapData.getCellAtIndex(new Pair<>(label.row,label.col)).getStateCountMap().stream().mapToInt(Integer::intValue).sum()>MIN_RANDOM_CELL){
                    label.setBackground(new Background(new BackgroundFill(Color.rgb(247,7,7,0.6),
                            CornerRadii.EMPTY,
                            Insets.EMPTY)));
                }*/
                label.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        MapData.addIllnessToCell(new Pair<>(label.getCol(), label.getRow()));
                        guiUpdater.updateLabels(State.I.getState(), grid);
                        guiUpdater.updateDataTable();
                    }
                    else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                        MapData.addExposedToCell(new Pair<>(label.getCol(), label.getRow()));
                        guiUpdater.updateLabels(State.E.getState(), grid);
                        guiUpdater.updateDataTable();
                    }
                });
            }
        }
        log.debug(String.valueOf(grid.getColumnCount()));
    }

    private void addCountryChoiceListener() {
        choice.setPromptText("Choose country");
        choice.setItems(FXCollections.observableArrayList("Polska"));
        choice.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldChoice, newChoice) -> guiContext.setCountry(newChoice));
    }

    private void addVaxChoiceListener(){
        vaxChoice.setPromptText("Choose vaccination rate");
        vaxChoice.setItems(FXCollections.observableArrayList(0.0,25.0,50.0,75.0));
        vaxChoice.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldChoice, newChoice) -> guiContext.setVaccinationRate(newChoice));
        vaxChoice.getSelectionModel().selectFirst();
    }

    private void fillDiseaseChoice() {
        diseaseInfo.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        diseaseInfo.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("value"));
        diseaseInfo.setItems(diseaseParamsData);
        diseaseInfo.setFixedCellSize(diseaseInfo.getPrefHeight() / diseaseParamsData.size() - 5);
        diseaseInfo.minHeightProperty()
                .bind(Bindings.size(diseaseInfo.getItems()).multiply(diseaseInfo.getFixedCellSize()).add(35));
        diseaseChooser.setPromptText("Choose disease");
        diseaseChooser.setItems(FXCollections.observableArrayList("FLU", "AH1N1", "SARS", "COVID19"));
        diseaseChooser.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldChoice, newChoice) -> {
                    guiContext.setDiseaseConfig(
                            DiseaseConfig.valueOf(newChoice));
                    Configuration.setDiseaseConfig(DiseaseConfig.valueOf(newChoice));
                    guiUpdater.updateDiseaseParams();
                    Configuration.BASE_MORTALITY = Configuration.DISEASE_CONFIG.getMortality();
                }));
    }

    private void fillSocialDistancingChooser() {
        socialDistancingLevelChoice.setPromptText("Social Distancing");
        socialDistancingLevelChoice.setItems(FXCollections.observableArrayList("NODISTANCING", "LEVEL1", "LEVEL2"));
        socialDistancingLevelChoice.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldChoice, newChoice) -> {
                    log.debug("OLD {}", guiContext.getDistancingLevel());
                    log.debug("NEW {}", DistancingLevel.valueOf((String) newChoice));
                    guiContext.setDistancingLevelChange(DistancingLevel.valueOf((String) newChoice));
                    guiContext.setDistancingLevelOld(guiContext.getDistancingLevel());
                    log.debug("CLICKED");
                    guiUpdater.updateDiseaseParams();
                }));
    }

    public void message(){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Simulation Finished");
        a.showAndWait();

    }
}

package pl.agh.kis.seirsimulation.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.controller.GuiController;
import pl.agh.kis.seirsimulation.model.Cell;
import pl.agh.kis.seirsimulation.model.Simulation;
import pl.agh.kis.seirsimulation.model.State;
import pl.agh.kis.seirsimulation.model.configuration.Configuration;
import pl.agh.kis.seirsimulation.model.data.MapData;
import pl.agh.kis.seirsimulation.output.writer.OutputDataDto;

import java.util.Objects;

@Slf4j
@Component
public class GuiUpdater {

    @Autowired GuiContext guiContext;

    @Autowired Simulation simulation;

    @Autowired GuiController guiController;

    private Thread thread;

    public void run() {
        guiContext.setSimRunning(true);
        thread = new Thread(() -> {
            Runnable updater = this::updateTest;

            while (guiContext.isSimRunning()) {
                Platform.runLater(updater);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void updateLabels(int state, GridPane gridPane) {
        for (int row = 0; row < guiContext.getNRows(); row++) {
            for (int col = 0; col < guiContext.getNCols(); col++) {
                reloadLabelAtIndex(new Pair<>(row, col), State.I, MapData.getCellAtIndex(new Pair<>(row, col)),
                        gridPane);
            }
        }
    }

    public void reloadLabelAtIndex(Pair<Integer, Integer> value1, State state,
            Cell cellAtIndex, GridPane grid) {
        ((Label) Objects.requireNonNull(GuiUtils.getNodeFromGridPane(grid, value1.getValue0(), value1.getValue1())))
                .setBackground(new Background(new BackgroundFill(getCellLabel(cellAtIndex.getStateCountMap().get(state.getState())),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    public Color getCellLabel(int stateNum){
        if(stateNum==0){
            return new Color(
                    1, 0, 0,
                    0);
        }else if (stateNum>0&&stateNum<=10){
            return Color.rgb(41,242,81,0.6);
        }else if (stateNum>10&&stateNum<=100){
            return Color.rgb(215,247,7,0.6);
        }else if (stateNum>100&&stateNum<=1000){
            return Color.rgb(247,191,7,0.6);
        }else if (stateNum>1000&&stateNum<=10000){
            return Color.rgb(247,119,7,0.6);
        }else return Color.rgb(247,7,7,0.6);
    }

    public void updateDataTable() {
        var data = guiContext.getSimulationData();
        var numbers = guiContext.getTableView();
        data.get(0).setValue(String.valueOf(guiContext.getDayOfSim()));
        data.get(1).setValue(String.valueOf(
                MapData.getNumberOfStateSummary(State.S) + MapData.getNumberOfStateSummary(State.I) + MapData
                        .getNumberOfStateSummary(State.E) + MapData.getNumberOfStateSummary(State.R)));
        data.get(2).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.E)));
        data.get(3).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.I)));
        data.get(4).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.R)));
        data.get(5).setValue(String.valueOf(MapData.getDeathNum()));
        numbers.refresh();
    }

    public void updateTest() {
        simulation.step();
        simulation.updateMortality();
        updateDiseaseParams();
        updateLabels(State.I.getState(), guiContext.getGridPane());
        updateHistory();
        updateDataTable();
        updateChartData();
        if (guiContext.isNotChanging()){
            guiContext.setSimRunning(false);
            log.debug("NONE CHANGES");
            guiController.message();
        }
        //TODO: sim ended no changes handle to 0 E,I
    }

    private void updateHistory() {
        guiContext.getHistoryData().add(OutputDataDto.builder().susceptible(MapData.getNumberOfStateSummary(State.S))
                .exposed(MapData.getNumberOfStateSummary(State.E)).infectious(
                        MapData.getNumberOfStateSummary(State.I)).recovered(MapData.getNumberOfStateSummary(State.R))
                .dead(MapData.getDeathNum())
                .day(guiContext.getDayOfSim()).build());
    }

    private void updateChartData() {
        final LineChart<String, Number> lineChart = guiContext.getChartData();
        var seriesCategory = String.valueOf(guiContext.getDayOfSim());
        //        lineChart.getData().get(0).getData().add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.S)));
        lineChart.getData().get(0).getData()
                .add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.E)));
        lineChart.getData().get(1).getData()
                .add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.I)));
        lineChart.getData().get(2).getData()
                .add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.R)));
        lineChart.getData().get(3).getData().add(new XYChart.Data<>(seriesCategory, MapData.getDeathNum()));
    }

    // TODO: 21.04.2020 move to history sthlike class updater and create unified interface

    public void prepareChart() {
        final Axis<String> xAxis = guiContext.getChartData().getXAxis();
        final NumberAxis yAxis = (NumberAxis) guiContext.getChartData().getYAxis();

        xAxis.setLabel("Day");
        xAxis.setAnimated(false);
        yAxis.setLabel("NO of people");
        yAxis.setAnimated(false);

        final LineChart<String, Number> lineChart = guiContext.getChartData();
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("History chart");
        lineChart.setAnimated(false);
        XYChart.Series<String, Number> series_E = new XYChart.Series<>();
        XYChart.Series<String, Number> series_I = new XYChart.Series<>();
        XYChart.Series<String, Number> series_R = new XYChart.Series<>();
        XYChart.Series<String, Number> series_D = new XYChart.Series<>();

        series_E.setName("E");
        series_I.setName("I");
        series_R.setName("R");
        series_D.setName("Death");

        //        lineChart.getData().add(series_S);
        lineChart.getData().add(series_E);
        lineChart.getData().add(series_I);
        lineChart.getData().add(series_R);
        lineChart.getData().add(series_D);
    }

    public void updateDiseaseParams() {
        var table = guiContext.getParamsTable();
        var data = table.getItems();
        data.get(0).setValue(String.valueOf(guiContext.getDiseaseConfig().getIncubation()));
        data.get(1).setValue(String.valueOf(guiContext.getDiseaseConfig().getInfection()));
        data.get(2).setValue(String.valueOf(guiContext.getDiseaseConfig().getMortality()));
        data.get(3).setValue(String.valueOf(guiContext.getDiseaseConfig().getReproduction()));
        data.get(4).setValue(String.valueOf(guiContext.getDiseaseConfig().getReproductionDistancing1()));
        data.get(5).setValue(String.valueOf(guiContext.getDiseaseConfig().getReproductionDistancing2()));
        table.refresh();
    }

    public void updateCountryInfo() {
        var table = guiContext.getParamsTable();
        var data = table.getItems();
        data.get(6).setValue(String.valueOf(Configuration.BIRTH_RATE));
        data.get(7).setValue(String.valueOf(Configuration.DEATH_RATE));
        data.get(8).setValue(String.valueOf(Configuration.MOVING_PPL_PERC));
        data.get(9).setValue(String.valueOf(Configuration.MOVING_PPL_SICK));
        table.refresh();
    }

    public void cleanSimData() {
        var table = guiContext.getParamsTable();
        var data = table.getItems();
        var populationTable = guiContext.getTableView();
        LineChart<String, Number> lineChart = guiContext.getChartData();

        data.forEach(row -> row.setValue(""));
        lineChart.getData().forEach(series -> series.getData().clear());
        populationTable.getItems().forEach(row -> row.setValue(""));

    }

}

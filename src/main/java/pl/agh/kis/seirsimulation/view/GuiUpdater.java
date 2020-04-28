package pl.agh.kis.seirsimulation.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
import pl.agh.kis.seirsimulation.model.Cell;
import pl.agh.kis.seirsimulation.model.Simulation;
import pl.agh.kis.seirsimulation.model.State;
import pl.agh.kis.seirsimulation.model.data.MapData;

import java.util.Objects;

@Slf4j
@Component
public class GuiUpdater {

    @Autowired GuiContext guiContext;

    @Autowired Simulation simulation;

    private Thread thread;

    public void run() {
        thread = new Thread(() -> {
            Runnable updater = this::updateTest;

            while (guiContext.isSimRunning()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(updater);
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
        int max = MapData.getMaxStateLevel(state);
        ((Label) Objects.requireNonNull(GuiUtils.getNodeFromGridPane(grid, value1.getValue1(), value1.getValue0())))
                .setBackground(new Background(new BackgroundFill(new Color(
                        1, 0, 0,
                        (double) cellAtIndex.getStateCountMap().get(state.getState()) / max), CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    public void updateDataTable() {
        var data = guiContext.getSimulationData();
        var numbers = guiContext.getTableView();
        data.get(0).setValue(String.valueOf(guiContext.getDayOfSim()));
        data.get(1).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.S)));
        data.get(2).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.E)));
        data.get(3).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.I)));
        data.get(4).setValue(String.valueOf(MapData.getNumberOfStateSummary(State.R)));
        numbers.refresh();
    }

    public void updateTest() {
        log.debug("update");
        simulation.step();
        updateLabels(State.I.getState(), guiContext.getGridPane());
        updateDataTable();
        updateChartData();
    }

    private void updateChartData() {
        final LineChart<String, Number> lineChart = guiContext.getHistory();
        var seriesCategory = String.valueOf(guiContext.getDayOfSim());
        lineChart.getData().get(0).getData().add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.S)));
        lineChart.getData().get(1).getData().add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.E)));
        lineChart.getData().get(2).getData().add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.I)));
        lineChart.getData().get(3).getData().add(new XYChart.Data<>(seriesCategory, MapData.getNumberOfStateSummary(State.R)));
    }

    // TODO: 21.04.2020 move to history sthlike class updater and create unified interface

    public void prepareChart() {
        final Axis<String> xAxis = guiContext.getHistory().getXAxis();
        final NumberAxis yAxis = (NumberAxis) guiContext.getHistory().getYAxis();

        xAxis.setLabel("Day");
        xAxis.setAnimated(false);
        yAxis.setLabel("NO of people");
        yAxis.setAnimated(false);

        final LineChart<String, Number> lineChart = guiContext.getHistory();
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("History chart");
        lineChart.setAnimated(false);
        XYChart.Series<String, Number> series_S = new XYChart.Series<>();
        XYChart.Series<String, Number> series_E = new XYChart.Series<>();
        XYChart.Series<String, Number> series_I = new XYChart.Series<>();
        XYChart.Series<String, Number> series_R = new XYChart.Series<>();

        series_S.setName("S");
        series_E.setName("E");
        series_I.setName("I");
        series_R.setName("R");

        lineChart.getData().add(series_S);
        lineChart.getData().add(series_E);
        lineChart.getData().add(series_I);
        lineChart.getData().add(series_R);
    }

    public void updateDiseaseParams() {
        var table = guiContext.getParamsTable();
        var data = table.getItems();
        log.debug(String.valueOf(guiContext.getDiseaseConfig().getIncubation()));
        data.get(0).setValue(String.valueOf(guiContext.getDiseaseConfig().getIncubation()));
        data.get(1).setValue(String.valueOf(guiContext.getDiseaseConfig().getInfection()));
        data.get(2).setValue(String.valueOf(guiContext.getDiseaseConfig().getMortality()));
        data.get(3).setValue(String.valueOf(guiContext.getDiseaseConfig().getReproduction()));
        table.refresh();
    }
}
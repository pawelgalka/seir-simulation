package pl.agh.kis.seirsimulation.controller;

import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@NoArgsConstructor
public class GuiContext {
    private String country;
    private double mapWidth;
    private double mapHeight;
    private boolean simRunning;
    private int nRows;
    private int nCols;
    private int dayOfSim;
    private TableView<TableData> tableView;
    private ObservableList<TableData> data;
    private GridPane gridPane;
    private LineChart<String, Number> history;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;

    public void dayStep(){
        dayOfSim++;
    }
}

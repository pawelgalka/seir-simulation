package pl.agh.kis.seirsimulation.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import pl.agh.kis.seirsimulation.model.Cell;
import pl.agh.kis.seirsimulation.model.data.MapData;

import java.sql.Struct;
import java.util.List;
import java.util.Map;

@Slf4j
public class GuiController {

    @FXML
    GridPane gridPane;

    @FXML
    ImageView background;

    public void init() {
        List<List<Cell>> people = MapData.getGridMap();
        background.setFitHeight(1000);
        background.setFitWidth(1000);
        gridPane.setGridLinesVisible(true);
        gridPane.setMaxSize(1000,1000);
        final int numCols = 50;
        final int numRows = 50;
        log.debug(String.valueOf(numCols));
        log.debug(String.valueOf(numRows));
        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                Label label = new Label(" ");
                label.setBackground(new Background(new BackgroundFill(new Color(1-(double)people.get(i).get(j).getStateCountMap().get(0)/ MapData.getMaxValue(),1-(double)people.get(i).get(j).getStateCountMap().get(0)/ MapData.getMaxValue(),1-(double)people.get(i).get(j).getStateCountMap().get(0)/ MapData.getMaxValue(),0.6),
                        CornerRadii.EMPTY, Insets.EMPTY)));
                label.setMinSize(Math.floorDiv(1000,50),Math.floorDiv(1000,50));
                gridPane.add(label, i, j);
            }
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}

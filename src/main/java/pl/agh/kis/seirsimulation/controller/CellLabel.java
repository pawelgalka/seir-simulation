package pl.agh.kis.seirsimulation.controller;

import javafx.scene.control.Label;
import lombok.Getter;

@Getter
public class CellLabel extends Label {
    int row, col;

    CellLabel(String s, int row, int col){
        super(s);
        this.row = row;
        this.col = col;
    }
}

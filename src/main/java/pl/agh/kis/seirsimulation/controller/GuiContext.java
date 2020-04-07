package pl.agh.kis.seirsimulation.controller;

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
}

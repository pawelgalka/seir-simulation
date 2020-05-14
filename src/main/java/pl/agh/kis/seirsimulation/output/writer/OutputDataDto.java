package pl.agh.kis.seirsimulation.output.writer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class OutputDataDto {
    private int day;
    private int susceptible;
    private int exposed;
    private int infectious;
    private int recovered;

}

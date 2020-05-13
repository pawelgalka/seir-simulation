package pl.agh.kis.seirsimulation.output.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static java.time.LocalDate.now;

@Component
public class CSVWriter {
    private static final String[] HEADERS = new String[] { "Date", "Susceptible", "Exposed", "Infectious",
            "Recovered" };
    private int counter = 0;

    public String createCSVFile(List<OutputDataDto> history, String diseaseName)
            throws IOException {
        String filename = new StringJoiner("_").add(String.valueOf(now().toEpochDay())).add(diseaseName)
                .add(counter++ +".csv").toString();
        FileWriter out = new FileWriter(filename);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(HEADERS))) {
            history.forEach(csvDataConsumer(printer));
        }
        return filename;
    }

    private Consumer<OutputDataDto> csvDataConsumer(CSVPrinter printer) {
        return dataEntry -> {
            try {
                printer.printRecord(dataEntry.getDay(),dataEntry.getSusceptible(), dataEntry.getExposed(), dataEntry.getInfectious(),
                        dataEntry.getRecovered());
            } catch (IOException ignored) {
            }
        };
    }

}

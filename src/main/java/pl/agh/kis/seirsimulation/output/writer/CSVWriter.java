package pl.agh.kis.seirsimulation.output.writer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static java.time.LocalDate.now;
@Slf4j
@Component
public class CSVWriter {
    private static final String[] HEADERS = new String[] { "Date", "Susceptible", "Exposed", "Infectious",
            "Recovered","Dead" };
    private int counter = 0;

    public String createCSVFile(List<OutputDataDto> history, String diseaseName,boolean createPdf)
            throws IOException, InterruptedException {
        String filename = new StringJoiner("_").add(String.valueOf(now().toEpochDay())).add(diseaseName)
                .add(counter++ +".csv").toString();
        FileWriter out = new FileWriter(filename);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(HEADERS))) {
            history.forEach(csvDataConsumer(printer));
        }
        if(createPdf){
            log.debug("Pdf creating");
            createPdfRaport(filename);
        }
        return filename;
    }

    private Consumer<OutputDataDto> csvDataConsumer(CSVPrinter printer) {
        return dataEntry -> {
            try {
                printer.printRecord(dataEntry.getDay(),dataEntry.getSusceptible(), dataEntry.getExposed(), dataEntry.getInfectious(),
                        dataEntry.getRecovered(),dataEntry.getDead());
            } catch (IOException ignored) {
            }
        };
    }
    private void createPdfRaport(String csvFileName) throws IOException, InterruptedException {
        String currentRelativePath = Paths.get("").toAbsolutePath().toString()+"\\raport.py";
        csvFileName=Paths.get("").toAbsolutePath().toString()+"\\"+csvFileName;
        Process p = Runtime.getRuntime().exec("python "+currentRelativePath+" "+csvFileName);
        p.waitFor();
        p.destroy();
    }

}

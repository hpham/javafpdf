package net.sourceforge.javafpdf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class PDFTutorial5 extends FPDF {
    private final String title = "20000 Leagues Under the Seas";
    private float y0;
    private int col = 0;

    public PDFTutorial5() {
        super();
    }


    public List<List<String>> LoadData(String fileName) {
        List<List<String>> records = new ArrayList<>();
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            stream.forEach(s -> {
                String[] values = s.split(";");
                records.add(Arrays.asList(values));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    // Simple table
    public void basicTable(String[] header, List<List<String>> data) throws IOException {
        // Header
        for (String col : header) {
            Cell(40, 7, col, Borders.allBorders());
        }
        Ln();
        // Data
        for (List<String> row : data) {
            for (String col : row) {
                Cell(40, 6, col, Borders.allBorders());
            }
            Ln();
        }
    }

    public void improveTable(String[] header, List<List<String>> data) throws IOException {
        List<Integer> w = Arrays.asList(40, 35, 40, 45);
        int sum = w.stream().mapToInt(num -> num).sum();
        // Header
        for (int i = 0; i < w.size(); i++) {
            Cell(w.get(i), 7, header[i], Borders.allBorders());
        }
        Ln();
        // Data
        for (List<String> row : data) {
            Cell(w.get(0), 6, row.get(0), new Borders(true, false, true, false));
            Cell(w.get(1), 6, row.get(1), new Borders(true, false, true, false));
            Cell(w.get(2), 6, row.get(2), new Borders(true, false, true, false), Position.RIGHTOF, Alignment.RIGHT, false, 0);
            Cell(w.get(3), 6, row.get(3), new Borders(true, false, true, false), Position.RIGHTOF, Alignment.RIGHT, false, 0);
            Ln();
        }
        Cell(sum, 0, "", new Borders(false, true, false, false));
    }

    public void fancyTable(String[] header, List<List<String>> data) throws IOException {

        setFillColor(255, 0, 0 );
        setTextColor(255);
        setDrawColor(128,0,0);
        setFont("Arial", new HashSet<FontStyle>(){{add(FontStyle.BOLD);}}, 14);
        setLineWidth(0.3f);
        List<Integer> w = Arrays.asList(40, 35, 40, 45);
        int sum = w.stream().mapToInt(num -> num).sum();
        // Header
        for (int i = 0; i < w.size(); i++) {
            Cell(w.get(i), 7, header[i], Borders.allBorders(),Position.RIGHTOF, Alignment.CENTER, true, 0);
        }
        Ln();
        // Data
        setFillColor(224, 235, 255);
        setTextColor(0);
        setFont("Arial", null, 14);
        boolean fill = false;
        for (List<String> row : data) {
            Cell(w.get(0), 6, row.get(0), new Borders(true, false, true, false), Position.RIGHTOF, Alignment.LEFT, fill, 0);
            Cell(w.get(1), 6, row.get(1), new Borders(true, false, true, false), Position.RIGHTOF, Alignment.LEFT, fill, 0);
            Cell(w.get(2), 6, row.get(2), new Borders(true, false, true, false), Position.RIGHTOF, Alignment.RIGHT, fill, 0);
            Cell(w.get(3), 6, row.get(3), new Borders(true, false, true, false), Position.RIGHTOF, Alignment.RIGHT, fill, 0);
            Ln();
            fill = !fill;
        }
        Cell(sum, 0, "", new Borders(false, true, false, false));
    }

    @Override
    public void Footer() {
    }

    @Override
    public void Header() {
    }

}

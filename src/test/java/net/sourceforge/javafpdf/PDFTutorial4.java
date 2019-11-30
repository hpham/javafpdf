package net.sourceforge.javafpdf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PDFTutorial4 extends FPDF {
    private final String title = "20000 Leagues Under the Seas";
    private float y0;
    private int col = 0;

    public PDFTutorial4() {
        super();
    }

    @Override
    public void Footer() {
        try {
            // Position at 1.5 cm from bottom
            setY(-15);
            // Arial italic 8
            final Set<FontStyle> set = new HashSet<FontStyle>() {{
                add(FontStyle.ITALIC);
            }};
            setFont("Arial", set, 8);
            // Text color in gray
            setTextColor(128);
            // Page number
            Cell(0, 10, "Page " + pageNo(), Position.NEXTLINE, Alignment.CENTER);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Header() {
        try {
            // Arial bold 15
            final Set<FontStyle> set = new HashSet<FontStyle>();
            set.add(FontStyle.BOLD);
            setFont("Arial", set, 15.0f);

            final float w = getStringWidth(title) + 6.0f;

            setX((210 - w) / 2);

            setDrawColor(0, 80, 180);
            setFillColor(230, 230, 0);
            setTextColor(220, 50, 50);
            // Thickness of frame (1 mm)
            setLineWidth(1);
            // Title
            Cell(w, 9, title, Borders.allBorders(), Position.NEXTLINE, Alignment.CENTER, true, 0);
            Ln(10);
            this.y0 = this.getY();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void setCol(final int col) {
        // Set position at a given column
        this.col = col;
        final float x = 10 + (col * 65);
        setLeftMargin(x);
        setX(x);
    }

    @Override
    public boolean acceptPageBreak() {
        // Method accepting or not automatic page break
        if (this.col < 2) {
            // Go to next column
            this.setCol(this.col + 1);
            // Set ordinate to top
            this.setY(this.y0);
            // Keep on page
            return false;
        } else {
            // Go back to first column
            this.setCol(0);
            // Page break
            return true;
        }
    }

    public void setChapterTitle(final int num, final String label) throws IOException {
        final Set<FontStyle> set = new HashSet<FontStyle>();
        // Arial 12
        setFont("Arial", set, 12);
        // Background color
        setFillColor(200, 220, 255);
        // Title
        Cell(0, 6, "Chapter " + num + ": " + label, null, Position.NEXTLINE, Alignment.JUSTIFIED, true, 0);
        // Line break
        Ln(4);
        this.y0 = this.getY();
    }

    public void setChapterBody(final String file) throws IOException, URISyntaxException {
        // Read text file
        final String txt = new String(Files.readAllBytes(Paths.get(file)));
        // Times 12
        final Set<FontStyle> set = new HashSet<FontStyle>();
        setFont("Times", set, 12);
        // Output justified text
        MultiCell(60, 5, txt);
        // Line break
        Ln();
        // Mention in italics

        set.add(FontStyle.ITALIC);
        setFont("Times", set, 12);
        Cell(0, 5, "(end of excerpt)");
        this.setCol(0);
    }

    public void printChapter(final int num, final String title, final String file) throws IOException, URISyntaxException {
        addPage();
        setChapterTitle(num, title);
        setChapterBody(file);
    }

}

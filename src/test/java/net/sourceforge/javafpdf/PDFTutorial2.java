package net.sourceforge.javafpdf;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PDFTutorial2 extends FPDF {

    public PDFTutorial2() {
        super();
    }

    @Override
    public void Footer() {
        // TODO Auto-generated method stub

    }
    // FIXME Border
    @Override
    public void Header() {
        // Logo
        try {
            Image("logo_pb.png", new Coordinate(10, 8), 10.0f, 8.0f, ImageType.PNG, 0);
            // Arial bold 15
            final Set<FontStyle> set = new HashSet<FontStyle>();
            set.add(FontStyle.BOLD);
            setFont("Arial", set, 15.0f);
            // Move to the right
            Cell(80.0f, 0f);
            // Title
            Cell(30.0f, 10.0f, "Title", new Borders(), Position.NEXTLINE, Alignment.CENTER, true, 0);
            // Line break
            Ln(20);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

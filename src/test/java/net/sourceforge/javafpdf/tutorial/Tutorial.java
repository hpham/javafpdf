package net.sourceforge.javafpdf.tutorial;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.javafpdf.*;
import org.junit.Test;

public class Tutorial {
    @Test
    public void test_tutor_1() throws IOException {
        final FPDF pdf = new PDFTutorial1();
        pdf.addPage();
        final Set<FontStyle> set = new HashSet<>();
        set.add(FontStyle.BOLD);
        pdf.setFont("Arial", set, 16);
        pdf.Cell(40, 10, "Hello World!");
        //pdf.Cell(60, 10, "Powered by FPDF.",Borders.noBorders(), Position.RIGHTOF , Alignment.CENTER, false, 0);
        final File temp = File.createTempFile("fpdf", ".pdf");
        pdf.output(temp);
        System.out.println(temp.getAbsolutePath());
    }

    @Test
    public void test_tutor_2() throws IOException {
        final FPDF pdf = new PDFTutorial2();

        pdf.aliasNbPages();
        pdf.addPage();
        final Set<FontStyle> set = new HashSet<>();
        pdf.setFont("Times", set, 12);
        for (int i = 1; i <= 40; i++) {
            pdf.Cell(0, 10, "Printing line number " + i, Position.NEXTLINE, Alignment.LEFT);
        }
        final File temp = File.createTempFile("fpdf", ".pdf");
        pdf.output(temp);
        System.out.println(temp.getAbsolutePath());
    }

    // FIXME fillColor do not work
    @Test
    public void test_tutor_3() throws IOException, URISyntaxException {
        final PDFTutorial3 pdf = new PDFTutorial3();

        pdf.setTitle("20000 Leagues Under the Seas");
        pdf.setAuthor("Jules Verne");
        pdf.printChapter(1, "A RUNAWAY REEF", "20k_c1.txt");
        pdf.printChapter(2, "THE PROS AND CONS", "20k_c2.txt");
        final File temp = File.createTempFile("fpdf", ".pdf");
        pdf.output(temp);
        System.out.println(temp.getAbsolutePath());
    }

    @Test
    public void test_tutor_4() throws IOException, URISyntaxException {
        final PDFTutorial4 pdf = new PDFTutorial4();

        pdf.setTitle("20000 Leagues Under the Seas");
        pdf.setAuthor("Jules Verne");
        pdf.printChapter(1, "A RUNAWAY REEF", "20k_c1.txt");
        pdf.printChapter(2, "THE PROS AND CONS", "20k_c2.txt");
        final File temp = File.createTempFile("fpdf", ".pdf");
        pdf.output(temp);
        System.out.println(temp.getAbsolutePath());
    }
}

package net.sourceforge.javafpdf;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class MinimalTest {

	@Test
	public void test_minimal_pdf() throws IOException {
		FPDF pdf = new JavaPDF();
		pdf.addPage();
		Set<FontStyle> set = new HashSet<FontStyle>();
		pdf.setFont("Arial", set, 16);
		pdf.Cell(40, 10, "Hello World!");
		File temp = new File("c:/temp/test.pdf");
		pdf.output(temp);
	}
}

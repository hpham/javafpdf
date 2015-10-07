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
		File temp = File.createTempFile("fpdf", ".pdf");
		pdf.output(temp);
		System.out.println(temp.getAbsolutePath());
	}

	@Test
	public void test_header_footer_pdf() throws IOException {
		FPDF pdf = new JavaPDF();
		pdf.aliasNbPages();
		pdf.addPage();
		Set<FontStyle> set = new HashSet<FontStyle>();
		pdf.setFont("Times", set, 12);
		for (int i = 1; i <= 40; i++) {
			pdf.Cell(0, 10, "Printing line number " + i, Position.BELOW, Alignment.LEFT);
		}
		File temp = File.createTempFile("fpdf", ".pdf");		
		pdf.output(temp);
		System.out.println(temp.getAbsolutePath());
	}
}

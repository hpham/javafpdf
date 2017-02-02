package net.sourceforge.javafpdf.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.sourceforge.javafpdf.Alignment;
import net.sourceforge.javafpdf.FPDF;
import net.sourceforge.javafpdf.FontStyle;
import net.sourceforge.javafpdf.JavaPDF;
import net.sourceforge.javafpdf.Position;

public class Tutorial {
	@Test
	public void test_tutor_1() throws IOException {
		FPDF pdf = new JavaPDF();
		pdf.addPage();
		Set<FontStyle> set = new HashSet<FontStyle>();
		set.add(FontStyle.BOLD);
		pdf.setFont("Arial", set, 16);
		pdf.Cell(40, 10, "Hello World!");
		File temp = File.createTempFile("fpdf", ".pdf");
		pdf.output(temp);
		System.out.println(temp.getAbsolutePath());
	}
	
	@Test
	public void test_tutor_2() throws IOException {
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

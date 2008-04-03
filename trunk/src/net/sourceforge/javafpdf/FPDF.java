/* $Id$
 * (K) 2008 All Rites Reversed -- Reprint what you like.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,  subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.javafpdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Faithful Java port of <a href="http://www.fpdf.org">FPDF for PHP</a>.
 * 
 * @author Olivier Plathey
 * @author Alan Plum
 * @since 1 Mar 2008
 * @version 1.53 / $Rev$
 */
public abstract class FPDF {
	/** Character width. Used to be global. */
	private static Map<String, Charwidths>	charwidths;

	/** Point. Base unit. */
	public static final float				PT	= 1f;

	/** Inch. 72 points. */
	public static final float				IN	= 72f;

	/** Millimeter. 72 / 25.4 points. */
	public static final float				MM	= (72 / 25.4f);

	/** Centimeter. 72 / 2.54 points. */
	public static final float				CM	= (72 / 2.54f);

	static {
		charwidths = new HashMap<String, Charwidths>();
	}

	protected static Charwidths getCharwidths(final String font)
			throws IOException {
		if (charwidths.get(font) == null) {
			Charwidths font_charwidths = new Charwidths(font);
			charwidths.put(font, font_charwidths);
		}
		return charwidths.get(font);
	}

	/** current page number */
	protected int									page;

	/** current object number */
	protected int									n;

	/** array of object offsets */
	protected Map<Integer, Integer>					offsets;

	/** buffer holding in-memory PDF */
	protected List<byte[]>							buffer;

	/** array containing pages */
	protected Map<Integer, List<byte[]>>			pages;

	/** current document state */
	protected int									state;

	/** compression flag */
	protected boolean								compress;

	/** default orientation */
	protected Orientation							defaultOrientation;

	/** current orientation */
	protected Orientation							currentOrientation;

	/** array indicating orientation changes */
	protected Map<Integer, Boolean>					orientationChanges;

	/** scale factor (number of points in user unit) */
	protected float									k;

	/** dimensions of page format in points */
	protected float									fwPt, fhPt;

	/** dimensions of page format in user unit */
	protected float									fw, fh;

	/** current dimensions of page in points */
	protected float									wPt, hPt;

	/** current dimensions of page in user unit */
	protected float									w, h;

	/** left margin */
	protected float									lMargin;

	/** top margin */
	protected float									tMargin;

	/** right margin */
	protected float									rMargin;

	/** page break margin */
	protected float									bMargin;

	/** cell margin */
	protected float									cMargin;

	/** current position in user unit for cell positioning */
	protected float									x, y;

	/** height of last cell printed */
	protected float									lastH;

	/** line width in user unit */
	protected float									lineWidth;

	/** array of standard font names */
	protected Map<String, String>					coreFonts;

	/** array of used fonts */
	protected Map<String, Font>						fonts;

	/** array of encoding differences */
	protected Map<Integer, String>					diffs;

	/** array of used images */
	protected Map<String, Map<String, Object>>		images;

	/** array of links in pages */
	protected Map<Integer, Map<Integer, Object>>	pageLinks;

	/** array of internal links */
	protected Map<Integer, Map<Integer, Float>>		links;

	/** current font family */
	protected String								fontFamily;

	/** current font style */
	protected Set<FontStyle>						fontStyle;

	/** underlining flag */
	protected boolean								underline;

	/** current font info */
	protected Font									currentFont;

	/** current font size in points */
	protected float									fontSizePt;

	/** current font size in user unit */
	protected float									fontSize;

	/** commands for drawing color */
	protected String								drawColor;

	/** commands for filling color */
	protected String								fillColor;

	/** commands for text color */
	protected String								textColor;

	/** indicates whether fill and text colors are different */
	protected boolean								colorFlag;

	/** word spacing */
	protected float									ws;

	/** automatic page breaking */
	protected boolean								autoPageBreak;

	/** threshold used to trigger page breaks */
	protected float									pageBreakTrigger;

	/** flag set when processing footer */
	protected boolean								inFooter;

	/** zoom display mode */
	protected Zoom									zoomMode;

	/** zoom factor (if mode is not set) */
	protected int									zoomFactor;

	/** layout display mode */
	protected Layout								layoutMode;

	/** title */
	protected String								title;

	/** subject */
	protected String								subject;

	/** author */
	protected String								author;

	/** keywords */
	protected String								keywords;

	/** creator */
	protected String								creator;

	/** alias for total number of pages */
	protected String								aliasNbPages;

	/** PDF version number */
	protected String								pdfVersion;

	private static final String						revision;

	static {
		// Some SVN magic to find out the revision of this class
		String rev = "$Rev$"; //$NON-NLS-1$
		revision = rev.substring(6, rev.length() - 2);
	}

	/**
	 * Default Constructor. Creates an FPDF object with Portrait orientation, MM
	 * as unit and A4 dimensions.
	 */
	public FPDF() {
		this(Orientation.PORTRAIT, MM, Format.A4);
	}

	/**
	 * Constructor. Creates an FPDF object with MM as unit and A4 dimensions.
	 * 
	 * @param orientation
	 *            the orientation
	 */
	public FPDF(final Orientation orientation) {
		this(orientation, MM, Format.A4);
	}

	/**
	 * Constructor. Creates an FPDF object with Portrait orientation and A4
	 * dimensions.
	 * 
	 * @param unit
	 *            the unit size in points
	 */
	public FPDF(final float unit) {
		this(Orientation.PORTRAIT, unit, Format.A4);
	}

	/**
	 * Constructor. Creates an FPDF object with A4 dimensions.
	 * 
	 * @param orientation
	 *            the orientation
	 * @param unit
	 *            the unit size in points
	 */
	public FPDF(final Orientation orientation, final float unit) {
		this(orientation, unit, Format.A4);
	}

	/**
	 * Constructor. Creates an FPDF object with Portrait orientation and MM as
	 * size.
	 * 
	 * @param format
	 *            the format
	 */
	public FPDF(final Format format) {
		this(Orientation.PORTRAIT, MM, format);
	}

	/**
	 * Constructor. Creates an FPDF object with MM as unit.
	 * 
	 * @param orientation
	 *            the orientation
	 * @param format
	 *            the format
	 */
	public FPDF(final Orientation orientation, final Format format) {
		this(orientation, MM, format);
	}

	/**
	 * Constructor.
	 * 
	 * @param orientation
	 *            the orientation
	 * @param unit
	 *            the unit size in points
	 * @param format
	 *            the format
	 */
	public FPDF(final Orientation orientation, final float unit,
			final Format format) {
		// Initialization of properties
		this.page = 0;
		this.n = 2;
		this.buffer = new ArrayList<byte[]>();
		this.pages = new HashMap<Integer, List<byte[]>>();
		this.orientationChanges = new HashMap<Integer, Boolean>();
		this.state = 0;
		this.fonts = new HashMap<String, Font>();
		this.diffs = new HashMap<Integer, String>();
		this.images = new HashMap<String, Map<String, Object>>();
		this.links = new HashMap<Integer, Map<Integer, Float>>();
		this.pageLinks = new HashMap<Integer, Map<Integer, Object>>();
		this.offsets = new HashMap<Integer, Integer>();
		this.inFooter = false;
		this.lastH = 0;
		this.fontFamily = null;
		this.fontStyle = null;
		this.fontSizePt = 12;
		this.underline = false;
		this.drawColor = "0 G"; //$NON-NLS-1$
		this.fillColor = "0 g"; //$NON-NLS-1$
		this.textColor = "0 g"; //$NON-NLS-1$
		this.colorFlag = false;
		this.ws = 0;
		// Standard fonts
		this.coreFonts = new HashMap<String, String>();
		this.coreFonts.put("courier", "Courier"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("courierB", "Courier-Bold"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("courierI", "Courier-Oblique"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("courierBI", "Courier-BoldOblique"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("helvetica", "Helvetica"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("helveticaB", "Helvetica-Bold"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("helveticaI", "Helvetica-Oblique"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("helveticaBI", "Helvetica-BoldOblique"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("times", "Times-Roman"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("timesB", "Times-Bold"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("timesI", "Times-Italic"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("timesBI", "Times-BoldItalic"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("symbol", "Symbol"); //$NON-NLS-1$//$NON-NLS-2$
		this.coreFonts.put("zapfdingbats", "ZapfDingbats"); //$NON-NLS-1$//$NON-NLS-2$
		// Scale factor
		this.k = unit;
		// Page format
		this.fwPt = format.getWidth();
		this.fhPt = format.getHeight();
		this.fw = this.fwPt / this.k;
		this.fh = this.fhPt / this.k;
		// Page orientation
		switch (orientation) {
		case PORTRAIT:
			this.wPt = this.fwPt;
			this.hPt = this.fhPt;
			break;
		case LANDSCAPE:
			this.wPt = this.fhPt;
			this.hPt = this.fwPt;
			break;
		}
		this.defaultOrientation = orientation;
		this.currentOrientation = this.defaultOrientation;
		this.w = this.wPt / this.k;
		this.h = this.hPt / this.k;
		// Page margins (1 cm)
		float margin = 28.35f / this.k;
		this.setMargins(margin, margin);
		// Interior cell margin (1 mm)
		this.cMargin = margin / 10;
		// Line width (0.2 mm)
		this.lineWidth = .567f / this.k;
		// Automatic page break
		this.setAutoPageBreak(true, 2 * margin);
		// Full width display mode
		this.setDisplayMode(Zoom.FULLWIDTH, Layout.DEFAULT);
		// Enable compression
		this.compress = false; // NOTE default was: true
		// Set default PDF version number
		this.pdfVersion = "1.3"; //$NON-NLS-1$
	}

	protected void _beginpage(final Orientation orientation) {
		this.page++;
		this.pages.put(Integer.valueOf(this.page), new ArrayList<byte[]>());
		this.state = 2;
		this.x = this.lMargin;
		this.y = this.tMargin;
		this.fontFamily = ""; //$NON-NLS-1$
		// Page orientation
		if (!orientation.equals(this.defaultOrientation)) {
			this.orientationChanges.put(Integer.valueOf(this.page),
					Boolean.TRUE);
		}
		if (!orientation.equals(this.currentOrientation)) {
			// Change orientation
			if (orientation.equals(Orientation.PORTRAIT)) {
				this.wPt = this.fwPt;
				this.hPt = this.fhPt;
				this.w = this.fw;
				this.h = this.fh;
			} else {
				this.wPt = this.fhPt;
				this.hPt = this.fwPt;
				this.w = this.fh;
				this.h = this.fw;
			}
			this.pageBreakTrigger = this.h - this.bMargin;
			this.currentOrientation = orientation;
		}
	}

	/** Underline text */
	protected String _dounderline(final float x, final float y, final String txt) {
		float w = this.getStringWidth(txt) + this.ws * (txt.split(" ")).length; //$NON-NLS-1$
		return String.format(Locale.ENGLISH, "%.2f %.2f %.2f %.2f re f", //$NON-NLS-1$
				Float.valueOf(x * this.k), Float.valueOf(this.h
						- (y - this.currentFont.getUp() / 1000 * this.fontSize)
						* this.k), Float.valueOf(w * this.k), Float
						.valueOf(-this.currentFont.getUt() / 1000
								* this.fontSizePt));
	}

	protected void _enddoc() {
		this._putheader();
		this._putpages();
		this._putresources();
		// Info
		this._newobj();
		this._out("<<"); //$NON-NLS-1$
		this._putinfo();
		this._out(">>"); //$NON-NLS-1$
		this._out("endobj"); //$NON-NLS-1$
		// Catalog
		this._newobj();
		this._out("<<"); //$NON-NLS-1$
		this._putcatalog();
		this._out(">>"); //$NON-NLS-1$
		this._out("endobj"); //$NON-NLS-1$
		// Cross-ref
		int o = this._length(this.buffer);
		this._out("xref"); //$NON-NLS-1$
		this._out("0 " + (this.n + 1)); //$NON-NLS-1$
		this._out("0000000000 65535 f "); //$NON-NLS-1$
		for (int i = 1; i <= this.n; i++) {
			this._out(String.format(Locale.ENGLISH, "%010d 00000 n ", //$NON-NLS-1$
					this.offsets.get(Integer.valueOf(i))));
		}
		// Trailer
		this._out("trailer"); //$NON-NLS-1$
		this._out("<<"); //$NON-NLS-1$
		this._puttrailer();
		this._out(">>"); //$NON-NLS-1$
		this._out("startxref"); //$NON-NLS-1$
		this._out(Integer.toString(o));
		this._out("%%EOF"); //$NON-NLS-1$
		this.state = 3;
	}

	/** End of page contents */
	protected void _endpage() {
		this.state = 1;
	}

	/** Add \ before \, ( and ) */
	protected String _escape(final String s) {
		if (s == null) {
			return null;
		}
		return s.replace(")", "\\)") //$NON-NLS-1$//$NON-NLS-2$
				.replace("(", "\\(") //$NON-NLS-1$//$NON-NLS-2$
				.replace("\\", "\\\\"); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Equivalent of PHP fread().
	 * 
	 * @throws IOException
	 *             if the stream can not be read.
	 */
	protected char[] _fread(final InputStream f, final int length)
			throws IOException {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			int in = f.read();
			chars[i] = (char) in;
		}
		return chars;
	}

	protected byte[] _freadb(final InputStream f, final int length)
			throws IOException {
		byte[] bytes = new byte[length];
		f.read(bytes);
		return bytes;
	}

	/**
	 * Reads bytes from a given stream and appends them to a given array, then
	 * returns the combined array.
	 */
	protected byte[] _freadb(final InputStream f, final int length,
			final byte[] bytes) throws IOException {
		byte[] b;
		int offset;
		if (bytes != null) {
			b = new byte[bytes.length + length];
			for (int i = 0; i < bytes.length; i++) {
				b[i] = bytes[i];
			}
			offset = bytes.length;
		} else {
			b = new byte[length];
			offset = 0;
		}
		f.read(b, offset, length);
		return b;
	}

	/**
	 * Read a 4-byte integer from file
	 * 
	 * @throws IOException
	 *             if the stream can not be read.
	 */
	protected int _freadint(final InputStream f) throws IOException {
		// We'll assume big-endian encoding here.
		int a = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			int in = f.read();
			byte b = (byte) in;
			a += (b & 0x000000FF) << shift;
		}
		return a;
	}

	/** Begin a new object */
	protected void _newobj() {
		this.n++;
		this.offsets.put(Integer.valueOf(this.n), Integer.valueOf(this
				._length(this.buffer)));
		this._out(this.n + " 0 obj"); //$NON-NLS-1$
	}

	protected int _length(final List<byte[]> buffer) {
		int len = 0;
		for (byte[] b : buffer) {
			len += b.length;
		}
		return len;
	}

	protected void _out(final String s) {
		// Add a line to the document
		if (this.state == 2) {
			this.pages.get(Integer.valueOf(this.page)).add(
					(s + '\n').getBytes());
		} else {
			/*
			 * NOTE This is a hack put in place because Java converts to true
			 * ISO-8859-1 -- rather than the Windows 125x faux "Latin-1" which
			 * is used almost everywhere else and supports the euro sign (unlike
			 * the real thing). This probably should be replaced/removed if it
			 * causes any trouble or unexpected side-effects. Binary data should
			 * probably not go through this method.
			 */
			try {
				this.buffer.add((s.replace('€', (char) 128) + '\n')
						.getBytes("ISO-8859-1")); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				this.buffer.add((s.replace('€', (char) 128) + '\n').getBytes());
			}
		}
	}

	protected Map<String, Object> _parsejpg(final File file) {
		// FIXME handle JPEGs
		throw new NotImplementedException();
	}

	/** Extract info from a PNG file */
	protected Map<String, Object> _parsepng(final File file) throws IOException {
		InputStream f = new FileInputStream(file);
		try {
			// Check signature
			char[] sig = new char[] { 137, 'P', 'N', 'G', 13, 10, 26, 10 };
			for (int i = 0; i < sig.length; i++) {
				int in = f.read();
				char c = (char) in;
				if (c != sig[i]) {
					throw new IOException("Not a PNG file: " + file); //$NON-NLS-1$
				}
			}
			this._fread(f, 4);
			// Read header chunk
			char[] chunk = new char[] { 'I', 'H', 'D', 'R' };
			for (int i = 0; i < chunk.length; i++) {
				int in = f.read();
				char c = (char) in;
				if (c != chunk[i]) {
					throw new IOException("Not a PNG file: " + file); //$NON-NLS-1$
				}
			}
			int w = this._freadint(f);
			int h = this._freadint(f);
			int bpc = f.read();
			if (bpc > 8) {
				throw new IOException("16-bit depth not supported: " + file); //$NON-NLS-1$
			}
			int ct = f.read();
			String colspace;
			if (ct == 0) {
				colspace = "DeviceGray"; //$NON-NLS-1$
			} else if (ct == 2) {
				colspace = "DeviceRGB"; //$NON-NLS-1$
			} else if (ct == 3) {
				colspace = "Indexed"; //$NON-NLS-1$
			} else {
				throw new IOException("Alpha channel not supported: " + file); //$NON-NLS-1$
			}
			if (f.read() != 0) {
				throw new IOException("Unknown compression method: " + file); //$NON-NLS-1$
			}
			if (f.read() != 0) {
				throw new IOException("Unknown filter method: " + file); //$NON-NLS-1$
			}
			if (f.read() != 0) {
				throw new IOException("Interlacing not supported: " + file); //$NON-NLS-1$
			}
			this._fread(f, 4);
			StringBuilder sb = new StringBuilder();
			sb.append("/DecodeParms <</Predictor 15 /Colors ").append( //$NON-NLS-1$
					ct == 2 ? 3 : 1).append(" /BitsPerComponent ").append(bpc) //$NON-NLS-1$
					.append(" /Columns ").append(w).append(">>"); //$NON-NLS-1$//$NON-NLS-2$
			String parms = sb.toString();
			// Scan chunks looking for palette, transparency and image data
			byte[] pal = null;
			byte[] trns = null;
			byte[] data = null;
			do {
				int n = this._freadint(f);
				String type = new String(this._fread(f, 4));
				if (type.equals("PLTE")) { //$NON-NLS-1$
					// Read palette
					pal = this._freadb(f, n);
					this._fread(f, 4);
				} else if (type.equals("tRNS")) { //$NON-NLS-1$
					// Read transparency info
					byte[] t = this._freadb(f, n);
					if (ct == 0) {
						trns = new byte[] { t[1] };
					} else if (ct == 2) {
						trns = new byte[] { t[1], t[3], t[5] };
					} else {
						int pos = new String(t).indexOf(0);
						if (pos != -1) {
							trns = new byte[] { (byte) pos };
						}
					}
					this._fread(f, 4);
				} else if (type.equals("IDAT")) { //$NON-NLS-1$
					// Read image data block
					data = this._freadb(f, n, data);
					this._fread(f, 4);
				} else if (type.equals("IEND")) { //$NON-NLS-1$
					break;
				} else {
					this._fread(f, n + 4);
				}
			} while (f.available() > 0);
			if (colspace.equals("Indexed") && (pal == null)) { //$NON-NLS-1$
				throw new IOException("Missing palette in " + file); //$NON-NLS-1$
			}
			Map<String, Object> image = new HashMap<String, Object>();
			image.put("w", Integer.valueOf(w)); //$NON-NLS-1$
			image.put("h", Integer.valueOf(h)); //$NON-NLS-1$
			image.put("cs", colspace); //$NON-NLS-1$
			image.put("bpc", Integer.valueOf(bpc)); //$NON-NLS-1$
			image.put("f", "FlateDecode"); //$NON-NLS-1$//$NON-NLS-2$
			image.put("parms", parms); //$NON-NLS-1$
			image.put("pal", pal); //$NON-NLS-1$
			image.put("trns", trns); //$NON-NLS-1$
			image.put("data", data); //$NON-NLS-1$
			image.put("i", Integer.valueOf(this.images.size() + 1)); //$NON-NLS-1$
			return image;
		} finally {
			f.close();
		}
	}

	protected void _putcatalog() {
		this._out("/Type /Catalog"); //$NON-NLS-1$
		this._out("/Pages 1 0 R"); //$NON-NLS-1$
		if ((this.zoomMode == null) && (this.zoomFactor > 0)) {
			this._out("/OpenAction [3 0 R /XYZ null null " //$NON-NLS-1$
					+ this.zoomFactor / 100 + "]"); //$NON-NLS-1$
		} else if (Zoom.FULLPAGE.equals(this.zoomMode)) {
			this._out("/OpenAction [3 0 R /Fit]"); //$NON-NLS-1$
		} else if (Zoom.FULLWIDTH.equals(this.zoomMode)) {
			this._out("/OpenAction [3 0 R /FitH null]"); //$NON-NLS-1$
		} else if (Zoom.REAL.equals(this.zoomMode)) {
			this._out("/OpenAction [3 0 R /XYZ null null 1]"); //$NON-NLS-1$
		}
		if (Layout.SINGLE.equals(this.layoutMode)) {
			this._out("/PageLayout /SinglePage"); //$NON-NLS-1$
		} else if (Layout.CONTINUOUS.equals(this.layoutMode)) {
			this._out("/PageLayout /OneColumn"); //$NON-NLS-1$
		} else if (Layout.TWO.equals(this.layoutMode)) {
			this._out("/PageLayout /TwoColumnLeft"); //$NON-NLS-1$
		}
	}

	protected void _putfonts() {
		for (String k : this.fonts.keySet()) {
			Font font = this.fonts.get(k);
			// Font objects
			this.fonts.get(k).setN(this.n + 1);
			Font.Type type = font.getType();
			String name = font.getName();
			if (type == Font.Type.CORE) {
				// Standard font
				this._newobj();
				this._out("<</Type /Font"); //$NON-NLS-1$
				this._out("/BaseFont /" + name); //$NON-NLS-1$
				this._out("/Subtype /Type1"); //$NON-NLS-1$
				if ((name != "Symbol") //$NON-NLS-1$
						&& (name != "ZapfDingbats")) { //$NON-NLS-1$
					this._out("/Encoding /WinAnsiEncoding"); //$NON-NLS-1$
				}
				this._out(">>"); //$NON-NLS-1$
				this._out("endobj"); //$NON-NLS-1$
			}
			// FIXME no support for embedded or user fonts!
		}
	}

	protected void _putheader() {
		this._out("%PDF-" + this.pdfVersion); //$NON-NLS-1$
	}

	protected void _putimages() {
		String filter = (this.compress) ? "/Filter /FlateDecode " : ""; //$NON-NLS-1$//$NON-NLS-2$
		// while(list(file,info)=each(this.images))
		for (String file : this.images.keySet()) {
			this._newobj();
			this.images.get(file).put("n", Integer.valueOf(this.n)); //$NON-NLS-1$
			this._out("<</Type /XObject"); //$NON-NLS-1$
			this._out("/Subtype /Image"); //$NON-NLS-1$
			this._out("/Width " //$NON-NLS-1$
					+ this.images.get(file).get("w")); //$NON-NLS-1$
			this._out("/Height " //$NON-NLS-1$
					+ this.images.get(file).get("h")); //$NON-NLS-1$
			if (this.images.get(file).get("cs") == "Indexed") {
				this
						._out("/ColorSpace [/Indexed /DeviceRGB "
								+ (((byte[]) this.images.get(file).get("pal")).length / 3 - 1)
								+ " " + (this.n + 1) + " 0 R]");
			} else {
				this._out("/ColorSpace /" + this.images.get(file).get("cs"));
				if (this.images.get(file).get("cs") == "DeviceCMYK") {
					this._out("/Decode [1 0 1 0 1 0 1 0]");
				}
			}
			this._out("/BitsPerComponent " + this.images.get(file).get("bpc"));
			if (this.images.get(file).get("f") != null) {
				this._out("/Filter /" + this.images.get(file).get("f"));
			}
			if (this.images.get(file).get("parms") != null) {
				this._out((String) this.images.get(file).get("parms"));
			}
			if (this.images.get(file).get("trns") != null) {
				byte[] trnsarr = ((byte[]) this.images.get(file).get("trns"));
				StringBuilder trns = new StringBuilder();
				trns.append("/Mask [ "); //$NON-NLS-1$
				for (int i = 0; i < trnsarr.length; i++) {
					trns.append(_stringify(trnsarr));
				}
				trns.append(']');
				this._out(trns.toString());
			}
			this._out("/Length "
					+ ((byte[]) (this.images.get(file).get("data"))).length
					+ ">>");
			try {
				this._putstream(new String((byte[]) this.images.get(file).get(
						"data"), "ISO-8859-1")); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (UnsupportedEncodingException e) {
				this._putstream(new String((byte[]) this.images.get(file).get(
						"data"))); //$NON-NLS-1$
			}
			this.images.get(file).put("data", null);
			this._out("endobj");
			// Palette
			if (this.images.get(file).get("cs") == "Indexed") {
				this._newobj();
				// pal=(this.compress) ? gzcompress(info["pal"]) : info["pal"];
				byte[] pal = (byte[]) this.images.get(file).get("pal");
				this._out("<<" + filter + "/Length " + pal.length + ">>");
				try {
					this._putstream(new String(pal, "ISO-8859-1")); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (UnsupportedEncodingException e) {
					this._putstream(new String(pal)); //$NON-NLS-1$
				}
				this._out("endobj");
			}
		}
	}

	protected void _putinfo() {
		this._out("/Producer " //$NON-NLS-1$
				+ this._textstring("Java FPDF 1.53." //$NON-NLS-1$
						+ revision));
		if (this.title != null) {
			this._out("/Title " + this._textstring(this.title)); //$NON-NLS-1$
		}
		if (this.subject != null) {
			this._out("/Subject " + this._textstring(this.subject)); //$NON-NLS-1$
		}
		if (this.author != null) {
			this._out("/Author " + this._textstring(this.author)); //$NON-NLS-1$
		}
		if (this.keywords != null) {
			this._out("/Keywords " + this._textstring(this.keywords)); //$NON-NLS-1$
		}
		if (this.creator != null) {
			this._out("/Creator " + this._textstring(this.creator)); //$NON-NLS-1$
		}
		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append("/CreationDate (D:"); //$NON-NLS-1$
		sb.append(cal.get(Calendar.YEAR));
		if (cal.get(Calendar.MONTH) < 9) {
			sb.append('0');
		}
		sb.append(cal.get(Calendar.MONTH) + 1);
		if (cal.get(Calendar.DATE) < 10) {
			sb.append('0');
		}
		sb.append(cal.get(Calendar.DATE));
		if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
			sb.append('0');
		}
		sb.append(cal.get(Calendar.HOUR_OF_DAY));
		if (cal.get(Calendar.MINUTE) < 10) {
			sb.append('0');
		}
		sb.append(cal.get(Calendar.MINUTE));
		if (cal.get(Calendar.SECOND) < 10) {
			sb.append('0');
		}
		sb.append(cal.get(Calendar.SECOND));
		sb.append(')');
		this._out(sb.toString());
		sb.delete(0, sb.length());
	}

	protected static String _stringify(final byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(256 + bytes[i]);
			sb.append(' ');
			sb.append(256 + bytes[i]);
			sb.append(' ');
		}
		return sb.toString();
	}

	protected static String _stringify(final List<byte[]> buffer) {
		int length = 0;
		for (byte[] b : buffer) {
			length += b.length;
		}
		byte[] bytes = new byte[length];
		int offset = 0;
		for (byte[] b : buffer) {
			for (int i = 0; i < b.length; i++) {
				bytes[offset + i] = b[i];
			}
			offset += b.length;
		}
		return new String(bytes);
	}

	protected void _putpages() {
		int nb = this.page;
		if (this.aliasNbPages != null) {
			// Replace number of pages
			for (int n = 1; n <= nb; n++) {
				List<byte[]> bytes = this.pages.get(Integer.valueOf(n));
				String s = _stringify(bytes).replace(this.aliasNbPages,
						String.valueOf(nb));
				bytes.clear();
				bytes.add(s.getBytes());
				this.pages.put(Integer.valueOf(n), bytes);
			}
		}
		if (Orientation.PORTRAIT.equals(this.defaultOrientation)) {
			this.wPt = this.fwPt;
			this.hPt = this.fhPt;
		} else {
			this.wPt = this.fhPt;
			this.hPt = this.fwPt;
		}
		String filter = (this.compress) ? "/Filter /FlateDecode " //$NON-NLS-1$
				: ""; //$NON-NLS-1$
		for (int n = 1; n <= nb; n++) {
			// Page
			this._newobj();
			this._out("<</Type /Page"); //$NON-NLS-1$
			this._out("/Parent 1 0 R"); //$NON-NLS-1$
			if ((this.orientationChanges.get(Integer.valueOf(n)) != null)
					&& this.orientationChanges.get(Integer.valueOf(n))
							.booleanValue()) {
				this._out(String.format(Locale.ENGLISH,
						"/MediaBox [0 0 %.2f %.2f]", //$NON-NLS-1$
						Float.valueOf(this.hPt), Float.valueOf(this.wPt)));
			}
			this._out("/Resources 2 0 R"); //$NON-NLS-1$
			if (this.pageLinks.containsKey(Integer.valueOf(n))) {
				// Links
				StringBuilder annots = new StringBuilder();
				annots.append("/Annots ["); //$NON-NLS-1$
				for (Map<Integer, Object> pl : this.pageLinks.values()) {
					annots.append("<</Type /Annot /Subtype /Link /Rect ["); //$NON-NLS-1$
					annots
							.append(String
									.format(
											Locale.ENGLISH,
											"%.2f %.2f %.2f %.2f", //$NON-NLS-1$
											pl.get(Integer.valueOf(0)), pl
													.get(Integer.valueOf(1)),
											Float.valueOf(((Float) pl
													.get(Integer.valueOf(0)))
													.floatValue()
													+ ((Float) pl.get(Integer
															.valueOf(2)))
															.floatValue()),
											Float.valueOf(((Float) pl
													.get(Integer.valueOf(1)))
													.floatValue()
													- ((Float) pl.get(Integer
															.valueOf(3)))
															.floatValue())));
					annots.append("] /Border [0 0 0] "); //$NON-NLS-1$
					if (pl.get(4) instanceof String) {
						annots.append("/A <</S /URI /URI "); //$NON-NLS-1$
						annots.append(this._textstring((String) pl.get(4)));
						annots.append(">>>>"); //$NON-NLS-1$
					} else {
						Map<Integer, Float> l = this.links.get(pl.get(4));
						float h = (this.orientationChanges.get(l.get(0))) ? this.wPt
								: this.hPt;
						annots.append(String.format(Locale.ENGLISH,
								"/Dest [%d 0 R /XYZ 0 %.2f null]>>", //$NON-NLS-1$
								Float.valueOf(1 + 2 * l.get(0)), Float
										.valueOf(h - l.get(1) * this.k)));
					}
				}
				annots.append("]"); //$NON-NLS-1$
				this._out(annots.toString());
			}
			this._out("/Contents " //$NON-NLS-1$
					+ (this.n + 1) + " 0 R>>"); //$NON-NLS-1$
			this._out("endobj"); //$NON-NLS-1$
			// Page content
			// FIXME implement gz compression
			// String p = (this.compress) ? gzcompress(this.pages.get(n))
			// : this.pages.get(n);
			String p = _stringify(this.pages.get(n));
			this._newobj();
			this._out("<<" + filter + "/Length " //$NON-NLS-1$//$NON-NLS-2$
					+ p.length() + ">>"); //$NON-NLS-1$
			this._putstream(p);
			this._out("endobj"); //$NON-NLS-1$
		}
		// Pages root
		this.offsets.put(1, this._length(this.buffer));
		this._out("1 0 obj"); //$NON-NLS-1$
		this._out("<</Type /Pages"); //$NON-NLS-1$
		StringBuilder kids = new StringBuilder();
		kids.append("/Kids ["); //$NON-NLS-1$
		for (int i = 0; i < nb; i++) {
			kids.append(3 + 2 * i);
			kids.append(" 0 R "); //$NON-NLS-1$
		}
		kids.append("]"); //$NON-NLS-1$
		this._out(kids.toString());
		this._out("/Count " + nb); //$NON-NLS-1$
		this._out(String.format(Locale.ENGLISH, "/MediaBox [0 0 %.2f %.2f]", //$NON-NLS-1$
				this.wPt, this.hPt));
		this._out(">>"); //$NON-NLS-1$
		this._out("endobj"); //$NON-NLS-1$
	}

	protected void _putresourcedict() {
		this._out("/ProcSet [/PDF /Text /ImageB /ImageC /ImageI]"); //$NON-NLS-1$
		this._out("/Font <<"); //$NON-NLS-1$
		StringBuilder s = new StringBuilder();
		for (Font font : this.fonts.values()) {
			s.append("/F"); //$NON-NLS-1$
			s.append(font.getI());
			s.append(' ');
			s.append(font.getN());
			s.append(" 0 R"); //$NON-NLS-1$
			this._out(s.toString());
			s.delete(0, s.length());
		}
		this._out(">>"); //$NON-NLS-1$
		this._out("/XObject <<"); //$NON-NLS-1$
		this._putxobjectdict();
		this._out(">>"); //$NON-NLS-1$
	}

	protected void _putresources() {
		this._putfonts(); // FIXME no support for embedded fonts
		this._putimages();
		// Resource dictionary
		this.offsets.put(Integer.valueOf(2), Integer.valueOf(this
				._length(this.buffer)));
		this._out("2 0 obj"); //$NON-NLS-1$
		this._out("<<"); //$NON-NLS-1$
		this._putresourcedict();
		this._out(">>"); //$NON-NLS-1$
		this._out("endobj"); //$NON-NLS-1$
	}

	protected void _putstream(final String s) {
		this._out("stream"); //$NON-NLS-1$
		this._out(s);
		this._out("endstream"); //$NON-NLS-1$
	}

	protected void _puttrailer() {
		this._out("/Size " + (this.n + 1)); //$NON-NLS-1$
		this._out("/Root " + this.n + " 0 R"); //$NON-NLS-1$//$NON-NLS-2$
		this._out("/Info " + (this.n - 1) + " 0 R"); //$NON-NLS-1$//$NON-NLS-2$
	}

	protected void _putxobjectdict() {
		StringBuilder s = new StringBuilder();
		for (Map<String, Object> image : this.images.values()) {
			s.append("/I"); //$NON-NLS-1$
			s.append(image.get("i")); //$NON-NLS-1$
			s.append(' ');
			s.append(image.get("n")); //$NON-NLS-1$
			s.append(" 0 R"); //$NON-NLS-1$
			this._out(s.toString());
			s.delete(0, s.length());
		}
	}

	protected String _textstring(final String s) {
		return "(" + this._escape(s) + ")"; //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Accept automatic page break or not
	 * 
	 * @return whether an automatic page break is accepted or not.
	 */
	public boolean acceptPageBreak() {
		return this.autoPageBreak;
	}

	/**
	 * Creates a new internal link.
	 * 
	 * @return the link's identifier.
	 */
	public int addLink() {
		int n = this.links.size() + 1;
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		map.put(Integer.valueOf(0), Float.valueOf(0));
		this.links.put(Integer.valueOf(n), map);
		return n;
	}

	/**
	 * Starts a new page with the default orientation.
	 * 
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void addPage() throws IOException {
		this.addPage(this.defaultOrientation);
	}

	/**
	 * Starts a new page.
	 * 
	 * @param orientation
	 *            the page orientation
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void addPage(final Orientation orientation) throws IOException {
		if (this.state == 0) {
			this.open();
		}
		String family = this.fontFamily;
		StringBuilder sb = new StringBuilder();
		Set<FontStyle> style = this.fontStyle;
		float size = this.fontSizePt;
		float lw = this.lineWidth;
		String dc = this.drawColor;
		String fc = this.fillColor;
		String tc = this.textColor;
		boolean cf = this.colorFlag;
		if (this.page > 0) {
			// Page footer
			this.inFooter = true;
			this.Footer();
			this.inFooter = false;
			// Close page
			this._endpage();
		}
		// Start new page
		if (orientation != null) {
			this._beginpage(orientation);
		} else {
			this._beginpage(this.defaultOrientation);
		}
		// Set line cap style to square
		this._out("2 J"); //$NON-NLS-1$
		// Set line width
		this.lineWidth = lw;
		this._out(String.format(Locale.ENGLISH,
				"%.2f w", Float.valueOf(lw * this.k))); //$NON-NLS-1$
		// Set font
		if (family != null) {
			this.setFont(family, style, size);
		}
		// Set colors
		this.drawColor = dc;
		if (dc != "0 G") { //$NON-NLS-1$
			this._out(dc);
		}
		this.fillColor = fc;
		if (fc != "0 g") { //$NON-NLS-1$
			this._out(fc);
		}
		this.textColor = tc;
		this.colorFlag = cf;
		// Page header
		this.Header();
		// Restore line width
		if (this.lineWidth != lw) {
			this.lineWidth = lw;
			this._out(String.format(Locale.ENGLISH,
					"%.2f w", Float.valueOf(lw * this.k))); //$NON-NLS-1$
		}
		// Restore font
		if (family != null) {
			this.setFont(family, style, size);
		}
		// Restore colors
		if (this.drawColor != dc) {
			this.drawColor = dc;
			this._out(dc);
		}
		if (this.fillColor != fc) {
			this.fillColor = fc;
			this._out(fc);
		}
		this.textColor = tc;
		this.colorFlag = cf;
	}

	/**
	 * Sets the alias for total number of pages to <code>{nb}</code>.
	 */
	public void aliasNbPages() {
		this.aliasNbPages("{nb}"); //$NON-NLS-1$
	}

	/**
	 * Defines an alias for total number of pages
	 * 
	 * @param alias
	 *            the alias
	 */
	public void aliasNbPages(final String alias) {
		if (alias == null) {
			this.aliasNbPages = "{nb}"; //$NON-NLS-1$
		}
		this.aliasNbPages = alias;
	}

	/**
	 * Output an empty cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, final float h) throws IOException {
		this.Cell(w, h, null, null, null, null, false, 0);
	}

	/**
	 * Output a cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, final float h, final String txt)
			throws IOException {
		this.Cell(w, h, txt, null, null, null, false, 0);
	}

	/**
	 * Output a cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param align
	 *            alignment of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, final float h, final String txt,
			final Alignment align) throws IOException {
		this.Cell(w, h, txt, null, null, align, false, 0);
	}

	/**
	 * Output a cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param ln
	 *            where the current position should go after the call
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, final float h, final String txt,
			final Position ln) throws IOException {
		this.Cell(w, h, txt, null, ln, null, false, 0);
	}

	/**
	 * Output a cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param ln
	 *            where the current position should go after the call
	 * @param align
	 *            alignment of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, final float h, final String txt,
			final Position ln, final Alignment align) throws IOException {
		this.Cell(w, h, txt, null, ln, align, false, 0);
	}

	/**
	 * Output a cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param link
	 *            link identifier of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, final float h, final String txt,
			final int link) throws IOException {
		this.Cell(w, h, txt, null, null, null, false, link);
	}

	/**
	 * Output a cell.
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param border
	 *            the border style of the cell
	 * @param ln
	 *            where the current position should go after the call
	 * @param align
	 *            alignment of the cell
	 * @param fill
	 *            whether to fill the cell
	 * @param link
	 *            link identifier of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void Cell(final float w, float h, final String txt,
			final Borders border, final Position ln, final Alignment align,
			final boolean fill, final int link) throws IOException {
		float k = this.k;
		float x;
		float y;
		if ((this.y + h > this.pageBreakTrigger) && !this.inFooter
				&& this.acceptPageBreak()) {
			// Automatic page break
			x = this.x;
			float ws = this.ws;
			if (ws > 0) {
				this.ws = 0;
				this._out("0 Tw"); //$NON-NLS-1$
			}
			this.addPage(this.currentOrientation);
			this.x = x;
			if (ws > 0) {
				this.ws = ws;
				this._out(String.format(Locale.ENGLISH, "%.3f Tw", //$NON-NLS-1$
						Float.valueOf(ws * k)));
			}
		}
		float w1 = (w == 0) ? this.w - this.rMargin - this.x : w;
		StringBuilder s = new StringBuilder();
		if ((fill) || ((border != null) && border.getAll())) {
			char op;
			if (fill) {
				op = ((border != null) && border.getAll()) ? 'B' : 'f';
			} else {
				op = 'S';
			}
			s.append(String.format(
					Locale.ENGLISH,
					"%.2f %.2f %.2f %.2f re %s ", //$NON-NLS-1$
					Float.valueOf(this.x * k), Float.valueOf((this.h - this.y)
							* k), Float.valueOf(w1 * k), Float.valueOf(-h * k),
					Float.valueOf(op)));
		}
		if (border != null) {
			x = this.x;
			y = this.y;
			if (border.getLeft()) {
				s.append(String.format(
						Locale.ENGLISH,
						"%.2f %.2f m %.2f %.2f l S ", //$NON-NLS-1$
						Float.valueOf(x * k), Float.valueOf((this.h - y) * k),
						Float.valueOf(x * k), Float.valueOf((this.h - (y + h))
								* k)));
			}
			if (border.getTop()) {
				s.append(String.format(
						Locale.ENGLISH,
						"%.2f %.2f m %.2f %.2f l S ", //$NON-NLS-1$
						Float.valueOf(x * k), Float.valueOf((this.h - y) * k),
						Float.valueOf((x + w1) * k), Float.valueOf((this.h - y)
								* k)));
			}
			if (border.getRight()) {
				s.append(String.format(Locale.ENGLISH,
						"%.2f %.2f m %.2f %.2f l S ", //$NON-NLS-1$
						Float.valueOf((x + w1) * k), Float.valueOf((this.h - y)
								* k), Float.valueOf((x + w1) * k), Float
								.valueOf((this.h - (y + h)) * k)));
			}
			if (border.getBottom()) {
				s.append(String.format(Locale.ENGLISH,
						"%.2f %.2f m %.2f %.2f l S ", //$NON-NLS-1$
						Float.valueOf(x * k), Float.valueOf((this.h - (y + h))
								* k), Float.valueOf((x + w1) * k), Float
								.valueOf((this.h - (y + h)) * k)));
			}
		}
		if (txt != null) {
			float dx;
			if (Alignment.RIGHT.equals(align)) {
				dx = w1 - this.cMargin - this.getStringWidth(txt);
			} else if (Alignment.CENTER.equals(align)) {
				dx = (w1 - this.getStringWidth(txt)) / 2;
			} else {
				dx = this.cMargin;
			}
			if (this.colorFlag) {
				s.append("q ").append(this.textColor).append(' '); //$NON-NLS-1$
			}
			String txt2 = txt.replace("\\", "\\\\") //$NON-NLS-1$//$NON-NLS-2$
					.replace("(", "\\(") //$NON-NLS-1$//$NON-NLS-2$
					.replace(")", "\\)"); //$NON-NLS-1$//$NON-NLS-2$
			s
					.append(String
							.format(Locale.ENGLISH,
									"BT %.2f %.2f Td (%s) Tj ET", //$NON-NLS-1$
									Float.valueOf((this.x + dx) * k), Float
											.valueOf((this.h - (this.y + .5f
													* h + .3f * this.fontSize))
													* k), txt2));
			if (this.underline) {
				s.append(' ');
				s.append(this._dounderline(this.x + dx, this.y + .5f * h + .3f
						* this.fontSize, txt));
			}
			if (this.colorFlag) {
				s.append(" Q"); //$NON-NLS-1$
			}
			if (link > 0) {
				this.Link(this.x + dx, this.y + .5f * h - .5f * this.fontSize,
						this.getStringWidth(txt), this.fontSize, link);
			}
		}
		if (s.length() > 0) {
			this._out(s.toString());
		}
		this.lastH = h;
		if ((ln != null) && !Position.RIGHTOF.equals(ln)) {
			// Go to next line
			this.y += h;
			if (Position.NEXTLINE.equals(ln)) {
				this.x = this.lMargin;
			}
		} else {
			this.x += w1;
		}
	}

	/**
	 * Cell with horizontal scaling if text is too wide
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param border
	 *            the borders of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @param align
	 *            the alignment of the cell text
	 * @param fill
	 *            whether to fill the cell background
	 * @param link
	 *            link identifier of the cell
	 * @param scale
	 *            the scaling method to use
	 * @param force
	 *            whether to enforce scaling even if not necessary
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFit(final float w, final float h, final String txt,
			final Borders border, final Position ln, final Alignment align,
			final boolean fill, final int link, final ScaleMode scale,
			final boolean force) throws IOException {
		// Get string width
		float str_width = this.getStringWidth(txt);

		// Calculate ratio to fit cell
		float w1 = (w == 0) ? this.w - this.rMargin - this.x : w;
		float ratio = (w1 - this.cMargin * 2) / str_width;

		boolean fit = ((ratio < 1) || ((ratio > 1) && force));
		if (fit) {
			switch (scale) {

			// Character spacing
			case CHARSPACE:
				// Calculate character spacing in points
				float char_space = (w1 - this.cMargin * 2 - str_width)
						/ Math.max(txt.length() - 1, 1) * this.k;
				// Set character spacing
				this._out(String.format(Locale.ENGLISH, "BT %.2f Tc ET", Float //$NON-NLS-1$
						.valueOf(char_space)));
				break;

			// Horizontal scaling
			case HORIZONTAL:
				// Calculate horizontal scaling
				float horiz_scale = ratio * 100.0f;
				// Set horizontal scaling
				this._out(String.format(Locale.ENGLISH, "BT %.2f Tz ET", Float //$NON-NLS-1$
						.valueOf(horiz_scale)));
				break;

			}
			// Override user alignment (since text will fill up cell)
			this.Cell(w1, h, txt, border, ln, null, fill, link);
		} else {
			// Pass on to Cell method
			this.Cell(w1, h, txt, border, ln, align, fill, link);
		}

		// Reset character spacing/horizontal scaling
		if (fit) {
			this._out("BT " + (ScaleMode.CHARSPACE.equals(scale) //$NON-NLS-1$
			? "0 Tc"//$NON-NLS-1$
					: "100 Tz") //$NON-NLS-1$
					+ " ET");//$NON-NLS-1$

		}
	}

	/**
	 * Cell with horizontal spacing only if necessary
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitScale(final float w, final float h, final String txt,
			final Position ln) throws IOException {
		this.CellFitScale(w, h, txt, null, ln, null, false, 0);
	}

	/**
	 * Cell with horizontal scaling only if necessary
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param border
	 *            the borders of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @param align
	 *            the alignment of the cell text
	 * @param fill
	 *            whether to fill the cell background
	 * @param link
	 *            link identifier of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitScale(final float w, final float h, final String txt,
			final Borders border, final Position ln, final Alignment align,
			final boolean fill, final int link) throws IOException {
		this.CellFit(w, h, txt, border, ln, align, fill, link,
				ScaleMode.HORIZONTAL, false);
	}

	/**
	 * Cell with horizontal spacing always
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitScaleForce(final float w, final float h,
			final String txt, final Position ln) throws IOException {
		this.CellFitScaleForce(w, h, txt, null, ln, null, false, 0);
	}

	/**
	 * Cell with horizontal scaling always
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param border
	 *            the borders of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @param align
	 *            the alignment of the cell text
	 * @param fill
	 *            whether to fill the cell background
	 * @param link
	 *            link identifier of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitScaleForce(final float w, final float h,
			final String txt, final Borders border, final Position ln,
			final Alignment align, final boolean fill, final int link)
			throws IOException {
		this.CellFit(w, h, txt, border, ln, align, fill, link,
				ScaleMode.HORIZONTAL, true);
	}

	/**
	 * Cell with character spacing only if necessary
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitSpace(final float w, final float h, final String txt,
			final Position ln) throws IOException {
		this.CellFitSpace(w, h, txt, null, ln, null, false, 0);
	}

	/**
	 * Cell with character spacing only if necessary
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param border
	 *            the borders of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @param align
	 *            the alignment of the cell text
	 * @param fill
	 *            whether to fill the cell background
	 * @param link
	 *            link identifier of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitSpace(final float w, final float h, final String txt,
			final Borders border, final Position ln, final Alignment align,
			final boolean fill, final int link) throws IOException {
		this.CellFit(w, h, txt, border, ln, align, fill, link,
				ScaleMode.CHARSPACE, false);
	}

	/**
	 * Cell with character spacing always
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param border
	 *            the borders of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @param align
	 *            the alignment of the cell text
	 * @param fill
	 *            whether to fill the cell background
	 * @param link
	 *            link identifier of the cell
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitSpaceForce(final float w, final float h,
			final String txt, final Borders border, final Position ln,
			final Alignment align, final boolean fill, final int link)
			throws IOException {
		// Same as calling CellFit directly
		this.CellFit(w, h, txt, border, ln, align, fill, link,
				ScaleMode.CHARSPACE, true);
	}

	/**
	 * Cell with character spacing always
	 * 
	 * @param w
	 *            width of the cell
	 * @param h
	 *            height of the cell
	 * @param txt
	 *            text of the cell
	 * @param ln
	 *            where the pointer should go after the call
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void CellFitSpaceForce(final float w, final float h,
			final String txt, final Position ln) throws IOException {
		this.CellFitSpaceForce(w, h, txt, null, ln, null, false, 0);
	}

	/**
	 * Terminate document
	 * 
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void close() throws IOException {
		if (this.state == 3) {
			return;
		}
		if (this.page == 0) {
			this.addPage(null);
		}
		// Page footer
		this.inFooter = true;
		this.Footer();
		this.inFooter = false;
		// Close page
		this._endpage();
		// Close document
		this._enddoc();
	}

	/**
	 * Method to be called when printing the footer. This should be overridden
	 * in your own class.
	 */
	public abstract void Footer();

	/**
	 * Get width of a string in the current font
	 * 
	 * @param s
	 *            the string
	 * @return the width of that string.
	 */
	public float getStringWidth(final String s) {
		float w = 0f;
		float l = s.length();
		for (int i = 0; i < l; i++) {
			w += this.currentFont.getCw().get(s.charAt(i));
		}
		return w * this.fontSize / 1000;
	}

	/**
	 * Get x position
	 * 
	 * @return the x position.
	 */
	public float getX() {
		return this.x;
	}

	/**
	 * Get y position
	 * 
	 * @return the y position.
	 */
	public float getY() {
		return this.y;
	}

	/**
	 * Method to be called when printing the header. This method should be
	 * overriden in your own class.
	 */
	public abstract void Header();

	/**
	 * Put an image on the page
	 * 
	 * @param file
	 *            name of the file to be inserted
	 * @param coords
	 *            the x/y coordinate of the image in the document
	 * @param w
	 *            the width of the image
	 * @param h
	 *            the height of the image
	 * @param type
	 *            the type of image in the file
	 * @param link
	 *            link identifier for the image
	 * @throws IOException
	 */
	public void Image(final String file, final Coordinate coords,
			final float w, final float h, final ImageType type, final int link)
			throws IOException {
		Map<String, Object> info = null;
		if (this.images.get(file) == null) {
			// First use of image, get info
			ImageType type1;
			if (type == null) {
				int pos = file.indexOf('.');
				if (pos == -1) {
					throw new IOException(
							"Image file has no extension and no type was specified: " //$NON-NLS-1$
									+ file);
				}
				type1 = ImageType
						.valueOf(file.substring(pos + 1).toUpperCase());
			} else {
				type1 = type;
			}
			if (ImageType.PNG.equals(type1)) {
				info = this._parsepng(new File(file));
			} else if (ImageType.JPEG.equals(type1)) {
				info = this._parsejpg(new File(file));
			} else {
				throw new IOException("Image type not supported."); //$NON-NLS-1$
			}
			// FIXME no support for other formats
			this.images.put(file, info);
		} else {
			info = this.images.get(file);
		}
		// Automatic width and height calculation if needed
		float w1 = w;
		float h1 = h;
		if ((w == 0) && (h == 0)) {
			// Put image at 72 dpi
			w1 = ((Integer) info.get("w")).floatValue() / this.k; //$NON-NLS-1$
			h1 = ((Integer) info.get("h")).floatValue() / this.k; //$NON-NLS-1$
		} else if (w == 0) {
			w1 = h * ((Integer) info.get("w")).floatValue() //$NON-NLS-1$
					/ ((Integer) info.get("h")).floatValue(); //$NON-NLS-1$
		} else if (h == 0) {
			h1 = w * ((Integer) info.get("h")).floatValue() //$NON-NLS-1$
					/ ((Integer) info.get("w")).floatValue(); //$NON-NLS-1$
		}
		this._out(String.format(
				Locale.ENGLISH,
				"q %.2f 0 0 %.2f %.2f %.2f cm /I%d Do Q", //$NON-NLS-1$
				Float.valueOf(w1 * this.k), Float.valueOf(h1 * this.k), Float
						.valueOf(coords.getX() * this.k), Float
						.valueOf((this.h - (coords.getY() + h1)) * this.k),
				info.get("i"))); //$NON-NLS-1$
		if (link > 0) {
			this.Link(coords.getX(), coords.getY(), w1, h1, link);
		}
	}

	/**
	 * Sets the line style.
	 * 
	 * @param style
	 *            the line style
	 */
	public void setLineStyle(final LineStyle style) {
		if (style.getWidth() != null) {
			float width_prev = this.lineWidth;
			this.setLineWidth(style.getWidth().floatValue());
			this.lineWidth = width_prev;
		}
		if (style.getCap() != null) {
			this._out(style.getCap().toString() + " J"); //$NON-NLS-1$
		}
		if (style.getJoin() != null) {
			this._out(style.getJoin().toString() + " j"); //$NON-NLS-1$
		}
		if (style.getDashes() != null) {
			StringBuilder sb = new StringBuilder();
			for (float dash : style.getDashes()) {
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append(String.format(Locale.ENGLISH,
						"%.2f", Float.valueOf(dash))); //$NON-NLS-1$
			}
			this._out(String.format(Locale.ENGLISH,
					"[%s] %.2f d", sb.toString(), Float //$NON-NLS-1$
							.valueOf(style.getPhase())));
		}
		if (style.getColor() != null) {
			this.setDrawColor(style.getColor());
		}
	}

	/**
	 * Draw a line.
	 * 
	 * @param start
	 *            the start of the line
	 * @param end
	 *            the end of the line
	 */
	public void Line(final Coordinate start, final Coordinate end) {
		this._out(String.format(Locale.ENGLISH, "%.2f %.2f m %.2f %.2f l S", //$NON-NLS-1$
				Float.valueOf(start.getX() * this.k), Float
						.valueOf((this.h - start.getY()) * this.k), Float
						.valueOf(end.getX() * this.k), Float
						.valueOf((this.h - end.getY()) * this.k)));
	}

	/** Put a link on the page */
	public void Link(final float x, final float y, final float w,
			final float h, final int link) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(Integer.valueOf(0), Float.valueOf(x * this.k));
		map.put(Integer.valueOf(1), Float.valueOf(this.hPt - y * this.k));
		map.put(Integer.valueOf(2), Float.valueOf(w * this.k));
		map.put(Integer.valueOf(3), Float.valueOf(h * this.k));
		map.put(Integer.valueOf(4), Float.valueOf(link));
		this.pageLinks.put(Integer.valueOf(this.page), map);
	}

	/** Line feed; default value is last cell height */
	public void Ln() {
		this.x = this.lMargin;
		this.y += this.lastH;
	}

	/** Line feed; default value is last cell height */
	public void Ln(final float h) {
		this.y += h;
	}

	/**
	 * Output text with automatic or explicit line breaks.
	 * 
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 * @param txt
	 *            the text
	 * @throws IOException
	 */
	public void MultiCell(final float w, final float h, final String txt)
			throws IOException {
		this.MultiCell(w, h, txt, null, null, false);
	}

	/**
	 * Output text with automatic or explicit line breaks.
	 * 
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void MultiCell(final float w, final float h, final String txt,
			final Borders border, final Alignment align, final boolean fill)
			throws IOException {
		float w1 = (w == 0) ? this.w - this.rMargin - this.x : w;
		Charwidths cw = this.currentFont.getCw();
		float wmax = (w1 - 2 * this.cMargin) * 1000 / this.fontSize;
		String s = (txt == null) ? "" : txt.replace("\r", ""); //$NON-NLS-1$//$NON-NLS-2$
		int nb = s.length();
		if ((nb > 0) && (s.charAt(nb - 1) == '\n')) {
			nb--;
		}
		Borders b = null;
		Borders b2 = null;
		if (border != null) {
			if (border.getAll()) {
				b = new Borders(true, true, true, false);
				b2 = new Borders(true, false, true, false);
			} else {
				b = new Borders(border.getLeft(), border.getTop(), border
						.getRight(), false);
				b2 = new Borders(border.getLeft(), false, border.getRight(),
						false);
			}
		}
		int sep = -1;
		int i = 0;
		int j = 0;
		int l = 0;
		int ls = 0;
		int ns = 0;
		int nl = 1;
		while (i < nb) {
			// Get next character
			char c = s.charAt(i);
			if (c == '\n') {
				// Explicit line break
				if (this.ws > 0) {
					this.ws = 0;
					this._out("0 Tw"); //$NON-NLS-1$
				}
				this.Cell(w1, h, s.substring(j, i), b, Position.BELOW, align,
						fill, 0);
				i++;
				sep = -1;
				j = i;
				l = 0;
				ns = 0;
				nl++;
				if ((border != null) && (nl == 2)) {
					b = b2;
				}
				continue;
			}
			if (c == ' ') {
				sep = i;
				ls = l;
				ns++;
			}
			l += cw.get(c);
			if (l > wmax) {
				// Automatic line break
				if (sep == -1) {
					if (i == j) {
						i++;
					}
					if (this.ws > 0) {
						this.ws = 0;
						this._out("0 Tw"); //$NON-NLS-1$
					}
					this.Cell(w1, h, s.substring(j, i), b, Position.BELOW,
							align, fill, 0);
				} else {
					if ((align == null) || Alignment.JUSTIFIED.equals(align)) {
						this.ws = (ns > 1) ? (wmax - ls) / 1000 * this.fontSize
								/ (ns - 1) : 0;
						this._out(String.format(Locale.ENGLISH, "%.3f Tw", //$NON-NLS-1$
								this.ws * this.k));
					}
					this.Cell(w1, h, s.substring(j, sep), b, Position.BELOW,
							align, fill, 0);
					i = sep + 1;
				}
				sep = -1;
				j = i;
				l = 0;
				ns = 0;
				nl++;
				if ((border != null) && (nl == 2)) {
					b = b2;
				}
			} else {
				i++;
			}
		}
		// Last chunk
		if (this.ws > 0) {
			this.ws = 0;
			this._out("0 Tw"); //$NON-NLS-1$
		}
		if ((border != null) && border.getBottom()) {
			b = new Borders(b.getLeft(), b.getTop(), b.getRight(), true);
		}
		this.Cell(w1, h, s.substring(j, i), b, Position.BELOW, align, fill, 0);
		this.x = this.lMargin;
	}

	/** Begin document */
	public void open() {
		this.state = 1;
	}

	/**
	 * Output PDF as String
	 * 
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public String output() throws IOException {
		// Finish document if necessary
		if (this.state < 3) {
			this.close();
		}
		return _stringify(this.buffer);
	}

	/** Output PDF to local file */
	public void output(final File file) throws IOException {
		// Finish document if necessary
		if (this.state < 3) {
			this.close();
		}
		OutputStream out = new FileOutputStream(file);
		for (byte[] bytes : this.buffer) {
			out.write(bytes);
		}
		out.close();
	}

	/** Output PDF to stream */
	public void output(final OutputStream out) throws IOException {
		// Finish document if necessary
		if (this.state < 3) {
			this.close();
		}
		for (byte[] bytes : this.buffer) {
			out.write(bytes);
		}
	}

	/**
	 * Gets the current page number.
	 * 
	 * @return the current page number.
	 */
	public int pageNo() {
		return this.page;
	}

	/**
	 * Draws a rectangle.
	 * 
	 * @param coords
	 *            the starting coordinates
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 * @param mode
	 *            the drawing mode
	 */
	public void Rect(final Coordinate coords, final float w, float h,
			final DrawMode mode) {
		if (mode != null) {
			this._out(String.format(
					Locale.ENGLISH,
					"%.2f %.2f %.2f %.2f re %s", //$NON-NLS-1$
					Float.valueOf(coords.getX() * this.k), Float
							.valueOf((this.h - coords.getY()) * this.k), Float
							.valueOf(w * this.k), Float.valueOf(-h * this.k),
					mode.getOp()));
		}
	}

	/**
	 * Draws a bezier curve.
	 * 
	 * @param start
	 *            the starting coordinates
	 * @param point1
	 *            the first control coordinates
	 * @param point2
	 *            the second control coordinates
	 * @param end
	 *            the ending coordinates
	 * @param mode
	 *            the drawing mode
	 */
	public void Curve(final Coordinate start, final Coordinate point1,
			final Coordinate point2, final Coordinate end, final DrawMode mode) {
		if (mode != null) {
			this._Point(start);
			this._Curve(point1, point2, end);
			this._out(String.valueOf(mode.getOp()));
		}
	}

	/**
	 * Draws an ellipse.
	 * 
	 * @param coords
	 *            the base coordinates
	 * @param rx
	 *            the horizontal radius
	 * @param ry
	 *            the vertical radius
	 * @param angle
	 *            the orientation angle
	 * @param mode
	 *            the drawing mode
	 * @param segments
	 *            the number of segments
	 */
	public void Ellipse(final Coordinate coords, final float rx,
			final float ry, final double angle, final DrawMode mode,
			final int segments) {
		this.Ellipse(coords, rx, ry, angle, 0, 360, mode, segments);
	}

	/**
	 * Draws an ellipse.
	 * 
	 * @param coords
	 *            the base coordinates
	 * @param rx
	 *            the horizontal radius
	 * @param ry
	 *            the vertical radius
	 * @param angle
	 *            the orientation angle
	 * @param aStart
	 *            the starting angle
	 * @param aFinish
	 *            the finishing angle
	 * @param mode
	 *            the drawing mode
	 * @param segments
	 *            the number of segments
	 */
	public void Ellipse(final Coordinate coords, final float rx,
			final float ry, final double angle, final double aStart,
			final double aFinish, final DrawMode mode, final int segments) {
		if ((rx > 0) && (mode != null)) {
			float ry1 = ry;
			float rx1 = rx;
			float x0 = coords.getX();
			float y0 = coords.getY();
			int segs = segments;
			if (ry <= 0) {
				ry1 = rx;
			}
			rx1 *= this.k;
			ry1 *= this.k;
			if (segs < 2) {
				segs = 2;
			}
			double aStartR = Math.toRadians(aStart);
			double aFinishR = Math.toRadians(aFinish);
			double aTotal = aFinishR - aStartR;
			double dt = aTotal / segs;
			double dtm = dt / 3;
			x0 = x0 * this.k;
			y0 = (this.h - y0) * this.k;
			if (angle != 0) {
				double a = -Math.toRadians(angle);
				this._out(String.format(
						Locale.ENGLISH,
						"q %.2f %.2f %.2f %.2f %.2f %.2f cm", //$NON-NLS-1$
						Math.cos(a), -1 * Math.sin(a), Math.sin(a),
						Math.cos(a), x0, y0));
				x0 = 0;
				y0 = 0;
			}
			double t1 = aStartR;
			double a0 = x0 + (rx * Math.cos(t1));
			double b0 = y0 + (ry * Math.sin(t1));
			double c0 = -rx * Math.sin(t1);
			double d0 = ry * Math.cos(t1);
			double a1, b1, c1, d1;
			this._Point(new Coordinate(a0 / this.k, this.h - (b0 / this.k)));
			for (int i = 1; i < segs; i++) {
				t1 = (i * dt) + aStartR;
				a1 = x0 + (rx * Math.cos(t1));
				b1 = y0 + (ry * Math.sin(t1));
				c1 = -rx * Math.sin(t1);
				d1 = ry * Math.cos(t1);
				this._Curve(new Coordinate((a0 + (c0 * dtm)) / this.k, this.h
						- ((b0 + (d0 * dtm)) / this.k)), new Coordinate(
						(a1 - (c1 * dtm)) / this.k, this.h
								- ((b1 - (d1 * dtm)) / this.k)),
						new Coordinate(a1 / this.k, this.h - (b1 / this.k)));
				a0 = a1;
				b0 = b1;
				c0 = c1;
				d0 = d1;
			}
			this._out(String.valueOf(mode.getOp()));
			if (angle != 0) {
				this._out("Q"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Draws a circle.
	 * 
	 * @param coords
	 *            the base coordinates
	 * @param r
	 *            the radius
	 * @param mode
	 *            the drawing mode
	 * @param segments
	 *            the number of segments
	 */
	public void Circle(final Coordinate coords, final float r,
			final DrawMode mode, final int segments) {
		this.Circle(coords, r, 0, 360, mode, segments);
	}

	/**
	 * Draws a circle.
	 * 
	 * @param coords
	 *            the base coordinates
	 * @param r
	 *            the radius
	 * @param aStart
	 *            the starting angle
	 * @param aFinish
	 *            the finishing angle
	 * @param mode
	 *            the drawing mode
	 * @param segments
	 *            the number of segments
	 */
	public void Circle(final Coordinate coords, final float r,
			final double aStart, final double aFinish, final DrawMode mode,
			final int segments) {
		this.Ellipse(coords, r, 0, 0, aStart, aFinish, mode, segments);
	}

	/**
	 * Draws a polygon.
	 * 
	 * @param coords
	 *            the coordinates of the polygon's vertices
	 * @param mode
	 *            the drawing mode
	 */
	public void Polygon(final Coordinate[] coords, final DrawMode mode) {
		if (mode != null) {
			this._Point(coords[0]);
			for (int i = 1; i < coords.length; i++) {
				this._Line(coords[i]);
			}
			this._Line(coords[0]);
			this._out(String.valueOf(mode.getOp()));
		}
	}

	/**
	 * Draws a regular polygon.
	 * 
	 * @param coords
	 *            the base coordinates
	 * @param r
	 *            the radius
	 * @param sides
	 *            the number of sides
	 * @param angle
	 *            the orientation angle
	 * @param mode
	 *            the drawing mode
	 */
	public void RegularPolygon(final Coordinate coords, final float r,
			final int sides, final double angle, final DrawMode mode) {
		int sides1 = (sides < 3) ? 3 : sides;
		Coordinate[] p = new Coordinate[sides1];
		for (int i = 0; i < sides1; i++) {
			double a = angle + (i * 360 / sides);
			double a_rad = Math.toRadians(a);
			p[i] = new Coordinate(coords.getX() + (r * Math.sin(a_rad)), coords
					.getY()
					+ (r * Math.cos(a_rad)));
		}
		this.Polygon(p, mode);
	}

	/**
	 * Draws a star polygon.
	 * 
	 * @param coords
	 *            the base coordinates
	 * @param r
	 *            the radius
	 * @param vertices
	 *            the number of vertices
	 * @param gaps
	 *            the number of gaps
	 * @param angle
	 *            the orientation angle
	 * @param mode
	 *            the drawing mode
	 */
	public void StarPolygon(final Coordinate coords, final float r,
			final int vertices, final int gaps, final double angle,
			final DrawMode mode) {
		int nv = (vertices < 2) ? 2 : vertices;
		Coordinate[] p2 = new Coordinate[nv];
		boolean[] visited = new boolean[nv];
		for (int i = 0; i < nv; i++) {
			double a = angle + (i * 360 / nv);
			double a_rad = Math.toRadians(a);
			p2[i] = new Coordinate(coords.getX() + (r * Math.sin(a_rad)),
					coords.getY() + (r * Math.cos(a_rad)));
			visited[i] = false;
		}
		Coordinate[] p = new Coordinate[nv];
		int i = 0;
		do {
			p[i] = p2[i];
			i += gaps;
			i %= nv;
		} while (!visited[i]);
		this.Polygon(p, mode);
	}

	/**
	 * Draws a rounded rectangle.
	 * 
	 * @param coords
	 *            the base coordinates.
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 * @param r
	 *            the corner radius
	 * @param mode
	 *            the drawing mode
	 */
	public void RoundedRect(final Coordinate coords, final float w,
			final float h, final float r, final DrawMode mode) {
		if (mode != null) {
			double myArc = 4 / 3 * (Math.sqrt(2) - 1);
			this._Point(new Coordinate(coords.getX() + r, coords.getY()));
			float xc = coords.getX() + w - r;
			float yc = coords.getY() + r;
			this._Line(new Coordinate(xc, coords.getY()));
			this._Curve(new Coordinate(xc + (r * myArc), yc - r),
					new Coordinate(xc + r, yc - (r * myArc)), new Coordinate(xc
							+ r, yc));
			xc = xc;
			yc = coords.getY() + h - r;
			this._Line(new Coordinate(coords.getX() + w, yc));
			this._Curve(new Coordinate(xc + r, yc + (r * myArc)),
					new Coordinate(xc + (r * myArc), yc + r), new Coordinate(
							xc, yc + r));
			xc = coords.getX() + r;
			yc = yc;
			this._Line(new Coordinate(xc, coords.getY() + h));
			this._Curve(new Coordinate(xc - (r * myArc), yc + r),
					new Coordinate(xc - r, yc + (r * myArc)), new Coordinate(xc
							- r, yc));
			xc = xc;
			yc = coords.getY() + r;
			this._Line(new Coordinate(coords.getX(), yc));
			this._Curve(new Coordinate(xc - r, yc - (r * myArc)),
					new Coordinate(xc - (r * myArc), yc - r), new Coordinate(
							xc, yc - r));
			this._out(String.valueOf(mode.getOp()));
		}
	}

	/**
	 * Sets a draw point.
	 * 
	 * @param start
	 *            the start point
	 */
	protected void _Point(final Coordinate start) {
		this._out(String.format(Locale.ENGLISH, "%.2f %.2f m", //$NON-NLS-1$
				start.getX() * this.k, (this.h - start.getY()) * this.k));
	}

	/**
	 * Draws a line from last draw point.
	 * 
	 * @param end
	 *            the end point
	 */
	protected void _Line(final Coordinate end) {
		this._out(String.format(Locale.ENGLISH, "%.2f %.2f l", //$NON-NLS-1$
				end.getX() * this.k, (this.h - end.getY()) * this.k));
	}

	/**
	 * Draws a bezier curve from last draw point.
	 * 
	 * @param control1
	 *            the first control point
	 * @param control2
	 *            the second control point
	 * @param end
	 *            the end point
	 */
	protected void _Curve(final Coordinate control1, final Coordinate control2,
			final Coordinate end) {
		this._out(String.format(
				Locale.ENGLISH,
				"%.2f %.2f %.2f %.2f %.2f %.2f c", //$NON-NLS-1$
				control1.getX() * this.k, (this.h - control1.getY()) * this.k,
				control2.getX() * this.k, (this.h - control2.getY()) * this.k,
				end.getX() * this.k, (this.h - end.getY()) * this.k));
	}

	/** Author of document */
	public void setAuthor(final String author) {
		this.author = author;
	}

	/** Set auto page break mode and triggering margin */
	public void setAutoPageBreak(final boolean auto) {
		this.setAutoPageBreak(auto, 0);
	}

	/** Set auto page break mode and triggering margin */
	public void setAutoPageBreak(final boolean auto, final float margin) {
		this.autoPageBreak = auto;
		this.bMargin = margin;
		this.pageBreakTrigger = this.h - margin;
	}

	/** Set page compression */
	public void setCompression(final boolean compress) {
		/* FIXME how to handle GZip in Java? */
		this.compress = compress;
		throw new NotImplementedException();
	}

	/** Creator of document */
	public void setCreator(final String creator) {
		this.creator = creator;
	}

	/** Set display mode in viewer */
	public void setDisplayMode(final Zoom zoom, final Layout layout) {
		/* FIXME wtf is "!is_string($zoom)" doing here? */
		if (zoom != null) {
			this.zoomMode = zoom;
			this.zoomFactor = 0;
		}
		if (layout != null) {
			this.layoutMode = layout;
		}
	}

	/** Set display mode in viewer */
	public void setDisplayMode(final int zoom, final Layout layout) {
		if (zoom > 0) {
			this.zoomMode = null;
			this.zoomFactor = zoom;
		}
		if (layout != null) {
			this.layoutMode = layout;
		}
	}

	/**
	 * Set color for all stroking operations.
	 * 
	 * @param color
	 *            a Color value.
	 */
	public void setDrawColor(final Color color) {
		if (color.isGrayscale()) {
			this.drawColor = String.format(Locale.ENGLISH,
					"%.3f G", Float.valueOf(color.getV() / 255f)); //$NON-NLS-1$
		} else {
			this.drawColor = String
					.format(Locale.ENGLISH, "%.3f %.3f %.3f RG", //$NON-NLS-1$
							Float.valueOf(color.getR() / 255f), Float
									.valueOf(color.getG() / 255f), Float
									.valueOf(color.getB() / 255f));
		}
		if (this.page > 0) {
			this._out(this.drawColor);
		}
	}

	/**
	 * Set color for all stroking operations
	 * 
	 * @param r
	 *            red value
	 * @param g
	 *            green value
	 * @param b
	 *            blue value
	 */
	public void setDrawColor(final int r, final int g, final int b) {
		this.setDrawColor(new Color(r, g, b));
	}

	/**
	 * Set color for all filling operations
	 * 
	 * @param color
	 *            a Color value
	 */
	public void setFillColor(final Color color) {
		if (color.isGrayscale()) {
			this.fillColor = String.format(Locale.ENGLISH,
					"%.3f g", Float.valueOf(color.getV() / 255f)); //$NON-NLS-1$
		} else {
			this.fillColor = String
					.format(Locale.ENGLISH, "%.3f %.3f %.3f rg", //$NON-NLS-1$
							Float.valueOf(color.getR() / 255f), Float
									.valueOf(color.getG() / 255f), Float
									.valueOf(color.getB() / 255f));
		}
		this.colorFlag = (this.fillColor != this.textColor);
		if (this.page > 0) {
			this._out(this.fillColor);
		}
	}

	/**
	 * Set color for all filling operations
	 * 
	 * @param r
	 *            red value
	 * @param g
	 *            green value
	 * @param b
	 *            blue value
	 */
	public void setFillColor(final int r, final int g, final int b) {
		this.setFillColor(new Color(r, g, b));
	}

	/**
	 * Select a font; size given in points.
	 * 
	 * @param family
	 *            the font family
	 * @param style
	 *            the font style
	 * @param size
	 *            the font size in points
	 * @throws IOException
	 *             if the font family is invalid.
	 */
	public void setFont(String family, Set<FontStyle> style, float size)
			throws IOException {
		if (family == null) {
			family = this.fontFamily;
		} else {
			family = family.toLowerCase();
		}
		if ("arial".equals(family)) { //$NON-NLS-1$
			family = "helvetica"; //$NON-NLS-1$
		} else if ("symbol".equals(family) //$NON-NLS-1$
				|| "zapfdingbats".equals(family)) { //$NON-NLS-1$
			style = null;
		}
		if ((style != null) && style.contains(FontStyle.UNDERLINE)) {
			this.underline = true;
		} else {
			this.underline = false;
		}
		if (size == 0) {
			size = this.fontSizePt;
		}
		// Test if font is already selected
		if (((this.fontFamily != null) && this.fontFamily.equals(family))
				&& (((this.fontStyle == null) && (style == null)) || ((this.fontStyle != null) && this.fontStyle
						.equals(style))) && (size == this.fontSizePt)) {
			return;
		}
		// Test if used for the first time
		StringBuilder sb = new StringBuilder();
		sb.append(family);
		if ((style != null) && style.contains(FontStyle.BOLD)) {
			sb.append(FontStyle.BOLD.getOp());
		}
		if ((style != null) && style.contains(FontStyle.ITALIC)) {
			sb.append(FontStyle.ITALIC.getOp());
		}
		String fontkey = sb.toString();
		if (this.fonts.get(fontkey) == null) {
			// Check if one of the standard fonts
			if (this.coreFonts.get(fontkey) != null) {
				int i = this.fonts.size() + 1;
				Font font = new Font(i, Font.Type.CORE, this.coreFonts
						.get(fontkey), -100, 50, getCharwidths(fontkey));
				this.fonts.put(fontkey, font);
			} else {
				throw new IOException("Undefined font: " //$NON-NLS-1$
						+ family + " " + style); //$NON-NLS-1$
			}
		}
		// Select it
		this.fontFamily = family;
		this.fontStyle = style;
		this.fontSizePt = size;
		this.fontSize = size / this.k;
		this.currentFont = this.fonts.get(fontkey);
		if (this.page > 0) {
			this._out(String.format(Locale.ENGLISH, "BT /F%d %.2f Tf ET", //$NON-NLS-1$
					this.currentFont.getI(), Float.valueOf(this.fontSizePt)));
		}
	}

	/** Set font style */
	public void setFontStyle(final Set<FontStyle> style) throws IOException {
		this.setFont(this.fontFamily, style, this.fontSizePt);
	}

	/** Set font size in points */
	public void setFontSize(final float size) {
		if (this.fontSizePt == size) {
			return;
		}
		this.fontSizePt = size;
		this.fontSize = size / this.k;
		if (this.page > 0) {
			this._out(String.format(Locale.ENGLISH, "BT /F%d %.2f Tf ET", //$NON-NLS-1$
					this.currentFont.getI(), Float.valueOf(this.fontSizePt)));
		}
	}

	/** Keywords of document */
	public void setKeywords(final String keywords) {
		this.keywords = keywords;
	}

	/** Set left margin */
	public void setLeftMargin(final float margin) {
		this.lMargin = margin;
		if ((this.page > 0) && (this.x < margin)) {
			this.x = margin;
		}
	}

	/** Set line width */
	public void setLineWidth(final float width) {
		this.lineWidth = width;
		if (this.page > 0) {
			this._out(String.format(Locale.ENGLISH,
					"%.2f w", Float.valueOf(width * this.k))); //$NON-NLS-1$
		}
	}

	/** Set destination of internal link */
	public void setLink(final int link, float y, int page) {
		if (y == -1) {
			y = this.y;
		}
		if (page == -1) {
			page = this.page;
		}
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		map.put(Integer.valueOf(page), Float.valueOf(y));
		this.links.put(Integer.valueOf(link), map);
	}

	/** Set left, top and right margins */
	public void setMargins(final float left, final float top) {
		this.setMargins(left, top, left);
	}

	/** Set left, top and right margins */
	public void setMargins(final float left, final float top, final float right) {
		this.lMargin = left;
		this.tMargin = top;
		this.rMargin = right;
	}

	/** Set right margin */
	public void setRightMargin(final float margin) {
		this.rMargin = margin;
	}

	/** Subject of document */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/** Set color for text */
	public void setTextColor(final Color color) {
		if (color.isGrayscale()) {
			this.textColor = String.format(Locale.ENGLISH,
					"%.3f g", Float.valueOf(color.getV() / 255f)); //$NON-NLS-1$
		} else {
			this.textColor = String
					.format(Locale.ENGLISH, "%.3f %.3f %.3f rg", //$NON-NLS-1$
							Float.valueOf(color.getR() / 255f), Float
									.valueOf(color.getG() / 255f), Float
									.valueOf(color.getB() / 255f));
		}
		this.colorFlag = (this.fillColor != this.textColor);
	}

	/** Set color for text */
	public void setTextColor(final int r, final int g, final int b) {
		this.setTextColor(new Color(r, g, b));
	}

	/** Title of document */
	public void setTitle(final String title) {
		this.title = title;
	}

	/** Set top margin */
	public void setTopMargin(final float margin) {
		this.tMargin = margin;
	}

	/** Set x position */
	public void setX(final float x) {
		if (x >= 0) {
			this.x = x;
		} else {
			this.x = this.w + x;
		}
	}

	/** Set x and y positions */
	public void setXY(final float x, final float y) {
		this.setY(y);
		this.setX(x);
	}

	/** Set y position and reset x */
	public void setY(final float y) {
		this.x = this.lMargin;
		if (y >= 0) {
			this.y = y;
		} else {
			this.y = this.h + y;
		}
	}

	/** Output a string */
	public void Text(final float x, final float y, final String txt) {
		StringBuilder s = new StringBuilder();
		s.append(String.format(Locale.ENGLISH,
				"BT %.2f %.2f Td (%s) Tj ET", //$NON-NLS-1$
				Float.valueOf(x * this.k),
				Float.valueOf((this.h - y) * this.k), this._escape(txt)));
		if (this.underline && (txt != null)) {
			s.append(' ').append(this._dounderline(x, y, txt));
		}
		if (this.colorFlag) {
			s.delete(0, s.length());
			s.append("q ").append(this.textColor) //$NON-NLS-1$
					.append(' ').append(s).append(" Q"); //$NON-NLS-1$
		}
		this._out(s.toString());
	}

	/**
	 * Output text in flowing mode.
	 * 
	 * @throws IOException
	 *             if the default font can not be loaded.
	 */
	public void write(final float h, final String txt, final int link)
			throws IOException {
		Charwidths cw = this.currentFont.getCw(); //$NON-NLS-1$
		float w = this.w - this.rMargin - this.x;
		float wmax = (w - 2 * this.cMargin) * 1000 / this.fontSize;
		String s = txt.replace("\r", ""); //$NON-NLS-1$//$NON-NLS-2$
		int nb = s.length();
		int sep = -1;
		int i = 0;
		int j = 0;
		int l = 0;
		int nl = 1;
		while (i < nb) {
			// Get next character
			char c = s.charAt(i);
			if (c == '\n') {
				// Explicit line break
				this.Cell(w, h, s.substring(j, i), null, Position.BELOW, null,
						false, link);
				i++;
				sep = -1;
				j = i;
				l = 0;
				if (nl == 1) {
					this.x = this.lMargin;
					w = this.w - this.rMargin - this.x;
					wmax = (w - 2 * this.cMargin) * 1000 / this.fontSize;
				}
				nl++;
				continue;
			}
			if (c == ' ') {
				sep = i;
			}
			l += cw.get(c);
			if (l > wmax) {
				// Automatic line break
				if (sep == -1) {
					if (this.x > this.lMargin) {
						// Move to next line
						this.x = this.lMargin;
						this.y += h;
						w = this.w - this.rMargin - this.x;
						wmax = (w - 2 * this.cMargin) * 1000 / this.fontSize;
						i++;
						nl++;
						continue;
					}
					if (i == j) {
						i++;
					}
					this.Cell(w, h, s.substring(j, i), null, Position.BELOW,
							null, false, link);
				} else {
					this.Cell(w, h, s.substring(j, sep), null, Position.BELOW,
							null, false, link);
					i = sep + 1;
				}
				sep = -1;
				j = i;
				l = 0;
				if (nl == 1) {
					this.x = this.lMargin;
					w = this.w - this.rMargin - this.x;
					wmax = (w - 2 * this.cMargin) * 1000 / this.fontSize;
				}
				nl++;
			} else {
				i++;
			}
		}
		// Last chunk
		if (i != j) {
			this.Cell(l / 1000 * this.fontSize, h, s.substring(j), null, null,
					null, false, link);
		}
	}
}
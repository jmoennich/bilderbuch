/**
 * bilderbuch - voice driven image search on the web
 * Copyright (C) 2014  Jan Mönnich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cuseb.bilderbuch.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.net.URL;
import java.util.Iterator;

@RestController
public class PdfController {

    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public void requestPdf(@RequestBody PdfRequest pdfRequest,
                           HttpSession session) {

        session.setAttribute("pdfRequest", pdfRequest);
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.GET)
    public void generatePdf(HttpSession session,
                            HttpServletResponse httpServletResponse) {

        try {
            PdfRequest pdfRequest = (PdfRequest) session.getAttribute("pdfRequest");
            httpServletResponse.setContentType("application/pdf");

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, httpServletResponse.getOutputStream());
            writer.setDefaultColorspace(PdfName.COLORSPACE, PdfName.DEFAULTRGB);

            //document.addAuthor(pdfRequest.getAuthor());
            //document.addTitle(pdfRequest.getTitle());
            document.setPageSize(new Rectangle(
                    Utilities.millimetersToPoints(156),
                    Utilities.millimetersToPoints(148)));
            document.open();

            FontFactory.defaultEmbedding = true;
            FontFactory.register("IndieRock.ttf", "IndieRock");
            Font font = FontFactory.getFont("IndieRock");
            BaseFont baseFont = font.getBaseFont();
            PdfContentByte cb = writer.getDirectContent();

            Iterator<PdfPage> pages = pdfRequest.getPages().iterator();
            while (pages.hasNext()) {

                PdfPage page = pages.next();
                if (page.getImage() != null) {

                    Image image = Image.getInstance(new URL(page.getImage().getUrl()));
                    image.setDpi(300, 300);
                    image.setAbsolutePosition(0f, 0f);
                    image.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
                    document.add(image);

                    cb.saveState();
                    cb.beginText();
                    cb.setColorFill(Color.WHITE);
                    cb.moveText(10f, 10f);
                    cb.setFontAndSize(baseFont, 18);
                    cb.showText(page.getSentence());
                    cb.endText();
                    cb.restoreState();

                    if (pages.hasNext()) {
                        document.newPage();
                    }
                }
            }
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

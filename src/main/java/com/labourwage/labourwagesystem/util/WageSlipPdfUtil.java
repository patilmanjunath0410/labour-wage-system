package com.labourwage.labourwagesystem.util;

import com.labourwage.labourwagesystem.dto.response.WageSlipResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class WageSlipPdfUtil {

    public byte[] generate(WageSlipResponse slip)
            throws Exception {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDType1Font fontBold = new PDType1Font(
                doc, Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(
                doc, Standard14Fonts.FontName.HELVETICA);

        PDPageContentStream cs =
                new PDPageContentStream(doc, page);

        float margin = 50;
        float y = 780;
        float lineHeight = 20;

        // ── Header ──────────────────────────────
        cs.beginText();
        cs.setFont(fontBold, 16);
        cs.newLineAtOffset(margin, y);
        cs.showText("WAGE SLIP — FORM XIV");
        cs.endText();
        y -= lineHeight;

        cs.beginText();
        cs.setFont(fontNormal, 10);
        cs.newLineAtOffset(margin, y);
        cs.showText("(Under Contract Labour Regulation Act)");
        cs.endText();
        y -= lineHeight * 1.5f;

        // ── Divider ─────────────────────────────
        cs.moveTo(margin, y);
        cs.lineTo(545, y);
        cs.stroke();
        y -= lineHeight;

        // ── Company Info ────────────────────────
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Company:", slip.getContractorName());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Site:", slip.getSiteName());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Month:",
                slip.getSlipMonth().format(
                        DateTimeFormatter.ofPattern("MMMM yyyy")));
        y -= lineHeight * 1.5f;

        // ── Worker Info ─────────────────────────
        cs.beginText();
        cs.setFont(fontBold, 12);
        cs.newLineAtOffset(margin, y);
        cs.showText("Worker Details");
        cs.endText();
        y -= lineHeight;

        drawRow(cs, fontBold, fontNormal,
                margin, y, "Name:", slip.getWorkerName());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Worker Code:", slip.getWorkerCode());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Skill:", slip.getSkillType());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Daily Rate:",
                "Rs. " + slip.getDailyWageRate());
        y -= lineHeight * 1.5f;

        // ── Attendance Summary ───────────────────
        cs.beginText();
        cs.setFont(fontBold, 12);
        cs.newLineAtOffset(margin, y);
        cs.showText("Attendance Summary");
        cs.endText();
        y -= lineHeight;

        drawRow(cs, fontBold, fontNormal,
                margin, y, "Full Days:",
                String.valueOf(slip.getFullDays()));
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Half Days:",
                String.valueOf(slip.getHalfDays()));
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Overtime Days:",
                String.valueOf(slip.getOvertimeDays()));
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Total Days Present:",
                String.valueOf(slip.getTotalDaysPresent()));
        y -= lineHeight * 1.5f;

        // ── Wage Calculation ─────────────────────
        cs.beginText();
        cs.setFont(fontBold, 12);
        cs.newLineAtOffset(margin, y);
        cs.showText("Wage Calculation");
        cs.endText();
        y -= lineHeight;

        drawRow(cs, fontBold, fontNormal,
                margin, y, "Gross Wage:",
                "Rs. " + slip.getGrossWage());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "PF Deduction (12%):",
                "Rs. " + slip.getPfDeduction());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "ESI Deduction (0.75%):",
                "Rs. " + slip.getEsiDeduction());
        y -= lineHeight;
        drawRow(cs, fontBold, fontNormal,
                margin, y, "Advance Deduction:",
                "Rs. " + slip.getAdvanceDeduction());
        y -= lineHeight * 1.5f;

        // ── Divider ─────────────────────────────
        cs.moveTo(margin, y);
        cs.lineTo(545, y);
        cs.stroke();
        y -= lineHeight;

        // ── Net Wage ────────────────────────────
        cs.beginText();
        cs.setFont(fontBold, 14);
        cs.newLineAtOffset(margin, y);
        cs.showText("NET WAGE PAYABLE: Rs. "
                + slip.getNetWage());
        cs.endText();
        y -= lineHeight * 2;

        // ── Footer ──────────────────────────────
        cs.moveTo(margin, y);
        cs.lineTo(545, y);
        cs.stroke();
        y -= lineHeight;

        cs.beginText();
        cs.setFont(fontNormal, 9);
        cs.newLineAtOffset(margin, y);
        cs.showText(
                "This is a computer generated wage slip. " +
                        "Generated by Labour Wage Management System.");
        cs.endText();

        cs.close();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        doc.save(out);
        doc.close();

        return out.toByteArray();
    }

    private void drawRow(PDPageContentStream cs,
                         PDType1Font labelFont,
                         PDType1Font valueFont,
                         float x, float y,
                         String label,
                         String value) throws Exception {
        cs.beginText();
        cs.setFont(labelFont, 10);
        cs.newLineAtOffset(x, y);
        cs.showText(label);
        cs.endText();

        cs.beginText();
        cs.setFont(valueFont, 10);
        cs.newLineAtOffset(x + 180, y);
        cs.showText(value != null ? value : "-");
        cs.endText();
    }
}
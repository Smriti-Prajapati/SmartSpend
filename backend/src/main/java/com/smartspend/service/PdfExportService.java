package com.smartspend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.smartspend.dto.ExpenseDTO;
import com.smartspend.dto.IncomeDTO;
import com.smartspend.model.User;
import com.smartspend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {

    @Autowired private ExpenseService expenseService;
    @Autowired private IncomeService incomeService;
    @Autowired private UserRepository userRepository;

    public byte[] generateReport(Long userId, LocalDate start, LocalDate end) throws DocumentException {
        User user = userRepository.findById(userId).orElseThrow();
        List<ExpenseDTO> expenses = expenseService.getByDateRange(userId, start, end);
        List<IncomeDTO> incomes = incomeService.getByDateRange(userId, start, end);

        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(30, 64, 175));
        Font subFont  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(30, 64, 175));
        Font hdrFont  = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
        Font smallFont= new Font(Font.FontFamily.HELVETICA, 9,  Font.NORMAL, BaseColor.GRAY);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        Paragraph title = new Paragraph("SmartSpend — Financial Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        Paragraph period = new Paragraph(start.format(fmt) + "  →  " + end.format(fmt), smallFont);
        period.setAlignment(Element.ALIGN_CENTER);
        doc.add(period);
        Paragraph userLine = new Paragraph(user.getName() + " (" + user.getEmail() + ")", smallFont);
        userLine.setAlignment(Element.ALIGN_CENTER);
        doc.add(userLine);
        doc.add(Chunk.NEWLINE);

        // Summary cards
        double totalIncome   = incomes.stream().mapToDouble(IncomeDTO::getAmount).sum();
        double totalExpenses = expenses.stream().mapToDouble(ExpenseDTO::getAmount).sum();
        double balance       = totalIncome - totalExpenses;

        PdfPTable summary = new PdfPTable(3);
        summary.setWidthPercentage(100);
        addSummaryCell(summary, "Total Income",   String.format("$%.2f", totalIncome),   new BaseColor(16, 185, 129));
        addSummaryCell(summary, "Total Expenses", String.format("$%.2f", totalExpenses), new BaseColor(239, 68, 68));
        addSummaryCell(summary, "Net Balance",    String.format("$%.2f", balance),
                balance >= 0 ? new BaseColor(16, 185, 129) : new BaseColor(239, 68, 68));
        doc.add(summary);
        doc.add(Chunk.NEWLINE);

        // Expenses
        doc.add(new Paragraph("Expenses (" + expenses.size() + ")", subFont));
        doc.add(Chunk.NEWLINE);
        PdfPTable expTable = new PdfPTable(new float[]{3, 2, 2, 3});
        expTable.setWidthPercentage(100);
        addHeader(expTable, hdrFont, "Title", "Category", "Amount", "Date");
        for (ExpenseDTO e : expenses) {
            expTable.addCell(new Phrase(e.getTitle(), bodyFont));
            expTable.addCell(new Phrase(e.getCategoryName(), bodyFont));
            expTable.addCell(new Phrase(String.format("$%.2f", e.getAmount()), bodyFont));
            expTable.addCell(new Phrase(e.getDate().toString(), bodyFont));
        }
        doc.add(expTable);
        doc.add(Chunk.NEWLINE);

        // Income
        doc.add(new Paragraph("Income (" + incomes.size() + ")", subFont));
        doc.add(Chunk.NEWLINE);
        PdfPTable incTable = new PdfPTable(new float[]{3, 2, 2, 3});
        incTable.setWidthPercentage(100);
        addHeader(incTable, hdrFont, "Title", "Source", "Amount", "Date");
        for (IncomeDTO i : incomes) {
            incTable.addCell(new Phrase(i.getTitle(), bodyFont));
            incTable.addCell(new Phrase(i.getSource() != null ? i.getSource() : "-", bodyFont));
            incTable.addCell(new Phrase(String.format("$%.2f", i.getAmount()), bodyFont));
            incTable.addCell(new Phrase(i.getDate().toString(), bodyFont));
        }
        doc.add(incTable);
        doc.close();
        return out.toByteArray();
    }

    private void addHeader(PdfPTable t, Font f, String... cols) {
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(c, f));
            cell.setBackgroundColor(new BaseColor(30, 64, 175));
            cell.setPadding(8);
            t.addCell(cell);
        }
    }

    private void addSummaryCell(PdfPTable t, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color); cell.setPadding(12);
        Font lf = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
        Font vf = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD,   BaseColor.WHITE);
        cell.addElement(new Paragraph(label, lf));
        cell.addElement(new Paragraph(value, vf));
        t.addCell(cell);
    }
}

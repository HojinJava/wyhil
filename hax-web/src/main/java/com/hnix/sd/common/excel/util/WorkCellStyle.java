package com.hnix.sd.common.excel.util;

import org.apache.poi.ss.usermodel.*;

public class WorkCellStyle {

    public static Font setCellFont(Font font, final int fontHeight, final boolean isBold) {
        font.setFontHeight((short) fontHeight);
        font.setBold(isBold);
        return font;
    }

    public static CellStyle setCellStyle(CellStyle style, Font font, ExcelFormat.ALIGN align, boolean isBorder, boolean isWarp, ExcelFormat.CELL cellStyle) {
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(
                align == ExcelFormat.ALIGN.CENTER ? HorizontalAlignment.CENTER
                        : align == ExcelFormat.ALIGN.LEFT ? HorizontalAlignment.LEFT
                        : HorizontalAlignment.RIGHT
        );
        style.setWrapText(isWarp);

        if (isBorder) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        if (cellStyle == ExcelFormat.CELL.BACKGROUND) {
            style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        style.setFont(font);

        return style;
    }

}

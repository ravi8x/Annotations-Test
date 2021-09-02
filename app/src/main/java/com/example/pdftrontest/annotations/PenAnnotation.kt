package com.example.pdftrontest.annotations

import androidx.annotation.Keep
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.tools.SmartPenInk
import com.pdftron.pdf.tools.ToolManager

@Keep
class PenAnnotation(ctrl: PDFViewCtrl) : SmartPenInk(ctrl) {
    companion object {
        var MODE = ToolManager.ToolMode.addNewMode(Annot.e_Ink)

        fun isForceNextSmartMode() = true
    }

    override fun getToolMode(): ToolManager.ToolModeBase {
        return MODE
    }
}
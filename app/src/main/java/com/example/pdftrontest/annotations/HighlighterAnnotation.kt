package com.example.pdftrontest.annotations

import androidx.annotation.Keep
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.tools.FreeHighlighterCreate
import com.pdftron.pdf.tools.ToolManager

@Keep
class HighlighterAnnotation(ctrl: PDFViewCtrl) : FreeHighlighterCreate(ctrl) {
    companion object {
        var MODE = ToolManager.ToolMode.addNewMode(Annot.e_Highlight)
        fun isForceNextSmartMode() = true
    }

    override fun getToolMode(): ToolManager.ToolModeBase {
        return MODE
    }
}
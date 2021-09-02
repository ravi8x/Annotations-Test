package com.example.pdftrontest.annotations

import androidx.annotation.Keep
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.tools.Pan
import com.pdftron.pdf.tools.ToolManager

@Keep
class PanTool(ctrl: PDFViewCtrl) : Pan(ctrl) {
    companion object {
        var MODE = ToolManager.ToolMode.addNewMode(Annot.e_FreeText)
        fun isForceNextSmartMode() = true
    }

    override fun getToolMode(): ToolManager.ToolModeBase {
        return MODE
    }
}
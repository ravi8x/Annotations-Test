package com.example.pdftrontest.annotations

import android.graphics.Color
import androidx.annotation.Keep
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.model.AnnotStyle
import com.pdftron.pdf.tools.FreeTextCreate
import com.pdftron.pdf.tools.ToolManager

@Keep
class TextAnnotation(ctrl: PDFViewCtrl) : FreeTextCreate(ctrl) {
    companion object {
        var MODE = ToolManager.ToolMode.addNewMode(Annot.e_FreeText)
        fun isForceNextSmartMode() = true
    }

    override fun getToolMode(): ToolManager.ToolModeBase {
        return MODE
    }

    override fun initTextStyle() {
        super.initTextStyle()
        val annotStyle = AnnotStyle()
        // 2. Set annotation type to annot style
        annotStyle.annotType = Annot.e_FreeText
        // 3. Set blue stroke, yellow fill color, thickness 5, opacity 0.8 to the annotation style.
        //annotStyle.setStyle(Color.TRANSPARENT, Color.TRANSPARENT, 0f, 1f)
        annotStyle.opacity = 1f
        annotStyle.textSize = 45.0f
        annotStyle.textColor = Color.RED

        setupAnnotProperty(annotStyle)
    }
}
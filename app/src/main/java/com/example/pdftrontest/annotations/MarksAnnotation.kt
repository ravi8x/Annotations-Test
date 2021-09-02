package com.example.pdftrontest.annotations

import android.text.Editable
import android.text.InputType
import android.text.method.DigitsKeyListener
import androidx.annotation.Keep
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.tools.FreeTextCreate
import com.pdftron.pdf.tools.ToolManager

@Keep
class MarksAnnotation(ctrl: PDFViewCtrl) : FreeTextCreate(ctrl) {
    companion object {
        var MODE = ToolManager.ToolMode.addNewMode(Annot.e_FreeText)
        fun isForceNextSmartMode() = true
    }

    override fun getToolMode(): ToolManager.ToolModeBase {
        return MODE
    }

    fun isMarksAnnotation() = true

    override fun createFreeText() {
        super.createFreeText()
        configureInput()
    }

    private fun configureInput() {
        mInlineEditText.editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        mInlineEditText.editText.isSingleLine = true
        mInlineEditText.editText.keyListener = DigitsKeyListener.getInstance("0123456789.")
    }

    override fun afterTextChanged(s: Editable?) {
        if (s?.startsWith("+") == false) {
            //s.insert(0, "+")
        }
    }
}
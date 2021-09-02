package com.example.pdftrontest

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.pdftrontest.annotations.*
import com.example.pdftrontest.databinding.ActivityMainBinding
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.tools.FreehandCreate
import com.pdftron.pdf.tools.Tool
import com.pdftron.pdf.tools.ToolManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), PDFViewCtrl.DocumentDownloadListener {
    private lateinit var binding: ActivityMainBinding
    private var mToolManager: ToolManager? = null
    private var toolCheckMarkAnnotation: ToolManager.Tool? = null
    private var toolCrossMarkAnnotation: ToolManager.Tool? = null
    private var toolHighlighterAnnotation: ToolManager.Tool? = null
    private var toolPenAnnotation: ToolManager.Tool? = null
    private var toolTextAnnotation: ToolManager.Tool? = null
    private var toolMarksAnnotation: ToolManager.Tool? = null
    private var toolPan: ToolManager.Tool? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolManager()
        setupAnnotations()
        setUpClicks()
        renderUrl()
    }

    private fun renderUrl() {
        val httpOptions = PDFViewCtrl.HTTPRequestOptions()
        val path = createPdfInCacheDir(this, "Hello")
        val remoteUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        binding.content.pdfviewctrl.openUrlAsync(remoteUrl, path, null, httpOptions)
        binding.content.pdfviewctrl.addDocumentDownloadListener(this)
    }

    private fun setupToolManager() {
        mToolManager = ToolManagerBuilder.from()
            .setRealTimeAnnotEdit(false)
            .addCustomizedTool(CheckMarkAnnotation.MODE, CheckMarkAnnotation::class.java)
            .addCustomizedTool(CrossMarkAnnotation.MODE, CrossMarkAnnotation::class.java)
            .addCustomizedTool(PenAnnotation.MODE, PenAnnotation::class.java)
            .addCustomizedTool(HighlighterAnnotation.MODE, HighlighterAnnotation::class.java)
            .addCustomizedTool(TextAnnotation.MODE, TextAnnotation::class.java)
            .addCustomizedTool(MarksAnnotation.MODE, MarksAnnotation::class.java)
            .build(this, binding.content.pdfviewctrl)
    }

    /**
     * Custom annotation tools
     * */
    private fun setupAnnotations() {
        mToolManager?.let { toolManager ->
            // Check mark
            toolCheckMarkAnnotation =
                toolManager.createTool(CheckMarkAnnotation.MODE, toolManager.tool)
            (toolCheckMarkAnnotation as Tool).isForceSameNextToolMode =
                CheckMarkAnnotation.isForceNextSmartMode()

            // Cross mark
            toolCrossMarkAnnotation =
                toolManager.createTool(CrossMarkAnnotation.MODE, toolManager.tool)
            (toolCrossMarkAnnotation as Tool).isForceSameNextToolMode =
                CrossMarkAnnotation.isForceNextSmartMode()

            // Pen
            toolPenAnnotation = toolManager.createTool(PenAnnotation.MODE, toolManager.tool)
            (toolPenAnnotation as Tool).isForceSameNextToolMode =
                PenAnnotation.isForceNextSmartMode()

            // Highlighter
            toolHighlighterAnnotation =
                toolManager.createTool(HighlighterAnnotation.MODE, toolManager.tool)
            (toolHighlighterAnnotation as Tool).isForceSameNextToolMode =
                HighlighterAnnotation.isForceNextSmartMode()

            // Free text
            toolTextAnnotation = toolManager.createTool(TextAnnotation.MODE, toolManager.tool)
            (toolTextAnnotation as Tool).isForceSameNextToolMode =
                TextAnnotation.isForceNextSmartMode()

            // Marks
            toolMarksAnnotation = toolManager.createTool(MarksAnnotation.MODE, toolManager.tool)
            (toolMarksAnnotation as Tool).isForceSameNextToolMode =
                MarksAnnotation.isForceNextSmartMode()

            // Pan
            toolPan = toolManager.createTool(PanTool.MODE, toolManager.tool)
            (toolPan as Tool).isForceSameNextToolMode =
                PanTool.isForceNextSmartMode()
        }
    }

    private fun usePenAnnotation() {
        mToolManager?.tool = toolPenAnnotation
    }

    private fun useHighlighterAnnotation() {
        mToolManager?.tool = toolHighlighterAnnotation
    }

    private fun useCheckMarkAnnotation() {
        mToolManager?.tool = toolCheckMarkAnnotation
    }

    private fun useCrossMarkAnnotation() {
        mToolManager?.tool = toolCrossMarkAnnotation
    }

    private fun setUpClicks() {
        binding.content.btnCheck.setOnClickListener {
            useCheckMarkAnnotation()
        }

        binding.content.btnCross.setOnClickListener {
            useCrossMarkAnnotation()
        }

        binding.content.btnPen.setOnClickListener {
            usePenAnnotation()
        }

        binding.content.btnHighlighter.setOnClickListener {
            useHighlighterAnnotation()
        }
    }

    private fun createPdfInCacheDir(
        context: Context,
        fileName: String?,
        includeRandomNumber: Boolean = false
    ): String? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val name = if (includeRandomNumber) {
            "${fileName}_${timeStamp.substring(0, 8)}"
        } else {
            fileName ?: "pdf_$timeStamp"
        }
        context.externalCacheDir?.mkdir()
        return File(context.externalCacheDir, "$name.pdf").absolutePath
    }

    override fun onDownloadEvent(
        state: PDFViewCtrl.DownloadState?,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: String?
    ) {
        Log.e("CCC", "Download state ${state} $p1 $p2 $p3 $p4")
        if (state == PDFViewCtrl.DownloadState.FINISHED) {
            Log.e("CCC", "Document downloaded")
        }
    }
}
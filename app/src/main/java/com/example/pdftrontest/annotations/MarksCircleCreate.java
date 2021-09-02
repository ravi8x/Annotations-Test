package com.example.pdftrontest.annotations;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Point;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.Circle;
import com.pdftron.pdf.annots.FreeText;
import com.pdftron.pdf.tools.FreeTextCreate;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.Utils;

@Keep
public class MarksCircleCreate extends FreeTextCreate {
    private static int THRESHOLD = 40;
    private Point mStart;
    private Point mKnee;
    private Point mEnd;
    private Rect mContentRect;
    private Circle mCircle;

    public MarksCircleCreate(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
        this.mUseEditTextAppearance = false;
    }

    public ToolManager.ToolModeBase getToolMode() {
        return ToolManager.ToolMode.CALLOUT_CREATE;
    }

    public int getCreateAnnotType() {
        return 1007;
    }

    protected void createFreeText() {
        try {
            this.preCreateCallout();
            boolean shouldUnlock = false;

            try {
                this.mPdfViewCtrl.docLock(true);
                shouldUnlock = true;
                this.createAnnot("");
                this.raiseAnnotationAddedEvent(this.mAnnot, this.mAnnotPageNum);
            } catch (Exception var7) {
                ((ToolManager) this.mPdfViewCtrl.getToolManager()).annotationCouldNotBeAdded(var7.getMessage());
                AnalyticsHandlerAdapter.getInstance().sendException(var7);
            } finally {
                if (shouldUnlock) {
                    this.mPdfViewCtrl.docUnlock();
                }

            }

            this.mNextToolMode = ToolManager.ToolMode.ANNOT_EDIT_ADVANCED_SHAPE;
            this.setCurrentDefaultToolModeHelper(this.getToolMode());
            this.mUpFromCalloutCreate = this.mOnUpOccurred;
        } catch (Exception var9) {
            AnalyticsHandlerAdapter.getInstance().sendException(var9, "CalloutCreate::createFreeText");
        }

    }

    private void preCreateCallout() throws PDFNetException {
        if (this.mTargetPointPageSpace != null) {
            Rect pageRect = Utils.getPageRect(this.mPdfViewCtrl, this.mPageNum);
            if (pageRect != null) {
                pageRect.normalize();
                this.mStart = new Point(this.mTargetPointPageSpace.x, this.mTargetPointPageSpace.y);
                this.mKnee = this.calcCalloutKneePt(pageRect);
                this.mEnd = this.calcCalloutEndPt(pageRect);
                this.mContentRect = this.calcCalloutContentRect(pageRect);
                this.mCircle = Circle.create(this.mPdfViewCtrl.getDoc(), pageRect);
            }

        }
    }

    protected Rect getFreeTextBBox(FreeText freeText, boolean isRightToLeft) throws PDFNetException {
        return this.mContentRect;
    }

    protected void setExtraFreeTextProps(FreeText freetext, Rect bbox) throws PDFNetException {
        super.setExtraFreeTextProps(freetext, bbox);
        if (this.mStart != null && this.mKnee != null && this.mEnd != null) {
            freetext.setCalloutLinePoints(this.mStart, this.mKnee, this.mEnd);
            freetext.setEndingStyle(3);
            freetext.setContentRect(bbox);
            freetext.setIntentName(1);
        }
    }

    private Point calcCalloutKneePt(Rect pageRect) throws PDFNetException {
        double pageX1 = pageRect.getX1();
        double pageX2 = pageRect.getX2();
        double pageMidX = (pageX1 + pageX2) / 2.0D;
        double pageY1 = pageRect.getY1();
        double pageY2 = pageRect.getY2();
        double pageMidY = (pageY1 + pageY2) / 2.0D;
        double x;
        double y;
        if (this.mStart.x > pageMidX) {
            if (this.mStart.y > pageMidY) {
                x = this.mStart.x - (double) THRESHOLD;
                y = this.mStart.y - (double) THRESHOLD;
            } else {
                x = this.mStart.x - (double) THRESHOLD;
                y = this.mStart.y + (double) THRESHOLD;
            }
        } else if (this.mStart.y > pageMidY) {
            x = this.mStart.x + (double) THRESHOLD;
            y = this.mStart.y - (double) THRESHOLD;
        } else {
            x = this.mStart.x + (double) THRESHOLD;
            y = this.mStart.y + (double) THRESHOLD;
        }

        return new Point(x, y);
    }

    private Point calcCalloutEndPt(Rect pageRect) throws PDFNetException {
        double x = this.mStart.x > this.mKnee.x ? Math.min(this.mKnee.x - (double) THRESHOLD, pageRect.getX2()) : Math.max(this.mKnee.x + (double) THRESHOLD, pageRect.getX1());
        double y = this.mKnee.y;
        return new Point(x, y);
    }

    private Rect calcCalloutContentRect(Rect pageRect) throws PDFNetException {
        double x1 = this.mEnd.x > this.mKnee.x ? this.mEnd.x : Math.max(this.mEnd.x - (double) (THRESHOLD * 2), pageRect.getX1());
        double y1 = Math.max(this.mEnd.y - (double) (THRESHOLD / 2), pageRect.getY1());
        double x2 = this.mEnd.x > this.mKnee.x ? Math.min(x1 + (double) (THRESHOLD * 2), pageRect.getX2()) : this.mEnd.x;
        double y2 = Math.min(y1 + (double) THRESHOLD, pageRect.getY2());
        return new Rect(x1, y1, x2, y2);
    }
}

package com.example.pdftrontest.annotations;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.example.pdftrontest.R;
import com.pdftron.common.Matrix2D;
import com.pdftron.filters.SecondaryFileFilter;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Image;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.PageSet;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.tools.Stamper;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.sdf.Obj;
import com.pdftron.sdf.ObjSet;


import java.io.File;

@Keep
public class CrossMarkAnnotation extends Stamper {
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.addNewMode(Annot.e_Stamp);

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }

    public CrossMarkAnnotation(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    public static boolean isForceNextSmartMode() {
        return true;
    }

    @Override
    protected void addStamp() {
        File resource = Utils.copyResourceToLocal(mPdfViewCtrl.getContext(), R.raw.ic_crossmark, "CrossMark", "png");
        addImageStamp(Uri.fromFile(resource), 0, null);
    }

    private boolean addImageStamp(Uri uri, int imageRotation, String filePath) {
        boolean shouldUnlock = false;
        SecondaryFileFilter filter = null;

        boolean var69;
        try {
            this.mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            PDFDoc doc = this.mPdfViewCtrl.getDoc();
            filter = new SecondaryFileFilter(this.mPdfViewCtrl.getContext(), uri);
            ObjSet hintSet = new ObjSet();
            Obj encoderHints = hintSet.createArray();
            encoderHints.pushBackName("JPEG");
            encoderHints.pushBackName("Quality");
            encoderHints.pushBackNumber(85.0D);
            Image img = Image.create(doc.getSDFDoc(), filter, encoderHints);
            int pageNum;
            if (this.mTargetPoint != null) {
                pageNum = this.mPdfViewCtrl.getPageNumberFromScreenPt((double) this.mTargetPoint.x, (double) this.mTargetPoint.y);
                if (pageNum <= 0) {
                    pageNum = this.mPdfViewCtrl.getCurrentPage();
                }
            } else {
                pageNum = this.mPdfViewCtrl.getCurrentPage();
            }

            if (pageNum <= 0) {
                boolean var60 = false;
                return var60;
            }

            Page page = doc.getPage(pageNum);
            int viewRotation = this.mPdfViewCtrl.getPageRotation();
            Rect pageViewBox = page.getBox(this.mPdfViewCtrl.getPageBox());
            Rect pageCropBox = page.getCropBox();
            int pageRotation = page.getRotation();
            Point size = new Point();
            Utils.getDisplaySize(this.mPdfViewCtrl.getContext(), size);
            int screenWidth = size.x < size.y ? size.x : size.y;
            int screenHeight = size.x < size.y ? size.y : size.x;
            double maxImageHeightPixels = (double) screenHeight * 0.25D;
            double maxImageWidthPixels = (double) screenWidth * 0.25D;
            double[] point1 = this.mPdfViewCtrl.convScreenPtToPagePt(0.0D, 0.0D, pageNum);
            double[] point2 = this.mPdfViewCtrl.convScreenPtToPagePt(20.0D, 20.0D, pageNum);
            double pixelsToPageRatio = Math.abs(point1[0] - point2[0]) / 20.0D;
            double maxImageHeightPage = maxImageHeightPixels * pixelsToPageRatio;
            double maxImageWidthPage = maxImageWidthPixels * pixelsToPageRatio;
            double stampWidth = (double) img.getImageWidth();
            double stampHeight = (double) img.getImageHeight();
            double pageWidth;
            if (imageRotation == 90 || imageRotation == 270) {
                pageWidth = stampWidth;
                stampWidth = stampHeight;
                stampHeight = pageWidth;
            }

            pageWidth = pageViewBox.getWidth();
            double pageHeight = pageViewBox.getHeight();
            double scaleFactor;
            if (pageRotation == 1 || pageRotation == 3) {
                scaleFactor = pageWidth;
                pageWidth = pageHeight;
                pageHeight = scaleFactor;
            }

            if (pageWidth < maxImageWidthPage) {
                maxImageWidthPage = pageWidth;
            }

            if (pageHeight < maxImageHeightPage) {
                maxImageHeightPage = pageHeight;
            }

            // NOTE: ScaleFactor is removed to have peroper image size
            scaleFactor = Math.min(maxImageWidthPage / stampWidth, maxImageHeightPage / stampHeight);
            //stampWidth *= scaleFactor;
            //stampHeight *= scaleFactor;
            if (viewRotation == 1 || viewRotation == 3) {
                double temp = stampWidth;
                stampWidth = stampHeight;
                stampHeight = temp;
            }

            com.pdftron.pdf.Stamper stamper = new com.pdftron.pdf.Stamper(2, 60, 60);
            if (this.mTargetPoint != null) {
                double[] pageTarget = this.mPdfViewCtrl.convScreenPtToPagePt((double) this.mTargetPoint.x, (double) this.mTargetPoint.y, pageNum);
                Matrix2D mtx = page.getDefaultMatrix();
                com.pdftron.pdf.Point pageTargetPoint = mtx.multPoint(pageTarget[0], pageTarget[1]);
                stamper.setAlignment(-1, -1);
                pageTargetPoint.x -= stampWidth / 2.0D;
                pageTargetPoint.y -= stampHeight / 2.0D;
                double leftEdge = pageViewBox.getX1() - pageCropBox.getX1();
                double bottomEdge = pageViewBox.getY1() - pageCropBox.getY1();
                if (pageTargetPoint.x > leftEdge + pageWidth - stampWidth) {
                    pageTargetPoint.x = leftEdge + pageWidth - stampWidth;
                }

                if (pageTargetPoint.x < leftEdge) {
                    pageTargetPoint.x = leftEdge;
                }

                if (pageTargetPoint.y > bottomEdge + pageHeight - stampHeight) {
                    pageTargetPoint.y = bottomEdge + pageHeight - stampHeight;
                }

                if (pageTargetPoint.y < bottomEdge) {
                    pageTargetPoint.y = bottomEdge;
                }

                stamper.setPosition(pageTargetPoint.x, pageTargetPoint.y);
            } else {
                stamper.setPosition(0.0D, 0.0D);
            }

            stamper.setAsAnnotation(true);
            int stampRotation = (4 - viewRotation) % 4;
            stamper.setRotation((double) stampRotation * 90.0D + (double) imageRotation);
            stamper.stampImage(doc, img, new PageSet(pageNum));
            int numAnnots = page.getNumAnnots();
            Annot annot = page.getAnnot(numAnnots - 1);
            Obj obj = annot.getSDFObj();
            obj.putNumber("pdftronImageStampRotation", 0.0D);
            if (annot.isMarkup()) {
                Markup markup = new Markup(annot);
                this.setAuthor(markup);
            }

            this.setAnnot(annot, pageNum);
            this.buildAnnotBBox();
            this.mPdfViewCtrl.update(annot, pageNum);
            this.raiseAnnotationAddedEvent(annot, pageNum);
            SharedPreferences settings = Tool.getToolPreferences(this.mPdfViewCtrl.getContext());
            String recentlyUsedFiles = settings.getString("image_stamper_most_recently_used_files", "");
            String[] recentlyUsedFilesArray = recentlyUsedFiles.split(" ");
            Boolean recentlyUsed = false;
            if (filePath != null) {
                String[] var50 = recentlyUsedFilesArray;
                int var51 = recentlyUsedFilesArray.length;

                int i;
                for (i = 0; i < var51; ++i) {
                    String recentlyUsedFile = var50[i];
                    if (filePath.equals(recentlyUsedFile)) {
                        recentlyUsed = true;
                        AnalyticsHandlerAdapter.getInstance().sendEvent(8, "stamper recent file");
                    }
                }

                if (!recentlyUsed) {
                    int length = recentlyUsedFilesArray.length < 6 ? recentlyUsedFilesArray.length : 5;
                    StringBuilder str = new StringBuilder(filePath);
                    i = 0;

                    while (true) {
                        if (i >= length) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("image_stamper_most_recently_used_files", str.toString());
                            editor.apply();
                            break;
                        }

                        str.append(" ").append(recentlyUsedFilesArray[i]);
                        ++i;
                    }
                }
            }

            var69 = true;
        } catch (Exception var57) {
            AnalyticsHandlerAdapter.getInstance().sendException(var57);
            boolean var7 = false;
            return var7;
        } finally {
            if (shouldUnlock) {
                this.mPdfViewCtrl.docUnlock();
            }

            Utils.closeQuietly(filter);
            this.mTargetPoint = null;
            this.safeSetNextToolMode();
        }

        return var69;
    }
}

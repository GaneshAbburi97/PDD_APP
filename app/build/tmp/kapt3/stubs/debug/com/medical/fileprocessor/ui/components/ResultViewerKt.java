package com.medical.fileprocessor.ui.components;

import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.*;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import com.medical.fileprocessor.model.ProcessingResult;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0003\u001a$\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\tH\u0007\u00a8\u0006\n"}, d2 = {"MetadataItem", "", "label", "", "value", "ResultViewer", "result", "Lcom/medical/fileprocessor/model/ProcessingResult;", "onOpenExternal", "Lkotlin/Function1;", "app_debug"})
public final class ResultViewerKt {
    
    /**
     * Component to display the results of a processed medical file.
     * Includes an image preview (if available), file details, and AI metadata.
     *
     * @param result The [ProcessingResult] data to display.
     * @param onOpenExternal Action to open the result in an external viewer or browser.
     */
    @androidx.compose.runtime.Composable()
    public static final void ResultViewer(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.model.ProcessingResult result, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenExternal) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MetadataItem(java.lang.String label, java.lang.String value) {
    }
}
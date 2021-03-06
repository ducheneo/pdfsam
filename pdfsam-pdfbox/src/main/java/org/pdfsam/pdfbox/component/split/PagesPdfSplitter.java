/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 06/mar/2015
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.pdfbox.component.split;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.pdfsam.pdfbox.component.PagesExtractor;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.split.SplitPages;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract component providing a skeletal implementation of the split execution.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of parameters the splitter needs to have all the information necessary to perform the split.
 */
public class PagesPdfSplitter<T extends AbstractSplitByPageParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(PagesPdfSplitter.class);

    private PDDocument document;
    private T parameters;
    private int totalPages;
    private MultipleOutputWriter outputWriter;
    private NextOutputStrategy splitPages;

    /**
     * Creates a new splitter that reads pages from the given document
     */
    public PagesPdfSplitter(PDDocument document, T parameters) {
        this.document = document;
        this.parameters = parameters;
        this.totalPages = document.getNumberOfPages();
        this.outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
        this.splitPages = new SplitPages(parameters.getPages(totalPages));
    }

    public void split(NotifiableTaskMetadata taskMetadata) throws TaskException {
        this.splitPages.ensureIsValid();
        PagesExtractor extractor = null;
        try {
            int outputDocumentsCounter = 0;
            for (int page = 1; page <= totalPages; page++) {
                if (this.splitPages.isOpening(page)) {
                    LOG.debug("Starting split at page {} of the original document", page);
                    outputDocumentsCounter++;
                    extractor = new PagesExtractor(document);
                }
                LOG.trace("Retaining page {} of the original document", page);
                extractor.retain(page);
                notifyEvent(taskMetadata).stepsCompleted(page).outOf(totalPages);
                if (this.splitPages.isClosing(page) || page == totalPages) {
                    File tmpFile = createTemporaryPdfBuffer();
                    LOG.debug("Created output temporary buffer {}", tmpFile);
                    extractor.setVersion(parameters.getVersion());
                    extractor.save(tmpFile);
                    String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                            nameRequest().page(page).originalName(parameters.getSource().getName())
                                    .fileNumber(outputDocumentsCounter));
                    outputWriter.addOutput(file(tmpFile).name(outName));

                    nullSafeCloseQuietly(extractor);
                    LOG.debug("Ending split at page {} of the original document", page);
                }
            }
        } finally {
            nullSafeCloseQuietly(extractor);
        }
        parameters.getOutput().accept(outputWriter);
    }

}

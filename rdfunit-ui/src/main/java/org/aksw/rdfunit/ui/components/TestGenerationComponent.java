package org.aksw.rdfunit.ui.components;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.ui.*;
import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.executors.TestGeneratorExecutorMonitor;
import org.aksw.rdfunit.ui.RDFUnitUISession;

import java.io.File;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/20/13 5:20 PM
 */
public class TestGenerationComponent extends VerticalLayout implements TestGeneratorExecutorMonitor {

    private final Table resultsTable = new Table("Test Results");

    public TestGenerationComponent() {
        initLayout();

    }

    public void initLayout() {
        this.setWidth("100%");

        resultsTable.setHeight("200px");
        resultsTable.addContainerProperty("Type", String.class, null);
        resultsTable.addContainerProperty("URI", Link.class, null);
        resultsTable.addContainerProperty("Automatic", AbstractComponent.class, null);
        resultsTable.addContainerProperty("Manual", AbstractComponent.class, null);
        resultsTable.setColumnCollapsingAllowed(true);
        resultsTable.setSelectable(true);
        resultsTable.setVisible(false);
        TestGenerationComponent.this.addComponent(resultsTable);
    }

    public void clearTableRowsAndHide() {
        resultsTable.removeAllItems();
        resultsTable.setVisible(false);
    }

    @Override
    public void generationStarted(final Source source, final long numberOfSources) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                resultsTable.setVisible(true);
                resultsTable.setPageLength((int) Math.min(7, numberOfSources));

            }
        });
    }

    @Override
    public void sourceGenerationStarted(final Source source, TestGenerationType generationType) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                Link uriLink = new Link(source.getUri(), new ExternalResource(source.getUri()));
                uriLink.setTargetName("_blank");
                resultsTable.addItem(new Object[]{
                        source.getClass().getSimpleName(), uriLink, new Label("-"), new Label("-")}, source);
                resultsTable.setCurrentPageFirstItemIndex(resultsTable.getCurrentPageFirstItemIndex() + 1);
            }
        });
    }

    @Override
    public void sourceGenerationExecuted(final Source source, final TestGenerationType generationType, final long testsCreated) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                Item item = resultsTable.getItem(source);
                if (testsCreated == 0)
                    return;

                String column = (generationType.equals(TestGenerationType.AutoGenerated) ? "Automatic" : "Manual");
                Property<Link> statusProperty = item.getItemProperty(column);
                String fileName = "";
                if (generationType.equals(TestGenerationType.AutoGenerated))
                    fileName = CacheUtils.getSourceAutoTestFile(RDFUnitUISession.getBaseDir() + "tests/", source);
                else
                    fileName = CacheUtils.getSourceManualTestFile(RDFUnitUISession.getBaseDir() + "tests/", source);

                statusProperty.setValue(new Link("" + testsCreated, new FileResource(new File(fileName))));
            }
        });
    }

    @Override
    public void generationFinished() {

        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {

            }
        });

    }
}
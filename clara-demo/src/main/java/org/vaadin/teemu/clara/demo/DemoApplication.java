package org.vaadin.teemu.clara.demo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.vaadin.teemu.clara.Clara;
import org.vaadin.teemu.clara.inflater.LayoutInflaterException;

import com.vaadin.Application;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class DemoApplication extends Application {

    private DemoController controller;
    private TextArea xmlArea;
    private HorizontalSplitPanel split = new HorizontalSplitPanel();
    private Window mainWindow;

    @Override
    public void init() {
        setTheme("clara");
        setMainWindow(mainWindow = new Window());

        controller = new DemoController(mainWindow);
        mainWindow.setContent(split);

        VerticalLayout editor = new VerticalLayout();
        editor.setSpacing(true);
        editor.setMargin(false, false, false, true);
        editor.setHeight("100%");
        editor.addComponent(xmlArea = createXmlArea());
        editor.setExpandRatio(xmlArea, 1.0f);
        editor.addComponent(createUpdateButton());

        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setMargin(true);
        wrapper.setSizeFull();
        wrapper.addComponent(createLogo());
        wrapper.addComponent(editor);
        wrapper.setExpandRatio(editor, 1.0f);
        split.setFirstComponent(wrapper);
        updateLayout();
    }

    private Component createLogo() {
        Embedded logo = new Embedded(null, new ThemeResource(
                "clara-logo-simplified-90x90.png"));
        logo.setHeight("90px");
        logo.setWidth("90px");
        return logo;
    }

    private TextArea createXmlArea() {
        TextArea area = new TextArea();
        area.setStyleName("xml-area");
        area.setCaption("XML");
        area.setSizeFull();
        area.setValue(readStartingPoint()); // initial value
        return area;
    }

    private Button createUpdateButton() {
        return new Button("Update", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                updateLayout();
            }
        });
    }

    /**
     * Returns the content of {@code demo-layout.xml} as a {@link String}.
     */
    private String readStartingPoint() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getClass()
                    .getClassLoader().getResourceAsStream("demo-layout.xml")));
            StringBuilder xml = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xml.append(line);
                xml.append("\n");
            }
            return xml.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void updateLayout() {
        try {
            VerticalLayout wrapper = new VerticalLayout();
            wrapper.setMargin(true);
            wrapper.setSizeFull();

            Component c = Clara.create(new ByteArrayInputStream(xmlArea
                    .getValue().toString().getBytes()), controller);

            Panel p = new Panel("Result");
            p.setStyleName("result");
            p.addComponent(c);
            p.setSizeFull();
            wrapper.addComponent(p);
            split.replaceComponent(split.getSecondComponent(), wrapper);
        } catch (LayoutInflaterException e) {
            mainWindow.showNotification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
        }
    }

}

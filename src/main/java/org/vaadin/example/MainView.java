package org.vaadin.example;

import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
        ** VERSION 1 **
        */
@Route("")
@PWA(name = "TAP Todo", shortName = "TAP Todo")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")

public class MainView extends VerticalLayout {

    private TaskService service = TaskService.getInstance();
    private Grid<Task> grid = new Grid<>(Task.class);

    private TextField filterText = new TextField();

    private TaskForm form = new TaskForm(this);

    public MainView() {

        filterText.setPlaceholder("Filter by list...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> updateList());

        Button addTaskBtn = new Button("Add new task");
        addTaskBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();      //clear removes a possible previous selection from the Grid
            form.setTask(new Task());           //setTask instantiates a new task object and passes it to the TaskForm for editing.
        });

        grid.setColumns("taskTitle", "list", "priority", "status");

        HorizontalLayout mainContent = new HorizontalLayout(grid, form);
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addTaskBtn);

        toolbar.setSpacing(true);

        mainContent.setSpacing(true);
        mainContent.setSizeFull();

        grid.setWidth("65%");
        form.setWidth("35%");

        add(toolbar, mainContent);

        setSizeFull();

        updateList();

        form.setTask(null);

        grid.asSingleSelect().addValueChangeListener(event ->           //addValueChangeListener adds a listener to the Grid
                form.setTask(grid.asSingleSelect().getValue()));    //setTask sets the selected task in the TaskForm
                                                                        //The getValue() method returns the Task in the selected row or null if there’s no selection, effectively showing or hiding the form accordingly.
    }

    public void updateList() {
        grid.setItems(service.findAll(filterText.getValue()));  // enviar value a backend
                                                                // filterText.getValue() returns the current string in the text field
    }
}

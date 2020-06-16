package org.vaadin.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.interceptor.InvokationOrder.PRIORITY;

public class TaskForm extends FormLayout {
    private TextField taskTitle = new TextField("Title");
    private TextArea taskDescription = new TextArea("Description");
    private ComboBox<TaskStatus> status = new ComboBox<>("Status");
    private TextField list = new TextField("List");
    private ComboBox<TaskPriority> priority = new ComboBox<>("Priority");
    private DatePicker deadLine = new DatePicker("Deadline");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private Binder<Task> binder = new Binder<>(Task.class);

    private MainView mainView;

    private TaskService service = TaskService.getInstance();
    
    private final Map<String, Component> manualBoundComponents = new HashMap<>();

    public TaskForm(MainView mainView) {
        this.mainView = mainView;

        status.setItems(TaskStatus.values());
        priority.setItems(TaskPriority.values());

        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(taskTitle, taskDescription, list, priority, status, deadLine, buttons);

        manualBoundComponents.put( "taskTitle", taskTitle );
        binder.forField( taskTitle )
                .withNullRepresentation( "" )
                .bind( Task::getTaskTitle, Task::setTaskTitle);

        manualBoundComponents.put( "taskDescription", taskDescription );
        binder.forField( taskDescription )
                .withNullRepresentation( "" )
                .bind( Task::getTaskDescription, Task::setTaskDescription);

        manualBoundComponents.put( "status", status );
        binder.forField( status )
        		.withNullRepresentation( TaskStatus.Pending )
                .bind( Task::getStatus, Task::setStatus);

        manualBoundComponents.put( "list", list );
        binder.forField( list )
                .withNullRepresentation( "" )
                .bind( Task::getList, Task::setList);

        manualBoundComponents.put( "priority", priority );
        binder.forField( priority )
        		.withNullRepresentation( TaskPriority.Default )
                .bind( Task::getPriority, Task::setPriority);

        manualBoundComponents.put( "deadLine", deadLine );
        binder.forField( deadLine )
        		.withNullRepresentation( LocalDate.of(2021, 1, 1) )
                .bind( Task::getDeadLine, Task::setDeadLine);
        
        binder.bindInstanceFields(this);

        save.addClickListener(event -> {
            try {
                save();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        delete.addClickListener(event -> {
            try {
                delete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void setTask(Task task) {
        binder.setBean(task);

        if (task == null) {
            setVisible(false);
        } else {
            setVisible(true);
            taskTitle.focus();
        }
    }

    private void save() throws IOException, InterruptedException {
        Task task = binder.getBean(); //gets the task instance that was bound to the input fields of the form.
        service.save(task); //performs the save action in the backend.

        if(service.findAll().stream().anyMatch(task1 -> task1.getId() == task.getId())){
            service.updateTask(task); // The list with all tasks contains the id -> PUT
        } else {service.postTask(task);} // The list with all tasks does NOT contain the id -> POST

        mainView.updateList(); //updates the list of tasks in the main view.
        setTask(null); //hides the form.
    }

    private void delete() throws IOException, InterruptedException {
        Task task = binder.getBean();
        service.delete(task);
        mainView.updateList();
        setTask(null);
    }
}

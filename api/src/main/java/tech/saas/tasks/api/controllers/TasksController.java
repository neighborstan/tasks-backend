package tech.saas.tasks.api.controllers;

import org.springframework.http.ResponseEntity;
import tech.saas.tasks.api.models.Task;

import java.util.List;

public class TasksController implements TasksApi {


    @Override
    public ResponseEntity<Task> completeTask(String id) {
        return null;
    }

    @Override
    public ResponseEntity<List<Task>> getTasks() {
        return null;
    }
}

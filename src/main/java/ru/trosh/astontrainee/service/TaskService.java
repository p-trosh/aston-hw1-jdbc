package ru.trosh.astontrainee.service;

import ru.trosh.astontrainee.model.task.TaskFullResponse;
import ru.trosh.astontrainee.model.task.TaskRequest;
import ru.trosh.astontrainee.model.task.TaskShortResponse;

import java.util.List;

public interface TaskService {

    List<TaskShortResponse> selectAll();

    TaskFullResponse selectById(Long id);

    TaskFullResponse create(TaskRequest request);

    TaskFullResponse update(Long id, TaskRequest request);

    void delete(Long id);

    void addTaskToWorker(Long taskId, Long workerId);

    void deleteTaskFromWorker(Long taskId, Long workerId);
}

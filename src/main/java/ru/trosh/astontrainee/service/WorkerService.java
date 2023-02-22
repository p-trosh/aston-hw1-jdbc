package ru.trosh.astontrainee.service;

import ru.trosh.astontrainee.model.worker.WorkerFullResponse;
import ru.trosh.astontrainee.model.worker.WorkerRequest;
import ru.trosh.astontrainee.model.worker.WorkerShortResponse;

import java.util.List;

public interface WorkerService {

    List<WorkerShortResponse> selectAll();

    WorkerFullResponse selectById(Long id);

    Long create(WorkerRequest request);

    WorkerFullResponse update(Long id, WorkerRequest request);

    void delete(Long id);
}
package ru.trosh.astontrainee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.trosh.astontrainee.model.task.TaskFullResponse;
import ru.trosh.astontrainee.model.task.TaskRequest;
import ru.trosh.astontrainee.service.DepartmentService;
import ru.trosh.astontrainee.service.TaskService;
import ru.trosh.astontrainee.service.WorkerService;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping()
    public String tasks(Model model) {
        model.addAttribute("tasks", taskService.selectAll());
        return "task/index";
    }

    @GetMapping("view/{id}")
    public String task(@PathVariable long id, Model model) {
        TaskFullResponse task = taskService.selectById(id);
        model.addAttribute("task", task);
        model.addAttribute("availableWorkers", workerService.getAvailableWorkersByTask(task));
        return "task/view";
    }

    @DeleteMapping("delete/{id}")
    public String deleteTask(@PathVariable long id, Model model) {
        taskService.delete(id);
        return "redirect:/task";
    }

    @GetMapping("create")
    public String createTask(Model model) {
        model.addAttribute("departments", departmentService.selectAll());
        return "task/edit";
    }

    @GetMapping("edit/{id}")
    public String editTask(@PathVariable long id, Model model) {
        model.addAttribute("departments", departmentService.selectAll());
        model.addAttribute("task", taskService.selectById(id));
        return "task/edit";
    }

    @PutMapping("edit/{id}")
    public String editTask(
            @PathVariable long id,
            @ModelAttribute final TaskRequest task,
            Model model) {
        taskService.update(id, task);
        return "redirect:/task";
    }

    @PostMapping("create")
    public String createTask(@ModelAttribute final TaskRequest task, Model model) {
        taskService.create(task);
        return "redirect:/task";
    }

    @PostMapping("{taskId}/{workerId}")
    public String addTaskToWorker(@PathVariable long taskId, @PathVariable long workerId, Model model) {
        taskService.addTaskToWorker(taskId, workerId);
        return "redirect:/task/view/" + taskId;
    }

    @DeleteMapping("{taskId}/{workerId}")
    public String deleteTaskFromWorker(@PathVariable long taskId, @PathVariable long workerId, Model model) {
        taskService.deleteTaskFromWorker(taskId, workerId);
        return "redirect:/task/view/" + taskId;
    }




}

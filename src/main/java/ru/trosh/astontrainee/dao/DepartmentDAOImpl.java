package ru.trosh.astontrainee.dao;

import org.springframework.stereotype.Component;
import ru.trosh.astontrainee.config.JDBCConnectionManager;
import ru.trosh.astontrainee.model.Department;
import ru.trosh.astontrainee.model.Task;
import ru.trosh.astontrainee.model.Worker;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DepartmentDAOImpl implements DepartmentDAO {
    private static final String SELECT_ALL_DEPARTMENTS = "SELECT * FROM department;";
    private static final String DELETE_DEPARTMENT = "DELETE FROM department WHERE id = ? ;";
    private static final String CREATE_DEPARTMENT = "INSERT INTO department (name) VALUES ( ? );";
    private static final String UPDATE_DEPARTMENT = "UPDATE department SET name = ? WHERE id = ? ;";
    private static final String SELECT_DEPARTMENT =
            "SELECT" +
            "  department.id AS d_id," +
            "  department.name AS d_name," +
            "  task.id AS t_id," +
            "  task.title AS t_title," +
            "  task.description AS t_description," +
            "  worker.id AS w_id," +
            "  worker.first_name AS w_first_name," +
            "  worker.last_name AS w_last_name" +
            " FROM department " +
            " LEFT JOIN task on task.department = department.id" +
            " LEFT JOIN worker on worker.department = department.id  " +
            " WHERE department.id = ? ;";

    public void create(Department department) {
        execute(CREATE_DEPARTMENT, department.getName());
    }

    public List<Department> selectAll() {
        try (Connection connection = JDBCConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_DEPARTMENTS)) {
            ResultSet resultSet = statement.executeQuery();
            List<Department> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(Department.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .build());
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Department selectById(Long id) {
        try (Connection connection = JDBCConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DEPARTMENT)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            return map(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void delete(Long id) {
        execute(DELETE_DEPARTMENT, id);
    }

    public void update(Department department) {
        execute(UPDATE_DEPARTMENT, department.getName(), department.getId());
    }

    private int execute(final String sql, final Object... values) {
        try (Connection connection = JDBCConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private Department map(final ResultSet resultSet) throws SQLException {
        Department department = null;
        if (resultSet.next()) {
            Set<Worker> workerSet = new HashSet<>();
            Set<Task> taskSet = new HashSet<>();
            department = Department.builder()
                    .id(resultSet.getLong("d_id"))
                    .name(resultSet.getString("d_name"))
                    .build();
            do {
                Long taskId = resultSet.getLong("t_id");
                Long workerId = resultSet.getLong("w_id");
                if (taskId != 0) {
                    taskSet.add(Task.builder()
                            .id(taskId)
                            .title(resultSet.getString("t_title"))
                            .description(resultSet.getString("t_description"))
                            .build());
                }
                if (workerId != 0) {
                    workerSet.add(Worker.builder()
                            .id(workerId)
                            .firstName(resultSet.getString("w_first_name"))
                            .lastName(resultSet.getString("w_last_name"))
                            .build());
                }
            } while (resultSet.next());
            department.setTasks(new ArrayList<>(taskSet));
            department.setWorkers(new ArrayList<>(workerSet));
        }
        return department;
    }
}

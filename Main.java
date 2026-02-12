#!/bin/bash
# Gera o projeto completo com todo código preenchido e zipa em 1 comando

PROJECT="nexusone"
ZIPFILE="nexusone_full_project.zip"

# Remove versão anterior
rm -rf $PROJECT
rm -f $ZIPFILE

echo "Criando estrutura de pastas..."
mkdir -p $PROJECT/{finance/{api,application,event,domain,infrastructure/persistence,infrastructure/mapper},task/{api,dto,application,event,domain,infrastructure/persistence,infrastructure/mapper},analytics/{api,application/listener,domain,infrastructure/persistence,infrastructure/redis},auth/{api,application,domain},shared/{base,event,util}}


# Finance

cat > $PROJECT/finance/api/FinanceController.java << EOF
package finance.api;

import finance.application.FinanceService;
import finance.application.event.TransactionCreatedEvent;
import finance.domain.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService service;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/transaction")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction t = service.createTransaction(transaction);
        publisher.publishEvent(new TransactionCreatedEvent(this, t.getUserId(), t.getAmount()));
        return ResponseEntity.ok(t);
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getTransactions(userId));
    }
}
EOF

cat > $PROJECT/finance/application/FinanceService.java << EOF
package finance.application;

import finance.domain.Transaction;
import finance.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final TransactionRepository repository;

    public Transaction createTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    public List<Transaction> getTransactions(Long userId) {
        return repository.findByUserId(userId);
    }
}
EOF

cat > $PROJECT/finance/application/event/TransactionCreatedEvent.java << EOF
package finance.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class TransactionCreatedEvent extends ApplicationEvent {

    private final Long userId;
    private final BigDecimal amount;

    public TransactionCreatedEvent(Object source, Long userId, BigDecimal amount) {
        super(source);
        this.userId = userId;
        this.amount = amount;
    }
}
EOF

cat > $PROJECT/finance/domain/Transaction.java << EOF
package finance.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private TransactionType type;
}
EOF

cat > $PROJECT/finance/domain/TransactionType.java << EOF
package finance.domain;

public enum TransactionType {
    INCOME, EXPENSE
}
EOF

cat > $PROJECT/finance/domain/repository/TransactionRepository.java << EOF
package finance.domain.repository;

import finance.domain.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByUserId(Long userId);
}
EOF

cat > $PROJECT/finance/infrastructure/persistence/TransactionEntity.java << EOF
package finance.infrastructure.persistence;

import finance.domain.TransactionType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
EOF

cat > $PROJECT/finance/infrastructure/persistence/TransactionRepositoryImpl.java << EOF
package finance.infrastructure.persistence;

import finance.domain.Transaction;
import finance.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setUserId(transaction.getUserId());
        entity.setAmount(transaction.getAmount());
        entity.setType(transaction.getType());
        entity = jpaRepository.save(entity);
        transaction.setId(entity.getId());
        return transaction;
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(e -> new Transaction(e.getId(), e.getUserId(), e.getAmount(), e.getType()))
                .collect(Collectors.toList());
    }
}
EOF

cat > $PROJECT/finance/infrastructure/persistence/TransactionJpaRepository.java << EOF
package finance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByUserId(Long userId);
}
EOF

cat > $PROJECT/finance/infrastructure/mapper/TransactionMapper.java << EOF
package finance.infrastructure.mapper;

import finance.domain.Transaction;
import finance.infrastructure.persistence.TransactionEntity;

public class TransactionMapper {

    public static TransactionEntity toEntity(Transaction t) {
        TransactionEntity e = new TransactionEntity();
        e.setId(t.getId());
        e.setUserId(t.getUserId());
        e.setAmount(t.getAmount());
        e.setType(t.getType());
        return e;
    }

    public static Transaction toDomain(TransactionEntity e) {
        return new Transaction(e.getId(), e.getUserId(), e.getAmount(), e.getType());
    }
}
EOF

# -----------------------------
# Task
# -----------------------------
cat > $PROJECT/task/api/TaskController.java << EOF
package task.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.application.TaskService;
import task.application.event.TaskCompletedEvent;
import task.domain.Task;
import task.api.dto.CreateTaskRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;
    private final ApplicationEventPublisher publisher;

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = service.createTask(request);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long taskId) {
        Task task = service.completeTask(taskId);
        publisher.publishEvent(new TaskCompletedEvent(this, task.getUserId(), task.getId()));
        return ResponseEntity.ok(task);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getTasksByUser(userId));
    }
}
EOF

cat > $PROJECT/task/api/dto/CreateTaskRequest.java << EOF
package task.api.dto;

import lombok.Data;
import task.domain.TaskPriority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateTaskRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TaskPriority priority;
}
EOF

cat > $PROJECT/task/application/TaskService.java << EOF
package task.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.api.dto.CreateTaskRequest;
import task.domain.Task;
import task.domain.TaskStatus;
import task.domain.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    public Task createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setUserId(request.getUserId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(TaskStatus.PENDING);
        return repository.save(task);
    }

    public Task completeTask(Long taskId) {
        Task task = repository.findById(taskId);
        task.setStatus(TaskStatus.COMPLETED);
        return repository.save(task);
    }

    public List<Task> getTasksByUser(Long userId) {
        return repository.findAllByUser(userId);
    }
}
EOF

cat > $PROJECT/task/application/event/TaskCompletedEvent.java << EOF
package task.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskCompletedEvent extends ApplicationEvent {
    private final Long userId;
    private final Long taskId;

    public TaskCompletedEvent(Object source, Long userId, Long taskId) {
        super(source);
        this.userId = userId;
        this.taskId = taskId;
    }
}
EOF

cat > $PROJECT/task/domain/Task.java << EOF
package task.domain;

import lombok.Data;

@Data
public class Task {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
}
EOF

cat > $PROJECT/task/domain/TaskPriority.java << EOF
package task.domain;

public enum TaskPriority {
    LOW, MEDIUM, HIGH
}
EOF

cat > $PROJECT/task/domain/TaskStatus.java << EOF
package task.domain;

public enum TaskStatus {
    PENDING, COMPLETED
}
EOF

cat > $PROJECT/task/domain/repository/TaskRepository.java << EOF
package task.domain.repository;

import task.domain.Task;

import java.util.List;

public interface TaskRepository {
    Task save(Task task);
    Task findById(Long id);
    List<Task> findAllByUser(Long userId);
}
EOF

cat > $PROJECT/task/infrastructure/persistence/TaskEntity.java << EOF
package task.infrastructure.persistence;

import lombok.Data;
import task.domain.TaskPriority;
import task.domain.TaskStatus;

import javax.persistence.*;

@Entity
@Table(name = "tasks")
@Data
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}
EOF

cat > $PROJECT/task/infrastructure/persistence/TaskRepositoryImpl.java << EOF
package task.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import task.domain.Task;
import task.domain.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final TaskJpaRepository jpaRepository;

    @Override
    public Task save(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setUserId(task.getUserId());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setPriority(task.getPriority());
        entity.setStatus(task.getStatus());
        entity = jpaRepository.save(entity);
        task.setId(entity.getId());
        return task;
    }

    @Override
    public Task findById(Long id) {
        TaskEntity entity = jpaRepository.findById(id).orElseThrow();
        Task task = new Task();
        task.setId(entity.getId());
        task.setUserId(entity.getUserId());
        task.setTitle(entity.getTitle());
        task.setDescription(entity.getDescription());
        task.setPriority(entity.getPriority());
        task.setStatus(entity.getStatus());
        return task;
    }

    @Override
    public List<Task> findAllByUser(Long userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(e -> {
                    Task t = new Task();
                    t.setId(e.getId());
                    t.setUserId(e.getUserId());
                    t.setTitle(e.getTitle());
                    t.setDescription(e.getDescription());
                    t.setPriority(e.getPriority());
                    t.setStatus(e.getStatus());
                    return t;
                })
                .collect(Collectors.toList());
    }
}
EOF

cat > $PROJECT/task/infrastructure/persistence/TaskJpaRepository.java << EOF
package task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByUserId(Long userId);
}
EOF

cat > $PROJECT/task/infrastructure/mapper/TaskMapper.java << EOF
package task.infrastructure.mapper;

import task.domain.Task;
import task.infrastructure.persistence.TaskEntity;

public class TaskMapper {

    public static TaskEntity toEntity(Task t) {
        TaskEntity e = new TaskEntity();
        e.setId(t.getId());
        e.setUserId(t.getUserId());
        e.setTitle(t.getTitle());
        e.setDescription(t.getDescription());
        e.setPriority(t.getPriority());
        e.setStatus(t.getStatus());
        return e;
    }

    public static Task toDomain(TaskEntity e) {
        Task t = new Task();
        t.setId(e.getId());
        t.setUserId(e.getUserId());
        t.setTitle(e.getTitle());
        t.setDescription(e.getDescription());
        t.setPriority(e.getPriority());
        t.setStatus(e.getStatus());
        return t;
    }
}
EOF

# Analytics

cat > $PROJECT/analytics/api/AnalyticsController.java << EOF
package analytics.api;

import analytics.application.AnalyticsService;
import analytics.domain.DashboardMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashboardMetrics> getDashboard(@PathVariable Long userId) {
        DashboardMetrics metrics = service.getDashboardMetrics(userId);
        return ResponseEntity.ok(metrics);
    }
}
EOF

cat > $PROJECT/analytics/application/AnalyticsService.java << EOF
package analytics.application;

import analytics.domain.DashboardMetrics;
import analytics.infrastructure.persistence.DashboardCacheRepository;
import finance.application.event.TransactionCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import task.application.event.TaskCompletedEvent;

import java.math.BigDecimal;

@Service
public class AnalyticsService {

    private final DashboardCacheRepository cacheRepository;

    public AnalyticsService(DashboardCacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @EventListener
    public void handleTransactionEvent(TransactionCreatedEvent event) {
        DashboardMetrics metrics = cacheRepository.getMetrics(event.getUserId());
        if (metrics == null) metrics = new DashboardMetrics(event.getUserId());
        metrics.addTransaction(event.getAmount());
        cacheRepository.saveMetrics(metrics);
    }

    @EventListener
    public void handleTaskEvent(TaskCompletedEvent event) {
        DashboardMetrics metrics = cacheRepository.getMetrics(event.getUserId());
        if (metrics == null) metrics = new DashboardMetrics(event.getUserId());
        metrics.incrementCompletedTasks();
        cacheRepository.saveMetrics(metrics);
    }

    public DashboardMetrics getDashboardMetrics(Long userId) {
        DashboardMetrics metrics = cacheRepository.getMetrics(userId);
        return metrics != null ? metrics : new DashboardMetrics(userId);
    }
}
EOF

cat > $PROJECT/analytics/application/listener/AnalyticsListener.java << EOF
package analytics.application.listener;

import analytics.application.AnalyticsService;
import finance.application.event.TransactionCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import task.application.event.TaskCompletedEvent;

@Component
public class AnalyticsListener {

    private final AnalyticsService service;

    public AnalyticsListener(AnalyticsService service) {
        this.service = service;
    }

    @EventListener
    public void onTransactionCreated(TransactionCreatedEvent event) {
        service.handleTransactionEvent(event);
    }

    @EventListener
    public void onTaskCompleted(TaskCompletedEvent event) {
        service.handleTaskEvent(event);
    }
}
EOF

cat > $PROJECT/analytics/domain/DashboardMetrics.java << EOF
package analytics.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardMetrics {

    private Long userId;
    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpense = BigDecimal.ZERO;
    private int completedTasks = 0;

    public DashboardMetrics(Long userId) {
        this.userId = userId;
    }

    public void addTransaction(BigDecimal amount) {
        if (amount.signum() >= 0) totalIncome = totalIncome.add(amount);
        else totalExpense = totalExpense.add(amount.abs());
    }

    public void incrementCompletedTasks() {
        completedTasks++;
    }
}
EOF

cat > $PROJECT/analytics/infrastructure/persistence/DashboardCacheRepository.java << EOF
package analytics.infrastructure.persistence;

import analytics.domain.DashboardMetrics;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class DashboardCacheRepository {

    private final RedisTemplate<String, DashboardMetrics> redisTemplate;

    public DashboardCacheRepository(RedisTemplate<String, DashboardMetrics> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveMetrics(DashboardMetrics metrics) {
        redisTemplate.opsForValue().set("dashboard:" + metrics.getUserId(), metrics, Duration.ofMinutes(5));
    }

    public DashboardMetrics getMetrics(Long userId) {
        return redisTemplate.opsForValue().get("dashboard:" + userId);
    }
}
EOF

cat > $PROJECT/analytics/infrastructure/redis/RedisConfig.java << EOF
package analytics.infrastructure.redis;

import analytics.domain.DashboardMetrics;
import org.springframework

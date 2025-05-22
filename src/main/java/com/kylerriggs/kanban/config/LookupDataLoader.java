package com.kylerriggs.kanban.config;

import com.kylerriggs.kanban.priority.Priority;
import com.kylerriggs.kanban.priority.PriorityRepository;
import com.kylerriggs.kanban.status.Status;
import com.kylerriggs.kanban.status.StatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class LookupDataLoader implements CommandLineRunner {

    private final List<String> DEFAULT_STATUSES = List.of("Backlog", "Todo", "In Progress", "Done", "Canceled");
    private final List<String> DEFAULT_PRIORITIES = List.of("Low", "Normal", "High", "Urgent");

    @PersistenceContext
    private EntityManager em;

    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;

    public LookupDataLoader(StatusRepository statusRepo, PriorityRepository priorityRepo) {
        this.statusRepository = statusRepo;
        this.priorityRepository = priorityRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedStatuses();
        seedPriorities();
    }

    private void seedStatuses() {
        if (statusRepository.count() < DEFAULT_STATUSES.size()) {
            statusRepository.deleteAllInBatch();

            // reset ID auto increment back to 1
            em.createNativeQuery("TRUNCATE TABLE statuses RESTART IDENTITY CASCADE").executeUpdate();

            DEFAULT_STATUSES.forEach(status -> statusRepository.save(new Status(null, status)));
        }
    }

    private void seedPriorities() {
        if (priorityRepository.count() < DEFAULT_PRIORITIES.size()) {
            priorityRepository.deleteAllInBatch();

            // reset ID auto increment back to 1
            em.createNativeQuery("TRUNCATE TABLE priorities RESTART IDENTITY CASCADE").executeUpdate();

            DEFAULT_PRIORITIES.forEach(priority -> priorityRepository.save(new Priority(null, priority)));
        }
    }

}

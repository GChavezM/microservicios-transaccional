package bo.edu.ucb.ms.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.ms.accounting.entity.Journal;

public interface JournalRepository extends JpaRepository<Journal, Integer> {

}

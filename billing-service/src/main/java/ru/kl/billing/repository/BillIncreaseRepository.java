package ru.kl.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kl.billing.model.BillIncrease;

public interface BillIncreaseRepository extends JpaRepository<BillIncrease, Long> {
}

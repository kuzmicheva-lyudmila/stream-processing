package ru.kl.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kl.billing.model.BillDecrease;

public interface BillDecreaseRepository extends JpaRepository<BillDecrease, Long> {
}

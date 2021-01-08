package ru.kl.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kl.billing.model.Bill;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;

public interface BillRepository extends JpaRepository<Bill, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select b from bill b where b.clientId = :clientId")
    Bill getBillBalanceWithLock(@Param("clientId") String clientId);

    @Modifying
    @Query(
            value = "update bill set balance = balance + :diff, update_time = :update_time where client_id = :client_id",
            nativeQuery = true
    )
    void setBillBalance(
            @Param("diff") Long diff,
            @Param("client_id") String clientId,
            @Param("update_time") LocalDateTime updateTime
    );
}

package ru.kl.billing.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.kl.billing.service.BillingService;

@RestController
@RequestMapping("billing")
@RequiredArgsConstructor
public class BillingController {

    private static final String AUTH_CLIENT_HEADER = "X-Auth-Client";

    private final BillingService billingService;

    @GetMapping
    public Long getBalance(@RequestHeader(value = AUTH_CLIENT_HEADER) String clientId) {
        return billingService.getBalance(clientId);
    }

    @PostMapping("/increase")
    public void increase(
            @RequestHeader(value = AUTH_CLIENT_HEADER) String clientId,
            @RequestBody Long amount
    ) {
        billingService.increase(clientId, amount);
    }

    @PostMapping
    public void createBill(@RequestHeader(value = AUTH_CLIENT_HEADER) String clientId) {
        billingService.createBill(clientId);
    }
}

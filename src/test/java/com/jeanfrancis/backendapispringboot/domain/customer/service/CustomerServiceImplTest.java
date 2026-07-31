package com.jeanfrancis.backendapispringboot.domain.customer.service;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import com.jeanfrancis.backendapispringboot.domain.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer inputCustomer;

    @BeforeEach
    void setUp() {
        inputCustomer = new Customer(null, "Quincaillerie Soro", "0707070707", "soro@email.com", "Dabou, CI", "ENTREPRISE");
    }

    @Test
    void shouldCreateCustomerWithGeneratedCodeSuccess() {
        // GIVEN (On simule qu'il y a déjà 0 client en base, et que le code CLI0001 n'existe pas)
        when(customerRepository.count()).thenReturn(0L);
        when(customerRepository.existsByCustomerCode("CLI0001")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN (On déclenche la création du client)
        Customer createdCustomer = customerService.createCustomer(inputCustomer);

        // THEN (On valide que l'algorithme a bien calculé le code incrémental "CLI0001")
        assertNotNull(createdCustomer);
        assertEquals("CLI0001", createdCustomer.getCustomerCode());
        assertEquals("Quincaillerie Soro", createdCustomer.getName());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}

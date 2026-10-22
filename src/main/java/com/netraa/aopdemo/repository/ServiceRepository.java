package com.netraa.aopdemo.repository;


import com.netraa.aopdemo.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    Service findByName(String name);

    boolean existsByName(String name);

}

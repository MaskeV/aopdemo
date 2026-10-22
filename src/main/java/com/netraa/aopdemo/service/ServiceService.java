package com.netraa.aopdemo.service;

import com.netraa.aopdemo.entity.Service;
import com.netraa.aopdemo.repository.ServiceRepository;
import org.springframework.stereotype.Component;

@Component
public class ServiceService {

    private ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Service saveService(String serviceName) {

        if(serviceRepository.existsByName(serviceName)){
            return serviceRepository.findByName(serviceName);
        }
        Service service = new Service();
        service.setName(serviceName);
        return serviceRepository.save(service);
    }

}

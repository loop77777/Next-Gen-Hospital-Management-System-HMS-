package com.hospital.decorator;

public abstract class ConsultationDecorator implements com.hospital.decorator.BillableService {
    protected com.hospital.decorator.BillableService decoratedService;

    public ConsultationDecorator(com.hospital.decorator.BillableService decoratedService) {
        this.decoratedService = decoratedService;
    }
}

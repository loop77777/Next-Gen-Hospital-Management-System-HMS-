package com.hospital.decorator;

public class LabTestDecorator extends ConsultationDecorator {
    private String testName;
    private double testCost;

    public LabTestDecorator(com.hospital.decorator.BillableService decoratedService, String testName, double testCost) {
        super(decoratedService);
        this.testName = testName;
        this.testCost = testCost;
    }

    @Override
    public double getCost() {
        return decoratedService.getCost() + testCost;
    }

    @Override
    public String getDescription() {
        return decoratedService.getDescription() + " + Lab Test (" + testName + ")";
    }
}
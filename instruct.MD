Problem Statement: Next-Gen Hospital Management System (HMS)
1. Executive Summary & Business Context
Modern multi-specialty hospital networks (such as Apollo, Fortis, and Manipal Hospitals) operate high-volume, dynamic environments handling thousands of outpatient consultations, inpatient admissions, diagnostic tests, and emergency cases daily.
Legacy hospital software and manual administrative workflows suffer from critical operational bottlenecks:
	Duplicate Patient Identifiers: Inability to locate unified patient histories, resulting in redundant registrations and conflicting medical records.
	Fragile Billing Integrations: Rigid payment modules unable to dynamically handle layered charges (e.g., base consultation fee + emergency surcharges + diagnostic lab tests + pharmacy prescriptions).
	Payment Gateway Lock-in: Tight coupling between billing engines and specific payment channels (Cash, Cards, UPI, Insurance claims).
	Communication Breakdown: Fragmented notification channels failing to update patients and care teams synchronously upon appointment bookings or billing receipts.
	Triage Inefficiencies: Standard FIFO queues causing life-threatening delays when critical emergency cases arrive.
2. Core Architectural Principles
To solve these challenges, the system must adhere strictly to object-oriented software engineering best practices:
+-----------------------------------------------------------------------------------+
|                            ENTERPRISE DESIGN PRINCIPLES                           |
+-----------------------------------------------------------------------------------+
|  1. SOLID Principles          |  Maintainable, scalable, loosely coupled modules.  |
|  2. GoF Design Patterns      |  Facade, Factory, Strategy, Decorator, Observer.   |
|  3. Layered Architecture     |  Controller -> Facade -> Service -> DAO -> Models. |
|  4. Thread Safety             |  Concurrent Map stores and Atomic ID sequence generators. |
+-----------------------------------------------------------------------------------+
3. Detailed Functional Requirements & Module Breakdown
Module A: System Configuration & Domain Identity
	Requirement A.1 (Singleton Pattern): The hospital’s core metadata (Hospital Name, Enterprise Code, Tax Registration / GSTIN, Location) must be globally accessible via a thread-safe, double-checked locking Singleton class (HospitalConfiguration). Double-instantiation via reflection or multi-threading must be strictly prevented.
	Requirement A.2 (Domain Subtyping & Inheritance): All human entities in the system (Patient, Doctor) must inherit from an abstract Person class, encapsulating shared traits (id, name, age, gender, phone, address).
	Requirement A.3 (Composition): Person must have a HAS-A relationship with an immutable Address object (street, city, state, zipCode).
Module B: Patient Lifecycle Management
	Requirement B.1 (Builder Pattern): Creating a Patient instance requires multiple optional attributes (bloodGroup, insurancePolicyNo, isAdmitted, priority). The system must implement a Patient.Builder to construct immutable patient objects cleanly while validating core fields (e.g., age >0, non-null name).
	Requirement B.2 (Duplicate Prevention): The PatientService must query the PatientDAOImpl via phone number prior to registration. If an existing record exists, an InvalidPatientDataException must be thrown.
	Requirement B.3 (Data Access Abstraction): The PatientDAOImpl must implement a GenericDAO<T, K> interface backed by a thread-safe ConcurrentHashMap.
Module C: Doctor Management & Fee Polymorphism
	Requirement C.1 (Factory Pattern): Instantiation of doctors must be encapsulated inside a DoctorFactory. Passing string keys (e.g., "CARDIOLOGY", "NEUROLOGY", "GENERAL") will return the corresponding concrete subtype (Cardiologist, Neurologist, GeneralPhysician).
	Requirement C.2 (Polymorphic Calculation): Each doctor subclass must override calculateConsultationFee() to calculate specialist surcharges dynamically over a base consultation fee.
Module D: Dynamic Bill Generation & Service Decorator
	Requirement D.1 (Decorator Pattern): Consultation bills must be calculated dynamically without class-explosion or rigid inheritance.
	The core bill begins with a BasicConsultation implementing BillableService.
	Additional charges must be added dynamically at runtime by wrapping the base service inside decorators: LabTestDecorator and PharmacyDecorator.
	Calling getCost() and getDescription() on the outer decorator must aggregate total costs and print an itemized item list.
                  [ BillableService (Interface) ]
                                 ^
                                 |
         +-----------------------+-----------------------+
         |                                               |
[ BasicConsultation ]                     [ ConsultationDecorator ]
  Cost: Base Doctor Fee                     # service: BillableService
                                                         ^
                                                         |
                                     +-------------------+-------------------+
                                     |                                       |
                           [ LabTestDecorator ]                    [ PharmacyDecorator ]
                             Cost: Service + Test Cost               Cost: Service + Med Cost
Module E: Payment Processing & Strategy Pattern
	Requirement E.1 (Loose Coupling via Strategy Pattern): The system must support flexible, pluggable payment channels without modifying the billing engine.
	All payment modes must implement PaymentStrategy with a processPayment(double amount) method:
	CashPayment: Validates cash tenders.
	CardPayment: Authorizes charges against card credentials.
	UpiPayment: Debits funds via NPCI virtual payment address (VPA).
	InsurancePayment: Submits claims against policy numbers.
Module F: Notification Engine & Observer Pattern
	Requirement F.1 (Publisher-Subscriber Mechanics): Important state changes (e.g., appointment booking confirmed, payment received) must automatically alert external services via an Observer pattern.
	HospitalFacade acts as the subject. When an event fires, it iterates through attached NotificationObserver instances (SmsNotificationService, EmailNotificationService, MobileAppNotificationService) and invokes update(String message).
Module G: System Facade & Workflow Orchestration
	Requirement G.1 (Facade Pattern): The complex internal subsystems (PatientService, Doctor, BillableService, PaymentStrategy, NotificationObserver, AppointmentDAOImpl) must be hidden behind a unified interface: HospitalFacade.
	Requirement G.2 (End-to-End Method Execution): The controller layer executes high-level calls such as processFullConsultationAndBilling(), leaving the HospitalFacade to coordinate lookup, fee decoration, payment authorization, and notification dispatching sequentially.
Module H: Emergency Triage & Data Structures
	Requirement H.1 (Heap-Based Triage Queue): Emergency room patient handling must not use standard FIFO queues. The system must maintain an EmergencyPriorityQueue backed by a Min-Heap (PriorityQueue<Patient>).
	Patients are dequeued based on their PatientPriority level (CRITICAL = 1, HIGH = 2, MEDIUM = 3, LOW = 4), ensuring critical accident victims are served ahead of non-urgent cases regardless of arrival time.
4. Requirement to Class Mapping Matrix
Functional Requirement	Design Pattern / OOP Feature	Primary Class(es) Involved
System Configuration	Singleton Pattern	HospitalConfiguration
Domain Inheritance	IS-A Relationship	Person ← Patient, Person ← Doctor
Address Value Object	Composition (HAS-A)	Address, Person
Patient Construction	Builder Pattern	Patient, Patient.Builder
Data Access Layer	DAO Pattern & Generics	GenericDAO<T,K>, PatientDAOImpl, AppointmentDAOImpl
Doctor Instantiation	Factory Pattern	DoctorFactory
Specialist Fee Calculation	Polymorphism	Doctor, Cardiologist, Neurologist, GeneralPhysician
Dynamic Charges	Decorator Pattern	BillableService, BasicConsultation, LabTestDecorator, PharmacyDecorator
Pluggable Payments	Strategy Pattern	PaymentStrategy, UpiPayment, CardPayment, CashPayment, InsurancePayment
Event Broadcasting	Observer Pattern	NotificationObserver, SmsNotificationService, EmailNotificationService
System Orchestration	Facade Pattern	HospitalFacade
Emergency Processing	Priority Queue (Min-Heap)	EmergencyPriorityQueue, PatientPriority


This class diagram captures all design patterns (Singleton, Builder, Factory, Strategy, Observer, Decorator, Facade, DAO), structural relationships (IS-A, HAS-A, 1-to-1, 1-to-N, N-to-M), and layered architecture.

+-------------------------------------------------------------------------------------------------------------------------------------------------+
|                                                               HOSPITAL SYSTEM ARCHITECTURE                                                     |
+-------------------------------------------------------------------------------------------------------------------------------------------------+

     [ Singleton Pattern ]
+-------------------------------+
|     HospitalConfiguration     |
+-------------------------------+
| - instance: HospitalConfig    |
| - hospitalName: String        |
| - hospitalCode: String        |
| - location: String            |
+-------------------------------+
| + getInstance(): Config       |
+-------------------------------+

     [ Domain Entities & Inheritance ]
                             +-----------------------------------+
                             |              Person               |
                             +-----------------------------------+
                             | # id: String                      |
                             | # name: String                    |
                             | # age: int                        |
                             | # gender: Gender                  |
                             | # phone: String                   |
                             | # address: Address                | <--------+ [ Composition (HAS-A) ]
                             +-----------------------------------+          |
                                       ^               ^                    |  +------------------------+
                                       |               |                    +--|        Address         |
                                       |               |                       +------------------------+
                     +-----------------+               +-------------------+   | - street, city, state, |
                     |                                                     |   |   zipCode: String      |
                     |                                                     |   +------------------------+
+------------------------------------------+         +-----------------------------------------+
|                 Patient                  |         |                 Doctor                  |
+------------------------------------------+         +-----------------------------------------+
| - bloodGroup: String                     |         | # department: Department                |
| - insurancePolicyNo: String              |         | # baseConsultationFee: double           |
| - isAdmitted: boolean                    |         +-----------------------------------------+
| - priority: PatientPriority              |         | + calculateConsultationFee(): double    |
+------------------------------------------+         +-----------------------------------------+
| + static class Builder                   |                              ^
+------------------------------------------+                              |
                                                                          | [ Polymorphism ]
                                                 +------------------------+------------------------+
                                                 |                                                 |
                                +----------------------------------+             +----------------------------------+
                                |           Cardiologist           |             |            Neurologist           |
                                +----------------------------------+             +----------------------------------+
                                | + calculateConsultationFee()     |             | + calculateConsultationFee()     |
                                +----------------------------------+             +----------------------------------+


     [ Observer Pattern ]                            [ Strategy Pattern ]                          [ Decorator Pattern ]
+-----------------------------+             +-----------------------------+             +-----------------------------+
|    NotificationObserver     |             |       PaymentStrategy       |             |       BillableService       |
+-----------------------------+             +-----------------------------+             +-----------------------------+
| + update(msg: String): void |             | + processPayment(): boolean |             | + getCost(): double         |
+-----------------------------+             +-----------------------------+             | + getDescription(): String  |
               ^                                           ^                            +-----------------------------+
               |                                           |                                           ^
      +--------+--------+                         +--------+--------+                                 |
      |                 |                         |                 |               +-----------------+-----------------+
+-----------+     +------------+            +-----------+     +-----------+         |                                   |
| SmsNotify |     | EmailNotify|            | UpiPay    |     | CardPay   |   +-------------------+           +-----------------------+
+-----------+     +------------+            +-----------+     +-----------+   | BasicConsultation |           | ConsultationDecorator |
                                                                                +-------------------+           +-----------------------+
                                                                                | - baseFee: double |           | # service: Service    |
                                                                                +-------------------+           +-----------------------+
                                                                                                                            ^
                                                                                                                            |
                                                                                                                  +---------+---------+
                                                                                                                  |                   |
                                                                                                         +------------------+ +---------------+
                                                                                                         | LabTestDecorator | | PharmacyDecor |
                                                                                                         +------------------+ +---------------+


     [ Layered Architecture, DAO & Facade Pattern ]
+-----------------------------------+             +-----------------------------------+             +-----------------------------------+
|         HospitalController        | ------------>|          HospitalFacade           | ------------>|          PatientService           |
+-----------------------------------+             +-----------------------------------+             +-----------------------------------+
| + handleRegistration()            |             | - patientService: PatientService  |             | - patientDAO: PatientDAOImpl      |
| + handleCheckout()                |             | - apptDAO: AppointmentDAOImpl     |             | - idSeq: AtomicLong               |
+-----------------------------------+             | - observers: List<Observer>       |             +-----------------------------------+
                                                  +-----------------------------------+             | + registerPatient(): Patient      |
                                                  | + bookAppointment(): Appointment  |             | + getSeniorCitizens(): List       |
                                                  | + processFullConsultation(): dbl  |             +-----------------------------------+
                                                  +-----------------------------------+                               |
                                                                    |                                                 |
                                                                    v                                                 v
                                                  +-----------------------------------+             +-----------------------------------+
                                                  |        AppointmentDAOImpl         |             |          PatientDAOImpl           |
                                                  +-----------------------------------+             +-----------------------------------+
                                                  | - apptMap: ConcurrentHashMap      |             | - patientMap: ConcurrentHashMap   |
                                                  +-----------------------------------+             +-----------------------------------+
                                                  | + save(appt: Appointment): void   |             | + save(patient: Patient): void    |
                                                  +-----------------------------------+             +-----------------------------------+



+----------+          +-------------------+          +----------------+          +---------------+          +-------------------+          +-----------------+          +-------------------+
| Patient  |          | HospitalController|          | HospitalFacade |          | PatientService|          | ConsultationBill  |          | PaymentStrategy |          | NotificationObserv|
+----------+          +-------------------+          +----------------+          +---------------+          +-------------------+          +-----------------+          +-------------------+
     |                          |                            |                           |                            |                          |                            |
     |--- 1. Request Checkout ->|                            |                           |                            |                          |                            |
     |                          |--- 2. processConsultation->|                           |                            |                          |                            |
     |                          |                            |--- 3. getPatient(id) ---->|                            |                          |                            |
     |                          |                            |<-- 4. Return Patient -----|                            |                          |                            |
     |                          |                            |                           |                            |                          |                            |
     |                          |                            |--- 5. Instantiate BasicConsultation(doctor.fee) ------>|                          |                            |
     |                          |                            |--- 6. Wrap inside LabTestDecorator ------------------->|                          |                            |
     |                          |                            |--- 7. Wrap inside PharmacyDecorator ------------------>|                          |                            |
     |                          |                            |                           |                            |                          |                            |
     |                          |                            |--- 8. getCost() -------------------------------------->|                          |                            |
     |                          |                            |<-- 9. Total Amount (₹3,450.00) -------------------------|                          |                            |
     |                          |                            |                           |                            |                          |                            |
     |                          |                            |--- 10. processPayment(totalAmount) ---------------------------------------------->|                            |
     |                          |                            |<-- 11. Payment Success (true) ---------------------------------------------------|                            |
     |                          |                            |                           |                            |                          |                            |
     |                          |                            |--- 12. notifyAllObservers("Invoice paid...") -------------------------------------------------------------->|
     |                          |                            |                           |                            |                          |                            |
     |                          |                            |                           |                            |                          |  [ Dispatch SMS ] ---------|
     |                          |                            |                           |                            |                          |  [ Dispatch Email ] -------|
     |                          |                            |                           |                            |                          |                            |
     |<-- 13. Consultation Done |                            |                           |                            |                          |                            |
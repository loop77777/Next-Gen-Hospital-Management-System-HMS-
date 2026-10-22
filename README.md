# Next-Gen Hospital Management System

A small, console-based Java demonstration of core hospital-management workflows and common object-oriented design patterns. The application wires sample services in memory and runs a complete demonstration from its `main` method.

## What the application demonstrates

- Registering patients and looking up patients by ID or phone number.
- Creating department-specific doctors and scheduling appointments.
- Building consultation bills with optional laboratory and pharmacy charges.
- Selecting a payment implementation at runtime.
- Sending appointment and payment notifications to registered observers.
- Ordering emergency patients by priority (critical patients are served first).
- Using a singleton for hospital configuration and a prototype-style clone for medical record snapshots.

This is a learning/demo application, not a production hospital system. It has no web interface, database, authentication, persistence, or real payment/notification integrations. Patient and appointment data live only in in-memory maps and are lost when the process exits; the launcher uses hard-coded sample data.

## Requirements

- JDK 8 or later (the IntelliJ project is currently configured to use JDK 26).
- IntelliJ IDEA is optional; the project can also be compiled from PowerShell.

There are no third-party libraries or Maven/Gradle build files.

## Run from PowerShell

Open PowerShell in the repository root and run:

```powershell
$sourceFiles = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force .\out | Out-Null
javac -encoding UTF-8 -d .\out $sourceFiles
java -cp .\out com.hospital.HospitalManagementApplication
```

The application prints the demonstration as it runs: configuration, doctors, patient registration, appointment booking, consultation billing/payment, senior-citizen filtering, and emergency queue dispatch.

## Run from IntelliJ IDEA

1. Open this repository as a project.
2. Configure a JDK (JDK 8+); the project currently requests JDK 26.
3. Ensure `src` is marked as a Sources Root. The checked-in IntelliJ module configuration already marks it as one.
4. Run `com.hospital.HospitalManagementApplication`.

## Source layout

| Package | Responsibility |
| --- | --- |
| `com.hospital` | Application entry point and demonstration wiring |
| `com.hospital.config` | Hospital singleton configuration |
| `com.hospital.dao` | Generic DAO contract and in-memory patient/appointment stores |
| `com.hospital.decorator` | Consultation bill composition |
| `com.hospital.dto` | Patient registration input |
| `com.hospital.exception` | Domain exceptions |
| `com.hospital.facade` | Appointment, billing, and notification workflows |
| `com.hospital.factory` | Department-specific doctor creation |
| `com.hospital.model` | Hospital domain entities and enums |
| `com.hospital.observer` | Notification observer implementations |
| `com.hospital.service` | Patient service and emergency queue |
| `com.hospital.strategy` | Payment contract and sample payment implementations |

`com.hospital.dto` also contains payment classes from an earlier/alternate implementation. The active application imports the payment strategy types from `com.hospital.strategy`.

## Design patterns in the code

- **Singleton:** `HospitalConfiguration`
- **Builder:** `Patient.Builder`
- **Factory:** `DoctorFactory`
- **Facade:** `HospitalFacade`
- **Observer:** `NotificationObserver` and notification services
- **Decorator:** `BillableService` and its consultation/lab/pharmacy decorators
- **Strategy:** `PaymentStrategy` implementations
- **Prototype:** `MedicalRecord.clone()`

## Limitations

The DAO implementations are in-memory only and are not backed by a database. Payment classes report success by printing a message, and notification classes print alerts rather than contacting external services. The project currently has no automated test suite or configurable runtime settings.

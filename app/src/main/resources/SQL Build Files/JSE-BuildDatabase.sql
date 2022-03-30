--CREATE SCHEMA lenscam_jsefuel_db;
USE lenscam_jsefuel_db;

CREATE TABLE Employee
(
    employeeId      int             NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lastName        varchar(25)     NOT NULL,
    firstName       varchar(25),
    middleName      varchar(25),
    salutation      varchar(15),
    title           varchar(25),
    birthDate       date,
    hireDate        date,
    active          bool,
    licenseState    varchar(2),
    licenseNumber   varchar(25),
    licenseExpires  date,
    licenseCdl      bool,
    address         varchar(30),
    city            varchar(25),
    state           varchar(2),
    zip             varchar(9),
    homePhone       varchar(10),
    cellPhone       varchar(10),
    workCell        varchar(10),
    email           varchar(50),
    jobTitle        varchar(50),
    vehicleId       int,
    lastDataSync    datetime,
    lastUpdated     datetime,
    lastUpdatedBy   int,
    INDEX name (lastName(6), firstName(6)),
    INDEX vehicle (vehicleId),
    INDEX workCell (workCell)
) ENGINE = InnoDB;

CREATE SQL SECURITY INVOKER VIEW FuelEmployee
    AS SELECT employeeId, lastName, firstName, middleName, birthDate,
        active, licenseExpires, licenseCdl, workCell, vehicleId,
        lastDataSync, lastUpdated, lastUpdatedBy 
        FROM Employee;

CREATE TABLE Vehicle
(
    vehicleId       int             NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vehicleNum      varchar(10)     NOT NULL,
    vehicleType     varchar(15)     NOT NULL,
    vin             varchar(17)     NOT NULL,
    modelYear       varchar(4)      NOT NULL,
    make            varchar(25)     NOT NULL,
    model           varchar(15)     NOT NULL,
    active          bool,
    employeeId      int             NOT NULL,
    licensePlate    varchar(10),
    licensePlateState   varchar(2),
    licensePlateExpires date,
    requiresCdl     bool,
    fuelType        int,
    fuelCapacity    int,
    beginMiles      int,
    lastFillMiles  	decimal(8, 1),
    lastOilChange   int,
    oilChangeFrequency  int,
    recommendedOil  varchar(25),
    lastService     int,
    nextService     int,
    lastUpdated     datetime,
    lastUpdatedBy   int,
	UNIQUE INDEX vehNum (vehicleNum),
    UNIQUE INDEX license (licensePlate),
    FOREIGN KEY empid (employeeId) REFERENCES Employee(employeeId)
) ENGINE = InnoDB;

CREATE SQL SECURITY INVOKER VIEW FuelVehicle
    AS SELECT vehicleId, vehicleNum, vehicleType, vin, modelYear,
        make, model, active, employeeId, licensePlate, requiresCdl,
        fuelType, fuelCapacity, beginMiles, lastFillMiles, lastOilChange, 
        oilChangeFrequency, recommendedOil, lastUpdated, lastUpdatedBy
        FROM Vehicle;

CREATE TABLE Provider
(
    providerId      int             NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name            varchar(50)     NOT NULL,
    address         varchar(30),
    city            varchar(25),
    state           varchar(2),
    zip             varchar(5),
    phone           varchar(10),
    email           varchar(50),
    salesRep        varchar(50),
    serviceRep      varchar(50),
    fuelTypes       int,
    services        int,
    active          bool,
    lastUpdated     datetime,
    lastUpdatedBy   int,
    INDEX nameAddress (name(15), address) 
) ENGINE = InnoDB;

CREATE SQL SECURITY INVOKER VIEW FuelProvider
    AS SELECT providerId, name, address, city, phone, fuelTypes,
        services, active, lastUpdated, lastUpdatedBy
        FROM Provider;

CREATE TABLE Fuel
(
    fuelId          int             NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vehicleId       int             NOT NULL,
    employeeId      int             NOT NULL,
    fillDate        datetime        NOT NULL,
    miles           decimal(8, 1)   NOT NULL,
    odometer        decimal(8, 1)   NOT NULL,
    quantity        decimal(8, 3)   NOT NULL,
    fillCost        decimal(8, 2)   NOT NULL,
    providerId      int             NOT NULL,
    lastUpdated     datetime,
    lastUpdatedBy   int,
    CONSTRAINT UNIQUE INDEX vehicleFill (vehicleId, fillDate),
    INDEX vehicleId (vehicleId),
    FOREIGN KEY (vehicleId) REFERENCES Vehicle (vehicleId) 
        ON DELETE CASCADE,
    FOREIGN KEY (employeeId) REFERENCES Employee (employeeId),
    FOREIGN KEY (providerId) REFERENCES Provider (providerId)
) ENGINE = InnoDB;

CREATE TABLE OilChange
(
    oilChangeId     int             NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vehicleId       int             NOT NULL,
    employeeId      int             NOT NULL,
    oilChangeDate   date            NOT NULL,
    odometer        decimal(8, 1)   NOT NULL,
    totalCost       decimal(8, 2)   NOT NULL,
    providerId      int             NOT NULL,
    lastUpdated     datetime,
    lastUpdatedBy   int,
    CONSTRAINT UNIQUE INDEX vehicleDate (vehicleId, oilChangeDate),
    INDEX vehicleId (vehicleId),
    FOREIGN KEY (vehicleId) REFERENCES Vehicle (vehicleId)
        ON DELETE CASCADE,
    FOREIGN KEY (employeeId) REFERENCES Employee (employeeId),
    FOREIGN KEY (providerId) REFERENCES Provider (providerId)
) ENGINE = InnoDB;

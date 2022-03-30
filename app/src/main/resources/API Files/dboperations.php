<?php

    class DbOperations{

        private $con;

        function __construct(){
            require_once dirname(__FILE__) . '/dbconnect.php';
            $db = new DbConnect;
            $this->con = $db->connect();
        }

        public function createFuelFill($fuelId, $vehicleId, $employeeId, $fillDate, 
                        $miles, $odometer, $quantity, $fillCost, $providerId, 
                        $lastUpdated, $lastUpdatedBy){
            if(!$this->fuelFillExists($vehicleId, $fillDate)){
                $stmt = $this->con->prepare("INSERT INTO Fuel 
                        (fuelId, vehicleId, employeeId, fillDate, 
                        miles, odometer, quantity, fillCost, providerId, 
                        lastUpdated, lastUpdatedBy)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                $stmt->bind_param("iiisddddisi", $fuelId, $vehicleId, $employeeId, 
                        $fillDate, $miles, $odometer, $quantity, $fillCost, $providerId, 
                        $lastUpdated, $lastUpdatedBy);
                if($stmt->execute()){
                    $newId = $this->con->insert_id;
                    $stmt = $this->con->prepare("UPDATE Vehicle 
                        SET lastFillMiles = ?, lastUpdated = ?, lastUpdatedBy = ? 
                        WHERE vehicleId = ?");
                    $stmt->bind_param("isii", $odometer, $lastUpdated,
                        $lastUpdatedBy, $vehicleId );
                        $stmt->execute();
                    return $newId;
                }else{
                    return FUEL_FILL_FAILURE;
                }
            }
            return FUEL_FILL_EXISTS;
        }

        public function createOilChange($oilChangeId, $vehicleId, $employeeId, 
                        $oilChangeDate, $odometer, $totalCost, $providerId, 
                        $lastUpdated, $lastUpdatedBy){
            if(!$this->oilChangeExists($vehicleId, $oilChangeDate)){
                $stmt = $this->con->prepare("INSERT INTO OilChange 
                        (oilChangeId, vehicleId, employeeId, 
                        oilChangeDate, odometer, totalCost, providerId, 
                        lastUpdated, lastUpdatedBy)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                $stmt->bind_param("iiisddisi", $oilChangeId, $vehicleId, $employeeId, 
                        $oilChangeDate, $odometer, $totalCost, $providerId, 
                        $lastUpdated, $lastUpdatedBy);
                if($stmt->execute()){
                    $newId = $this->con->insert_id;
                    $stmt = $this->con->prepare("UPDATE Vehicle 
                        SET lastOilChange = ?, lastUpdated = ?, lastUpdatedBy = ? 
                        WHERE vehicleId = ?");
                    $stmt->bind_param("isii", $odometer, $lastUpdated,
                        $lastUpdatedBy, $vehicleId );
                        $stmt->execute();
                    return $newId;
                }else{
                    return OIL_CHANGE_FAILURE;
                }
            }
            return OIL_CHANGE_EXISTS;
        }

        public function oilChangeExists($vehicleId, $oilChangeDate){
            $stmt = $this->con->prepare("SELECT vehicleId, oilChangeDate FROM OilChange 
                    WHERE vehicleId = ? AND oilChangeDate = ?");
            $stmt->bind_param("is", $vehicleId, $oilChangeDate);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }

        public function getOilChangeByVehicleChangeDate($vehicleId, $oilChangeDate) {
            $stmt = $this->con->prepare("SELECT oilChangeId FROM OilChange 
                    WHERE vehicleId = ? AND oilChangeDate = ?");
            $stmt->bind_param("is", $vehicleId, $oilChangeDate);
            $stmt->execute();
            $stmt->bind_result($fuelId);
            $stmt->fetch();
            return $fuelId;            
        }

        public function getEmployees($lastSync){
            $stmt = $this->con->prepare("SELECT 
                    employeeId, lastName, firstName, middleName, 
                    birthDate, active, licenseExpires, licenseCdl, 
                    workCell, vehicleId, lastDataSync, lastUpdated, 
                    lastUpdatedBy FROM Employee WHERE lastUpdated > ?");
            $stmt->bind_param('s', $lastSync);
            $stmt->execute();
            $stmt->bind_result($employeeId, $lastName, $firstName, $middleName, 
                    $birthDate, $active, $licenseExpires, $licenseCdl, 
                    $workCell, $vehicleId, $lastDataSync, $lastUpdated, 
                    $lastUpdatedBy);
            $employees = array();
            while($stmt->fetch()){
                $employee = array();
                $employee['employeeId'] = $employeeId;
                $employee['lastName'] = $lastName;
                $employee['firstName'] = $firstName;
                $employee['middleName'] = $middleName;
                $employee['birthDate'] = $birthDate;
                $employee['active'] = $active;
                $employee['licenseExpires'] = $licenseExpires;
                $employee['licenseCdl'] = $licenseCdl;
                $employee['workCell'] = $workCell;
                $employee['vehicleId'] = $vehicleId;
                $employee['lastDataSync'] = $lastDataSync;
                $employee['lastUpdated'] = $lastUpdated;
                $employee['lastUpdatedBy'] = $lastUpdatedBy;
                array_push($employees, $employee);
            }
            return $employees;
        }

        public function getEmployeeByPhone($phone){
            $stmt = $this->con->prepare("SELECT 
                    employeeId, lastName, firstName, middleName, 
                    birthDate, active, licenseExpires, licenseCdl, 
                    workCell, vehicleId, lastDataSync, lastUpdated, 
                    lastUpdatedBy FROM Employee WHERE workCell = ?");
            $stmt->bind_param("s", $phone);
            $stmt->execute();
            $stmt->bind_result($employeeId, $lastName, $firstName, $middleName, 
                    $birthDate, $active, $licenseExpires, $licenseCdl, 
                    $workCell,  $vehicleId, $lastDataSync, $lastUpdated, 
                    $lastUpdatedBy);
            $stmt->fetch();
            $employee = array();
            $employee['employeeId'] = $employeeId;
            $employee['lastName'] = $lastName;
            $employee['firstName'] = $firstName;
            $employee['middleName'] = $middleName;
            $employee['birthDate'] = $birthDate;
            $employee['active'] = $active;
            $employee['licenseExpires'] = $licenseExpires;
            $employee['licenseCdl'] = $licenseCdl;
            $employee['workCell'] = $workCell;
            $employee['vehicleId'] = $vehicleId;
            $employee['lastDataSync'] = $lastDataSync;
            $employee['lastUpdated'] = $lastUpdated;
            $employee['lastUpdatedBy'] = $lastUpdatedBy;
        return $employee;
        }

        public function getVehicles($lastSync){
            $stmt = $this->con->prepare("SELECT 
                    vehicleId, vehicleNum, vehicleType, vin, 
                    modelYear, make, model, active, employeeId, 
                    licensePlate, requiresCdl, fuelType, fuelCapacity, 
                    beginMiles, lastFillMiles, lastOilChange, 
                    oilChangeFrequency, recommendedOil, lastUpdated, 
                    lastUpdatedBy FROM Vehicle WHERE lastUpdated > ?");
            $stmt->bind_param('s', $lastSync);
            $stmt->execute();
            $stmt->bind_result($vehicleId, $vehicleNum, $vehicleType, $vin, 
                    $modelYear, $make, $model, $active, $employeeId, 
                    $licensePlate, $requiresCdl, $fuelType, $fuelCapacity,
                     $beginMiles, $lastFillMiles, $lastOilChange, 
                     $oilChangeFrequency, $recommendedOil, $lastUpdated, 
                     $lastUpdatedBy);
            $vehicles = array();
            while($stmt->fetch()){
                $vehicle = array();
                $vehicle['vehicleId'] = $vehicleId;
                $vehicle['vehicleNum'] = $vehicleNum;
                $vehicle['vehicleType'] = $vehicleType;
                $vehicle['vin'] = $vin;
                $vehicle['modelYear'] = $modelYear;
                $vehicle['active'] = $active;
                $vehicle['employeeId'] = $employeeId;
                $vehicle['licensePlate'] = $licensePlate;
                $vehicle['requiresCdl'] = $requiresCdl;
                $vehicle['fuelType'] = $fuelType;
                $vehicle['fuelCapacity'] = $fuelCapacity;
                $vehicle['beginMiles'] = $beginMiles;
                $vehicle['lastFillMiles'] = $lastFillMiles;
                $vehicle['lastOilChange'] = $lastOilChange;
                $vehicle['oilChangeFrequency'] = $oilChangeFrequency;
                $vehicle['recommendedOil'] = $recommendedOil;
                $vehicle['lastUpdated'] = $lastUpdated;
                $vehicle['lastUpdatedBy'] = $lastUpdatedBy;
                array_push($vehicles, $vehicle);
            }
            return $vehicles;
        }

        public function getProviders($lastSync){
            $stmt = $this->con->prepare("SELECT 
                    providerId, name, address, city, 
                    phone, fuelTypes, services, active,
                    lastUpdated, lastUpdatedBy
                    FROM Provider WHERE lastUpdated > ?");
            $stmt->bind_param("s", $lastSync);
            $stmt->execute();
            $stmt->bind_result($providerId, $name, $address, $city, 
                    $phone, $fuelTypes, $services, $active,
                    $lastUpdated, $lastUpdatedBy);
            $providers = array();
            while($stmt->fetch()){
                $provider = array();
                $provider['providerId'] = $providerId;
                $provider['name'] = $name;
                $provider['address'] = $address;
                $provider['city'] = $city;
                $provider['phone'] = $phone;
                $provider['fuelTypes'] = $fuelTypes;
                $provider['services'] = $services;
                $provider['active'] = $active;
                $provider['lastUpdated'] = $lastUpdated;
                $provider['lastUpdatedBy'] = $lastUpdatedBy;
                array_push($providers, $provider);
            }
            return $providers;
        }

        public function getFuelFills($lastSync){
            $stmt = $this->con->prepare("SELECT 
                    fuelId, vehicleId, employeeId, fillDate, 
                    miles, odometer, quantity, fillCost, providerId,
                    lastUpdated, lastUpdatedBy 
                    FROM Fuel WHERE lastUpdated > ?");
            $stmt->bind_param("s", $lastSync);
            $stmt->execute();
            $stmt->bind_result($fuelId, $vehicleId, $employeeId, $fillDate, 
                    $miles, $odometer, $quantity, $fillCost, $providerId,
                    $lastUpdated, $lastUpdatedBy);
            $fuels = array();
            while($stmt->fetch()){
                $fuel = array();
                $fuel['fuelId'] = $fuelId;
                $fuel['vehicleId'] = $vehicleId;
                $fuel['employeeId'] = $employeeId;
                $fuel['fillDate'] = $fillDate;
                $fuel['miles'] = $miles;
                $fuel['odometer'] = $odometer;
                $fuel['quantity'] = $quantity;
                $fuel['fillCost'] = $fillCost;
                $fuel['providerId'] = $providerId;
                $fuel['lastUpdated'] = $lastUpdated;
                $fuel['lastUpdatedBy'] = $lastUpdatedBy;
                array_push($fuels, $fuel);
            }
            return $fuels;
        }

        public function getOilChanges($lastSync){
            $stmt = $this->con->prepare("SELECT 
                    oilChangeId, vehicleId, employeeId, oilChangeDate, 
                    odometer, totalCost, providerId,
                    lastUpdated, lastUpdatedBy 
                    FROM OilChange WHERE lastUpdated > ?");
            $stmt->bind_param("s", $lastSync);
            $stmt->execute();
            $stmt->bind_result($oilChangeId, $vehicleId, $employeeId, 
                $oilChangeDate, $odometer, $totalCost, $providerId,
                $lastUpdated, $lastUpdatedBy);
            $oilChanges = array();
            while($stmt->fetch()){
                $oilChange = array();
                $oilChange['oilChangeId'] = $oilChangeId;
                $oilChange['vehicleId'] = $vehicleId;
                $oilChange['employeeId'] = $employeeId;
                $oilChange['oilChangeDate'] = $oilChangeDate;
                $oilChange['odometer'] = $odometer;
                $oilChange['totalCost'] = $totalCost;
                $oilChange['providerId'] = $providerId;
                $oilChange['lastUpdated'] = $lastUpdated;
                $oilChange['lastUpdatedBy'] = $lastUpdatedBy;
                array_push($oilChanges, $oilChange);
            }
            return $oilChanges;
        }

        private function fuelFillExists($vehicleId, $fillDate){
            $stmt = $this->con->prepare("SELECT vehicleId, fillDate FROM Fuel 
                    WHERE vehicleId = ? AND fillDate = ?");
            $stmt->bind_param("is", $vehicleId, $fillDate);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }

        public function getFuelFillByVehicleFillDate($vehicleId, $fillDate) {
            $stmt = $this->con->prepare("SELECT fuelId FROM Fuel 
                    WHERE vehicleId = ? AND fillDate = ?");
            $stmt->bind_param("is", $vehicleId, $fillDate);
            $stmt->execute();
            $stmt->bind_result($fuelId);
            $stmt->fetch();
            return $fuelId;            
        }

    }
    
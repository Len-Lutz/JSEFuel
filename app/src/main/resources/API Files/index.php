<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Slim\Factory\AppFactory;

require __DIR__ . '/../../vendor/autoload.php';

require __DIR__ . '/../includes/dboperations.php';

$app = new \Slim\App([
    'settings'=>[
        'displayErrorDetails'=>true
    ]
]);

//***************
//	Function: getEmployees
//
//	Paramaters:	
//		lastSync - Date in format 'yyyy-mm-dd hh:mm:ss'. Indicates last time
//				this phone was synchronized with server
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered.
//			If successful, also returns an array of all employee records that have a
//				'lastUpdated' date more recent than the supplied 'lastSync' date.
//
//		status - http status 200 (if successful), appropriate http status code on failure
//
//	Notes:
//			
//***************
$app->get('/getEmployees', function(Request $request, Response $response){
	if(!haveEmptyParameters(array('lastSync'), $request, $response)){
		$request_data = $_REQUEST;

		$lastSync = $request_data['lastSync'];
		$db = new DbOperations;

		$employees = $db->getEmployees($lastSync);

		$response_data = array();

		$response_data['error'] = false;
		$response_data['employees'] = $employees;

		$response->write(json_encode($response_data));

		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200);
	}

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(422);
});

//***************
//	Function: getEmployeeByPhone
//
//	Paramaters:	
//		phone - phone number of this phone, obtained programatically
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered.
//			If successful, also returns the employee record that has the provided
//				phone number in its 'workCell' field
//
//		status - http status 200 (if successful), appropriate http status code on failure
//
//	Notes:
//		Only used when synchronizing the database for the first time.  After that, the
//			employee information is obtained from the internal database.
//		
//***************
$app->get('/getEmployeeByPhone', function(Request $request, Response $response){
	$status = 422;
	if(!haveEmptyParameters(array('phone'), $request, $response)){
		$request_data = $_REQUEST;

		$phone=$request_data['phone'];

		$db = new DbOperations;

		$employee = $db->getEmployeeByPhone($phone);

		$response_data = array();
		if($employee['employeeId'] == null){
			$response_data['error'] = true;
			$response_data['message'] = 'Phone number not found.';
			$status = 404;
		}else{
			$response_data['error'] = false;
			$response_data['employee'] = $employee;
			$status = 200;
		}


		$response->write(json_encode($response_data));

		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200);
	}

    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus($status);
});

//***************
//	Function: getVehicles
//
//	Paramaters:	
//		lastSync - Date in format 'yyyy-mm-dd hh:mm:ss'. Indicates last time
//				this phone was synchronized with server
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered.
//			If successful, also returns an array of all vehicle records that have a
//				'lastUpdated' date more recent than the supplied 'lastSync' date.
//
//		status - http status 200 (if successful), appropriate http status code on failure
//
//***************
$app->get('/getVehicles', function(Request $request, Response $response){
	if(!haveEmptyParameters(array('lastSync'), $request, $response)){
		$request_data = $_REQUEST;

		$lastSync = $request_data['lastSync'];
		$db = new DbOperations;

		echo($lastSync);

		$vehicles = $db->getVehicles($lastSync);

		$response_data = array();

		$response_data['error'] = false;
		$response_data['vehicles'] = $vehicles;

		$response->write(json_encode($response_data));
	}

	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);
});

//***************
//	Function: getProviders
//
//	Paramaters:	
//		lastSync - Date in format 'yyyy-mm-dd hh:mm:ss'. Indicates last time
//				this phone was synchronized with server
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered.
//			If successful, also returns an array of all provider records that have a
//				'lastUpdated' date more recent than the supplied 'lastSync' date.
//
//		status - http status 200 (if successful), appropriate http status code on failure
//
//***************
$app->get('/getProviders', function(Request $request, Response $response){
	if(!haveEmptyParameters(array('lastSync'), $request, $response)){
		$request_data = $_REQUEST;

		$lastSync = $request_data['lastSync'];
		$db = new DbOperations;

		$providers = $db->getProviders($lastSync);

		$response_data = array();

		$response_data['error'] = false;
		$response_data['providers'] = $providers;

		$response->write(json_encode($response_data));
	}

	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);
});

//***************
//	Function: getFuelFills
//
//	Paramaters:	
//		lastSync - Date in format 'yyyy-mm-dd hh:mm:ss'. Indicates last time
//				this phone was synchronized with server
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered.
//			If successful, also returns an array of all fuelfill records that have a
//				'lastUpdated' date more recent than the supplied 'lastSync' date.
//
//		status - http status 200 (if successful), appropriate http status code on failure
//
//***************
$app->get('/getFuelFills', function(Request $request, Response $response){
	if(!haveEmptyParameters(array('lastSync'), $request, $response)){
		$request_data = $_REQUEST;

		$lastSync = $request_data['lastSync'];
		$db = new DbOperations;

		$fuels = $db->getFuelFills($lastSync);

		$response_data = array();

		$response_data['error'] = false;
		$response_data['fuels'] = $fuels;

		$response->write(json_encode($response_data));
	}

	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);
});

//***************
//	Function: getOilChanges
//
//	Paramaters:	
//		lastSync - Date in format 'yyyy-mm-dd hh:mm:ss'. Indicates last time
//				this phone was synchronized with server
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered.
//			If successful, also returns an array of all oilChange records that have a
//				'lastUpdated' date more recent than the supplied 'lastSync' date.
//
//		status - http status 200 (if successful), appropriate http status code on failure
//
//***************
$app->get('/getOilChanges', function(Request $request, Response $response){
	if(!haveEmptyParameters(array('lastSync'), $request, $response)){
		$request_data = $_REQUEST;

		$lastSync = $request_data['lastSync'];
		$db = new DbOperations;

		$oilchanges = $db->getOilChanges($lastSync);

		$response_data = array();

		$response_data['error'] = false;
		$response_data['oilchanges'] = $oilchanges;

		$response->write(json_encode($response_data));
	}

	return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(200);
});

//***************
//	Function: createFuelFill
//
//	Parameters:	
//		list of all fields in the fuelfill record with their data
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered
//			'message' = text indicating success or reason for failure.
//			
//		status - http status 200 (if successful), appropriate http status code on failure
//
//***************
$app->post('/createFuelFill', function(Request $request, Response $response){
    if(!haveEmptyParameters(array('fuelId', 'vehicleId', 'employeeId', 'fillDate',
				'miles', 'odometer', 'quantity', 'fillCost', 'providerId',
				'lastUpdated', 'lastUpdatedBy'), $request, $response)){
        $request_data = $_REQUEST;

        $fuelId = $request_data['fuelId'];
        $vehicleId = $request_data['vehicleId'];
        $employeeId = $request_data['employeeId'];
        $fillDate = $request_data['fillDate'];
		$miles = $request_data['miles'];
		$odometer = $request_data['odometer'];
		$quantity = $request_data['quantity'];
		$fillCost = $request_data['fillCost'];
		$providerId = $request_data['providerId'];
		$lastUpdated = $request_data['lastUpdated'];
		$lastUpdatedBy = $request_data['lastUpdatedBy'];

        $db = new DbOperations;

        $result = $db->createFuelFill($fuelId, $vehicleId, $employeeId, $fillDate,
				$miles, $odometer, $quantity, $fillCost, $providerId,
				$lastUpdated, $lastUpdatedBy);

        if($result == FUEL_FILL_FAILURE){
            $message = array();
            $message['error'] = true;
            $message['message'] = 'Unspecified Error Occured.';

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(422);
        }else if($result == FUEL_FILL_EXISTS){
            $message = array();
            $message['error'] = true;
            $message['message'] = 'Fuel Entry Already Exists.';
			$result = $db->getFuelFillByVehicleFillDate($vehicleId, $fillDate);
			if($result > 0){
				$message['newId'] = $result;
			}

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(409);
        } else {
            $message = array();
            $message['error'] = false;
            $message['message'] = 'Fuel Entry created successfully.';
			$message['newId'] = $result ;

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(201);
		}
	}
    return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(422);
});

//***************
//	Function: CreateOilChange
//
//	Parameters:	
//		list of all fields in the oilChange record with their data
//
//	Returns:
//		json formatted response indicating success or failure
//			'error' = false if there were no errors,
//			'error' = true if errors were encountered
//			'message' = text indicating success or reason for failure.
//			
//		status - http status 200 (if successful), appropriate http status code on failure
//
//***************
$app->post('/createOilChange', function(Request $request, Response $response){
    if(!haveEmptyParameters(array('oilChangeId', 'vehicleId', 'employeeId',
				'oilChangeDate', 'odometer', 'totalCost', 'providerId',
				'lastUpdated', 'lastUpdatedBy'), $request, $response)){
        $request_data = $_REQUEST;

        $oilChangeId = $request_data['oilChangeId'];
        $vehicleId = $request_data['vehicleId'];
        $employeeId = $request_data['employeeId'];
        $oilChangeDate = $request_data['oilChangeDate'];
		$odometer = $request_data['odometer'];
		$totalCost = $request_data['totalCost'];
		$providerId = $request_data['providerId'];
		$lastUpdated = $request_data['lastUpdated'];
		$lastUpdatedBy = $request_data['lastUpdatedBy'];

        $db = new DbOperations;

        $result = $db->createOilChange($oilChangeId, $vehicleId, $employeeId, 
				$oilChangeDate, $odometer, $totalCost, $providerId,
				$lastUpdated, $lastUpdatedBy);

		if($result == OIL_CHANGE_FAILURE){
            $message = array();
            $message['error'] = true;
            $message['message'] = 'Unspecified Error Occured.';

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(422);
        }else if($result == OIL_CHANGE_EXISTS){
            $message = array();
            $message['error'] = true;
            $message['message'] = 'Oil Change Entry Already Exists.';
			$result = $db->getOilChangeByVehicleChangeDate($vehicleId, $oilChangeDate);
			if($result > 0){
				$message['newId'] = $result;
			}

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(409);
        } else {
            $message = array();
            $message['error'] = false;
            $message['message'] = 'Oil Change Entry created successfully.';
			$message['newId'] = $result ;

            $response->write(json_encode($message));

            return $response
                        ->withHeader('Content-type', 'application/json')
                        ->withStatus(201);
        }
    }
    return $response
                ->withHeader('Content-type', 'application/json')
                ->withStatus(422);
});

//***************
//	Function: haveEmptyParameters
//
//	Paramaters:	
//		required_params: array list of all fields that are required for calling routine
//		request: 
//		response: 
//
//	Returns:
//		false - if all required parameters have been provided
//		json formatted response indicating 'error' = true and a list of the 
//		parameters that are missing or empty
//
//***************
function haveEmptyParameters($required_params, $request, $response){
    $error = false;
    $error_params = '';
    $request_params = $_REQUEST;

    foreach($required_params as $param){
        if(!isset($request_params[$param]) || strlen($request_params[$param])<1){
            $error = true;
            $error_params .= $param . ', ';
        }
    }

    if($error){
        $error_detail = array();
        $error_detail['error'] = true;
        $error_detail['message'] = 'Required parameters (' . substr($error_params, 0, -2) . ') are missing or empty';
        $response->write(json_encode($error_detail));
    }
    return $error;
}

$app->run();

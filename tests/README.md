# cs122b-tests
Postman (newman) test scripts for HWs 

We will post 
1. `sample tests` before HW deadline so you can have a sense of what the tests looks like and write your own tests.

1. `actual grading tests` after HW deadline so you can double check.


## Description of tests

Postman (newman) is a javascript tool to test RESTful services. 
You can change the method (GET/POST/DELETE/PUT, etc.), authentication (Bearer Token/OAuth, etc.), Headers, Params(usually for GET), Body (usually for POST) for a certain request.

There are two scripts part you could write: `Pre-request Script` and `Tests`

* `Pre-request Script` runs before the request is made, you may generate some random value and set to global variables.
* `Tests` runs after the request is done, you may make unit tests to assert on the response you get.

## To use the tests with `Postman`

1. Download and install latest `Postman@7.0.7`

1. On the rop left corner, click `import`

1. Select the test json to be imported. For example, the `BasicService.postman_collection.json`

1. On the top right corner, next to `Environment`, click the `Manage Environment` button

1. Add an environment with two keys: `host` = `localhost`, `port` = your service port. You may also use other environment variables.

1. Then run the collection of your requests. Under `Data` section, select the data.json files assoicated with each test.


## To use the tests with `newman`

1. Download `newman@4.4.1` with `npm install -g newman`. Get `npm` if you do not have it.

1. Run with the following command:
`newman run <test_json> --global-var 'host=<your_service_host>' --global-var 'port=<your_service_port>' -d <data_file>`

    For example:
    `newman run BasicService.postman_collection.json --global-var 'host=localhost' --global-var 'port=8080' -d ./hw1-test-data.json`
    
    
## To add more tests yourself
1. Add new requests in postman

1. Define your request API with `http://{{host}}:{{port}}` + `<API_END_POINT>`

1. Select HTTP method

1. Define Param (for GET) or Body (for POST)

1. If using Json, select `application/json`

1. Define your `Pre-request Script` and `Tests`

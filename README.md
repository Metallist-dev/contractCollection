# Contract Collection

Keeping track of expenses especially for long-running contracts is oftentimes quite difficult. Additionally, one may
have to search quite a long time in huge folders to find the contract number or any secret for authentication.
Keeping these sensible data in an online cloud service is oftentimes tightly connected to security concerns. To help
keeping these data in a quite secure place (i.e. the local machine) this software was created.

*note: in the current state I have **not** conducted any production tests, only automated software tests; for full reports see the [documentation section](#documentation)*

---
## Table of Contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Features](#features)
* [Upcoming Features](#upcoming-features)
* [Documentation](#documentation)

---
## General info
* Status: under development
* Version: 0.0.2-alpha
* Build status: [![backend-CI](https://github.com/Metallist-dev/contractCollection/actions/workflows/ci-backend.yaml/badge.svg?branch=main)](https://github.com/Metallist-dev/contractCollection/actions/workflows/ci-backend.yaml)
* Code Coverage: [![coverage](https://metallist-images.de/github/contractcollection/main/coverage.svg)](https://metallist-images.de/github/contractcollection/main/coverage.svg)
* Licence: no licence until now - **thereby all rights reserved** *(please ask for permission)*

[Back to top](#table-of-contents)

---
## Technologies

### Backend
| **Product / Library**  | **Version**  | **License**        | **Use**                             |
|------------------------|:------------:|--------------------|-------------------------------------|
| Java                   | 17 (openjdk) | GPL 2.0            | Basic Implementation                |
| Spring Boot Framework  |    3.0.2     | Apache License 2.0 | Basic Implementation                |
| Lombok                 |   1.18.22    | MIT License        | Reduction of boilerplate code       |
| FasterXML Jackson Core |    2.14.2    | Apache License 2.0 | Processing of JSON used by the API  |
| Springdoc OpenAPI 3.0  |    1.6.14    | Apache License 2.0 | API documentation, Swagger (live)   |
| OpenAPI generator      |    6.3.0     | Apache License 2.0 | API documentation                   |
| Swagger UI             |    4.15.5    | Apache License 2.0 | API documentation, Swagger (static) |
| TestNG                 |    7.7.1     | Apache License 2.0 | Test implementation                 |
| Mockito                |    0.4.31    | MIT License        | Test implementation                 |
| Jacoco                 |    0.8.8     | EPL 2.0            | Test reports                        |


[Back to top](#table-of-contents)

---
## Features

* stores important and useful data regarding contracts
* implements CRUD[^1]-operations on contracts
* communication via REST[^2]-API between frontend and backend
    * documentation complies with OpenAPI 3.0 standard
* import and export functionality using json-files

[^1]: Create, Read, Update, Delete  
[^2]: Representational State Transfer

[Back to top](#table-of-contents)

---
## Upcoming Features

#### for [Milestone "Beta release"](https://github.com/Metallist-dev/contractCollection/milestone/1):
* frontend (desktop or web) - see issue #9
* contact data for the regarding contract - see issue #2

#### for [Version 2](https://github.com/Metallist-dev/contractCollection/milestone/3) or later:
* rework backend with TypeScript - see issue #14
* create mobile (Android) app - no issue created


[Back to top](#table-of-contents)

---
## Documentation

* API documentation
    * [raw yaml documentation](Backend/src/main/resources/static/contractcollection-api.yaml)
    * [rendered html documentation](https://metallist-images.de/github/contractcollection/main/docs/openapi/index.html)
    * [Swagger UI](https://metallist-images.de/github/contractcollection/main/docs/swaggerUI-Backend/index.html)
* [Javadoc documentation](https://metallist-images.de/github/contractcollection/main/docs/javadoc/index-all.html)
* [Jacoco coverage report](https://metallist-images.de/github/contractcollection/main/docs/jacoco/jacocoHtml/index.html)


[Back to top](#table-of-contents)
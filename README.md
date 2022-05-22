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
* Version: 0.0.1-alpha
* Build status: [![backend-CI](https://github.com/Metallist-dev/contractCollection/actions/workflows/ci-backend.yaml/badge.svg?branch=security-implementation)](https://github.com/Metallist-dev/contractCollection/actions/workflows/ci-backend.yaml)
* Code Coverage: [![coverage](https://metallist-images.de/github/contractcollection/security-implementation/coverage.svg)](https://metallist-images.de/github/contractcollection/security-implementation/coverage.svg)
* Licence: no licence until now - **thereby all rights reserved** *(please ask for permission)*

[Back to top](#table-of-contents)

---
## Technologies

### Backend
| **Product / Library**  | **Version** | **License**        | **Use**                             |
|------------------------|:-----------:|--------------------|-------------------------------------|
| Java                   |     11      | Apache License 2.0 | Basic Implementation                |
| Spring Boot Framework  |    2.6.3    | Apache License 2.0 | Basic Implementation                |
| Lombok                 |   1.18.22   | MIT License        | Reduction of boilerplate code       |
| FasterXML Jackson Core |   2.13.1    | Apache License 2.0 | Processing of JSON used by the API  |
| Springdoc OpenAPI 3.0  |    1.6.6    | Apache License 2.0 | API documentation, Swagger (live)   |
| OpenAPI generator      |    5.4.0    | Apache License 2.0 | API documentation                   |
| Swagger UI             |   4.10.3    | Apache License 2.0 | API documentation, Swagger (static) |
| TestNG                 |     7.5     | Apache License 2.0 | Test implementation                 |
| Mockito                |   0.4.16    | MIT License        | Test implementation                 |
| Jacoco                 |    0.7.8    | EPL 2.0            | Test reports                        |


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
  * [rendered html documentation](https://metallist-images.de/github/contractcollection/security-implementation/docs/openapi/index.html)
  * [Swagger UI](https://metallist-images.de/github/contractcollection/security-implementation/docs/swaggerUI-Backend/index.html)
* [Javadoc documentation](https://metallist-images.de/github/contractcollection/security-implementation/docs/javadoc/index-all.html)
* [Jacoco coverage report](https://metallist-images.de/github/contractcollection/security-implementation/docs/jacoco/jacocoHtml/index.html)

[Back to top](#table-of-contents)
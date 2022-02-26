# Contract Collection

Keeping track of expenses especially for long-running contracts is oftentimes quite difficult. Additionally, one may 
have to search quite a long time in huge folders to find the contract number or any secret for authentication.
Keeping these sensible data in an online cloud service is oftentimes tightly connected to security concerns. To help
keeping these data in a quite secure place (i. e. the local machine) this software was created.

***attention: although it's the aim to store the data securely, it's still not implemented! It's still in some kind of pre-alpha-state.***

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
* Version: (0.0.1 somewhere in progress)
* Build status: [![backend-CI](https://github.com/Metallist-dev/contractCollection/actions/workflows/ci-backend.yaml/badge.svg?branch=main)](https://github.com/Metallist-dev/contractCollection/actions/workflows/ci-backend.yaml)
* Code Coverage: [![coverage](https://metallist-images.de/github/contractcollection/coverage.svg)](https://metallist-images.de/github/contractcollection/coverage.svg)
* Licence: no licence until now - **thereby all rights reserved** *(please ask for permission)*

[Back to top](#table-of-contents)

---
## Technologies

### Backend
| **Product / Library**      | **Version** | **License**        | **Use**                            |
|----------------------------|:-----------:|--------------------|------------------------------------|
| Java                       |     11      | Apache License 2.0 | Basic Implementation               |
| Spring Boot Framework      |    2.6.3    | Apache License 2.0 | Basic Implementation               |
| Lombok                     |   1.18.22   | MIT License        | Reduction of boilerplate code      |
| MariaDB Java Client (JDBC) |    3.0.3    | LGPL 2.1           | Connection to database             |
| FasterXML Jackson Core     |   2.13.1    | Apache License 2.0 | Processing of JSON used by the API |
| Springdoc OpenAPI 3.0      |    1.6.6    | Apache License 2.0 | API documentation, Swagger         |
| OpenAPI generator          |    5.4.0    | Apache License 2.0 | API documentation                  |
| TestNG                     |     7.5     | Apache License 2.0 | Test implementation                |
| Mockito                    |   0.4.16    | MIT License        | Test implementation                |
| Jacoco                     |    0.7.8    | EPL 2.0            | Test reports                       |


[Back to top](#table-of-contents)

---
## Features

* stores important and useful data regarding contracts
* implements CRUD[^1]-operations on contracts
* storage of data in a database
* communication via REST[^2]-API between frontend and backend
  * complies with OpenAPI 3.0 standard

[^1]: Create, Read, Update, Delete  
[^2]: Representational state transfer

[Back to top](#table-of-contents)

---
## Upcoming Features

for [Milestone "Beta release"](https://github.com/Metallist-dev/contractCollection/milestone/1)
* frontend (desktop or web) - see issue #9
* encryption of data - see issue #5
* import/export of data via json-files - see issue #5
* authentication on startup - see issue #10


[Back to top](#table-of-contents)

---
## Documentation

* API documentation
  * [raw yaml documentation](Backend/src/main/resources/static/contractcollection-api.yaml)
  * [rendered html documentation](https://metallist-images.de/github/contractcollection/docs/openapi/index.html)
* [Javadoc documentation](https://metallist-images.de/github/contractcollection/docs/javadoc/index-all.html)
* [Jacoco coverage report](https://metallist-images.de/github/contractcollection/docs/jacoco/jacocoHtml/index.html)

[Back to top](#table-of-contents)
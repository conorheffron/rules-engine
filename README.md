# rules-engine

### Quick Start


### Build & Run App

mvn clean install spring-boot:run

### Read Rules & Store in Map
http://localhost:8080/api/executetask

Executed Camunda BPMN

### Sample API queries 
 - If Match, Return list of rules returned with HTTP Status 200 
 - else empty list & 400 Bad Request


http://localhost:8080/api/test
["ES","PT"]

http://localhost:8080/api/eval?feature=new-checkout&country=IE&appVersion=1&tier=2
[]

http://localhost:8080/api/eval?feature=new-checkout&country=PT&appVersion=120&tier=2

http://localhost:8080/api/eval?feature=new-checkout&country=ES&appVersion=400&tier=2

http://localhost:8080/api/eval?feature=new-checkout&country=ES&appVersion=100&tier=2


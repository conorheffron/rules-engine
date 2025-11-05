# rules-engine

### Build & Run App
```shell
mvn clean install spring-boot:run
```

### Read Rules & Store in Map
```
http://localhost:8080/api/executetask
```
#### Response 200 & Message Below:
```
Executed Camunda BPMN
```

### Sample API queries 
 - If Match, Return list of rules returned with HTTP Status 200 
 - else empty list & 400 Bad Request

#### Test End-Point
```
http://localhost:8080/api/test
["ES","PT"]
```

<img width="370" height="161" alt="image" src="https://github.com/user-attachments/assets/61b8f516-3781-4b7e-81a9-cc9ccba9e2f1" />


# Rule Matcher
#### No Matches (see application yml)
```
http://localhost:8080/api/eval?feature=new-checkout&country=IE&appVersion=1&tier=2
[]
```

#### 2 Matches for feature, country, & app version rules (see application yml - tier not checked yet)
```
http://localhost:8080/api/eval?feature=new-checkout&country=PT&appVersion=120&tier=2
```

<img width="810" height="416" alt="image" src="https://github.com/user-attachments/assets/972445c2-be54-4499-a38c-1413fcae5831" />


#### 2 Matches for feature, country, & app version rules (see application yml - tier not checked yet)
```
http://localhost:8080/api/eval?feature=new-checkout&country=ES&appVersion=400&tier=2
```

#### 1 Match for feature & country (see application yml - tier not checked yet)
```
http://localhost:8080/api/eval?feature=new-checkout&country=ES&appVersion=100&tier=2
```



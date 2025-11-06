# rules-engine

### Build & Run App
```shell
mvn clean install spring-boot:run
```

### Read Rules & Store in Map
```
http://localhost:8080/api/executetask
```
<img width="1526" height="120" alt="image" src="https://github.com/user-attachments/assets/573406fa-29f2-4a58-9df6-38c45e2cee40" />


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
```

<img width="761" height="163" alt="image" src="https://github.com/user-attachments/assets/490bd7d3-71e8-4281-b04e-efa4c79add37" />


#### 2 Matches for feature, country, & app version rules
```
http://localhost:8080/api/eval?feature=new-checkout&country=PT&appVersion=120&tier=2
```

<img width="810" height="416" alt="image" src="https://github.com/user-attachments/assets/972445c2-be54-4499-a38c-1413fcae5831" />


#### 2 Matches for feature, country, & app version rules 
```
http://localhost:8080/api/eval?feature=new-checkout&country=ES&appVersion=400&tier=2
```

#### 1 Match for feature & country
```
http://localhost:8080/api/eval?feature=new-checkout&country=ES&appVersion=100&tier=2
```

<img width="1286" height="286" alt="image" src="https://github.com/user-attachments/assets/cfb0ddd5-efb4-448a-9f12-5177172159ef" />

#### 2 Matches for feature & country
```
http://localhost:8080/api/eval?feature=old-checkout&country=IRL&appVersion=100&tier=gold
```

<img width="787" height="339" alt="image" src="https://github.com/user-attachments/assets/12a27a51-23c9-44bc-92c6-8628d26c6eb2" />

#### 3 Matches for feature, country, & tier
```
http://localhost:8080/api/eval?feature=new-checkout&country=PT&appVersion=140&tier=gold
```

<img width="802" height="467" alt="image" src="https://github.com/user-attachments/assets/3a7b9ce8-8ff9-4073-81da-14a738504e33" />


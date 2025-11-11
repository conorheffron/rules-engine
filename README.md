# rules-engine

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

[![Java CI with Maven](https://github.com/conorheffron/rules-engine/actions/workflows/maven.yml/badge.svg)](https://github.com/conorheffron/rules-engine/actions/workflows/maven.yml)

### Sample Rules defined in app configuration for a set of `features`
 - Each feature can be `enabled or disabled` & each rule per feature can have `attr`, `op`, & `values` value/values list.
 - Rule Group logic not implemented yet - see #1 for intended / expected implementation details.
```yml
feature:
  new-checkout:
    enabled: true
    ruleGroups:
      all:
        - { attr: country, op: IN, values: [ "ES", "PT" ] }
        - { attr: appVersion, op: GTE, values: [ "120" ] }
      any:
        - { attr: tier, op: IN, values: [ "gold", "platinum" ] }

  search-v2:
    enabled: false

  beta-banner:
    enabled: true

  old-checkout:
    enabled: true
    ruleGroups:
      all:
        - { attr: country, op: IN, values: [ IRL ] }
      any:
        - { attr: tier, op: IN, values: [ gold, platinum ] }
```

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

#### 2 Matches for feature, tier, & country
```
http://localhost:8080/api/eval?feature=old-checkout&country=IRL&appVersion=100&tier=gold
```

<img width="787" height="339" alt="image" src="https://github.com/user-attachments/assets/12a27a51-23c9-44bc-92c6-8628d26c6eb2" />

#### 3 Matches for feature, country, app version, & tier
```
http://localhost:8080/api/eval?feature=new-checkout&country=PT&appVersion=140&tier=gold
```

<img width="802" height="467" alt="image" src="https://github.com/user-attachments/assets/3a7b9ce8-8ff9-4073-81da-14a738504e33" />

#### No Matches -> Feature Disabled
```
http://localhost:8080/api/eval?feature=search-v2&country=PT&appVersion=140&tier=gold
```
<img width="763" height="103" alt="image" src="https://github.com/user-attachments/assets/d5272bb0-5227-4bc5-a555-7c4ddc80c238" />

---

## Camunda UI Tools such as Cockpit, Admin Panel & Tasklist (login with credentials in yml)
```
http://localhost:8080/
```
### Screenshots of Embedded Camunda UI

#### Start Process
<img width="1727" height="437" alt="image" src="https://github.com/user-attachments/assets/58cf84a9-4bc7-4ddc-a303-5935776c4cb9" />

#### Task List
<img width="2446" height="779" alt="image" src="https://github.com/user-attachments/assets/3f80c09a-1527-49b6-8bb5-2a85c1400603" />

#### Cock Pit
<img width="2443" height="415" alt="image" src="https://github.com/user-attachments/assets/f9cd9c1a-ac80-41f8-9ca7-08d53f912cca" />

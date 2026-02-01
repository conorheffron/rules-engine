# rules-engine

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

[![Java CI with Maven](https://github.com/conorheffron/rules-engine/actions/workflows/maven.yml/badge.svg)](https://github.com/conorheffron/rules-engine/actions/workflows/maven.yml)

### Sample Rules defined in app configuration for a set of `features`
 - Each `feature` can be `enabled or disabled`
 - Each rule per feature can have `attr`, `op`, & `values` value/values list.
 - `Rule Group` logic for keys `all` & `any` not implemented - see https://github.com/conorheffron/rules-engine/issues/13 for intended / expected implementation details.
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
mvn clean install test spring-boot:run
```

### Read Rules & Store in Map
```
http://localhost:8080/api/executetask
```
<img width="1526" height="120" alt="image" src="https://github.com/user-attachments/assets/573406fa-29f2-4a58-9df6-38c45e2cee40" />


### Sample API queries 
 - If Match, Return list of rules returned with HTTP Status 200 
 - else empty list & 400 Bad Request

#### Test End-Point GET Request
```
http://localhost:8080/api/test
```
#### Test End-Point Response
```
["IRE","ES"]
```

<img width="1227" height="442" alt="image" src="https://github.com/user-attachments/assets/f2cb35da-1303-4945-9d05-f39526e79c7c" />


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

#### No Matches → Feature Disabled (Bad Request -> HTTP Status Code 400)
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


## Sample Logs (from test cases above):
```jshelllanguage
____                                 _         ____  _       _    __
    / ___| __ _ _ __ ___  _   _ _ __   __| | __ _  |  _ \| | __ _| |_ / _| ___  _ __ _ __ ___
| |   / _` | '_ ` _ \| | | | '_ \ / _` |/ _` | | |_) | |/ _` | __| |_ / _ \| '__| '_ ` _ \
| |__| (_| | | | | | | |_| | | | | (_| | (_| | |  __/| | (_| | |_|  _| (_) | |  | | | | | |
\____/\__,_|_| |_| |_|\__,_|_| |_|\__,_|\__,_| |_|   |_|\__,_|\__|_|  \___/|_|  |_| |_| |_|

Spring-Boot:  (v3.5.10)
Camunda Platform: (v7.24.0)
Camunda Platform Spring Boot Starter: (v7.24.0)

2026-02-01T00:27:05.219Z  INFO 31622 --- [           main] i.example.flag.evaluator.ApiApplication  : Starting ApiApplication using Java 25 with PID 31622 (/Users/conorheffron/workspace/fd-flag-eval/target/classes started by conorheffron in /Users/conorheffron/workspace/fd-flag-eval)
2026-02-01T00:27:05.222Z  INFO 31622 --- [           main] i.example.flag.evaluator.ApiApplication  : No active profile set, falling back to 1 default profile: "default"
2026-02-01T00:27:06.060Z  INFO 31622 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-02-01T00:27:06.082Z  INFO 31622 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 14 ms. Found 0 JPA repository interfaces.
2026-02-01T00:27:06.602Z  INFO 31622 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-02-01T00:27:06.615Z  INFO 31622 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-02-01T00:27:06.615Z  INFO 31622 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.50]
2026-02-01T00:27:06.680Z  INFO 31622 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-02-01T00:27:06.681Z  INFO 31622 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1407 ms
2026-02-01T00:27:06.699Z  INFO 31622 --- [           main] .c.b.s.b.s.r.CamundaJerseyResourceConfig : Configuring camunda rest api.
2026-02-01T00:27:06.720Z  INFO 31622 --- [           main] .c.b.s.b.s.r.CamundaJerseyResourceConfig : Finished configuring camunda rest api.
2026-02-01T00:27:07.063Z  INFO 31622 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2026-02-01T00:27:07.121Z  INFO 31622 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.41.Final
2026-02-01T00:27:07.158Z  INFO 31622 --- [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2026-02-01T00:27:07.547Z  INFO 31622 --- [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-02-01T00:27:07.584Z  INFO 31622 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-02-01T00:27:08.021Z  INFO 31622 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection conn0: url=jdbc:h2:file:./camunda-h2-database user=SA
2026-02-01T00:27:08.024Z  INFO 31622 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-02-01T00:27:08.092Z  INFO 31622 --- [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
Database driver: undefined/unknown
Database version: 2.3.232
Autocommit mode: undefined/unknown
Isolation level: undefined/unknown
Minimum pool size: undefined/unknown
Maximum pool size: undefined/unknown
2026-02-01T00:27:08.619Z  INFO 31622 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-02-01T00:27:08.624Z  INFO 31622 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-02-01T00:27:08.684Z  INFO 31622 --- [           main] org.camunda.bpm.spring.boot              : STARTER-SB040 Setting up jobExecutor with corePoolSize=3, maxPoolSize:10
2026-02-01T00:27:08.757Z  INFO 31622 --- [           main] org.camunda.bpm.engine.cfg               : ENGINE-12003 Plugin 'CompositeProcessEnginePlugin[genericPropertiesConfiguration, camundaProcessEngineConfiguration, camundaDatasourceConfiguration, camundaJobConfiguration, camundaHistoryConfiguration, camundaMetricsConfiguration, camundaAuthorizationConfiguration, createAdminUserConfiguration, failedJobConfiguration, CreateFilterConfiguration[filterName=All tasks], disableDeploymentResourcePattern, eventPublisherPlugin, ApplicationContextClassloaderSwitchPlugin, SpringBootSpinProcessEnginePlugin]' activated on process engine 'default'
2026-02-01T00:27:08.774Z  INFO 31622 --- [           main] org.camunda.bpm.spring.boot              : STARTER-SB020 ProcessApplication enabled: autoDeployment via springConfiguration#deploymentResourcePattern is disabled
2026-02-01T00:27:08.774Z  INFO 31622 --- [           main] o.c.b.s.b.s.event.EventPublisherPlugin   : EVENTING-001: Initialized Camunda Spring Boot Eventing Engine Plugin.
2026-02-01T00:27:08.775Z  INFO 31622 --- [           main] o.c.b.s.b.s.event.EventPublisherPlugin   : EVENTING-003: Task events will be published as Spring Events.
2026-02-01T00:27:08.775Z  INFO 31622 --- [           main] o.c.b.s.b.s.event.EventPublisherPlugin   : EVENTING-005: Execution events will be published as Spring Events.
2026-02-01T00:27:08.775Z  INFO 31622 --- [           main] o.c.b.s.b.s.event.EventPublisherPlugin   : EVENTING-009: Listeners will not be invoked if a skipCustomListeners API parameter is set to true by user.
2026-02-01T00:27:08.780Z  INFO 31622 --- [           main] o.c.b.s.b.s.event.EventPublisherPlugin   : EVENTING-007: History events will be published as Spring events.
2026-02-01T00:27:08.783Z  INFO 31622 --- [           main] org.camunda.spin                         : SPIN-01010 Discovered Spin data format provider: org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormatProvider[name = application/json]
2026-02-01T00:27:08.955Z  INFO 31622 --- [           main] org.camunda.spin                         : SPIN-01010 Discovered Spin data format provider: org.camunda.spin.impl.xml.dom.format.DomXmlDataFormatProvider[name = application/xml]
2026-02-01T00:27:09.050Z  INFO 31622 --- [           main] org.camunda.spin                         : SPIN-01009 Discovered Spin data format: org.camunda.spin.impl.xml.dom.format.DomXmlDataFormat[name = application/xml]
2026-02-01T00:27:09.050Z  INFO 31622 --- [           main] org.camunda.spin                         : SPIN-01009 Discovered Spin data format: org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat[name = application/json]
2026-02-01T00:27:09.142Z  INFO 31622 --- [           main] org.camunda.bpm.dmn.feel.scala           : FEEL/SCALA-01001 Spin value mapper detected
2026-02-01T00:27:11.337Z  INFO 31622 --- [           main] org.camunda.bpm.engine                   : ENGINE-00001 Process Engine default created.
2026-02-01T00:27:11.397Z  INFO 31622 --- [           main] org.camunda.bpm.spring.boot              : STARTER-SB016 Skip initial filter creation, the filter with this name already exists: All tasks
2026-02-01T00:27:11.418Z  WARN 31622 --- [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2026-02-01T00:27:11.784Z  INFO 31622 --- [           main] o.c.b.s.b.s.w.f.LazyInitRegistration     : lazy initialized org.camunda.bpm.spring.boot.starter.webapp.filter.LazyProcessEnginesFilter@3caee3a8
2026-02-01T00:27:11.839Z  INFO 31622 --- [           main] o.c.b.s.b.s.w.f.LazyInitRegistration     : lazy initialized org.camunda.bpm.spring.boot.starter.webapp.filter.LazySecurityFilter@6870cfac
2026-02-01T00:27:11.971Z  INFO 31622 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-02-01T00:27:12.094Z  INFO 31622 --- [           main] org.camunda.bpm.container                : ENGINE-08024 Found processes.xml file at file:/Users/conorheffron/workspace/fd-flag-eval/target/classes/META-INF/processes.xml
2026-02-01T00:27:12.094Z  INFO 31622 --- [           main] org.camunda.bpm.container                : ENGINE-08025 Detected empty processes.xml file, using default values
2026-02-01T00:27:12.100Z  INFO 31622 --- [           main] org.camunda.bpm.container                : ENGINE-08023 Deployment summary for process archive 'apiApplication':

processes/first.bpmn
processes/loanApproval.bpmn

2026-02-01T00:27:12.409Z  INFO 31622 --- [           main] org.camunda.bpm.application              : ENGINE-07021 ProcessApplication 'apiApplication' registered for DB deployments [c1e6702d-ff01-11f0-958c-ba78e369f0f4]. Will execute process definitions

loanApproval[version: 1, id: loanApproval:1:c1fe6501-ff01-11f0-958c-ba78e369f0f4]
rules-init[version: 1, id: rules-init:1:c1fd5390-ff01-11f0-958c-ba78e369f0f4]
Deployment does not provide any case definitions.
2026-02-01T00:27:12.506Z  INFO 31622 --- [           main] org.camunda.bpm.container                : ENGINE-08050 Process application apiApplication successfully deployed
2026-02-01T00:27:12.509Z  INFO 31622 --- [           main] i.example.flag.evaluator.ApiApplication  : Started ApiApplication in 7.809 seconds (process running for 8.291)
2026-02-01T00:27:12.512Z  INFO 31622 --- [           main] org.camunda.bpm.engine.jobexecutor       : ENGINE-14014 Starting up the JobExecutor[org.camunda.bpm.engine.spring.components.jobexecutor.SpringJobExecutor].
2026-02-01T00:27:12.513Z  INFO 31622 --- [ingJobExecutor]] org.camunda.bpm.engine.jobexecutor       : ENGINE-14018 JobExecutor[org.camunda.bpm.engine.spring.components.jobexecutor.SpringJobExecutor] starting to acquire jobs
2026-02-01T00:27:28.987Z  INFO 31622 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-02-01T00:27:28.987Z  INFO 31622 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-02-01T00:27:28.988Z  INFO 31622 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2026-02-01T00:27:29.043Z  INFO 31622 --- [nio-8080-exec-1] i.e.f.e.controller.FlagController        : Test yml value is: true
2026-02-01T00:27:33.202Z  INFO 31622 --- [nio-8080-exec-3] i.e.flag.evaluator.service.RulesService  : Initialize rules from local features config
2026-02-01T00:27:36.793Z  INFO 31622 --- [nio-8080-exec-4] i.e.f.e.controller.FlagController        : Evaluating feature: new-checkout for country: IE appVersion: 1 tier: 2
2026-02-01T00:27:36.798Z  WARN 31622 --- [nio-8080-exec-4] i.e.f.e.controller.FlagController        : Rules did not match for feature new-checkout
2026-02-01T00:27:41.426Z  INFO 31622 --- [nio-8080-exec-6] i.e.f.e.controller.FlagController        : Evaluating feature: new-checkout for country: PT appVersion: 120 tier: 2
2026-02-01T00:27:41.427Z  INFO 31622 --- [nio-8080-exec-6] i.e.f.e.controller.FlagController        : Rules set for feature new-checkout is [Rule{attr='country', op='IN', values={0=ES, 1=PT}}, Rule{attr='appVersion', op='GTE', values={0=120}}]
2026-02-01T00:27:44.926Z  INFO 31622 --- [nio-8080-exec-7] i.e.f.e.controller.FlagController        : Evaluating feature: new-checkout for country: ES appVersion: 400 tier: 2
2026-02-01T00:27:44.927Z  INFO 31622 --- [nio-8080-exec-7] i.e.f.e.controller.FlagController        : Rules set for feature new-checkout is [Rule{attr='country', op='IN', values={0=ES, 1=PT}}, Rule{attr='appVersion', op='GTE', values={0=120}}]
2026-02-01T00:27:47.979Z  INFO 31622 --- [nio-8080-exec-8] i.e.f.e.controller.FlagController        : Evaluating feature: new-checkout for country: ES appVersion: 100 tier: 2
2026-02-01T00:27:47.979Z  INFO 31622 --- [nio-8080-exec-8] i.e.f.e.controller.FlagController        : Rules set for feature new-checkout is [Rule{attr='country', op='IN', values={0=ES, 1=PT}}]
2026-02-01T00:27:50.899Z  INFO 31622 --- [io-8080-exec-10] i.e.f.e.controller.FlagController        : Evaluating feature: old-checkout for country: IRL appVersion: 100 tier: gold
2026-02-01T00:27:50.900Z  INFO 31622 --- [io-8080-exec-10] i.e.f.e.controller.FlagController        : Rules set for feature old-checkout is [Rule{attr='country', op='IN', values={0=IRL}}, Rule{attr='tier', op='IN', values={0=gold, 1=platinum}}]
2026-02-01T00:27:54.035Z  INFO 31622 --- [nio-8080-exec-9] i.e.f.e.controller.FlagController        : Evaluating feature: new-checkout for country: PT appVersion: 140 tier: gold
2026-02-01T00:27:54.036Z  INFO 31622 --- [nio-8080-exec-9] i.e.f.e.controller.FlagController        : Rules set for feature new-checkout is [Rule{attr='country', op='IN', values={0=ES, 1=PT}}, Rule{attr='appVersion', op='GTE', values={0=120}}, Rule{attr='tier', op='IN', values={0=gold, 1=platinum}}]
2026-02-01T00:27:57.487Z  INFO 31622 --- [nio-8080-exec-2] i.e.f.e.controller.FlagController        : Evaluating feature: search-v2 for country: PT appVersion: 140 tier: gold
2026-02-01T00:27:57.487Z  WARN 31622 --- [nio-8080-exec-2] i.e.f.e.controller.FlagController        : Feature search-v2 is not enabled.
```

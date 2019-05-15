# jenkins-job-monitor
Monitora as taferas do Jenkins

# Configuração
application.yml
```
jenkins:
  url: http://jenkins.compsis.com.br:8080
  username: jenkins
  password: ********
  jobs:
    - job-name
    - folder/job-name
  hooks:
    - status: SUCCESS
      url: https://maker.ifttt.com/trigger/switch01_off/with/key/privatekey
    - status: FAILURE
      url: https://maker.ifttt.com/trigger/switch01_on/with/key/privatekey
```

# VUT FIT PDB project
Simple app for real estate agency

## Build

Create file `settings.xml` and put this file into `${user.home}/.m2/settings.xml` (in GNU/Linux it is `~/.m2/settings.xml`, in Windows it is `C:\Users\YOUR_WINDOWS_LOGIN\.m2\settings.xml`) and replace `ORACLE_ACCOUNT_USERNAME` with your Oracle acount login (email) and `ORACLE_ACCOUNT_PASSWORD` with you Oracle account password.

```xml
<?xml version="1.0"?>
<settings
    xmlns="http://maven.apache.org/SETTINGS/1.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0                                           http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <interactiveMode/>
  <usePluginRegistry/>
  <offline/>
  <pluginGroups/>
  <servers>
    <server>
      <id>maven.oracle.com</id>
      <username>ORACLE_ACCOUNT_USERNAME</username>
      <password>ORACLE_ACCOUNT_PASSWORD</password>
      <configuration>
        <basicAuthScope>
          <host>ANY</host>
          <port>ANY</port>
          <realm>OAM 11g</realm>
        </basicAuthScope>
        <httpConfiguration>
          <all>
            <params>
              <property>
                <name>http.protocol.allow-circular-redirects</name>
                <value>%b,true</value>
              </property>
            </params>
          </all>
        </httpConfiguration>
      </configuration>
    </server>
  </servers>
  <mirrors/>
  <proxies/>
  <profiles/>
  <activeProfiles/>
</settings>
```

You can create the Oracle account at https://login.oracle.com/mysso/signon.jsp

Finally, you will need to register into Oracle Maven repository at http://www.oracle.com/webfolder/application/maven/index.html (follow the "visit the registration site" link to http://www.oracle.com/webapps/maven/register/license.html) Ignore error "Unable to register user" after accepting the License Agreement, it is OK.

In project root run:

```shell
mvn package
mvn compile
```

To generate Javadoc documentation run:

```shell
mvn javadoc:javadoc
```

## Configuration

In project root create file `config.properties` with this contents:

```
login=ORACLE_DB_LOGIN
password=ORACLE_DB_PASSWORD
jbdc=ORACLE_DB_JDBC
```

where `ORACLE_DB_LOGIN` and `ORACLE_DB_PASSWORD` are your login credentials to Oracle database. With `ORACLE_DB_JDBC` you can specify JDBC connection string.

Optionally you can specify:

```
debug-true
```

for printing debug information to standard output.

## Run

In project root run:

```shell
./run.sh
```

To clean generated files run:

```shell
./clean.sh
```

## License

    Copyright 2017 Matúš Bútora, Andrej Klocok, Tomáš Vlk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
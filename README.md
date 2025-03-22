# JUnit5 Database Utils - `junit5-dbutils`

## Artifact Coordinates
```
<groupId>org.ldejonghe.utils.junit5</groupId>
<artifactId>junit5-dbutils</artifactId>
<version>1.0.0-jakarta</version>
```

## Purpose
`junit5-dbutils` is a JUnit 5 extension library designed to:
- Replace DBUnit-like functionality
- Load database datasets from XML
- Integrate with JUnit 5 Jupiter lifecycle
- Remain Jakarta EE 10 / Spring Boot 3.x compatible

## Architecture Overview
```
org.ldejonghe.utils.junit5.db
│
├── @LoadDataset             --> Custom annotation to specify XML dataset
├── DatasetLoaderExtension   --> JUnit5 Extension that processes @LoadDataset
└── XmlDataLoader            --> Utility to parse XML and populate database
```

- `@LoadDataset`: placed on test methods, defines which dataset to load
- `DatasetLoaderExtension`: automatically detects `@LoadDataset` and loads data
- `XmlDataLoader`: parses a flat DBUnit-style XML and inserts data using JDBC

## Usage Example

### 1. Add as Dependency
```xml
<dependency>
    <groupId>org.ldejonghe.utils.junit5</groupId>
    <artifactId>junit5-dbutils</artifactId>
    <version>1.0.0-jakarta</version>
    <scope>test</scope>
</dependency>
```

### 2. Example Dataset XML (`test-users.xml`)
```xml
<dataset>
    <user id="1" name="John" age="30"/>
    <user id="2" name="Jane" age="25"/>
</dataset>
```

### 3. Write the Test
```java
@ExtendWith(DatasetLoaderExtension.class)
class UserRepositoryTest {

    static DataSource dataSource = ... // your DataSource

    static DatasetLoaderExtension extension = new DatasetLoaderExtension(dataSource);

    @RegisterExtension
    static DatasetLoaderExtension register = extension;

    @Test
    @LoadDataset("src/test/resources/test-users.xml")
    void testFindUser() {
        // Your test logic here
    }
}
```

The dataset will be loaded into the database before the test runs.

## Extensibility Ideas (Future Work)
- `@ExpectedDataset` to verify database state after test
- JSON dataset support
- Automatic cleanup or rollback

## Requirements
- Java 17 or higher
- JUnit 5
- JDBC DataSource
- JAXB Runtime (for XML parsing)

## License
MIT 

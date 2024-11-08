# java-lens

Automatic lens generation to support transformations to records.

[![Maven Central Version](https://img.shields.io/maven-central/v/nl.bvkatwijk/java-lens?versionPrefix=0)](https://mvnrepository.com/artifact/nl.bvkatwijk/java-lens)
[![ci](https://github.com/bvkatwijk/java-lens/actions/workflows/gradle.yml/badge.svg)](https://github.com/bvkatwijk/java-lens/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/github/bvkatwijk/java-lens/graph/badge.svg?token=9aIaRmZ2ON)](https://codecov.io/github/bvkatwijk/java-lens)

## Purpose
If you use records and you want to apply some transformation, code can get verbose, repetitive and error-prone. For example:
```java
public record Person(String name, Address address, Address work, List<Person> friends) { }
public record Address(String street, int number, City city) implements LensOps<Address> { }
public record City(String name) { }

public static Person moveToNewYork(Person person) {
    Address address = original.address;
    var updatedCity = new City("New York");
    var updatedAddress = new Address(address.street, address.number, updatedCity);
    return new Person(person.name, updatedAddress, person.work, person.friends);
}
```
Using Lenses you can annotate your records, giving you a DSL to make specific changes:

```java
public static Person moveToNewYork(Person person) {
    return PersonLens.µ
            .address()
            .city()
            .name()
            .with("New York")
            .apply(ALICE);
}
```

## Usage
Add `@Lenses` annotation to your record(s)
```java
@Lenses
public record Person(String name, Address address, Address work, List<Person> friends) { }
```
See [Examples](./example) for more usage examples.

## Development

### Annotation Debugging
Run gradle compiler in debug mode
```bash
./gradlew --no-daemon -Dorg.gradle.debug=true clean build
```
Gradle process will wait for debugger to attach.
Run IDE debugging process for port 5005. 

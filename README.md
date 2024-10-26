# java-lens

Automatic lens generation to support transformations to records.

## Install
`TODO Publish, then add maven/gradle setup here`

## Usage
```java
@With // Add Lombok with
@Lenses // Generate a PersonLens helper class
public record Person(String name) { }
```

You can create modifying functions using lenses:

https://github.com/bvkatwijk/java-lens/blob/main/lens/src/test/java/nl/bvkatwijk/lens/PersonLensTest.java#L23-L25


You can use lens chaining to perform deep transformation:
```java
PersonLens.ROOT
        .address()
        .city()
        .name(),
    name -> "New York");
```

In vanilla java this would be done

You can curry the lens with an operation to have a function for the outer type:

```java
UnaryOperator<Person> nameCapitalizer = PersonLens.NAME.apply(String::toUpperCase);
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

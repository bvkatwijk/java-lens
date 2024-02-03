# java-lens

Automatic lens generation to easily apply transformations to records.

## Install
`TODO Publish, then add maven/gradle setup here`

## Usage
```java
@Lenses // <-- 1. Add annotation
public record Person(String name, Address address)
    implements ApplyLens<Person> // <-- 2. (Optional) Add convenience method
{
     
}
```

Now you can quickly apply transformations, such as changing username:
```java
var bob = alice.with(PersonLens.NAME, name -> "Bob");
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

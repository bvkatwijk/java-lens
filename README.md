# java-lens

Automatic lens generation to support transformations to records.

## Install
`TODO Publish, then add maven/gradle setup here`

## Usage
Add `@Lenses` annotation to generate a helper class:

https://github.com/bvkatwijk/java-lens/blob/f2c223515c4f1396c07e40730af0175a94debeb2/lens/src/test/java/nl/bvkatwijk/lens/example/PersonLensTest.java#L19-L23

This creates Lens instances that you can use:
https://github.com/bvkatwijk/java-lens/blob/f2c223515c4f1396c07e40730af0175a94debeb2/lens/src/test/java/nl/bvkatwijk/lens/example/PersonLensTest.java#L40-L41

You can use this to transform record components:
https://github.com/bvkatwijk/java-lens/blob/f2c223515c4f1396c07e40730af0175a94debeb2/lens/src/test/java/nl/bvkatwijk/lens/example/PersonLensTest.java#L46-L50

If you need to perform transformations multiple levels deep, you can use Root chaining:
https://github.com/bvkatwijk/java-lens/blob/f2c223515c4f1396c07e40730af0175a94debeb2/lens/src/test/java/nl/bvkatwijk/lens/example/PersonLensTest.java#L55-L59

See [Examples](./example) for more usage examples.

## Development

### Annotation Debugging
Run gradle compiler in debug mode
```bash
./gradlew --no-daemon -Dorg.gradle.debug=true clean build
```
Gradle process will wait for debugger to attach. 
Run IDE debugging process for port 5005. 

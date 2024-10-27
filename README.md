# java-lens

Automatic lens generation to support transformations to records.

[![ci](https://github.com/bvkatwijk/java-lens/actions/workflows/gradle.yml/badge.svg)](https://github.com/bvkatwijk/java-lens/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/github/bvkatwijk/java-lens/graph/badge.svg?token=9aIaRmZ2ON)](https://codecov.io/github/bvkatwijk/java-lens)

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

If you want to apply multiple transformations on the same instance, adding the `LensOps` interface can be useful:
https://github.com/bvkatwijk/java-lens/blob/f2c223515c4f1396c07e40730af0175a94debeb2/lens/src/test/java/nl/bvkatwijk/lens/example/PersonLensTest.java#L64-L67

See [Examples](./example) for more usage examples.

## API Terminology
Note: subject to change - especially if the community or Java introduces terminology in this realm
- See [JEP 468](https://openjdk.org/jeps/468).
- See [Lombok With](https://projectlombok.org/features/With)
  - [Lombok post considering With naming](https://www.patreon.com/posts/wither-is-bad-29453159)

### Get
Retrieve property 
- `get`: read access to property

### With
Write access to property (set a new value without considering previous value).
Alternative terms could be `set`, `copy`

### Modify
Transform access to property (set new value based on previous value).
Alternative terms could be `set`, `copy`. Also, the distinction from `with` could be dropped.

## Development

### Annotation Debugging
Run gradle compiler in debug mode
```bash
./gradlew --no-daemon -Dorg.gradle.debug=true clean build
```
Gradle process will wait for debugger to attach.
Run IDE debugging process for port 5005. 

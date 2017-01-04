Local Development
=================
This project uses the new composite gradle build feature, which is not yet supported by intellij.
In order to make changes, it's easiest if you open up the `plugin` directory and the `tests` directory separately in Android Studio.
Testing local changes can be done using `./gradlew test` from the root of the repository.

Test Local Changes
------------------
This will build your local changes for the plugin, and run the integration tests.

1. `./gradlew test`

Publish to mavenLocal
---------------------

1. Update `ARTIFACT_VERSION` in `plugin/gradle.properties`
2. Update `ARTIFACT_VERSION` in `tests/gradle.properties`
3. `cd plugin`
4. `./gradlew install`

Releasing to bintray
--------------------

1. Update `ARTIFACT_VERSION` in `plugin/gradle.properties`
2. Update `ARTIFACT_VERSION` in `tests/gradle.properties`
3. `cd plugin`
4. `./gradlew bintrayUpload`

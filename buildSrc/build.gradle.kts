plugins { `kotlin-dsl` }

repositories { mavenCentral() }

@Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }
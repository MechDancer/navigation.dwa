import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	kotlin("jvm") version "1.3-M2"
}

group = "mechdancer"
version = "1.0-SNAPSHOT"

repositories {
	maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
	mavenCentral()
	jcenter()
}

dependencies {
	compile(kotlin("stdlib-jdk8"))
	compile("org.mechdancer:linearalgebra:0.1.1")
	testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

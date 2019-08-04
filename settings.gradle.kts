rootProject.name = "house-med-assistant"

include("house-med-assistant-app")


pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { setUrl("http://repo.spring.io/milestone") }
    }

    val springBootVersion: String by settings
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useModule("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
            }
        }
    }
}
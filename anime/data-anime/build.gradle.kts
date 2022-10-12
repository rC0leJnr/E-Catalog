apply {
    from("$rootDir/kotlin-library.gradle")
    plugin("kotlin-kapt")
    plugin("kotlin-parcelize")
    plugin("dagger.hilt.android.plugin")
}

dependencies {

    "implementation"(project(Modules.core))

    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.converter)
    "implementation"(Retrofit.okhttp)
    "implementation"(Retrofit.interceptor)

    "implementation"(Hilt.android)
    "kapt"(Hilt.compiler)

    "implementation"(Room.roomRuntime)
    "implementation"(Room.room)
    "implementation"(Room.paging)
    "kapt"(Room.compiler)

    "implementation"(Paging.paging)
    "implementation"(Paging.runtime)

    "implementation"(Coroutines.core)
}
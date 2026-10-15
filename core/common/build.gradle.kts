
plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.fakestore.core.common"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }
}

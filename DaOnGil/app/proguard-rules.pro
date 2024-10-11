# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 소스 파일 및 라인 정보 유지
-keepattributes SourceFile,LineNumberTable

# 소스 파일의 변수명 변경
-renamesourcefileattribute SourceFile

# Firebase Cloud Messaging
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keep class com.squareup.retrofit2.** { *; }
-keep class okhttp3.** { *; }

# kakao login
-keep interface com.kakao.sdk.** { *; }

# 카카오 SDK 관련 클래스와 메서드를 난독화에서 제외
-keep class com.kakao.sdk.** { *; }
-keep class com.kakao.auth.** { *; }
-keep class com.kakao.network.** { *; }
-keep class com.kakao.util.** { *; }
-keep class com.kakao.util.helper.** { *; }
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# naver
-keep class com.navercorp.nid.NaverIdLoginSDK { *; }
-keep class org.simpleframework.xml.** { *; }
-keep public class com.navercorp.nid.** {
    public *;
}

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

# 서버 응답 데이터 필드 이름 난독화 예외 처리
-keep class kr.techit.lion.data.dto.** { *; }
-keep class com.squareup.moshi.** { *; }
-keepattributes Signature
-keepattributes Annotation
-keep class kr.techit.lion.data.dto.request.util.AdapterProvider { *; }

-keep class kr.techit.lion.data.database.** {
    <fields>;
}

# Kotlin 특수 클래스 유지
-keepclassmembers class **$WhenMappings { <fields>; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { public <methods>; }
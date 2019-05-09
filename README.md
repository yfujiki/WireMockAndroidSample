# WireMockAndroidSample
Trying to make wiremock work with latest Android setup. 

In complete. 

Cleared several stages though.

## Cleared issues
### Error: MethodHandle.invoke and MethodHandle.invokeExact are only supported starting with Android O (--min-api 26)
You can avoid this build error by downgrading jetty-server version. 

```
android: {
...
    configurations.all {
        resolutionStrategy {
            force 'org.eclipse.jetty:jetty-server:9.4.14.v20181114'
        }
    }
}
```

### More than one file was found with OS independent path 'META-INF/DEPENDENCIES'

You can avoid this build error by excluding 'META-INF/DEPENDENCIES' from packageOptions

```
android: {
...
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
    }
}
```

### java.lang.NoSuchFieldError: No static field INSTANCE of type Lorg/apache/http/conn/ssl/AllowAllHostnameVerifier; in class Lorg/apache/http/conn/ssl/AllowAllHostnameVerifier; or its superclasses (declaration of 'org.apache.http.conn.ssl.AllowAllHostnameVerifier' appears in /system/framework/framework.jar!classes3.dex)

I think what this runtime error (it happens when you run UI test) means is that the wiremock library is using old `HTTPClient` that resides in `org.apache.httpcomponents`.
So, what I tried was to exclude that module from wiremock's dependency and use alternative `HTTPClient`.

```
dependencies: {
...
    androidTestImplementation("com.github.tomakehurst:wiremock-jre8:2.23.2") {
        exclude group: 'org.apache.httpcomponents', module: 'httpclient'
    }

    androidTestImplementation 'org.apache.httpcomponents:httpclient-android:4.3.5.1'
}
```

But now I get runtime error like

```
java.lang.NoClassDefFoundError: Failed resolution of: Lorg/apache/http/protocol/HttpRequestExecutor;
at org.apache.http.impl.client.HttpClientBuilder.build(HttpClientBuilder.java:695)
at com.github.tomakehurst.wiremock.http.HttpClientFactory.createClient(HttpClientFactory.java:91)
at com.github.tomakehurst.wiremock.http.ProxyResponseRenderer.<init>(ProxyResponseRenderer.java:59)
at com.github.tomakehurst.wiremock.core.WireMockApp.buildStubRequestHandler(WireMockApp.java:155)
at com.github.tomakehurst.wiremock.WireMockServer.<init>(WireMockServer.java:74)
at com.github.tomakehurst.wiremock.junit.WireMockRule.<init>(WireMockRule.java:43)
at com.github.tomakehurst.wiremock.junit.WireMockRule.<init>(WireMockRule.java:39)
at com.yfujiki.wiremocksample.MainActivityTest.<init>(MainActivityTest.kt:27)
...
```

To be continued...



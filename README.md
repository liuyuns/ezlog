# ezlog
Simple, easy, and light weight logger for Android.<br>
Logs are grouped into four categories:
 * Tracing function enter/exit
 * Information
 * Warning
 * Error

<i>All logs are printed for debug build, and only warn and error are printed for release build.</i>

## usage

build.gradle

```groovy
compile 'io.github.liuyuns:ezlog:1.0.0'
```

## apis

```Java
  Logger logger = Logger.getLogger("Sample");

  // Tracing enter & exit of a method
  logger.ee();    // log enter & exit
  logger.enter();
  logger.exit();

  // Info groups;
  logger.info(String message);
  logger.info(String msgFormat, Object... args);

  // Warning groups:
  logger.warn(String message);
  logger.warn(String msgFormat, Object... args);

  // Error groups:
  logger.error(Exception e);
  logger.error(String errorMessage);
  logger.error(String msgFormat, Object... args);
  logger.error(Exception e, String msgFormat, Object... args)
```

## sample

```Java
public class SampleFragment extends Fragment{
  Logger log = Logger.getLogger("SampleFragment");

  public void onCreate(Bundle savedInstanceState){
    super.onCreate();
    log.ee();       // simply log an enter and an exit entry.
  }

  public void onHiddenChanged(boolean hidden){
    log.enter("hidden:%b", hidden);
    super.onHiddenChanged(hidden);

    if (!hidden){
      log.info("process it for fragment becomes visible");
      // logic
      try{
        throw new Exception("TestException");
      }
      catch(Exception ex){
        log.error(ex, "Error when doing something");
      }
    }

    log.exit();
  }
}
    
```

# license

<pre>
Copyright 2017 Easton Liu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
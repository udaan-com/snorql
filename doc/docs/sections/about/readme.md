
SNORQL comes with two modules

- snorql-framework
- snorql-extensions

### What is snorql-framework?

This is a basic framework of the underlying tool used to build metrics. It is extensible in nature and contains all the models, interfaces and classes that can be used to build your own metric.


### What is snorql-extensions?

This is built using `snorql-framework` and acts as a repository of SQL metrics that can be consumed in your project layer.
It exposes useful SQL metrics that can be integrated & used with your application

Current extensions include:

- Blocked Queries
- Long-running Queries
- Active Queries <br>
  ... [to view the complete list please click here](https://github.com/udaan-com/snorql/wiki/snorql-extensions)

You can also add your own metrics by following the instructions below under [Build your own custom metrics using snorql](#build-your-own-custom-metrics-using-snorql).

> See issues with [new-metric](https://github.com/udaan-com/snorql/labels/new-metric) label for details on newer extension planned in the roadmap.


<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

* [Kotlin](https://kotlinlang.org/)
* [Maven](https://maven.apache.org/)

<p align="right">(<a href="#top">back to top</a>)</p>


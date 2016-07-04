# Hystrix Context Spring Boot starter

> A Spring Boot starter for making Hystrix context aware

[![Build Status](https://travis-ci.org/jmnarloch/hystrix-context-spring-boot-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/hystrix-context-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/hystrix-context-spring-boot-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/hystrix-context-spring-boot-starter?branch=master)

## Setup

Add the Spring Boot starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>hystrix-context-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

## Properties

The only supported property is `hystrix.wrappers.enabled` which allows to disable this extension.

```
hystrix.wrappers.enabled=true # true by default
```

## License

Apache 2.0

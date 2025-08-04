## :star: New Features

- Make dependencies on AssertJ and JUnit in `spring-core-test` optional [#34612](https://github.com/spring-projects/spring-framework/issues/34612)
- Suggest compilation with `-parameters` when `AspectJAdviceParameterNameDiscoverer` fails against ambiguity [#34609](https://github.com/spring-projects/spring-framework/issues/34609)
- SseBuilder in ServerResponse should allow empty comment [#34608](https://github.com/spring-projects/spring-framework/issues/34608)
- MockServerWebExchange does not allow setting the ApplicationContext on the base class [#34601](https://github.com/spring-projects/spring-framework/issues/34601)
- `FormHttpMessageConverter` should throw `HttpMessageNotReadableException` when the http form data is invalid [#34594](https://github.com/spring-projects/spring-framework/pull/34594)
- Provide a method to retrieve all singleton autowire candidates from the bean factory [#34591](https://github.com/spring-projects/spring-framework/issues/34591)

## :lady_beetle: Bug Fixes

- PathMatchingResourcePatternResolver regression for jar root scanning in 6.2.4 [#34607](https://github.com/spring-projects/spring-framework/issues/34607)
- AbstractReactiveTransactionManager throws IllegalStateException when rollback fails after commit attempt [#34595](https://github.com/spring-projects/spring-framework/issues/34595)
- Recursively boxing/unboxing nested inline value classes [#34592](https://github.com/spring-projects/spring-framework/pull/34592)

## :notebook_with_decorative_cover: Documentation

- `MvcUriComponentsBuilder` javadocs inaccurately reflects usage of forwarded headers [#34615](https://github.com/spring-projects/spring-framework/issues/34615)
- Fix formatting and update links to scripting libraries and HDIV [#34603](https://github.com/spring-projects/spring-framework/pull/34603)
- Remove dubious link to MockObjects Web site in reference manual [#34593](https://github.com/spring-projects/spring-framework/issues/34593)
- Fix `StringUtils#uriDecode` Javadoc [#34590](https://github.com/spring-projects/spring-framework/issues/34590)

## :hammer: Dependency Upgrades

- Upgrade to ASM 9.8 (for early Java 25 support) [#34600](https://github.com/spring-projects/spring-framework/issues/34600)

## :heart: Contributors

Thank you to all the contributors who worked on this release.

[@dforrest-es](https://github.com/dforrest-es), [@dmitrysulman](https://github.com/dmitrysulman), [@dspasic](https://github.com/dspasic), [@dsyer](https://github.com/dsyer), [@Helmsdown](https://github.com/Helmsdown), [@jhoeller](https://github.com/jhoeller), [@kristofdepypere](https://github.com/kristofdepypere), [@ngocnhan-tran1996](https://github.com/ngocnhan-tran1996), [@rstoyanchev](https://github.com/rstoyanchev), [@sdeleuze](https://github.com/sdeleuze), [@SledgeHammer01](https://github.com/SledgeHammer01), [@xardael](https://github.com/xardael)
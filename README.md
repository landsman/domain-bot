# GoDaddy Bot

⚠️ Be aware of this stupid GoDaddy limitation!

> Access to parts of our Domains API in Production may require meeting certain criteria: Availability API: Limited to accounts with 50 or more domains. Management and DNS APIs: Limited to accounts with 10 or more domains and/or an active Discount Domain Club – Premier Membership plan.

Check domain availability, send notification or buy it for me.

## Doc

- [API keys and documentation](https://developer.godaddy.com)

## Build and deploy

Jar file

```bash
./gradlew build
```

GraalVM

```bash
./gradlew nativeCompile --stacktrace
```
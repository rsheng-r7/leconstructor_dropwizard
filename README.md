orchestrator_test_dropwizard
=============

This is a bare DropWizard REST API; the rest is up to you.

Verifying Application Status
============================

This service exposes, via admin port 8888, a `/healthcheck` resource, which accepts only a `GET` request. A response code of `HTTP 200 OK` indicates a healthy service; anything else should be considered unhealthy.

Also exposed, via admin port, is a similar `/readycheck` resource. This is a "deeper" health check, which also reports on the status of required external services.
Where all external services are reachable, the response code is `HTTP 200 OK`. Otherwise, it is `HTTP 503 SERVICE UNAVAILABLE`. Included is a JSON response body:

```json
{
    "ready": true,
    "dependencies": {
         "SomeApi": true,
         "otherApi": true
    }
}
```

Building and Testing
====================

For various reasons, `logentries-parent` requires at least one commit before building is possible:
```
git init
git add README.md
git commit -m "Initial commit"
```

The project can then be fully built, including testing, by running `mvn clean verify`.
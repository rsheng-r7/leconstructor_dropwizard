Feature: HealthCheck/ReadyCheck

  @healthcheck
  Scenario: Service is healthy
    When I send an admin GET request to "/healthcheck"
    Then I get a response with status "200"
    And the response body indicates that the service is healthy

  @readycheck
  Scenario: Service is ready
    When I send an admin GET request to "/readycheck"
    Then I get a response with status "200"
    And the response body indicates that the service is ready
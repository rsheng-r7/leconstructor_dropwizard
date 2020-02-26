#! /bin/sh

bash ./acceptance_tests/stop.sh

EXECUTOR_NUMBER="${EXECUTOR_NUMBER:-0}"
REPO_NAME="orchestrator_test_dropwizard"
PROJECT_NAME="${REPO_NAME}_${EXECUTOR_NUMBER}"

cd docker && docker-compose -p "${PROJECT_NAME}" up -d


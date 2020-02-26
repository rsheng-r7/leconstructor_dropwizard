#! /bin/sh

EXECUTOR_NUMBER="${EXECUTOR_NUMBER:-0}"
REPO_NAME="orchestrator_test_dropwizard"
PROJECT_NAME="${REPO_NAME}_${EXECUTOR_NUMBER}"

docker exec "${PROJECT_NAME}_orchestrator_test_dropwizard-tests_1" bash -c "/docker/wait-for.sh orchestrator_test_dropwizard:8888 -t 60 && behave /tests $@"


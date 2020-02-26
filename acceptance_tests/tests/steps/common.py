from behave import then

import requests


@when('I send a GET request to "{path}"')
def get_request_to_url(context, path):
    context.response.update(requests.get(f'{context.base_url}{path}'))


@when('I send an admin GET request to "{path}"')
def get_request_to_admin_url(context, path):
    context.response.update(requests.get(f'{context.admin_url}{path}'))


@then('I get a response with status "{code:d}"')
def response_with_status(context, code):
    assert context.response.status_code == code, \
        f'Expected response code "{code}" but was "{context.response.status_code}"'
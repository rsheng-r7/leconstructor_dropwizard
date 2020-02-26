from behave import when, then


@then('the response body indicates that the service is ready')
def response_body_indicates_service_ready(context):
    assert context.response.status_code == 200
    assert context.response.body == {
        'ready': True,
        'dependencies': {}
    }


@then('the response body indicates that the service is healthy')
def response_body_indicates_service_healty(context):
    response_body = context.response.body
    assert context.response.status_code == 200
    assert response_body.get('deadlocks').get('healthy')
    assert response_body.get('base').get('healthy')

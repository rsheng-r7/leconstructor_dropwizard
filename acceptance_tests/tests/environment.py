from behave import use_fixture, fixture


class CustomResponse(object):
    """
    Custom Response object
    """
    def __init__(self, response=None):
        self.status_code = None if response is None else response.status_code
        self.body = None if response is None else response.json()

    def update(self, resp):
        """
        Update response
        """
        self.status_code = resp.status_code
        try:
            self.body = resp.json()
        except ValueError:
            self.body = resp.text


@fixture
def response(context):
    resp = CustomResponse()
    context.response = resp
    return resp


def before_all(context):
    use_fixture(response, context)
    context.base_url = 'http://orchestrator_test_dropwizard:9999'
    context.admin_url = 'http://orchestrator_test_dropwizard:8888'

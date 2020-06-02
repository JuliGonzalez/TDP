# flask_web/main.py

from flask import Flask
from flask import request
app = Flask(__name__)


@app.route("/")
def hello():
    return "Hello World from Flask"


@app.route("/connections", methods=['POST'])
def handle_connections():
    connection = request.get_json()
    id = connection['id']
    return "the id is: {}".format(id)


if __name__ == "__main__":
    # Only for debugging while developing
    app.run(host='0.0.0.0', debug=True, port=80)

from flask import Flask, request
from bson.json_util import dumps
from bson.objectid import ObjectId
import pymongo

app = Flask(__name__)
client = pymongo.MongoClient("mongodb+srv://hackdays:hackdays@cluster0-yih1u.mongodb.net/test?retryWrites=true")
database = client["hackdays"]
topics = database["topics"]


@app.route('/threads', methods=['GET'])
def get_threads():
    """
    Gets all threads from the database and returns them as JSON.
    :return: a JSON string containing all threads including nested comments
    """
    return dumps([x for x in topics.find().sort('rating', pymongo.DESCENDING)])


@app.route('/', methods=['GET'])
def index():
    return "<h1>FUCK THIS SHIT!</h1>"


@app.route('/threads/<topic_id>', methods=['GET'])
def get_thread(topic_id):
    """
    Gets a thread based on an ID
    :return: details for one specific thread
    """
    return dumps(topics.find_one({'_id': ObjectId(topic_id)}))


@app.route('/threads/add', methods=['POST'])
def add_thread():
    """
    Adds a thread to the database based on the given post data.
    """
    document = {
        "header": request.args.get("header"),
        "author": request.args.get("author"),
        "rating": 0,
        "comments": []
    }
    topics.insert_one(document)
    return "Done"


@app.route('/threads/comment/<topic_id>', methods=['POST'])
def add_comment(topic_id):
    t = topics.find_one({'_id': ObjectId(topic_id)})
    comments = t['comments']
    comments.append([request.args.get("comment"), 0, request.args.get("author")])
    upd = {"$set": {"comments": comments}}
    topics.update_one({'_id': ObjectId(topic_id)}, upd)
    return "Done"


@app.route('/threads/like/<topic_id>', methods=['POST'])
def like_thread(topic_id):
    t = topics.find_one({'_id': ObjectId(topic_id)})
    r = t['rating']
    r += 1
    upd = {"$set": {"rating": r}}
    topics.update_one({'_id': ObjectId(topic_id)}, upd)
    return "Done"


@app.route('/threads/like/<topic_id>/<int:post_number>', methods=['POST'])
def like(topic_id, post_number):
    t = topics.find_one({'_id': ObjectId(topic_id)})
    comments = t['comments']
    comments[post_number][1] += 1
    upd = {"$set": {"comments": comments}}
    topics.update_one({'_id': ObjectId(topic_id)}, upd)
    return "Done"


@app.route('/threads/search/')
def list_threads():
    return dumps([x for x in topics.find().sort('rating', pymongo.DESCENDING)])


@app.route('/threads/search/<query>')
def search(query):
    if query == "" or query == " ":
        return dumps([x for x in topics.find().sort('rating', pymongo.DESCENDING)])
    else:
        topics.create_index([('header', 'text')])
        return dumps(topics.find({"$text": {"$search": query}}).sort('rating', pymongo.DESCENDING))


if __name__ == '__main__':
    app.run()

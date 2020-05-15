from flask import Flask, request
from bson.json_util import dumps
from bson.objectid import ObjectId
import pymongo

# create app
app = Flask(__name__)

# connect to database collection
client = pymongo.MongoClient("mongodb+srv://hackdays:<password>@cluster0-yih1u.mongodb.net/test?retryWrites=true")
database = client["hackdays"]
topics = database["topics"]


@app.route('/threads', methods=['GET'])
def get_threads():
    """
    Gets all threads from the database and returns them as JSON.
    :return: a JSON string containing all threads including nested comments ordered by the rating in descending order
    """
    return dumps([x for x in topics.find().sort('rating', pymongo.DESCENDING)])


@app.route('/', methods=['GET'])
def index():
    """
    A placeholder for accessing the index route
    :return:
    """
    return "<h1>Welcome to LufthanSTARS!</h1>"


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

    # create a document with the given information
    # NOTE that a new topic does not have comments and ratings
    document = {
        "header": request.args.get("header"),
        "author": request.args.get("author"),
        "rating": 0,
        "comments": []
    }

    # add to database
    topics.insert_one(document)
    return "Done"


@app.route('/threads/comment/<topic_id>', methods=['POST'])
def add_comment(topic_id):
    """
    Adds a comment to a given topic
    :param topic_id: the id of the topic the comment will be appended at
    :return:
    """

    # find topic
    t = topics.find_one({'_id': ObjectId(topic_id)})

    # add comment
    comments = t['comments']
    comments.append([request.args.get("comment"), 0, request.args.get("author")])

    # update database
    upd = {"$set": {"comments": comments}}
    topics.update_one({'_id': ObjectId(topic_id)}, upd)
    return "Done"


@app.route('/threads/like/<topic_id>', methods=['POST'])
def like_topic(topic_id):
    """
    Like a certain topic
    :param topic_id: the given topic
    :return:
    """

    # find topic
    t = topics.find_one({'_id': ObjectId(topic_id)})
    r = t['rating']

    # increase rating
    r += 1

    # update in database
    upd = {"$set": {"rating": r}}
    topics.update_one({'_id': ObjectId(topic_id)}, upd)
    return "Done"


@app.route('/threads/like/<topic_id>/<int:post_number>', methods=['POST'])
def like_comment(topic_id, post_number):
    """
    Like a specific comment
    :param topic_id: the id of the topic
    :param post_number: the index of the comment
    :return:
    """

    # find topic
    t = topics.find_one({'_id': ObjectId(topic_id)})

    # increase rating
    comments = t['comments']
    comments[post_number][1] += 1

    # update in database
    upd = {"$set": {"comments": comments}}
    topics.update_one({'_id': ObjectId(topic_id)}, upd)
    return "Done"


@app.route('/threads/search/', methods=["GET"])
def list_threads():
    """
    Lists all topics for an empty search
    :return: all topics
    """
    return dumps([x for x in topics.find().sort('rating', pymongo.DESCENDING)])


@app.route('/threads/search/<query>', methods=['GET'])
def search(query):
    """
    Lists all topics containing the given search query.
    :param query: the search query
    :return: all topics containing a specific search query
    """
    # remove all white spaces to cover all kinds of empty search queries
    query = query.strip()

    # empty search - list all
    if query == "":
        return dumps([x for x in topics.find().sort('rating', pymongo.DESCENDING)])

    # filter according to search
    else:
        topics.create_index([('header', 'text')])
        return dumps(topics.find({"$text": {"$search": query}}).sort('rating', pymongo.DESCENDING))


# run app
if __name__ == '__main__':
    app.run()

var express = require('express');
var router = express.Router();
var url = require('url');
var mgclient = require('mongodb').MongoClient, assert = require('assert');
var curl = 'mongodb://localhost:27017/fitso';
var hash = require('object-hash');
var subgroups = {
    'g': ["bodyweight", "crossfit", "weights"],
    'c': ["run", "swim", "bike"],
    's': ["basketball", "football", "soccer", "vollyball", "ultimate"]
}
/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});

router.get('/new', function(req, res, next) {
    // name = %s email = %s, gender = mfo

    //Insert some validation here.
    var name = req.query.name;
    var email = req.query.email;
    var gender = req.query.gender;
    // test if the email is already in the database
    var uid = hash(email);
    mgclient.connect(curl, function(err, db){
        var col = db.collections("users");
        col.find({"uid": uid}).toArray(function(err, docs){
            assert.equal(err, null);
            if(docs.length > 0){
                res.send(
                    {"err": "EMAIL_ALREADY_EXISTS", "res": null}
                )
                return;
            }
            else{
                var doc = {"uid": uid,
                           "name": name,
                           "gender": gender,
                           "email": email
                        };
                col.insertOne(doc, function(err, r){
                    assert.equal(null, err)
                    assert.equal(1, r.insertedCount)
                })
                res.send({"err": null, "res": "success"})
            }
        })
        db.close();
    })
})

router.get('/groups', function(req, res, next) {
    // {g, c, s}, = {subgroups ?? }
})
module.exports = router;

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
    var age = req.query.age;
    var regex = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i
    var numrex = /^[1-9][0-9]?$/
    var gendex = /m|o|f/
    console.error(regex.test(email))
    // validate email
    if(!regex.test(email)){
        res.send({"err": "INVALID_EMAIL", "res": null});
    } else if (!numrex.test(age)) {
        res.send({"err": "INVALID_AGE", "res": null});
    } else if (!gendex.test(gender)) {
        res.send({"err": "INVALID_GENDER", "res": null});
    }
    else {
    // test if the email is already in the database
        age = parseInt(age); 
        var uid = hash(email);
        mgclient.connect(curl, function(err, db){
            if(err != null){ console.err(err)}
            var col = db.collection("users");
            col.find({"uid": uid}).toArray(function(err, docs){
                assert.equal(err, null);
                if(docs.length > 0){
                    res.send(
                        {"err": "EMAIL_ALREADY_EXISTS", "res": null}
                    )
                    db.close();
                    return;
                }
                else{
                    var doc = {"uid": uid,
                               "name": name,
                               "gender": gender,
                               "email": email,
                               "age": age
                            };
                    col.insertOne(doc, function(err, r){
                        assert.equal(null, err)
                        assert.equal(1, r.insertedCount)
                    })
                    db.close();
                    res.send({"err": null, "res": "success"})
                }
            })
        })
    }
})

router.get('/groups', function(req,res, next) {
    // {g, c, s}, = {subgroups ?? }
})
module.exports = router;

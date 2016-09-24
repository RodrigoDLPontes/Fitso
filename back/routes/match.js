var express = require('express');
var router = express.Router();
var url = require('url');
var mgclient = require('mongodb').MongoClient, assert = require('assert');
var curl = 'mongodb://localhost:27017/fitso';
var hash = require('object-hash');

var checkEmail = function(email){
    var regex = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i
    return regex.test(email)
}

//The user wants to create a new match, so find the top 10 profile matches within
//A certain distance and return that in a JSON array.
//new?lat=%d&lon=%d&dist=%d&email=%s&sport=%s
//Dist will be there but not implemented yet.
router.get("/new", function(req, res, next){
    if(!("email" in req.query) || !checkEmail(req.query.email)){
        res.send({"err": "INVALID_EMAIL", "res": null})
    } else if (!("sport" in req.query)) {
        res.send({"err": "NO_SPORT", "res": null})
    } else if (!("lat" in req.query) || !("lon" in req.query)){
        res.send({"err": "NO_POSITION", "res": null })
        db.close();
    } else {
        mgclient.connect(curl, function(err, db){
            var col = db.collection("users")
            col.find({"email": req.query.email}, {"name": 1, "skill": 1, "age": 1, "email": 1}).toArray(function(err, docs){
                assert.equal(err, null)
                if (docs.length != 1 ){
                    res.send({"err": "INVALID_EMAIL", "res": docs.length});
                } else {
                    mat = db.collection("matches")
                    var temp = {
                        sport: req.query.sport,
                        people: docs,
                        lat: parseFloat(req.query.lat),
                        lon: parseFloat(req.query.lon)
                    }
                    console.error(temp)
                    var tid = hash(JSON.stringify(temp))
                    console.error("tid", tid)
                    mat.insertOne({
                        id: tid,
                        sport: req.query.sport,
                        people: docs,
                        lat: parseFloat(req.query.lat),
                        lon: parseFloat(req.query.lon)
                    })
                    db.close();
                    res.send({"err": null, "res": "SUCCESS"})
                }
            })

        })
    }
})

//the user wnats to join an existing match
//join?matchid=%d&email=%s // the %d will be provided as a string with the browse request.
router.get("/join", function(req, res, next){
    if(!("matchid" in req.query)){
        res.send({"err": "NO_MATCH", "res": null})
    } else if (!("email" in req.query) || !checkEmail(req.query.email)){
        res.send({"err": "INVALID_EMAIL", "res": null})
    } else {
        mgclient.connect(curl, function(err, db){
            var matches = db.collection("matches")
            var users = db.collection("users")
            users.find({email: req.query.email}, {"name": 1, "skill": 1, "age": 1, "email": 1}).toArray(function(err, docs){
                console.error("FOUND", docs)
                if(docs.length != 1 ){
                    res.send({"err": "INVALID_EMAIL", "res": null})
                    db.close()
                } else {
                    console.error("IDMatch")
                    matches.updateOne({id: req.query.matchid}, {$addToSet: {"people": docs}},
                        function(err, result){
                            try {
                                assert.equal(err, null)
                                assert.equal(1, result.matchedCount)
                                res.send({"err": null, "res": "SUCCESS"})
                            } catch (e) {
                                res.send({"err": e, "res": null})
                            } finally {
                                db.close();
                            }
                        }
                    )
                }
            })
        })
    }
})

//the user wants to see all the different matches in the area
// browse?email=%s&lat=%d&lon=%d&dist=%d with dist not implemented yet.
router.get("/browse", function(req, res, next){
    if (!("email" in req.query) || ! checkEmail(req.query.email)){
        res.send({err: "INVALID_EMAIL", res: null})
    } else {
        mgclient.connect(curl, function(err, db){
            var matches = db.collection('matches');
            var c = matches.find({
                $and: [{lat: {$lt: parseFloat(req.query.lat)+1}}, {lat: {$gt: parseFloat(req.query.lat) -1 }}],
                $and: [{lon: {$lt: parseFloat(req.query.lon)+1}}, {lon: {$gt: parseFloat(req.query.lon) -1 }}]
            });
            c.each(function(err, item){
                console.error(item)
            })
        })
    }
})

module.exports = router;

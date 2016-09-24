var express = require('express');
var router = express.Router();
var url = require('url');
var mgclient = require('mongodb').MongoClient, assert = require('assert');
var curl = 'mongodb://localhost:27017/fitso';

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
                    mat.insertOne({
                        sport: req.query.sport,
                        people: docs,
                        lat: req.query.lat,
                        lon: req.query.lon
                    })
                    db.close();
                    res.send({"err": null, "res": "SUCCESS"})
                }
            })

        })
    }
})

//the user wnats to join an existing match
//join?matchid=%d // the %d will be provided as a string with the browse request.
router.get("/join", function(req, res, next){

})

//the user wants to see all the different matches in the area
// browse?lat=%d&lon=%d&dist=%d with dist not implemented yet.
router.get("/browse", function(req, res, next){

})

module.exports = router;

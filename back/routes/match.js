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
//new?lat=%d&lon=%d&dist=%d&email=%s
//Dist will be there but not implemented yet.
router.get("/new", function(req, res, next){

})

//The user has swiped, so lets initialize a new match element.
//create?parter=%s&email=%s&lat=%d&lon=%d&sport=%s
router.get("/create", function(req, res, next){
    if(!("partner" in req.query) || !checkEmail(req.query.partner)){
        res.send({"err": "INVALID_PARTNER_EMAIL", "res": null});
        db.close();
    } else if (!("email" in req.query) || !checkEmail(req.query.email)){
        res.send({"err": "INVALID_EMAIL", "res": null})
        db.close();
    } else if (!("lat" in req.query) || !("lon" in req.query)){
        res.send({"err": "NO_POSITION", "res": null })
        db.close();
    } else {
        mgclient.connect(curl, function(err, db){
            var col = db.collection("users");
            col.find({"email": {$in: [req.query.email, req.query.partner]}}, {"name": 1, "skill": 1, "age": 1, "email": 1} ).toArray(function(err, docs){
                if(docs.length != 2){
                    res.send({"err": "wrong docs", "res": docs.length})
                } else {
                    mat = db.collection("matches")
                    mat.insertOne({
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

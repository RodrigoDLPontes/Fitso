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
                    var tid = hash(JSON.stringify(temp))
                    mat.findOne({id: tid}, function(err, doc){
                        if(! (doc == undefined)){
                            res.send({"err": "ALREADY_EXISTS", "res": null});
                        } else {
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
                    matches.updateOne({id: req.query.matchid}, {$addToSet: {"people": docs[0]}},
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
var inRange = function(userLoc, otherLoc){
  var dist;

  dist = Math.sqrt((69*Math.abs(otherLoc[0] - userLoc[0]))^2 + (54.6*Math.abs(otherLoc[1] - userLoc[1]))^2);
  return dist
}
var compUser = function(usr1, usr2){
  a1 = usr1.age;
  s1 = usr1.skill;
  a2 = usr2.age;
  s2 = usr2.skill;

  num1 = Math.E^(((Math.abs(a1-a2))^2)/(((2)*((6.5)*s1+a1)^2)));
  den1 = Math.sqrt(2*(a1+(6.5)*s1)^2*Math.PI);
  score1 = num1/den1;

  num2 = (Math.E)^(((Math.abs(a1-a2))^2)/((2)*((6.5)*s2+a1)^2));
  den2 = Math.sqrt(2*(a1+(6.5)*s2)^2*Math.PI);
  score2 = num2/den2;

  if(score1 <= score2){
    return (score1/score2)*100;
  }
  else{
    return (score2/score1)*100;
  }
}

//the user wants to see all the different matches in the area
// browse?email=%s&lat=%d&lon=%d&dist=%d with dist not implemented yet.
router.get("/browse", function(req, res, next){
    if (!("email" in req.query) || ! checkEmail(req.query.email)){
        res.send({err: "INVALID_EMAIL", res: null})
    } else {
        mgclient.connect(curl, function(err, db){
            var matches = db.collection('matches');
            var col = db.collection('users');
            var me ;
            col.findOne({email: req.query.email}, function(err, docs){
                me = docs;
            })
            var fin = [];

            var c = matches.find({
                $and: [{lat: {$lt: parseFloat(req.query.lat)+1}}, {lat: {$gt: parseFloat(req.query.lat) -1 }}],
                $and: [{lon: {$lt: parseFloat(req.query.lon)+1}}, {lon: {$gt: parseFloat(req.query.lon) -1 }}]
            }).toArray(function(err, docs){
                for(i in docs){
                    u = docs[i]
                    console.log(u)
                    var isIn = inRange([req.query.lat, req.query.lon], [u.lat, u.lon])
                    console.error("RANGE: ", isIn)
                    if(isIn < 10){
                        var tscore = 0;
                        for(var i = 0; i < u.people.length; i++){
                            tscore += compUser(u.people[i], me);
                        }
                        u.dist = isIn;
                        u.comp = tscore;
                        fin.push(u)
                   }
                }
                console.log(fin)
                var by_dist = fin;
                var by_match = fin;
                by_dist.sort(function(a,b){return a.dist - b.dist})
                by_match.sort(function(a,b){return b.comp - a.comp})
                res.send({"err": null, "res": {dist: by_dist, match: by_match}})
            });
        })
    }
})

module.exports = router;

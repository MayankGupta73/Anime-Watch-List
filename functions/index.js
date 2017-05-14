var functions = require('firebase-functions');
var malScraper = require('mal-scraper')

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.seasonal = functions.https.onRequest((req,res) =>{
    malScraper.getSeason(2017, 'spring').then((result) => {
         TV = result.info.TV
         res.send(TV)
    }).catch((err) => {
        console.log(err)
    })
    
    console.log("Done")
})
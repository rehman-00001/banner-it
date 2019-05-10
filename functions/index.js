const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.feedback = functions.https.onRequest((request, response) => {
    if (request && request.body) {
        let currentDate = new Date();
        admin.database().ref(`/feedback/${currentDate.getFullYear()}/${currentDate.getMonth()+1}`)
            .push(request.body).then((success) => {
                response.status(200).send('Feedback submitted!');
            });
    } else {
        response.status(500).send('No input provided');
    }
});
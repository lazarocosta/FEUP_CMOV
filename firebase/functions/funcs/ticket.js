const functions = require('firebase-functions');
const admin = require('firebase-admin');
const crypto = require('crypto');
const UIDGenerator = require('uid-generator');
const cors = require('cors')({origin: true});
const uuidv1 = require('uuid/v1');
/**
Function to validateticket the icket
Parameters: ticketId -> 
        name ->
Output: JSON with id value 
Teste:
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/validateTicket --data '{"data" : { "ticketId" : "58e415e0-d792-11e8-b573-033213b03f30", "userId":"58e415e0-d792-11e8-b573-033213b03f30"}}' -g -H "Content-Type: application/json"
*/
const validTicket = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const ticketId = req.body.data.ticketId;
        const userId = req.body.data.userId;

        if(!ticketId) {
            res.status(400).send({ 'result': "Please enter a ticketId."});
            return;
        }

        if(!userId) {
            res.status(400).send({ 'result': "Please enter a userId."});
            return;
        }

        var usersRef = admin.firestore().collection('customer');
        usersRef.where('id', '==', userId).get()
        .then(snapshot => {

            if(snapshot.size != 1){
                res.status(400).send({ 'result':"Invalid userId"});
                return;
            }

            snapshot.forEach(userdoc => {
                var ticketsUserRef = admin.firestore().collection('customer').doc(userdoc.id).collection('ticket');

                ticketsUserRef.where('id', '==', ticketId).get()
                .then(snapshot2 => {
         
                    if(snapshot2.size != 1){
                        res.status(400).send({ 'data':"Invalid ticketId"});
                        return;
                    }
                    snapshot2.forEach(ticketdoc => {
                        const state = ticketdoc.data().state;
                        if(state == "not used") {
                            const ticketref = admin.firestore().collection('customer').doc(userdoc.id).collection('ticket').doc(ticketdoc.id);
                            ticketref.update({
                            state: 'used',
                            },{merge:true})  
                            res.status(200).send({'state':'validated'});
                        } 
                        else if(state == "used") {
                            res.status(200).send({'result':'was already used'});
                        }    

                    });
                })
                .catch(error => {
                    res.status(400).send({ 'result':"Error"});
                    console.error("Error", error);
                });
            });
        }).catch(error =>  {
            res.status(400).send({ 'result':"Error"});
            console.error("Error r", error);
        });
        return;
    });
});

module.exports = {
    validTicket
}
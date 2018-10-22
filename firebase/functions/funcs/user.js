const functions = require('firebase-functions');
const admin = require('firebase-admin');
const crypto = require('crypto');
// const UIDGenerator = require('uid-generator');
const cors = require('cors')({origin: true});
const uuidv1 = require('uuid/v1');

/**
Function to register the customer
Parameters: publicKey -> 
        name ->
        nif ->
        creditCardType ->
        creditCardNumber ->
        creditCardValidity ->
Output: JSON with id value 
Teste:
     curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/register --data '{"data" : { "publicKey" : "000030002300002", "name":"TESTE", "nif":"12121212", "creditCardType":"asas", "creditCardNumber":"11", "creditCardValiditya": "01 Jan 2020 00:00:00 GMT" }}' -g -H "Content-Type: application/json"
*/

const register = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const publicKey = req.body.data.publicKey;
        const name = req.body.data.name;
        const nif = req.body.data.nif;
        const creditCardType = req.body.data.creditCardType;
        const creditCardNumber = req.body.data.creditCardNumber;
        const creditCardValidity = req.body.data.creditCardValidity;

        if(!publicKey) {
            res.status(400).send({ 'id': "Please enter a publicKey."});
            return;
        }

        if(!name) {
            res.status(400).send({ 'id': "Please enter a name."});
            return;
        }

        if(!nif) {
            res.status(400).send({ 'id': "Please enter a nif."});
            return;
        }

        if(!creditCardType) {
            res.status(400).send({ 'id': "Please enter a creditCardType."});
            return;
        }

        if(!creditCardNumber) {
            res.status(400).send({ 'id': "Please enter a creditCardNumber."});
            return;
        }

        if(!creditCardValidity) {
            res.status(400).send({ 'id': "Please enter a creditCardValidity."});
            return;
        }


        const id = uuidv1();

        admin.firestore().collection('customer').add({
            publicKey: publicKey,
            name: name,
            nif: parseInt(nif),
            id:id,
            }).then(function(docRef) {
                admin.firestore().collection('customer').doc(docRef.id).collection('creditCard').add({
                    type: creditCardType,
                    number: parseInt(creditCardNumber),
                    validity: Date.parse(creditCardValidity),
                }).catch(function(error) {
                    res.status(400).send({ 'id':"Error adding creditCard of the user"});
                    console.error("Error adding creditCard of the user ", error);
                })
            .catch(function(error) {
                res.status(400).send({ 'id':"Error adding customer"});
                console.error("Error adding user", error);
            });
        res.status(200).send({'id':id});
        return;
        }).catch(err => {
            console.log(err);
            res.status(400).send({ 'id':"Could not create user!"});
            return;
        });
    });
});

module.exports={
    register,
}

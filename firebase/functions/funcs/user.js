const functions = require('firebase-functions');
const admin = require('firebase-admin');
const crypto = require('crypto');
const UIDGenerator = require('uid-generator');
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
     curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/register --data '{"data" : { "publicKey" : "000030002300002", "name":"TESTE", "nif":"12121212", "creditCardType":"asas", "creditCardNumber":"11", "creditCardValidity": "October 13, 2014 11:13:00" }}' -g -H "Content-Type: application/json"
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
        const nifValid = parseInt(nif);
        if(isNaN(nifValid)){
            res.status(400).send({ 'id':"nif not is number"});
            console.error("nif not is number: ", error);
            return;
        }

        admin.firestore().collection('customer').add({
            publicKey: publicKey,
            name: name,
            nif: nifValid,
            id:id,
            }).then(function(docRef) {
                const number = parseInt(creditCardNumber);
                const date = Date.parse(creditCardValidity);

                if(isNaN(number)|| is_date(date)) {
                    admin.firestore().collection('customer').doc(docRef.id).delete();
                    res.status(400).send({ 'id':"creditCardNumber not is number or creditCardValidity not is date"});
                    console.error("creditCardNumber not is number or creditCardValidity not is date: ", error);
                    return;
                }

                admin.firestore().collection('customer').doc(docRef.id).collection('creditCard').add({
                    type: creditCardType,
                    number: number,
                    validity: date,
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


/**
Function to login a participant
Parameters: username -> the USER name
	    password ->the password of the user
Output: JSON with data value that could be "Please enter a username|password", "invalid username|password", or the token of the session
TEST: 
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/login --data '{"data" : { "username" : "manuel", "password":"TESTE"}}' -g -H "Content-Type: application/json"
*/


const login = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const name = req.body.data.name;
        const password = req.body.data.password;

        if(!name){
            res.status(400).send({ 'data':"Please enter a name."});
            return;
        }

        if(!password){
            res.status(400).send({ 'data':"Please enter a password."});
            return;
        }

        var usersRef = admin.firestore().collection('customer');

        usersRef.where('name', '==', name).get()
            .then(snapshot => {
            if(snapshot.size > 1){
            res.status(400).send({ 'data':"Invalid name|password"});
            return;
        }
        if(snapshot.size < 1){
            res.status(400).send({ 'data':"Invalid name|password"});
            return;
        }
        snapshot.forEach(doc => {

            console.log(doc.id, '=>', doc.data().name);
        var userPass = doc.data().password;

        var hash = crypto.createHash('sha256').update(password).digest('base64');
        console.log(userPass)
        console.log(hash)

        //if password matches, generate token, save it in db and send it
        if(hash === userPass) {

            const uidGen = new UIDGenerator(256, UIDGenerator.BASE62);
            const token = uidGen.generateSync().toString();
            const date = new Date().toString();

            admin.firestore().collection('sessions').add({
                name: name,
                token: token,
                date: date
            });

            res.status(200).send({ 'data': token });

        }else {
            res.status(400).send({ 'data':"Invalid name/password."});
        }
        });
        return;
        })
        .catch(err => {
            console.log(err);
            res.status(400).send({ 'data':"Invalid name|password."});
            return;
        });

    });
});


module.exports={
    register,
    login,
}

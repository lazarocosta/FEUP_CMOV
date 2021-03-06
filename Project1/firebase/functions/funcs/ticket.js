const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cors = require('cors')({origin: true});
const uuidv1 = require('uuid/v1');
const random = require('random')
const crypto = require('crypto');
var Buffer = require('buffer/').Buffer
var getPem = require('rsa-pem-from-mod-exp');

/**
Function to validateticket the icket
Parameters: ticketId -> 
        userId ->
Output: JSON with result value 
Teste:
    curl -X POST https://luisbarbosa.ddns.net:5000/us-central1-cmov-d52d6.cloudfunctions.net/validTickets --data ' { "tickets":{"ticketId1" : "c53151b0-e415-11e8-82ca-35782305cc78", "tick":"349bcf80-e416-11e8-82ca-35782305cc78", "ticket3":"08c7ded0-e416-11e8-82ca-35782305cc78"}, "userId":"739c7ea0-e407-11e8-a890-d53adf44ae9e"}' -g -H "Content-Type: application/json"

    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/validTickets --data ' { "tickets":{"ticketId1" : "fdd775a0-e2c9-11e8-8f21-b3cacf712523"}, "userId":"739c7ea0-e407-11e8-a890-d53adf44ae9e"}' -g -H "Content-Type: application/json"
*/
const validTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        body = JSON.parse(req.body.toString())
        console.log(body)
        const tickets = body.tickets;
        const userId = body.userId;

        if(!tickets) {
            res.status(200).send({'error': "Please enter tickets."});
            return;
        }
        if (tickets.length == 0){
            res.status(200).send({ 'error': "Empty tickets array."});
            return;
        }

        if(!userId) {
            res.status(200).send({ 'error': "Please enter a user id."});
            return;
        }
        var listTickets = [];
        var resultTickets = []
        var performanceIdGeral;
        var usersRef = admin.firestore().collection('customer');

        for (ticket in tickets) {
            listTickets.push(tickets[ticket])
        }
        if(listTickets.length>4){
            res.status(200).send({ 'error':"Can only validate 4 tickets."});
            return; 
        }

        validTicketsAuxiliary(listTickets, userId).then(result=>{
            console.log(result)
           if(result.performanceIdGeral==undefined){
                res.status(200).send({ 'error':result});
                return;
           }
           performanceIdGeral =  result.performanceIdGeral;
           resultTickets = result.resultTickets;
           pastEvent(performanceIdGeral).then(result=>{
               console.log(result)
               if(!result){
                    resultTickets.forEach(ticketdoc=>{
                        usersRef.doc(userId).collection('ticket').doc(ticketdoc).update({
                        state: 'used',
                        },{merge:true}) 
                    })
                    res.status(200).send({ 'data':true});
                    return;
                }
                else {
                    res.status(200).send({ 'error':"Event already held."});
                    return;
                }
            })
        })
        .catch(error =>  {
            res.status(200).send({ 'error':"An error occurred."});
            return;
        });
    });
});


/**
Function to buyTicket the icket
Parameters: Id -> id of ticket
        userId ->
        numbersTickets->
Output: JSON with result value 
Teste:
//5QQ3bv9JkuiskIqn35x5
//4YMjcrIXgaZmIzNH8BDF
 
    curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/buyTickets --data ' {"signature":"asasasa", "data":{"performances":{"performance":{"id":"5QQ3bv9JkuiskIqn35x5","numberTickets":"1"}}, "userId":"340ab1d0-e731-11e8-b054-5d30f732ed63"}}' -g -H "Content-Type: application/json"

    curl -X POST http://localhost:5000/cmov-d52d6/us-central1/buyTickets --data ' {"signature":"asasasa", "data":{"performances":{"performance":{"id":"5QQ3bv9JkuiskIqn35x5","numberTickets":"1"}}, "userId":"340ab1d0-e731-11e8-b054-5d30f732ed63"}}' -g -H "Content-Type: application/json"

    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/buyTickets --data ' {"signature":"asasasa", "data":{"performances":{"performance":{"id":"5QQ3bv9JkuiskIqn35x5","numberTickets":"1"}}, "userId":"340ab1d0-e731-11e8-b054-5d30f732ed63"}}' -g -H "Content-Type: application/json"
*/
const buyTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const buffer = Buffer.from(req.rawBody)
        const dataBytes = buffer.slice(0, -64)
        const signatureBytes = buffer.slice(-64)
        const dataString = dataBytes.toString()
        const data = JSON.parse(dataString);
        const performances = data.performances
        const userId = data.userId

        var obj = {}

        if(!userId) {
            res.status(200).send({ 'error': "Please enter a user id."});
            return;
        }

        if(!performances) {
            res.status(200).send({ 'error': "Please enter performances."});
            return;
        }
        var listperformances=[]
        for(elem in performances){
            if(!performances[elem].id || !performances[elem].numberTickets ) {
                res.status(200).send({ 'error': "Please insert a ticket of the form {id, numberTickets}."});
                return;
            }
            var ticket = {
                id : performances[elem].id,
                numberTickets: Number(performances[elem].numberTickets)
            }
            listperformances.push(ticket)
        }

        var resultTickets = [];
        var vouchers = [];
        var listperformancesDefault= []
        var priceOfTickets=0;

        VerifySignature(userId, dataBytes,signatureBytes).then(result=>{
            if(!result){
                res.status(200).send({ 'error':"Signature error."});
                return;
            }
        
            listperformances.forEach(elem=>{
                admin.firestore().collection('ticket').doc(elem.id).get()
                .then(doc=>{
                    var ticket = {
                        id:elem.id,
                        date: doc.data().date,
                        price:doc.data().price,
                        name:doc.data().name,
                        numberTickets: elem.numberTickets,
                        numberOfFirstTicket: doc.data().sold,
                    }
                    priceOfTickets = priceOfTickets + doc.data().price* elem.numberTickets
                    listperformancesDefault.push(ticket)
                })
            })
            admin.firestore().collection('customer').doc(userId).collection('creditCard').get()
            .then(snapshot =>{
                if(snapshot.size != 1){
                    res.status(200).send({ 'error':"Invalid user id."});
                    return;
                }
                snapshot.forEach(creditCardDoc => { 
                    var valueSpentMod100 = creditCardDoc.data().valueSpentMod100
                    var amountSpend = valueSpentMod100 + priceOfTickets


                    var numberOfVouchers= Math.floor(amountSpend/100);
                    if( numberOfVouchers != 0 ) {
                        for (i=1; i<= numberOfVouchers; i++){
                            const idVoucher = uuidv1();
                            var voucher = {
                                id: idVoucher,
                                productCode : '5%discountCafeteria',
                                state:'not used' 
                            };
                            vouchers.push(voucher);
                            admin.firestore().collection('customer').doc(userId).collection('voucher').doc(idVoucher).set(voucher); 
                        }
                    }

                    admin.firestore().collection('customer').doc(userId).collection('creditCard').doc(creditCardDoc.id).update({
                        valueSpentMod100: amountSpend % 100, 
                    },{merge:true})

                    //adicionar vouchers
                    listperformancesDefault.forEach(ticketGeral=> {
                        admin.firestore().collection('ticket').doc(ticketGeral.id).update({
                            sold: ticketGeral.numberTickets + ticketGeral.numberOfFirstTicket
                        },{merge:true})
    
                        var place = ticketGeral.numberOfFirstTicket - 1;
                        
                        var max = Number(ticketGeral.numberTickets);
                        for(let j=0; j<max; j++){
                            place++
                            const idTicket = uuidv1();
                            const state = 'not used'

                            var ticket = {
                                id: idTicket ,
                                date: ticketGeral.date,
                                state: state,
                                name:ticketGeral.name,
                                place:place,
                                performanceId:ticketGeral.id
                            };
                            resultTickets.push(ticket);
                            
                            admin.firestore().collection('customer').doc(userId).collection('ticket').doc(idTicket).set(ticket)

                            const idVoucher = uuidv1();
                            const typeOfOfferBoolean = random.boolean();
                            var typeOfOffer;
                            if (typeOfOfferBoolean) {
                                typeOfOffer = 'freecoffee'
                            } else typeOfOffer = 'popcorn'

                            var voucher = {
                                id: idVoucher,
                                productCode : typeOfOffer,
                                state: state
                            };
                            vouchers.push(voucher);

                            admin.firestore().collection('customer').doc(userId).collection('voucher').doc(idVoucher).set(voucher);
                        }
                    })
                    var vou = "vouchers";
                    obj[vou] = vouchers;
        
                    var tickets = "tickets";
                    obj[tickets] = resultTickets;

                })
                res.status(200).send({'data':obj});
                return;
            })
            .catch(error =>{
                res.status(200).send({ 'error':error});
                return;
            });
        })
        .catch(error =>{
            res.status(200).send({ 'error':error});
            return;
        });
    });
});

/**
Function to 
Parameters:

Output: JSON with result value 
Teste:
    curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/listTickets --data '{}' -g -H "Content-Type: application/json"

    curl -X POST http://localhost:5000/cmov-d52d6/us-central1/listTickets  --data '{}' -g -H "Content-Type: application/json"

    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listTickets --data '{}' -g -H "Content-Type: application/json"
*/
const listTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        var result = []
        admin.firestore().collection('ticket').get()
        .then( snapshot => {
            snapshot.forEach(elemTicket => {
                var datenow =  Date.now();
                var myDate = new Date(elemTicket.data().date);
                if(datenow - myDate < 0) {
                    var ticket = {
                        price:elemTicket.data().price,
                        date: elemTicket.data().date,
                        name: elemTicket.data().name,
                        id: elemTicket.id,
                    }
                    result.push(ticket)
                }
            })
            res.status(200).send({ 'data':result});
            return; 
        })
        .catch(error =>{
        res.status(200).send({ 'error':error});
        return;
        });
    });
})


/*
curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/deletePerformances --data '{}' -g -H "Content-Type: application/json"

curl -X POST http://localhost:5000/cmov-d52d6/us-central1/deletePerformances --data '{}' -g -H "Content-Type: application/json"

*/
const  deletePerformances = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {


        admin.firestore().collection('ticket').get()
        .then(snapshot=>{
            snapshot.forEach(performance=>{
                admin.firestore().collection('ticket').doc(performance.id).delete();
            })
            res.status(200).send({ 'data':true});
            return;
        })
        .catch(error =>  {
            return "An error occurred."
        });
    })
})

/*
curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/addPerformances --data '{}' -g -H "Content-Type: application/json"

curl -X POST http://localhost:5000/cmov-d52d6/us-central1/addPerformances --data '{}' -g -H "Content-Type: application/json"
    
curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/addPerformances --data '{}' -g -H "Content-Type: application/json"

*/
const  addPerformances = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        for(var i=0; i<10; i++){
            var nameS = "evento"+ i;

        admin.firestore().collection('ticket').add({
            date: new Date("Wed Nov 14 2019 11:23:20 GMT+0000"),
            name:nameS,
            price:10 + i,
            sold:0
        }).then(()=>{
            if(i=9){
                res.status(200).send({ 'data':true});
                return;
            }
        })


        }
    })
})
/*
*
*    auxiliary functions
*
*
*/

function validTicketsAuxiliary(listTickets, userId){
    var printError="";
    var performanceIdGeral=""
    var resultTickets = [];
    var obj = {}
    var usersRef = admin.firestore().collection('customer');
    var promises = [];

    listTickets.forEach(ticket => {
        const p=usersRef.doc(userId).collection('ticket').doc(ticket).get()
        promises.push(p)
    })
    return Promise.all(promises)
    .then(snapshot => {
        if(snapshot.length==0){
            printError="User or tickets not found.";
            return;
        }
        snapshot.forEach(ticketdoc => {
            const state = ticketdoc.data().state;
            const performanceId = ticketdoc.data().performanceId;

            if(performanceIdGeral==""){
                performanceIdGeral = performanceId
            }
            else{
                if(performanceIdGeral!= performanceId){
                    printError = 'Tickets not of the same performance.';
                    return;
                }
            }
            if(state == "used") {
                printError = 'Already validated ticket.';
                return; 
            }
            resultTickets.push(ticketdoc.id)
        })

        if(printError !=""){
            return printError
        }
        else {
            obj["performanceIdGeral"] = performanceIdGeral;
            obj["resultTickets"] = resultTickets
            return obj;
        }
    })
    .catch(error =>  {
        return "An error occurred."
    });
}

function pastEvent(performanceId) {

    return admin.firestore().collection('ticket').doc(performanceId).get()
    .then(doc => {
        var datenow =  Date.now();
        var myDate = new Date(doc.data().date);
        if(datenow - myDate > 0) {
            return true;
        }
        else 
            return false;
    })
    .catch(error =>  {
        return "An error occurred."
    });
    
}

function VerifySignature(userId, dataBytes, signatureBytes){

    return admin.firestore().collection('customer').doc(userId).get()
    .then(doc => {
        console.log(doc.data())
        const modulus = doc.data().publicKey.modulus;
        const exponent = doc.data().publicKey.publicExponent;
        const pem = getPem(modulus, exponent);
        const verify = crypto.createVerify('RSA-SHA256')
        verify.update(dataBytes);
        return verify.verify(pem, signatureBytes);
    })
    .catch(error =>  {
        console.log(error)
        return false
    });
}

module.exports = {
    validTickets,
    buyTickets,
    listTickets,
    addPerformances,
    deletePerformances
}

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const crypto = require('crypto');
const UIDGenerator = require('uid-generator');
const cors = require('cors')({origin: true});
const uuidv1 = require('uuid/v1');
const random = require('random')
/**
Function to validateticket the icket
Parameters: ticketId -> 
        userId ->
Output: JSON with result value 
Teste:
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/validTickets --data ' { "tickets":{"ticketId1" : "fdd775a0-e"}, "userId":"c2345b70-e14e-11e8-b90b-6368751702e3"}' -g -H "Content-Type: application/json"

    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/validTickets --data ' { "tickets":{"ticketId1" : "fdd775a0-e2c9-11e8-8f21-b3cacf712523"}, "userId":"c2345b70-e14e-11e8-b90b-6368751702e3"}' -g -H "Content-Type: application/json"
*/
const validTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const tickets = req.body.tickets;
        const userId = req.body.userId;

        if(!tickets) {
            res.status(200).send({ 'error': "Please enter a tickets."});
            return;
        }
        if (tickets.length==0){
            res.status(200).send({ 'error': "Empty tickets array."});
            return;
        }

        if(!userId) {
            res.status(200).send({ 'error': "Please enter a userId."});
            return;
        }
        var listTickets = [];
        var resultTickets = []
        var performanceIdGeral="";
        var allValidated= true;

        for (ticket in tickets) {
            listTickets.push(tickets[ticket])
        }

        var promises = []
        var usersRef = admin.firestore().collection('customer');
        listTickets.forEach(ticket => {
            const p=usersRef.doc(userId).collection('ticket').doc(ticket).get()
            promises.push(p)
        })
        return Promise.all(promises)
        .then(snapshot => {
            if(snapshot.length==0){
                res.status(200).send({ 'error': "User or tickets not found."});
                return;
            }
            snapshot.forEach(ticketdoc => {
                const state = ticketdoc.data().state;
                const performanceId = ticketdoc.data().performanceId;
                if(performanceIdGeral==""){
                    performanceIdGeral= performanceId
                }
                else{
                    if(performanceIdGeral!= performanceId){
                    allValidated= false;
                        res.status(200).send({ 'error':'tickets not of the same performance'});
                        return;
                    }
                }
                if(state == "used") {
                    allValidated= false;

                    res.status(200).send({ 'error':'already validated ticket'});
                    return; 
                }
                console.log('adicinou')
                resultTickets.push(ticketdoc.id)
            })

            if(allValidated){
                console.log('aqui')
                resultTickets.forEach(ticketdoc=>{
                    usersRef.doc(userId).collection('ticket').doc(ticketdoc).update({
                    state: 'used',
                    },{merge:true}) 
                })
            }
            res.status(200).send({ 'data':true});
            return;
        })
        .catch(error =>  {
            console.error("Error: ", error);
            res.status(200).send({ 'error':"error"});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/buyTickets --data ' { "tickets":{"ticket":{"id":"5QQ3bv9JkuiskIqn35x5","numberTickets":"1"}}, "userId":"57900f70-e1d6-11e8-a855-57782ab5d15f"}' -g -H "Content-Type: application/json"
*/
const buyTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const tickets = req.body.tickets;
        const userId = req.body.userId;

        var obj = {}

        if(!userId) {
            res.status(200).send({ 'error': "Please enter a userId."});
            return;
        }

        if(!tickets) {
            res.status(200).send({ 'error': "Please enter a tickets."});
            return;
        }
        var listTickets=[]
        for(elem in tickets){
            if(!tickets[elem].id || !tickets[elem].numberTickets ) {
                res.status(200).send({ 'error': "Please insert a ticket of the form {id, numberTickets}."});
                return;
            }
            var ticket = {
                id : tickets[elem].id,
                numberTickets: Number(tickets[elem].numberTickets)
            }
            listTickets.push(ticket)
        }

        var resultTickets = [];
        var vouchers = [];
        var listTicketsDefault= []
        var priceOfTickets=0;

        listTickets.forEach(elem=>{
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
                listTicketsDefault.push(ticket)
            })
        })
        admin.firestore().collection('customer').doc(userId).collection('creditCard').get()
        .then(snapshot =>{
            if(snapshot.size != 1){
                res.status(200).send({ 'error':"Invalid userId"});
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
                listTicketsDefault.forEach(ticketGeral=> {
                    admin.firestore().collection('ticket').doc(ticketGeral.id).update({
                        sold: ticketGeral.numberTickets + ticketGeral.numberOfFirstTicket
                    },{merge:true})
   
                    var place = ticketGeral.numberOfFirstTicket - 1;
                    
                    var max = Number(ticketGeral.numberTickets);
                    for(let j=0; j<max; j++){
                        place++
                        const idTicket = uuidv1();
                        const state = 'not used'

                        console.log('aqui')
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
    });
});

/**
Function to 
Parameters:

Output: JSON with result value 
Teste:
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listTickets --data '{}' -g -H "Content-Type: application/json"
*/
const listTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        var result = []
        admin.firestore().collection('ticket').get()
        .then( snapshot => {
            snapshot.forEach(elemTicket => {
                var ticket = {
                    price:elemTicket.data().price,
                    date: elemTicket.data().date,
                    name: elemTicket.data().name,
                    name: elemTicket.data().name,

                    id: elemTicket.id,
                }
                result.push(ticket)
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

module.exports = {
    validTickets,
    buyTickets,
    listTickets,
}
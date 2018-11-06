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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/validTicket --data ' { "tickets":{"ticketId1" : "d007fcb0-dddf-11e8-83c6-09fd741136c7", "ticketId2": "26feb680-dde0-11e8-83c6-09fd741136c7"}, "userId":"9a9432a0-dddb-11e8-bb3a-112a346d95e2"}' -g -H "Content-Type: application/json"
*/
const validTicket = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const tickets = req.body.tickets;
        const userId = req.body.userId;

        if(!tickets) {
            res.status(200).send({ 'error': "Please enter a tickets."});
            return;
        }

        if(!userId) {
            res.status(200).send({ 'error': "Please enter a userId."});
            return;
        }
        var listTickets = [];
        var resultTickets = []

        for (ticket in tickets) {
            console.log(ticket, tickets[ticket])
            listTickets.push(tickets[ticket])
        }

        var promises = []
        var usersRef = admin.firestore().collection('customer');
        listTickets.forEach(ticket => {

            console.log(ticket)
            const p=usersRef.doc(userId).collection('ticket').doc(ticket).get()
            promises.push(p)
        })
        return Promise.all(promises)
        .then(snapshot => {
            snapshot.forEach(ticketdoc => {
                console.log(ticketdoc)
                console.log(ticketdoc.data())
                const state = ticketdoc.data().state;
                if(state == "not used") {
                    const ticketref = usersRef.doc(userId).collection('ticket').doc(ticketdoc.id);
                    ticketref.update({
                    state: 'used',
                    },{merge:true}) 
                    
                    var resultTicket = {
                        id:ticketdoc.id,
                        state:'validaded'
                    }
                    resultTickets.push(resultTicket);
                } 
                else if(state == "used") {
                    var resultTicket = {
                        id:ticketdoc.id,
                        state:'already been validaded'
                    }
                    resultTickets.push(resultTicket);
                }    
            })
            res.status(200).send({ 'data':resultTickets});
            return;
        })
        .catch(error =>  {
            console.error("Error: ", error);
            res.status(200).send({ 'error':'Invalid userId or Invalid ticketsId'});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/buyTickets --data ' { "tickets":{"ticket":{"id":"4YMjcrIXgaZmIzNH8BDF","numberTickets":"1"}}, "userId":"c2345b70-e14e-11e8-b90b-6368751702e3"}' -g -H "Content-Type: application/json"
*/
const buyTickets = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const tickets = req.body.tickets;
        const userId = req.body.userId;


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
                console.log('numero tickets', elem.numberTickets)
                console.log('firs',doc.data().sold )
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
            console.log(snapshot)
            if(snapshot.size != 1){
                res.status(200).send({ 'error':"Invalid userId"});
                return;
            }
            snapshot.forEach(creditCardDoc => { 
                console.log(creditCardDoc.data())
                var cardValue = creditCardDoc.data().value
                var valueSpentMod100 = creditCardDoc.data().valueSpentMod100
                var amountSpend = valueSpentMod100 + priceOfTickets
                console.log(priceOfTickets)
                console.log(amountSpend)
                console.log(amountSpend % 100)

                 if(priceOfTickets > cardValue){
                    res.status(200).send({ 'error':"insufficient funds"});
                    return;
                }

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
                    value: cardValue - priceOfTickets,
                    valueSpentMod100: amountSpend % 100, 
                },{merge:true})

                //adicionar vouchers
                listTicketsDefault.forEach(ticket=> {
                    admin.firestore().collection('ticket').doc(ticket.id).update({
                        sold: ticket.numberTickets + ticket.numberOfFirstTicket
                    },{merge:true})
   
                    var place = ticket.numberOfFirstTicket - 1;
                    
                    var max = Number(ticket.numberTickets);
                    for(let j=0; j<max; j++){
                        place++
                        const idTicket = uuidv1();
                        const state = 'not used'

                        var ticket = {
                            id: idTicket ,
                            date: ticket.date,
                            state: state,
                            name:ticket.name,
                            place:place

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
                var indexVouchers=1;
                result=" 'vouchers': ["
                vouchers.forEach( voucher =>{
                    result = result +"{'id':'" + voucher.id +  "'," + "'productCode': '" + voucher.productCode + "'}";
                    if(indexVouchers < vouchers.length) {
                        result = result + ',';
                        indexVouchers++
                    }else result = result + "],"
                })

                result = result + "'tickets': [";
                var indexTicket = 1;
                resultTickets.forEach( ticket =>{
                    result = result +"{'id':'" + ticket.id + "'," + "'place':'"+ticket.place + "'," + "'date': '" + ticket.date + "'}";
                    if(indexTicket < resultTickets.length) {
                        result = result + ',';
                        indexTicket++
                    }else result = result + "]"
                })


            })
            res.status(200).send({'data':result});
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
                console.log(elemTicket.data())
                var ticket = {
                    price:elemTicket.data().price,
                    date: elemTicket.data().date,
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
    validTicket,
    buyTickets,
    listTickets,
}
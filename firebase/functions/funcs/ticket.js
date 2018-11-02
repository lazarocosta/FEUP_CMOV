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
            res.status(400).send({ 'error': "Please enter a tickets."});
            return;
        }

        if(!userId) {
            res.status(400).send({ 'error': "Please enter a userId."});
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
            res.status(400).send({ 'error':'Invalid userId or Invalid ticketsId'});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/buyTicket --data ' { "id":"4YMjcrIXgaZmIzNH8BDF","numbersTickets":"1", "userId":"9a9432a0-dddb-11e8-bb3a-112a346d95e2"}' -g -H "Content-Type: application/json"
*/
const buyTicket = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const ticketId = req.body.id;
        const numbersTickets = req.body.numbersTickets;
        const userId = req.body.userId;

        if(!numbersTickets) {
            res.status(400).send({ 'error': "Please enter a number of tickets."});
            return;
        }

        if(!userId) {
            res.status(400).send({ 'error': "Please enter a userId."});
            return;
        }

        if(!ticketId) {
            res.status(400).send({ 'error': "Please enter a ticketId."});
            return;
        }

        var tickets = [];
        var vouchers = [];
        var ticketDefault;
        var result='a'

        admin.firestore().collection('ticket').doc(ticketId).get()
        .then(doc=>{
            promises= []
            ticketDefault = {
                date: doc.data().date,
                local:doc.data().local,
                price:doc.data().price,
                name:doc.data().name
            }
        })

        admin.firestore().collection('customer').doc(userId).collection('creditCard').get()
        .then(snapshot =>{
            console.log(snapshot)
            if(snapshot.size != 1){
                res.status(400).send({ 'error':"Invalid userId"});
                return;
            }
            snapshot.forEach(creditCardDoc => { 
                console.log(creditCardDoc.data())
                var cardValue = creditCardDoc.data().value
                var valueSpentMod100 = creditCardDoc.data().valueSpentMod100
                var priceOfTickets = ticketDefault.price * numbersTickets
                var valueSpend = valueSpentMod100 + priceOfTickets
                console.log(priceOfTickets)
                console.log(valueSpend)
                console.log(valueSpend % 100)

                 if(priceOfTickets > cardValue){
                    res.status(400).send({ 'error':"insufficient funds"});
                    return;
                }

                if( Math.floor(valueSpend/100) != 0 ) {
                    const idVoucher = uuidv1();
                    var voucher = {
                        id: idVoucher,
                        productCode : '5%discountCafeteria',
                        state:'not used' 
                    };
                    vouchers.push(voucher);

                    admin.firestore().collection('customer').doc(userId).collection('voucher').doc(idVoucher).set(voucher); 
                }

                admin.firestore().collection('customer').doc(userId).collection('creditCard').doc(creditCardDoc.id).update({
                    value: cardValue - priceOfTickets,
                    valueSpentMod100: valueSpend % 100, 
                },{merge:true})


                //adicionar vouchers
                for(i=1; i<=numbersTickets; i++){
                    const idTicket = uuidv1();
                    const state = 'not used'

                    var ticket = {
                        id: idTicket ,
                        date: ticketDefault.date,
                        state: state,
                        name:ticketDefault.name,
                        local:ticketDefault.local

                    };
                    tickets.push(ticket);
                    
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
                tickets.forEach( ticket =>{
                    result = result +"{'id':'" + ticket.id + "'," + "'date': '" + ticket.date + "'}";
                    if(indexTicket < tickets.length) {
                        result = result + ',';
                        indexTicket++
                    }else result = result + "]"
                })


            })
            res.status(200).send({'data':result});
            return;
        })
        .catch(error =>{
            res.status(400).send({ 'error':error});
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
                    local: elemTicket.data().local,
                    id: elemTicket.id 
                }
                result.push(ticket)
            })
            res.status(200).send({ 'data':result});
            return; 
        })
        .catch(error =>{
        res.status(400).send({ 'error':error});
        return;
        });
    });
})

module.exports = {
    validTicket,
    buyTicket,
    listTickets,
}
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/validTicket --data '{"data" : { "ticketId" : "239793a0-d9e3-11e8-bdc7-e3808b3e4670", "userId":"58e415e0-d792-11e8-b573-033213b03f30"}}' -g -H "Content-Type: application/json"
*/
const validTicket = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const ticketId = req.body.data.ticketId;
        const userId = req.body.data.userId;

        if(!ticketId) {
            res.status(400).send({ 'data': "Please enter a ticketId."});
            return;
        }

        if(!userId) {
            res.status(400).send({ 'data': "Please enter a userId."});
            return;
        }

        var usersRef = admin.firestore().collection('customer');
        usersRef.where('id', '==', userId).get()
        .then(snapshot => {

            if(snapshot.size != 1){
                res.status(400).send({ 'data':"Invalid userId"});
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
                            res.status(200).send({'data':'validated'});
                        } 
                        else if(state == "used") {
                            res.status(200).send({'data':'was already used'});
                        }    

                    });
                })
                .catch(error => {
                    res.status(400).send({ 'data':"Error"});
                    console.error("Error", error);
                });
            });
        }).catch(error =>  {
            res.status(400).send({ 'data':"Error"});
            console.error("Error: ", error);
        });
        return;
    });
});


/**
Function to validateticket the icket
Parameters: ticketId -> 
        userId ->
Output: JSON with result value 
Teste:
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/buyTicket --data '{"data" : { "date" : "22-12-12","numbersTickets":"1", "userId":"58e415e0-d792-11e8-b573-033213b03f30", "priceTicket":"10"}}' -g -H "Content-Type: application/json"
*/
const buyTicket = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const date = req.body.data.date;
        const numbersTickets = req.body.data.numbersTickets;
        const userId = req.body.data.userId;
        const priceTicket = req.body.data.priceTicket;


        if(!numbersTickets) {
            res.status(400).send({ 'data': "Please enter a number of tickets."});
            return;
        }

        if(!userId) {
            res.status(400).send({ 'data': "Please enter a userId."});
            return;
        }

        if(!date) {
            res.status(400).send({ 'data': "Please enter a date."});
            return;
        }

        if(!priceTicket) {
            res.status(400).send({ 'data': "Please enter a price of the ticket."});
            return;
        }
        var tickets = [];
        var vouchers = [];

        var usersRef = admin.firestore().collection('customer');
        usersRef.where('id', '==', userId).get()
        .then(snapshot => {

            if(snapshot.size != 1){
                res.status(400).send({ 'data':"Invalid userId"});
                return;
            }
            snapshot.forEach(userdoc => {
                var creditCardRef = usersRef.doc(userdoc.id).collection('creditCard')
                creditCardRef.get()
                .then(snapshot2 =>{
                    snapshot2.forEach(creditCardDoc => { 
                        var cardValue = creditCardDoc.data().value
                        var valueSpentMod100 = creditCardDoc.data().valueSpentMod100
                        var priceOfTickets = priceTicket * numbersTickets
                        var valueSpend = valueSpentMod100 + priceOfTickets
                
                        console.log(priceOfTickets)
                        console.log(valueSpend)
                        console.log(valueSpend % 100)

                        if(priceOfTickets > cardValue){
                            res.status(400).send({ 'data':"insufficient funds"});
                            return;
                        }

                        if( Math.floor(valueSpend/100) != 0 ) {
                            const idVoucher = uuidv1();
                            var voucher = {
                                id: idVoucher,
                                productCode : '5%discountCafeteria'
                            };
                            vouchers.push(voucher);

                            admin.firestore().collection('customer').doc(userdoc.id).collection('voucher').add({
                                id: idVoucher,
                                state: 'not used',
                                productCode: voucher.productCode
                            }) 
                        }

                        creditCardRef.doc(creditCardDoc.id).update({
                            value: cardValue - priceOfTickets,
                            valueSpentMod100: valueSpend % 100, 
                        },{merge:true})


                        /*adicionar vouchers*/
                        for(i=1; i<=numbersTickets; i++){
                            const idTicket = uuidv1();
                            const state = 'not used'

                            var ticket = {
                                id: idTicket ,
                                date: date
                            };
                           tickets.push(ticket);
                            
                            admin.firestore().collection('customer').doc(userdoc.id).collection('ticket').add({
                                id: idTicket ,
                                date: date,
                                state: state,
                            })


                            const idVoucher = uuidv1();
                            const typeOfOfferBoolean = random.boolean();
                            var typeOfOffer;
                            if (typeOfOfferBoolean) {
                                typeOfOffer = 'free coffe'
                            } else typeOfOffer = 'popcorn'

                            var voucher = {
                                id: idVoucher,
                                productCode : typeOfOffer,
                            };
                            vouchers.push(voucher);

                            admin.firestore().collection('customer').doc(userdoc.id).collection('voucher').add({
                                id: idVoucher,
                                state: state,
                                productCode: typeOfOffer
                            })

                        }
                        var result="{ 'vouchersNumber': '" + vouchers.length + "',";
                        vouchers.forEach( voucher =>{
                            result = result +"'voucher': {  'id':'" + voucher.id +  "'," + "'productCode': '" + voucher.productCode + "'},";

                        })

                        result = result + "'ticketsNumber': '" + tickets.length + "',";
                        var indexTicket=1;
                        tickets.forEach( ticket =>{
                            result = result +"'ticket': {" + "'id':'" + ticket.id + "'," + "'date': '" + ticket.date + "'}";
                            if(indexTicket < tickets.length) {
                                result = result + ',';
                                indexTicket++
                            }
                        })
                        res.status(200).send({'data':result});
                        return;

                    })
                }).catch(error =>{
                    res.status(400).send({ 'data':"Error"});
                    console.error("Error: ", error);
                });
            })

        }).catch(error =>{
            res.status(400).send({ 'data':"Error"});
            console.error("Error: ", error);
        });
        return;
    });
});

module.exports = {
    validTicket,
    buyTicket
}
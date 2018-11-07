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
Output: JSON with result value 
Teste:
     curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/register --data ' { "publicKey" : "000030002300002", "name":"TESTE", "nif":"12121212", "creditCardType":"asas", "creditCardNumber":"11", "creditCardValidity": "October 13, 2014 11:13:00" }' -g -H "Content-Type: application/json"
*/

const register = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const publicKey = req.body.publicKey;
        const name = req.body.name;
        const nif = req.body.nif;
        const creditCardType = req.body.creditCardType;
        const creditCardNumber = req.body.creditCardNumber;
        const creditCardValidity = req.body.creditCardValidity;


        if(!publicKey) {
            res.status(200).send({ 'error': "Please enter a publicKey."});
            return;
        }

        if(!name) {
            res.status(200).send({ 'error': "Please enter a name."});
            return;
        }

        if(!nif) {
            res.status(200).send({ 'error': "Please enter a nif."});
            return;
        }

        if(!creditCardType) {
            res.status(200).send({ 'error': "Please enter a creditCardType."});
            return;
        }

        if(!creditCardNumber) {
            res.status(200).send({ 'error': "Please enter a creditCardNumber."});
            return;
        }

        if(!creditCardValidity) {
            res.status(200).send({ 'error': "Please enter a creditCardValidity."});
            return;
        }

        const id = uuidv1();
        const nifValid = parseInt(nif);
        if(isNaN(nifValid)){
            res.status(200).send({ 'error':"nif not is a number"});
            console.error("nif not is a number: ", error);
            return;
        }

        var user = {
            publicKey: publicKey,
            name: name,
            nif: nifValid,
            id:id,
        };
        var adduser;
        admin.firestore().collection('customer').doc(id).set(user)
        .then(function() {

            const number = parseInt(creditCardNumber);
            var myDate = new Date(creditCardValidity);
            promises = []

            if(isNaN(number)|| isNaN(myDate.getMonth())) {
                adduser= false;
                var p = admin.firestore().collection('customer').doc(id).delete()
                promises.push(p)
                res.status(200).send({ 'error':"creditCardNumber not is number or the validity is not a date"});
                console.error("creditCardNumber not is number or the validity is not a date: ");
                return;
            }else {
                const p =  admin.firestore().collection('customer').doc(id).collection('creditCard').add({
                        type: creditCardType,
                        number: number,
                        validity: myDate,
                        valueSpentMod100: 0,

                        })
                    promises.push(p);
                    adduser= true;
            }
            return Promise.all(promises)
        })
        .then(snapshot => {
            if(adduser) {
                res.status(200).send({ 'data':id});
                return 
            }else {
                res.status(200).send({ 'error':'Could not create user!'});
                return
            }
        })
        .catch(err => {
            res.status(200).send({ 'error':"Could not create user!"});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/login --data ' { "name" : "manuel", "password":"TESTE"}' -g -H "Content-Type: application/json"
*/

const login = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        const name = req.body.name;
        const password = req.body.password;

        if(!name){
            res.status(200).send({ 'error':"Please enter a name."});
            return;
        }

        if(!password){
            res.status(200).send({ 'error':"Please enter a password."});
            return;
        }

        var usersRef = admin.firestore().collection('customer');

        usersRef.where('name', '==', name).get()
            .then(snapshot => {
                if(snapshot.size > 1){
                    res.status(200).send({ 'error':"Invalid name|password"});
                    return;
                }
                if(snapshot.size < 1){
                    res.status(200).send({ 'error':"Invalid name|password"});
                    return;
                }
                snapshot.forEach(doc => {

                    var userPass = doc.data().password;

                    var hash = crypto.createHash('sha256').update(password).digest('base64');

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

                    } else {
                        res.status(200).send({ 'error':"Invalid name/password."});
                    }
                });
                return;
            })
            .catch(err => {
                console.log(err);
                res.status(200).send({ 'error':"Invalid name|password."});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/payOrder --data ' { "userId":"57900f70-e1d6-11e8-a855-57782ab5d15f","products": {"product1":{"docProduct":"wxR6vHBwaYqXVPmPvJBk", "quantity":"2" }} }' -g -H "Content-Type: application/json"
*/
const payOrder = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const userId = req.body.userId;
        const vouchers = req.body.vouchers;
        const products = req.body.products;

        if(!userId){
            res.status(200).send({ 'error':"Please enter a userId."});
            return;
        }

        if(!products){
            res.status(200).send({ 'error':"Please enter a products."});
        }
        if(vouchers){
            if(vouchers.length >2){
                res.status(200).send({ 'error':"It is not allowed to enter more than 2 voucher."});
            }
        }

        var discont=0;
        var freecoffee = 0;
        var popcorn = 0;
        var valueCoffe=0;
        var valuepopCorn =0;
        var lisproducts = [];
        var priceProducts= 0;
        var numberOfCoffee = 0;
        var numberOfPopcorn = 0;
        var voucherUsed = [];
        var listVoucher = [];
        var creditCardUser;
        var productsPurchased = []
        var obj = {}
        var usersRef= admin.firestore().collection('customer');


        for(key in vouchers){
            listVoucher.push(vouchers[key])
        }

        for(key in products){
            var product = {
                docProduct:products[key].docProduct,
                quantity: products[key].quantity
            }
            lisproducts.push(product)
        }

        var index1=0
        lisproducts.forEach(product =>{
            index1++

            var docProduct =  product.docProduct;
            var quantity = product.quantity;
            if(!docProduct || !quantity) {
                res.status(200).send({ 'error':"Please enter the doc and quantity of product1"});
                return;  
            }
       
            admin.firestore().collection('product').doc(docProduct).get()
            .then(doc=> {
                var nameProduct = doc.data().name;
                var priceProduct = doc.data().price;
                priceProducts = priceProducts + priceProduct * quantity;

                var purchase = {
                    nameProduct:nameProduct,
                    priceProduct:priceProduct,
                    quantity:quantity
                }
                productsPurchased.push(purchase);

                if(nameProduct == 'freecoffee') {
                    numberOfCoffee++
                    valueCoffe = priceProduct;
                }

                if(nameProduct == 'popcorn') {
                    numberOfPopcorn++;
                    valuepopCorn = priceProduct;
                }

                if(index1==lisproducts.length){
                    const promises = []
                    listVoucher.forEach(voucher => {
                        const p = usersRef.doc(userId).collection('voucher').doc(voucher).get()
                        promises.push(p)
                    })
                    return Promise.all(promises)
                }
            })
            .then(snapshot => {
                var index2=0
                const promises = []

                snapshot.forEach( doc => {
                    index2++
                    var used = false;
                    if(doc.data().state == "not used") {
                        if(doc.data().productCode =="5%discountCafeteria") {
                            if(discont !=0.05){
                                discont = 0.05;
                                used = true;
                            }
                        }else if(doc.data().productCode =="freecoffee") {
                            if(numberOfCoffee > freecoffee ){
                                freecoffee++
                                used = true;
                            }
                        }else if(doc.data().productCode =="popcorn") {
                            if( numberOfPopcorn > popcorn){ 
                                popcorn++;
                                used = true;
                            }
                        }
    
                        if (used){
                            voucherUsed.push(doc.id)
                        }
                    } else {
                        console.log('used')
                    }
                    if(index2==snapshot.length){
                        return Promise.all(promises)
                    }
                })
            })
            .then(snapshot => {
                usersRef.doc(userId).collection('creditCard').get()
                .then(snapshot => {
                    if(snapshot.size != 1){
                        res.status(200).send({ 'error':"Credit card not found"});
                        return;
                    }
                    const promises = []
                    
                    snapshot.forEach(creditCard =>{
                        creditCardUser = creditCard.id

                        const p = usersRef.doc(userId).collection('creditCard').doc(creditCardUser).get()
                        promises.push(p);
                    })
                    return Promise.all(promises);

                })
                .then(snapshot => { 
                    var valueSpent;
                    snapshot.forEach(docreditcard => { 
                        var valueSpentMod100 = docreditcard.data().valueSpentMod100;
                        var voucher = (valueSpentMod100 + priceProducts)/100;
                        valueSpent =priceProducts*(1-discont) - valueCoffe*freecoffee - valuepopCorn*popcorn;
        
   
                            if(valueSpent > 0 ){
                                usersRef.doc(userId).collection('creditCard').doc(creditCardUser).update({
                                    valueSpentMod100: (valueSpentMod100 + valueSpent)%100,
                                },{merge:true})
                            }

                            if(voucher>=1) {
                                const idVoucher = uuidv1();
                                var newVoucher = {
                                    id: idVoucher,
                                    state: 'not used',
                                    productCode: '5%discountCafeteria'
                                }

                                admin.firestore().collection('customer').doc(userId).collection('voucher').doc(idVoucher).set(newVoucher); 
                            }
                    })

                    voucherUsed.forEach(voucherId => {
                        usersRef.doc(userId).collection('voucher').doc(voucherId).update({
                            state: 'used',
                        },{merge:true})
                    }) 

                    var valueSpendkey = "valueSpend";
                    obj[valueSpendkey] = valueSpent;

                    var vouvherKey = 'vouchers'
                    obj[vouvherKey] = voucherUsed;
                    addProductUser(userId, productsPurchased)
                    
                    getNumberOrder().then(number=>{
                        if(number!=null){
                            var numberKey = "number"; 
                            obj[numberKey] = number;

                            res.status(200).send({ 'data':obj});
                            return;
                        }else {
                            res.status(200).send({ 'error':'error'});
                            return;
                        }
                    })
                    .catch(err => {
                        console.log(err);
                        res.status(200).send({ 'error':"Error"});
                        return;
                    });

                })
                .catch(err => {
                    console.log(err);
                    res.status(200).send({ 'error':"Error"});
                    return;
                });
            })
            .catch(err => {
                console.log(err);
                res.status(200).send({ 'error':"Not found product"});
                return;
            });
        })
    })
})

/**
Function to list Tickets Of User
Parameters: username -> the USER name
Output: JSON with 
TEST: 
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listTicketsNotUsed --data ' {"userId":"c2345b70-e14e-11e8-b90b-6368751702e3" }' -g -H "Content-Type: application/json"
*/

const listTicketsNotUsed = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const userId = req.body.userId;
        var result = []

        admin.firestore().collection('customer').doc(userId).collection('ticket').get()
        .then(snapshot => {
            snapshot.forEach(ticketdoc => {
                if(ticketdoc.data().state =="not used") {
                    var ticket = {
                        id: ticketdoc.id,
                        name: ticketdoc.data().name,
                        date: ticketdoc.data().date,
                        place: ticketdoc.data().place,
                    }
                    result.push(ticket);
                }   
            })
            res.status(200).send({ 'data':result});
            return;
        })
        .catch (error=> {
            console.log(error);
            res.status(200).send({ 'error':"Error"});
            return;
        })
    })
})

  
/**
Function to list Tickets Of User
Parameters: username -> the USER name
Output: JSON with 
TEST: 
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listTransactionsUser --data ' {"userId":"c2345b70-e14e-11e8-b90b-6368751702e3" }' -g -H "Content-Type: application/json"
*/

const listTransactionsUser = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const userId = req.body.userId;
        var ticketsResult = [];
        var vouchersResult = [];
        var productsResult = [];
        var obj = {};

        admin.firestore().collection('customer').doc(userId).collection('ticket').get()
        .then(snapshot => {
            snapshot.forEach(ticketdoc => {
                if(ticketdoc.data().state =="not used") {
                    var ticket = {
                        id: ticketdoc.id,
                        name: ticketdoc.data().name,
                        date: ticketdoc.data().date,
                        place: ticketdoc.data().place,
                    }
                    ticketsResult.push(ticket);
                }   
            })
            admin.firestore().collection('customer').doc(userId).collection('voucher').get()
            .then(snapshot => {
                snapshot.forEach(voucherdoc => {
                    if(voucherdoc.data().state =="not used") {
                        var voucher = {
                            id: voucherdoc.id,
                            productCode: voucherdoc.data().productCode,
                        }
                        vouchersResult.push(voucher);
                    }   
                })
                admin.firestore().collection('customer').doc(userId).collection('productsPurchased').get()
                .then(snapshot => {
                    snapshot.forEach(productdoc => {
                        var product = {
                            nameProduct: productdoc.data().nameProduct,
                            priceProduct: productdoc.data().priceProduct,
                            quantity: productdoc.data().quantity,

                        }
                        productsResult.push(product);
                    })
                    var vouchers = "vouchers";
                    obj[vouchers] = vouchersResult;
          
                    var tickets = "tickets";
                    obj[tickets] = ticketsResult;

                    var products = "products";
                    obj[products] = productsResult;


                    res.status(200).send({ 'data':obj});
                    return;
                })
            })
        })
        .catch (error=> {
            console.log(error);
            res.status(200).send({ 'error':"Error"});
            return;
        })
    })
})


/**
Function to list vouchers Of User
Parameters: 
        userId  
Output: JSON with 
TEST: 
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listVouchersUser --data ' {"userId":"9a9432a0-dddb-11e8-bb3a-112a346d95e2" }' -g -H "Content-Type: application/json"
*/
const listVouchersUser = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const userId = req.body.userId;
        var vouchersResult = [];

        admin.firestore().collection('customer').doc(userId).collection('voucher').get()
        .then(snapshot => {
            snapshot.forEach(voucherdoc => {
                if(voucherdoc.data().state =="not used") {
                    var voucher = {
                        id: voucherdoc.id,
                        productCode: voucherdoc.data().productCode,
                    }
                    vouchersResult.push(voucher);
                }   
            })

            res.status(200).send({ 'data':vouchersResult});
            return;
        })
        .catch (error=> {
            console.log(error);
            res.status(200).send({ 'error':error});
            return;
        })
    })
})



/*
*
*    auxiliary functions
*
*
*/
function getNumberOrder() {
    return admin.firestore().collection('order').doc('9WV3XMOLfQ6qrn4hPHll').get()
    .then(result=>{
        var number= result.data().number+1
        admin.firestore().collection('order').doc('9WV3XMOLfQ6qrn4hPHll').update({
            number:number
        },{merge:true})
        number = "0000" + number
        number = number.slice(-4);
        return number;
    })
    .catch(error=>{
        return null;
    })
}

function addProductUser(userId, lisproducts){
    lisproducts.forEach(product=>{
        admin.firestore().collection('customer').doc(userId).collection('productsPurchased').add({
            nameProduct:product.nameProduct,
            priceProduct:product.priceProduct,
            quantity:product.quantity
        })
    })
}

module.exports={
    register,
    login,
    payOrder,
    listTicketsNotUsed,
    listTransactionsUser,
    listVouchersUser
}

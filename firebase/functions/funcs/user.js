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
            res.status(400).send({ 'data': "Please enter a publicKey."});
            return;
        }

        if(!name) {
            res.status(400).send({ 'data': "Please enter a name."});
            return;
        }

        if(!nif) {
            res.status(400).send({ 'data': "Please enter a nif."});
            return;
        }

        if(!creditCardType) {
            res.status(400).send({ 'data': "Please enter a creditCardType."});
            return;
        }

        if(!creditCardNumber) {
            res.status(400).send({ 'data': "Please enter a creditCardNumber."});
            return;
        }

        if(!creditCardValidity) {
            res.status(400).send({ 'data': "Please enter a creditCardValidity."});
            return;
        }

        const id = uuidv1();
        const nifValid = parseInt(nif);
        if(isNaN(nifValid)){
            res.status(400).send({ 'data':"nif not is number"});
            console.error("nif not is number: ", error);
            return;
        }

        admin.firestore().collection('customer').add({
            publicKey: publicKey,
            name: name,
            nif: nifValid,
            id:id,
            })
            .then(function(docRef) {
                const number = parseInt(creditCardNumber);
                const date = Date.parse(creditCardValidity);

                if(isNaN(number)) {
                    admin.firestore().collection('customer').doc(docRef.id).delete();
                    res.status(400).send({ 'data':"creditCardNumber not is number"});
                    console.error("creditCardNumber not is number: ", error);
                    return;
                }

                admin.firestore().collection('customer').doc(docRef.id).collection('creditCard').add({
                    type: creditCardType,
                    number: number,
                    validity: date,
                    value: 100,
                    amountSpent: 0,

                })
                .catch(function(error) {
                    res.status(400).send({ 'data':"Error adding creditCard of the user"});
                    console.error("Error adding creditCard of the user ", error);
                })
            .catch(function(error) {
                res.status(400).send({ 'data':"Error adding customer"});
                console.error("Error adding user", error);
            });
        res.status(200).send({'data':id});
        return;
        }).catch(err => {
            console.log(err);
            res.status(400).send({ 'data':"Could not create user!"});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/login --data '{"data" : { "name" : "manuel", "password":"TESTE"}}' -g -H "Content-Type: application/json"
*/

const login = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        console.log(0)


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

                    } else {
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


/**
Function to login a participant
Parameters: username -> the USER name
	    password ->the password of the user
Output: JSON with data value that could be "Please enter a username|password", "invalid username|password", or the token of the session
TEST: 
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/payOrder --data '{"data" : { "userId":"58e415e0-d792-11e8-b573-033213b03f30","voucher1":"hMZOQoarX5vUyhAHugla","voucher2":"w81YmNZ9NBAXkq1864UJ","product1":{"docProduct":"26QU3Rxbt3OdOyO8UP4X", "quantity":"1" } }}' -g -H "Content-Type: application/json"
*/
const payOrder = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const userId = req.body.data.userId;
        const voucher1 = req.body.data.voucher1;
        const voucher2 = req.body.data.voucher2;
        const product1 = req.body.data.product1;
        const product2 = req.body.data.product2;

        var docProduct1;
        var quantity1;
        var docProduct2;
        var quantity2;

        let listVoucher = []

        if(!userId){
            res.status(400).send({ 'data':"Please enter a userId."});
            return;
        }

        if(voucher1){
            listVoucher.push(voucher1);
        }

        if(voucher2){
            listVoucher.push(voucher2);
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
        var insuficiente = false;
        var userdocId;
        var voucherUsed = []

        if(product1) {
            docProduct1 = req.body.data.product1.docProduct;
            quantity1 = req.body.data.product1.quantity;
            if(!docProduct1 || !quantity1) {
                res.status(400).send({ 'data':"Please enter the doc and quantity of product1"});
                return;  
            }
       
            admin.firestore().collection('product').doc(docProduct1).get()
            .then(doc=> {
                var nameProduct = doc.data().name;
                var priceProduct = doc.data().price;
                priceProducts = priceProducts + priceProduct * quantity1;

                var product = {
                    name: nameProduct,
                    price: priceProduct,
                    quantity: quantity1
                }

                if(nameProduct == 'freecoffee') {
                    numberOfCoffee++
                    valueCoffe = priceProduct;
                }

                if(nameProduct == 'popcorn') {
                    numberOfPopcorn++;
                    valuepopCorn = priceProduct;
                }

                lisproducts.push(product);

            }) 
            .catch(err => {
                console.log(err);
                res.status(400).send({ 'data':"Not found product"});
                return;
            }); 
        }

        if(product2) {
            docProduct2 = req.body.data.product2.docProduct;
            quantity1 = req.body.data.product2.quantity;
            if(!docProduct2 || !quantity2) {
                res.status(400).send({ 'data':"Please enter the doc and quantity of product1"});
                return;  
            }
       
            admin.firestore().collection('product').doc(docProduct2).get()
            .then(doc=> {
                var nameProduct = doc.data().name;
                var priceProduct = doc.data().price;
                priceProducts = priceProducts + priceProduct * quantity2;

                var product = {
                    name: nameProduct,
                    price: priceProduct,
                    quantity: quantity2
                }

                if(nameProduct == 'freecoffee') {
                    numberOfCoffee++
                    valueCoffe = priceProduct;
                }

                if(nameProduct == 'popcorn') {
                    numberOfPopcorn++;
                    valuepopCorn = priceProduct;
                }

                lisproducts.push(product);

            }) 
            .catch(err => {
                console.log(err);
                res.status(400).send({ 'data':"Not found product"});
                return;
            }); 
        }
        
        var usersRef = admin.firestore().collection('customer');
        usersRef.where('id', '==', userId).get()
        .then(snapshot => {
            const promisses = []
            if(snapshot.size != 1){
                res.status(400).send({ 'data':"Invalid userId"});
                return;
            }
            snapshot.forEach( userdoc => { 
                console.log('userdoc', userdoc)
                const p = usersRef.doc(userdoc.id).get()
                promisses.push(p)
            }) 
            return Promise.all(promisses)
        })
        .then(snapshot => {
            if(snapshot.length != 1){
                res.status(400).send({ 'data':"error customer"});
                return;
            }
            const promisses = []
            snapshot.forEach( userdoc => {
                console.log('user', userdoc.id)
                userdocId = userdoc.id
            })

            listVoucher.forEach(voucher => {
                console.log(voucher)
                const p = usersRef.doc(userdocId).collection('voucher').doc(voucher).get()
                promisses.push(p)
           })
           return Promise.all(promisses)
        })
        .then(snapshot => {
            console.log(snapshot)
            const promisses = []
            snapshot.forEach( doc => {
                console.log('voucher', doc.id)
                const p = usersRef.doc(userdocId).collection('voucher').doc(doc.id).get()
                promisses.push(p)
            }) 
            return Promise.all(promisses)
        })
        .then(snapshot => {
            snapshot.forEach( doc => {
                var used = false;
                promisses = []
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
                        if( numberOfPopcorn > numberOfPopcorn){ 
                            popcorn++;
                            used = true;
                        }
                    }

                    if (used){
                        console.log('usou')
                        voucherUsed.push(doc.id)
                    }
                } else {
                    console.log('used')
                }
            })
            usersRef.doc(userdocId).collection('creditCard').get()
            .then(snapshot => { 
                console.log(snapshot)

                if(snapshot.size != 1){
                    res.status(400).send({ 'data':"Credit card not found"});
                    return;
                }
    
                snapshot.forEach(docreditcard => { 
                    var valueCreditCard = docreditcard.data().value;
                    var valueSpentMod100 = docreditcard.data().valueSpentMod100;
                    var voucher = (valueSpentMod100 + priceProducts)/100;
                    var valueSpent =priceProducts*(1-discont) - valueCoffe*freecoffee - valuepopCorn*popcorn;
    
                    if(valueCreditCard < valueSpent) {
                        insuficiente = true;  
                    } else {
                        if(valueSpent > 0 ){
                            usersRef.doc(userdocId).collection('creditCard').doc(docreditcard.id).update({
                                value: valueCreditCard - valueSpent,
                                valueSpentMod100: (valueSpentMod100 + valueSpent)%100,
                            },{merge:true})
                        }
    
                        if(voucher>=1) {
                            const idVoucher = uuidv1();
    
                            admin.firestore().collection('customer').doc(userdoc.id).collection('voucher').add({
                                id: idVoucher,
                                state: 'not used',
                                productCode: '5%discountCafeteria'
                            })      
                        }
                        insuficiente= false;
                    }
                })
                if(insuficiente) {
                    res.status(400).send({ 'data':"insufficient funds"});
                    return;
                } else {
                    voucherUsed.forEach(voucherId => {
                        usersRef.doc(userdocId).collection('voucher').doc(voucherId).update({
                            state: 'used',
                        },{merge:true})
                    })
                    res.status(200).send({ 'data':"bem"});
                }
    
            })
            .catch(err => {
                console.log(err);
                res.status(400).send({ 'data':"Error"});
                return;
            });
        })
        .catch(err => {
            console.log(err);
            res.status(400).send({ 'data':"Error"});
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/teste --data '{"data" : { }}' -g -H "Content-Type: application/json"
*/

const teste = functions.https.onRequest((req, res) => {
    // return  cors(req, res, () => {
        var userId = '58e415e0-d792-11e8-b573-033213b03f30'
        admin.firestore().collection('customer').where('id', '==', userId).get()
        .then(snapshot => {
            const promisse= []
            if(snapshot.size != 1){
                res.status(400).send({ 'data':"Invalid userId"});
                return;
            }
            snapshot.forEach( userdoc => { 
                console.log(userdoc.id)
                const p = admin.firestore().collection('customer').doc(userdoc.id).get()
                promisse.push(p)
            }) 
            return Promise.all(promisse)
        })
        .then (result => {
            console.log(result)
            res.status(200).send({ 'data':"bem"});

        })
        .catch (error=> {
            console.log(error);
            res.status(400).send({ 'data':"Error"});
        })

})



module.exports={
    register,
    login,
    payOrder,
    teste,
}

/*




                    snapshot.forEach(docreditcard => { 
                        console.log('chegou...')

                        var valueCreditCard = docreditcard.data().value;
                        var valueSpentMod100 = docreditcard.data().valueSpentMod100;
                        var voucher = (valueSpentMod100 + priceProducts)/100;

                        var valueSpent =priceProducts*(1-discont) - valueCoffe*freecoffee - valuepopCorn*popcorn;

                        if(valueCreditCard < priceProducts) {
                            insuficiente = true;
                            res.status(400).send({ 'data':"insufficient funds"});
                            return;  
                        } else {

                            creditCard.doc(docreditcard.id).update({
                                value: valueCreditCard - valueSpent,
                                valueSpentMod100: (valueSpentMod100 + priceProducts)%100,
                                },{merge:true})

                            if(voucher>=1) {
                                const idVoucher = uuidv1();
    
                                admin.firestore().collection('customer').doc(userdoc.id).collection('voucher').add({
                                    id: idVoucher,
                                    state: 'not used',
                                    productCode: '5%discountCafeteria'
                                })      
                            }
                            res.status(200).send({ 'data':"bem"});
                            return;  

                        }
                    })
                })
            });
        })
*/
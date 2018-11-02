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
            res.status(400).send({ 'error': "Please enter a publicKey."});
            return;
        }

        if(!name) {
            res.status(400).send({ 'error': "Please enter a name."});
            return;
        }

        if(!nif) {
            res.status(400).send({ 'error': "Please enter a nif."});
            return;
        }

        if(!creditCardType) {
            res.status(400).send({ 'error': "Please enter a creditCardType."});
            return;
        }

        if(!creditCardNumber) {
            res.status(400).send({ 'error': "Please enter a creditCardNumber."});
            return;
        }

        if(!creditCardValidity) {
            res.status(400).send({ 'error': "Please enter a creditCardValidity."});
            return;
        }

        const id = uuidv1();
        const nifValid = parseInt(nif);
        if(isNaN(nifValid)){
            res.status(400).send({ 'error':"nif not is a number"});
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
            console.log(id)

            const number = parseInt(creditCardNumber);
            var myDate = new Date(creditCardValidity);
            promises = []

            if(isNaN(number)|| isNaN(myDate.getMonth())) {
                adduser= false;
                var p = admin.firestore().collection('customer').doc(id).delete()
                promises.push(p)
                res.status(400).send({ 'error':"creditCardNumber not is number or the validity is not a date"});
                console.error("creditCardNumber not is number or the validity is not a date: ");
                return;
            }else {
                const p =  admin.firestore().collection('customer').doc(id).collection('creditCard').add({
                        type: creditCardType,
                        number: number,
                        validity: myDate,
                        value: 100,
                        valueSpentMod100: 0,

                        })
                    promises.push(p);
                    adduser= true;
            }
            return Promise.all(promises)
        })
        .then(snapshot => {
            console.log(snapshot)
            if(adduser) {
                res.status(200).send({ 'data':id});
                return 
            }else {
                res.status(400).send({ 'error':'Could not create user!'});
                return
            }
        })
        .catch(err => {
            console.log(err);
            res.status(400).send({ 'error':"Could not create user!"});
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
        console.log(0)


        const name = req.body.name;
        const password = req.body.password;

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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/payOrder --data ' { "userId":"9a9432a0-dddb-11e8-bb3a-112a346d95e2","vouchers":{"voucher1":"26feb681-dde0-11e8-83c6-09fd741136c7","voucher2":"ba9fe020-dde1-11e8-9205-4f7286452f10"},"products": {"product1":{"docProduct":"26QU3Rxbt3OdOyO8UP4X", "quantity":"1" }} }' -g -H "Content-Type: application/json"
*/
const payOrder = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const userId = req.body.userId;
        const vouchers = req.body.vouchers;
        const products = req.body.products;

        let listVoucher = []

        if(!userId){
            res.status(400).send({ 'data':"Please enter a userId."});
            return;
        }

        if(!products){
            res.status(400).send({ 'data':"Please enter a products."});
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
        var voucherUsed = []

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

        lisproducts.forEach(product =>{
            var docProduct =  product.docProduct;
            var quantity = product.quantity;
            if(!docProduct || !quantity) {
                res.status(400).send({ 'error':"Please enter the doc and quantity of product1"});
                return;  
            }
       
            admin.firestore().collection('product').doc(docProduct).get()
            .then(doc=> {
                var nameProduct = doc.data().name;
                var priceProduct = doc.data().price;
                priceProducts = priceProducts + priceProduct * quantity;

                var product = {
                    name: nameProduct,
                    price: priceProduct,
                    quantity: quantity
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
        })

        console.log(lisproducts)
        const promises = []
        var usersRef = admin.firestore().collection('customer');

        listVoucher.forEach(voucher => {
            console.log(voucher)
            const p = usersRef.doc(userId).collection('voucher').doc(voucher).get()
            promises.push(p)
        })
        return Promise.all(promises)
        .then(snapshot => {
            console.log(snapshot)
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
            usersRef.doc(userId).collection('creditCard').get()
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
                            usersRef.doc(userId).collection('creditCard').doc(docreditcard.id).update({
                                value: valueCreditCard - valueSpent,
                                valueSpentMod100: (valueSpentMod100 + valueSpent)%100,
                            },{merge:true})
                        }
    
                        if(voucher>=1) {
                            const idVoucher = uuidv1();
    
                            admin.firestore().collection('customer').doc(userId).collection('voucher').add({
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
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/teste --data ' { }' -g -H "Content-Type: application/json"
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

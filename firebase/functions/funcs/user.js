const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cors = require('cors')({origin: true});
const uuidv1 = require('uuid/v1');
const crypto = require('crypto');



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
     curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/register --data ' { "publicKey" : "000030002300002", "name":"TESTE", "nif":"12121212", "creditCardType":"asas", "creditCardNumber":"11", "creditCardValidity": "2019-12-12" }' -g -H "Content-Type: application/json"
*/

const register = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        body = JSON.parse(req.body.toString())
        const publicKey = body.publicKey;
        const name = body.name;
        const nif = body.nif;
        const creditCardType = body.creditCardType;
        const creditCardNumber = body.creditCardNumber;
        const creditCardValidity = body.creditCardValidity;
        console.log("publicKey", publicKey);

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
            res.status(200).send({ 'error':"Nif not is a number"});
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
        var errorOccurred= "Could not create user!"
        admin.firestore().collection('customer').doc(id).set(user)
        .then(function() {

            const number = parseInt(creditCardNumber);
            promises = [];
            var datenow =  Date.now();
            var dateArray = creditCardValidity.split("-");
            var myDate = new Date(dateArray[0], dateArray[1],dateArray[2]);

            if(isNaN(number)|| isNaN(myDate.getMonth())) {
                adduser= false;
                admin.firestore().collection('customer').doc(id).delete()
                errorOccurred ="CreditCardNumber not is number or the validity is not a date";
                return;
            }
            if(datenow - myDate > 0) {
                adduser= false;
                admin.firestore().collection('customer').doc(id).delete()
                errorOccurred ="Expired card validity.";
                return;
            }
            else {
                const p =  admin.firestore().collection('customer').doc(id).collection('creditCard').add({
                        type: creditCardType,
                        number: number,
                        validity: myDate,
                        valueSpentMod100: 0,

                        })
                promises.push(p);
                adduser = true;
            }
            return Promise.all(promises)
        })
        .then(snapshot => {
            if(adduser) {
                res.status(200).send({ 'data':id});
                return 
            }else {
                res.status(200).send({ 'error':errorOccurred});
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
1053ff40-e51e-11e8-8996-7f28f998adb6
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/payOrder --data ' { "userId":"739c7ea0-e407-11e8-a890-d53adf44ae9e","vouchers": {"voucher1":"c531c6e0-e415-11e8-82ca-35782305cc78", "voucher":"08c85400-e416-11e8-82ca-35782305cc78"},"products": {"product1":{"docProduct":"26QU3Rxbt3OdOyO8UP4X", "quantity":"1" },"product2":{"docProduct":"wxR6vHBwaYqXVPmPvJBk", "quantity":"1" }}}' -g -H "Content-Type: application/json"

    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/payOrder --data '{"signature":"asasa","data": { "userId":"739c7ea0-e407-11e8-a890-d53adf44ae9e","vouchers": {"voucher":"c531c6e0-e415-11e8-82ca-35782305cc78"},"products": {"product1":{"docProduct":"3wa7bd4ZhRHyb7lbdfqj", "quantity":"3" },"product2":{"docProduct":"wxR6vHBwaYqXVPmPvJBk", "quantity":"2" }}}}' -g -H "Content-Type: application/json"
*/
const payOrder = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        const dataBytes = res.rawBody.slice(0, -64)
        const signatureBytes = res.rawBody.slice(-64)
        const dataString = dataBytes.toString()
        const data = JSON.parse(dataString).data;
        const userId = data.userId;
        const vouchers = data.vouchers;
        const products = data.products;


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
        var listproducts = [];
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
            listproducts.push(product)
        }

        VerifySignature(userId, dataBytes,signatureBytes).then(result=>{
            console.log(result)

            if(!result){
                res.status(200).send({ 'error':"Signature error"});
                return;
            }

            getProducts(listproducts).then(result=>{
                if(result.priceProducts == undefined){
                    res.status(200).send({ 'error':result});
                    return;
                }
                productsPurchased = result.productsPurchased;
                priceProducts = result.priceProducts;
                numberOfCoffee = result.numberOfCoffee;
                valueCoffe = result.valueCoffe;
                numberOfPopcorn = result.numberOfPopcorn;
                valuepopCorn = result.valuepopCorn;

            })
            .then(snapshot => {
                getVouchers(listVoucher, userId, numberOfCoffee, numberOfPopcorn).then(result=>{
                    if(result.discont == undefined && result != null){
                        res.status(200).send({ 'error':result});
                        return;  
                    }
                    if(result != null){
                        discont = result.discont;
                        freecoffee = result.freecoffee;
                        popcorn = result.popcorn;
                        voucherUsed = result.voucherUsed
                    }
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
                            valueSpent = priceProducts*(1-discont) - valueCoffe*freecoffee - valuepopCorn*popcorn;
            
                                if(valueSpent > 0 ){
                                    usersRef.doc(userId).collection('creditCard').doc(creditCardUser).update({
                                        valueSpentMod100: (valueSpentMod100 + valueSpent) % 100,
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
                            if(number != null){
                                var numberKey = "number"; 
                                obj[numberKey] = number;
        
                                res.status(200).send({ 'data':obj});
                                return;
                            }else {
                                res.status(200).send({ 'error':'Error'});
                                return;
                            }
                        })
                        .catch(err => {
                            res.status(200).send({ 'Error':err});
                            return;
                        });
                    })
                    .catch(err => {
                        res.status(200).send({ 'Error':err});
                        return;
                    });
                })
            })
            .catch(err => {
                res.status(200).send({ 'error':err});
                return;
            });   
        })
        .catch(err => {
            res.status(200).send({ 'error':err});
            return;
        });     
    })
})

/**
Function to list Tickets Of User
Parameters: username -> the USER name
Output: JSON with 
TEST: 
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listTicketsNotUsed --data ' {"userId":"57900f70-e1d6-11e8-a855-57782ab5d15f" }' -g -H "Content-Type: application/json"
*/

const listTicketsNotUsed = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {
        body = JSON.parse(req.body.toString())
        console.log(body)
        const userId = body.userId;
        var result = []

        admin.firestore().collection('customer').doc(userId).collection('ticket').get()
        .then(snapshot => {
            snapshot.forEach(ticketdoc => {
                if(ticketdoc.data().state == "not used") {
                    var ticket = {
                        id: ticketdoc.id,
                        name: ticketdoc.data().name,
                        date: ticketdoc.data().date,
                        place: ticketdoc.data().place,
                        performanceId: ticketdoc.data().performanceId,performaceId
                    }
                    result.push(ticket);
                }   
            })
            res.status(200).send({ 'data':result});
            return;
        })
        .catch (error=> {
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

        body = JSON.parse(req.body.toString())
        console.log(body)
        const userId = body.userId;
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
                        performanceId: ticketdoc.data().performanceId,
                        state:ticketdoc.data().state
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
                            state:voucherdoc.data().state

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

                    console.log(obj)
                    res.status(200).send({ 'data':obj});
                    return;
                })
            })
        })
        .catch (error=> {
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
        body = JSON.parse(req.body.toString())
        console.log(body)
        const userId = body.userId;
        var vouchersResult = [];

        admin.firestore().collection('customer').doc(userId).collection('voucher').get()
        .then(snapshot => {
            snapshot.forEach(voucherdoc => {
                if(voucherdoc.data().state == "not used") {
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

function addProductUser(userId, listproducts){
    listproducts.forEach(product=>{
        admin.firestore().collection('customer').doc(userId).collection('productsPurchased').add({
            nameProduct:product.nameProduct,
            priceProduct:product.priceProduct,
            quantity:product.quantity
        })
    })
}

function getProducts(listproducts) {
    var productsPurchased = []
    var priceProducts = 0;
    var numberOfCoffee = 0;
    var valueCoffe = 0;
    var numberOfPopcorn = 0;
    var valuepopCorn = 0;
    var obj = {};
    var error = ""

    var promises= [];
    var listquantity = [];
    var i=0
    listproducts.forEach(product =>{
        var docProduct =  product.docProduct;
        var quantity = product.quantity;
        if(!docProduct || !quantity) {
            error = "Please enter the doc and quantity of product";
            return;
        }
        listquantity.push(quantity);
   
        const p = admin.firestore().collection('product').doc(docProduct).get()
        promises.push(p);
    })
    return Promise.all(promises)
    .then(snapshot =>{
        if(error!=""){
            return error;
        }
        snapshot.forEach(doc=>{
            var quantity = Number(listquantity[i])
            var nameProduct = doc.data().name;
            var priceProduct = doc.data().price;
            priceProducts = priceProducts + priceProduct * quantity;

            var purchase = {
                nameProduct:nameProduct,
                priceProduct:priceProduct,
                quantity:quantity
            }
            productsPurchased.push(purchase);

            if(nameProduct == 'coffee') {
                numberOfCoffee = numberOfCoffee + quantity;
                valueCoffe = priceProduct;
            }

            if(nameProduct == 'popcorn') {
                numberOfPopcorn = numberOfPopcorn + quantity;
                valuepopCorn = priceProduct;
            }
            i++
        })
            obj["productsPurchased"] = productsPurchased;
            obj["priceProducts"] = priceProducts;
            obj["numberOfCoffee"] = numberOfCoffee;
            obj["valueCoffe"] = valueCoffe;
            obj["numberOfPopcorn"] = numberOfPopcorn;
            obj["valuepopCorn"] = valuepopCorn;
            
            return obj;
    })
    .catch(error=>{
        return "Error";
    })
}

function getVouchers(listVouchers, userId, numberOfCoffee, numberOfPopcorn) {
    var discont =0;
    var freecoffee =0;
    var popcorn=0;
    var voucherUsed = [];
    var obj = {};

    if(listVouchers.length == 0){
        return null;
    }

    const promises = [];
    var usersRef= admin.firestore().collection('customer');

    listVouchers.forEach(voucher => {
        const p = usersRef.doc(userId).collection('voucher').doc(voucher).get()
        promises.push(p)
    })
    return Promise.all(promises)
    .then(snapshot=>{
        snapshot.forEach( doc => {
            var used = false;

            if(doc.data().state == "not used") {
                if(doc.data().productCode == "5%discountCafeteria") {
                    if(discont !=0.05){
                        discont = 0.05;
                        used = true;
                    }
                }else if(doc.data().productCode == "freecoffee") {
                    if(numberOfCoffee > freecoffee ){
                        freecoffee++
                        used = true;
                    }
                }else if(doc.data().productCode == "popcorn") {
                    if( numberOfPopcorn > popcorn){ 
                        popcorn++;
                        used = true;
                    }
                }

                if (used){
                    voucherUsed.push(doc.id)
                }
            } 
        })

        obj["discont"] = discont;
        obj["freecoffee"] = freecoffee;
        obj["popcorn"] = popcorn;
        obj["voucherUsed"] = voucherUsed
        return obj;
    })
    .catch(error=>{
        return "Invalid voucher";
    })
}

function VerifySignature(userId, dataBytes, signatureBytes){

    return admin.firestore().collection('customer').doc(userId).get()
    .then(doc => {
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

module.exports={
    register,
    payOrder,
    listTicketsNotUsed,
    listTransactionsUser,
    listVouchersUser
}
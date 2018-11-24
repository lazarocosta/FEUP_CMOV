const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cors = require('cors')({origin: true});


/**
Function to list of products
Parameters:

Output: JSON with result value 
Teste:
    curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/listProducts --data '{}' -g -H "Content-Type: application/json"

    curl -X POST http://localhost:5000/cmov-d52d6/us-central1/listProducts --data '{}' -g -H "Content-Type: application/json"

    curl -X POST http://192.168.1.65:5000/cmov-d52d6/us-central1/listProducts --data '{}' -g -H "Content-Type: application/json"
    
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/listProducts --data ' {}' -g -H "Content-Type: application/json"
*/
const listProducts = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {

        var products = [];
        admin.firestore().collection('product').get()
        .then(result =>{
            result.forEach(doc=>{
                var product = {
                    name: doc.data().name,
                    price:doc.data().price,
                    id: doc.id
                }
                products.push(product)
            })
            res.status(200).send({'data':products});
        })
        .catch(error =>  {
            res.status(200).send({ 'error':error});
            return;
        });
    });
});

/*
    curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/addProducts --data ' {}' -g -H "Content-Type: application/json"

    curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/addProducts --data '{}' -g -H "Content-Type: application/json"

    curl -X POST http://localhost:5000/cmov-d52d6/us-central1/addProducts --data '{}' -g -H "Content-Type: application/json"
**
*/

const addProducts = functions.https.onRequest((req,res)=>{
    return cors(req,res, () =>{

        var product1 ={
            name:'Coffee',
            price:1,
            id:"1"
        }

        var product2 ={
            name:'Popcorn',
            price:2,
            id:"2"
        }
        var product3 ={
            name:'Sandwich',
            price:5,
            id:"3"
        }
        var product4 ={
            name:'Soda drink',
            price:4,
            id:"4"
        }

        admin.firestore().collection('product').doc(product1.id).set(product1)
        .then(()=>{
            admin.firestore().collection('product').doc(product2.id).set(product2)
            .then(()=>{
                admin.firestore().collection('product').doc(product3.id).set(product3)
                .then(()=>{
                    admin.firestore().collection('product').doc(product4.id).set(product4)
                    .then(()=>{
                        res.status(200).send({ 'data':true});
                        return;
                    }).catch(error =>  {
                        res.status(200).send({ 'data':error});
                        return "An error occurred."
                    });
                }).catch(error =>  {
                    res.status(200).send({ 'data':error});
                    return "An error occurred."
                });
                
            }).catch(error =>  {
                res.status(200).send({ 'data':error});
                return "An error occurred."
            });
        }).catch(error =>  {
            res.status(200).send({ 'data':error});
            return "An error occurred."
        });
    })
})

/*
curl -X POST https://us-central1-cmov-d52d6.cloudfunctions.net/deleteProducts --data '{}' -g -H "Content-Type: application/json"

curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/deleteProducts --data '{}' -g -H "Content-Type: application/json"

curl -X POST http://localhost:5000/cmov-d52d6/us-central1/deleteProducts --data '{}' -g -H "Content-Type: application/json"

*/
const  deleteProducts = functions.https.onRequest((req, res) => {
    return  cors(req, res, () => {


        admin.firestore().collection('product').get()
        .then(snapshot=>{
            snapshot.forEach(performance=>{
                admin.firestore().collection('product').doc(performance.id).delete();
            })
            res.status(200).send({ 'data':true});
            return;
        })
        .catch(error =>  {
            return "An error occurred."
        });
    })
})

module.exports= {
    listProducts,
    addProducts,
    deleteProducts
}
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
                    price:doc.data().price
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
    curl -X POST http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1/addProducts --data '{}' -g -H "Content-Type: application/json"

    curl -X POST http://localhost:5000/cmov-d52d6/us-central1/addProducts --data '{}' -g -H "Content-Type: application/json"
**
*/

const addProducts = functions.https.onRequest((req,res)=>{
    return cors(req,res, () =>{

        admin.firestore().collection('product').add({
            name:'popcorn',
            price:2
        })
        
        admin.firestore().collection('product').add({
            name:'coffee',
            price:2
        })

        res.status(200).send({ 'data':true});
        return;
    })
})

/*
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
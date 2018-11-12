const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cors = require('cors')({origin: true});
/**
Function to list of products
Parameters:

Output: JSON with result value 
Teste:
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

module.exports= {
    listProducts
}
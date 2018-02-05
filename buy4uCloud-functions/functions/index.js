const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.orderCopy = functions.database.ref('buyers/{uid}/orders/{key}').onWrite(event =>{
    const uid = event.params.uid;
    const order = event.data;
    // const order = event.data;
    // const uid = order.child('uid').val();
    const itemId = order.child('itemId').val();
    console.log("uid:"+ uid);
    console.log("itemId:"+ itemId);

    // admin.database
    admin.database().ref(`orders/1/uid`).set('testing');

    return -1;
});

exports.orderAddedNotification = functions.database.ref('orders/{id}').onWrite(event =>{
    const orderId = event.params.id;
    const order = event.data;
    const buyer = order.child('uid').val();
    const itemId = order.child('itemId').val();
    const qty = order.child('qty').val();
    console.log("order id:" + orderId);
    console.log("order: " + buyer +"/"+ itemId +"/"+ qty);
    
    const brokerPromise = admin.database().ref(`/broker/token`).once('value');
    return Promise.all([brokerPromise]).then(results =>{
        const token = results[0].val();
        console.log("Broker token:" + token);

        const payload = {
            data: {
              type: '1',
              title: 'Order placed',
              body: 'Testing',
            }
        };
        return admin.messaging().sendToDevice(token, payload).then(response => {
            response.results.forEach((result, index) => {
              const error = result.error;
              if (error) {
                console.error('Failure sending notification to', token, error);
                // Cleanup the tokens who are not registered anymore.
                if (error.code === 'messaging/invalid-registration-token' ||
                    error.code === 'messaging/registration-token-not-registered') {
                
                }
              }
          });
        });
    });
});


//firebase deploy 部屬程式上雲端
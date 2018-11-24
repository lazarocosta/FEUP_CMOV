
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);


const fs = require('fs');
const path = require('path');

// Folder where all your individual Cloud Functions files are located.
const FUNCTIONS_FOLDER = './funcs';

fs.readdirSync(path.resolve(__dirname, FUNCTIONS_FOLDER)).forEach(file => { // list files in the folder.
  if(file.endsWith('.js')) {
    const fileBaseName = file.slice(0, -3); // Remove the '.js' extension
    const thisFunction = require(`${FUNCTIONS_FOLDER}/${fileBaseName}`);
    for(var i in thisFunction) {
        exports[i] = thisFunction[i];
    }
  }
});

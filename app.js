import express from "express";
import bodyParser from "body-parser";
import { fileURLToPath } from "url";
import { dirname } from "path";
import dotenv from "dotenv";
import path from "path";
import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";
import { getAuth } from "firebase/auth";
import router from "./routes/routing.js";

dotenv.config();//load environment vars from .env
const app = express();

//defining __dirname
const __filename= fileURLToPath(import.meta.url);//get current modules filename
const __dirname = path.dirname(__filename); //get directory name

// Web app's Firebase configuration
const firebaseConfig = {
    apiKey: process.env.FIREBASE_API_KEY,
    authDomain: process.env.FIREBASE_AUTH_DOMAIN,
    projectId: process.env.FIREBASE_PROJECT_ID,
    storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
    messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID,
    appId: process.env.FIREBASE_APP_ID,
    databaseURL: process.env.FIREBASE_DB_URL
};

//init firebase app,db,auth
const firebaseApp = initializeApp(firebaseConfig);
const db = getDatabase(firebaseApp);
const auth = getAuth(firebaseApp);

//middleware 
app.use(bodyParser.urlencoded({extended: true}));
//setting ejs as templating engine
app.set('view engine','ejs');
//serve static files (css, images etc.) from public folder
app.use(express.static(path.join(__dirname,'public')));

//Use router
app.use('/',router);

//start localhost server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`)//log url when server boots
})

export { db,auth };
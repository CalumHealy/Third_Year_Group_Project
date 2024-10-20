import express from 'express';
import { db } from '../app.js';
import { ref,set,get } from 'firebase/database';
import {getAuth, sendPasswordResetEmail} from 'firebase/auth'
import argon2  from 'argon2'; //for password hashing

const router = express.Router();

//function to clean email address for firebase path
const safeEmail = (email) => {
    return email.replace(/[@.]/g,(match) =>{
        switch (match) {
            case '@':
                return '_at_';//replaces @ with '_at_'
            case '.':
                return '_dot_';//replaces . with '_dot_'
            default:
                return match;
        }
    })
};

//Function to restore email address when being called
const restoreEmail = (safeEmail) => {
    return safeEmail.replace(/_at_/g,'@').replace(/_dot_/g,'.');
};

//Initial login route
router.get('/',(req,res) => {
    res.render('index'); //render login.ejs
});

//Register route
router.get('/register',(req,res) =>{
    res.render('register'); //render register.ejs
});

//register post
router.post('/register',async (req,res) =>{
    const {username,password,phoneNo} = req.body;

    try{
        //hash password before storage
        const hashedPassword = await argon2.hash(password);

        //create reference to user data in firebase and encode email so it can be pushed to firebase
        const safeUsername = safeEmail(username);
        const userRef = ref(db,'users/'+ safeUsername);

        //set user data
        await set(userRef, {
            username,
            password: hashedPassword,
            phoneNo
        });

        console.log('User registered successfully');
        res.redirect('/');

    }catch (error){
        console.error("Error registering user: ",error);
        res.status(500).send("Error registering user");
    }
});

//Forgot Password route
router.get('/forgotPassword',(req,res) =>{
    res.render('forgotPass'); //render forgotPass.ejs 
});

//Forgot Password email send
router.post('/forgotPassword',async (req,res) =>{
    const { email } = req.body;
    console.log(email);
    const auth = getAuth();

    try{
        await sendPasswordResetEmail(auth,email)
        .then(() =>{
            console.log(`Password reset email sent to ${email}`);
            res.send("Password reset email sent successfully");
        })
        .catch((error) =>{
            const errorCode = error.code;
            const errorMessage = error.message;
            console.error(`ErrorCode: ${errorCode},ErrorMessage: ${errorMessage}`);
            res.status(405).send("Error sending password reset email.")
        });
    }catch(error){
        console.error("Error sending reset email: ",error);
        res.status(500).send("Error trying to send password reset email");
    }
});

//Home route
router.get('/home', (req,res) => {
    res.render('home'); //render home.ejs
});

//login retrieval
router.post('/login', async (req,res) =>{
    const { username, password} = req.body;
    
    if(!username || !password){
        return res.status(400).send("Username and password are required.")
    }

    try{
        //create reference to user data in firebase
        const safeUsername = safeEmail(username);
        const userRef = ref(db, 'users/' + safeUsername);

        //get user data
        const snapshot = await get(userRef);

        if (!snapshot.exists()){
            return res.status(400).send("User not found.");
        }

        const userData = snapshot.val();

        //verify password
        const isPassword = await argon2.verify(userData.password,password);
        if (!isPassword){
            return res.status(401).send("Invalid password.");
        }

        console.log("User logged in successfully.");
        res.redirect('/home'); //if login is a success then send the fund manager through to the home screen

    }catch(error){
        console.error("Error logging into website",error);
        res.status("402").send("Failed to login.");
    }
});

// stock invest route
router.get('/buystock',async (req,res)=>{
    res.render('buystock'); //renders buystock.ejs
});

// crypto invest route
router.get('/buycrypto',async (req,res)=>{
    res.render('buycrypto'); //renders buycrypto.ejs
});

// help route
router.get('/help',async (req,res)=>{
    res.render('help'); //renders help.ejs
});

// settings route
router.get('/settings',async (req,res)=>{
    res.render('settings'); //renders setting.ejs
});

// review route
router.get('/review',async (req,res)=>{
    res.render('review'); //renders review.ejs
});

export default router;
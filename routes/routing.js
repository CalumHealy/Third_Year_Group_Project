import express from 'express';
import { db } from '../app.js';
import { ref,set,get } from 'firebase/database';
import {getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword, sendPasswordResetEmail} from 'firebase/auth'
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
        const auth = getAuth();
        const userCredentials = await createUserWithEmailAndPassword(auth, username, password);
        const user = userCredentials.user;

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
    
    try{
        const auth = getAuth();
        await sendPasswordResetEmail(auth,email);
        console.log(`Password reset email sent to ${email}`);
        res.send("Password reset email sent successfully");
    }catch(error){
        console.error("Error sending reset email: ",error);
        res.status(500).send("Error trying to send password reset email");
    }
});

//Home route
router.get('/home', (req,res) => {
    const username = req.session.username;
    if(!username){
        res.redirect('/'); //if no username redirect back to login
    }
    res.render('home', {username}); //render home.ejs and pass username to ejs
});

//login retrieval
router.post('/login', async (req,res) =>{
    const { username, password} = req.body;
    
    if(!username || !password){
        return res.status(400).send("Email and password are required.")
    }

    try{
        const auth = getAuth();
        const userCredentials = await signInWithEmailAndPassword(auth,username,password);
        const user = userCredentials.user;

        req.session.username = username; //store username in session
        console.log("User logged in successfully.");
        res.redirect('/home'); //if login is a success then send the fund manager through to the home screen

    }catch(error){
        console.error("Error logging into website",error);
        res.status("402").send("Failed to login.");
    }
});

// select_company route
router.get('/select_company',async (req,res)=>{
    res.render('select_company'); //renders select_company.ejs
});

//add_company route
router.get('/add_company',async (req,res)=>{
    res.render('add_company'); //renders add_company.ejs
});

// stock invest route
router.get('/buystock',async (req,res)=>{
    res.render('buystock'); //renders buystock.ejs
});

// crypto invest route
router.get('/buycrypto',async (req,res)=>{
    res.render('buycrypto'); //renders buycrypto.ejs
});

// add funds route
router.get('/add_funds',async (req,res)=>{
    res.render('add_funds'); //renders add_funds.ejs
});

// buy confirm route
router.get('/buy_confirm',async (req,res)=>{
    res.render('buy_confirm'); //render buy_confirm.ejs
});

// sell confirm route
router.get('/sell_confirm',async (req,res)=>{
    res.render('sell_confirm'); //render sell_confirm.ejs
});

// help route
router.get('/help',async (req,res)=>{
    res.render('help'); //renders help.ejs
});

// help chatbot route
router.get('/chatbot', async (req,res)=>{
    res.render('help_chatbot'); //renders help_chatbot.ejs
});

// help form route
router.get('/helpform', async (req,res)=>{
    res.render('help_form'); //renders help_form.ejs
});

// settings route
router.get('/settings',async (req,res)=>{
    const username = req.session.username;
    if(!username){
        res.redirect('/'); //if no username redirect back to login
    }
    res.render('settings', {username}); //renders setting.ejs and passes username to ejs
});

// upgrade plan route
router.get('/upgrade', async (req,res)=>{
    res.render('upgrade_plan'); //renders upgrade_plan.ejs
});

// review route
router.get('/review',async (req,res)=>{
    res.render('review'); //renders review.ejs
});

//write review route
router.get('/write_review', async (req,res)=>{
    res.render('write_review'); //renders write_review.ejs
});

export default router;
import express from 'express';
import { db } from '../app.js';
import { ref,set,get,update, push, getDatabase } from 'firebase/database';
import {getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword, sendPasswordResetEmail} from 'firebase/auth'
import argon2  from 'argon2'; //for password hashing
import e from 'express';

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
        const userRef = ref(db,'users/'+ user.uid);

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
        res.redirect('/select_company'); //if login is a success then send the fund manager through to the select company screen

    }catch(error){
        console.error("Error logging into website",error);
        res.status(402).send("Failed to login.");
    }
});

// select_company route
router.get('/select_company',async (req,res)=>{
    const auth = getAuth();
    const user = auth.currentUser;

    if (!user){
        return res.render('/'); //ensure user auth
    }
    try{
        const companiesRef = ref(db, `users/${user.uid}/companies`);
        const snapshot = await get(companiesRef);

        //check companies exist
        const companies = snapshot.exists() ? snapshot.val(): [];

        console.log(companies);
        res.render('select_company', {companies}); //renders select_company.ejs and passes company data to ejs
    }catch (error){
        console.log("Error fetching companies: ",error);
        res.status(500).send("Error fetching companies");
    }
    
});

// select_company post
router.post('/select_company',async (req,res)=>{
    const {companyId}= req.body

    if (!companyId){
        return res.status(400).send("Company ID is required.");
    }

    req.session.companyId = companyId; //store companyId in session
    console.log(`Company selected ${companyId}`);
    res.redirect('/home') //redirect to home screen
});

//add_company route
router.get('/add_company',async (req,res)=>{
    res.render('add_company'); //renders add_company.ejs
});

//add_company post
router.post('/add_company',async (req,res)=>{
    const {company_name} = req.body;

    if (!company_name){
        return res.status(400).send("Company name is required.");
    }

    try{
        const auth = getAuth();
        const user = auth.currentUser;
        if (!user){
            return res.redirect('/')
        }
        const db = getDatabase();

        const companiesRef = ref(db,`users/${user.uid}/companies`);
        //push new companies into companies node
        const newCompanyRef = push(companiesRef);

        //set up company data
        await set(newCompanyRef,{
            company_name,
            wallet: {
                balance: 0 //init wallet balance
            }
        });
        console.log(`Company ${company_name} added successfully`);
        res.redirect('/select_company');
    }catch (error){
        console.error("Error adding company: ",error);
        res.status(500).send("Company could not successfully be added.");
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

// add funds route
router.get('/add_funds',async (req,res)=>{
    res.render('add_funds', {PAYPAL_CLIENT_ID: process.env.PAYPAL_CLIENT_ID }); //renders add_funds.ejs
});

// update wallet funds
router.post('/api/update-wallet', async (req,res)=>{
    const amount = parseFloat(req.body.amount); // get amount from req body
    const companyId = req.session.companyId; ///get companyId from session
    console.log(req.body);

    if (!companyId || !amount){
        return res.status(400).send("Selected Company and amount are required.");
    }

    try{
        const auth = getAuth();
        const user = auth.currentUser;

        if (!user){
            return res.status(400).send("User not authenticated");
        }

        const WalletRef = ref(db, `users/${user.uid}/companies/${companyId}/wallet`); 

        //get current balance
        const snapshot = await get(WalletRef);
        if (!snapshot.exists()){
            return res.status(404).send("Wallet not found.");
        }

        const currentBal = snapshot.val().balance;

        //update balance
        const newBal = currentBal + amount;

        await update(WalletRef, {
            balance: newBal //update balance
        });

        console.log(`Wallet updated. New bal: €${newBal}`);
        res.send(`Funds added successfully. New bal: €${newBal}`);
    }catch (error){
        console.error("Error updating wallet", error);
        res.status(500).send("Error updating wallet.");
    }
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
    const auth = getAuth();
    const user = auth.currentUser;
    const companyId = req.session.companyId;

    if(!username || !user){
        res.redirect('/'); //if no username redirect back to login
    }
    try{
        //ref to company in firebase
        const companyRef = ref(db, `users/${user.uid}/companies/${companyId}`);
        const companySnapshot = await get(companyRef);

        //check if company exists and retrieve its data
        if (companySnapshot.exists()){
            const companyData = companySnapshot.val();
            const balance = companyData.wallet ? companyData.wallet.balance : 0;
            const companyName = companyData.company_name || "Company";

            res.render('settings', {username, balance, companyName}); //renders setting.ejs and passes username to ejs
        }else{
            console.log("Company not found in db");
            res.redirect('select_company');
        }
    } catch (error){
        console.log("Company not found in db");
        res.status(500).send("Error fetching company data")
    }

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
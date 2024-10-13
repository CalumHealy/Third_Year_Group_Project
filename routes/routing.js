import express from 'express';
import { db } from '../app.js';
import { ref,set } from 'firebase/database';
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

//Home route
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

export default router;
import express from 'express';
import { db } from '../app.js';
import { ref,set,get,update, push, getDatabase, remove } from 'firebase/database';
import { getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword, sendPasswordResetEmail, GoogleAuthProvider, signInWithCredential } from 'firebase/auth';
import { fetchStocks, fetchStockDetails} from '../services/polygonService.js';
import { fetchCryptoList, fetchCryptoDetails} from '../services/cryptoServices.js';
import { fetchHistoricalData } from '../services/chartServices.js';
import { OAuth2Client } from 'google-auth-library';
import argon2  from 'argon2'; //for password hashing
import e from 'express';

const router = express.Router();
const provider = new GoogleAuthProvider();
const client = new OAuth2Client('826845466167-mejd10akc8gqoh6tnlovlmdogkgf54ff.apps.googleusercontent.com');

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
router.get('/home',async (req,res) => {
    const username = req.session.username;
    const auth = getAuth();
    const user = auth.currentUser;
    const companyId = req.session.companyId;

    if(!username){
        res.redirect('/'); //if no username redirect back to login
    }

    const stockRef = ref(db, `users/${user.uid}/companies/${companyId}/stocks`);
    const stockSnapshot = await get(stockRef);

    const stocks = stockSnapshot.exists() ? Object.entries(stockSnapshot.val()).map(([key,value]) => ({
        key: key,
        name: value.name,
        price: value.price,
        quantity: value.quantity
    })) : [];

    const LiveStock = await Promise.all(
        stocks.map(async (stock) =>{
            try{
                const stockDetails = await fetchStockDetails(stock.name);
                const currentPrice = stockDetails.price;
                const profitLoss = (currentPrice - stock.price) * stock.quantity;
                const historicalData = await fetchHistoricalData(stock.name);

                // Fetch AI recommendation for the stock
                const aiRecommendation = await fetch(`https://ai-recommendations-production.up.railway.app/api/recommendations/${encodeURIComponent(stock.name)}`)
                    .then(response => {
                        if (!response.ok) throw new Error('Failed to fetch AI recommendation');
                        return response.json();
                    })
                    .then(data => data.recommendation || "No recommendation available")
                    .catch(err => {
                        console.error(`Error fetching AI recommendation for ${stock.name}:`, err);
                        return "No recommendation available";
                    });

                return{
                    key: stock.key,
                    name: stock.name,
                    price: stock.price,
                    quantity: stock.quantity,
                    currentPrice,
                    profitLoss,
                    historicalData,
                    aiRecommendation
                };
            }catch(error){
                console.log(`Error fetching live details for ${stock.name}`,error.message);
                return{
                    key: stock.key,
                    name: stock.name,
                    price: stock.price,
                    quantity: stock.quantity,
                    currentPrice: "N/A",
                    profitLoss: 0,
                    historicalData: [],
                    aiRecommendation: "No data available"
                };
            }
        })
    );
    

    const cryptoRef = ref(db, `users/${user.uid}/companies/${companyId}/cryptos`);
    const cryptoSnapshot = await get(cryptoRef);

    const cryptos = cryptoSnapshot.exists() ? Object.entries(cryptoSnapshot.val()).map(([key,value]) => ({
        key: key,
        name: value.name,
        price: value.price,
        quantity: value.quantity
    })) : [];

    const cryptoMapping = {
        "Bitcoin":"bitcoin",
        "Ethereum":"ethereum",
        "XRP":"ripple",
        "USDT":"tether",
        "Solana":"solana",
        "BNB":"binancecoin",
        "Dogecoin":"dogecoin",
        "Cardano":"cardano",
        "USDC":"usd-coin",
        "Lido Staked Ether":"staked-ether",
        "Avalanche":"avalanche-2",
        "Tron":"tron",
        "Shiba Inu":"shiba-inu",
        "Toncoin":"the-open-network",
        "Stellar":"stellar",
        "Chainlink":"chainlink",
        "Wrapped stETH":"wrapped-steth",
        "Polkadot":"polkadot",
        "Hyperliquid":"hyperliquid",
        "Wrapped Bitcoin":"wrapped-bitcoin"
    }

    const LiveCrypto = await Promise.all(
        cryptos.map(async (crypto)=>{
            const mappedId = cryptoMapping[crypto.name] || crypto.name;
            try{
                const cryptoDetails = await fetchCryptoDetails(mappedId);
                const currentPrice = cryptoDetails.price;
                const profitLoss = (currentPrice - crypto.price) * crypto.quantity;
                const historicalData = await fetchHistoricalData(mappedId);

                // Fetch AI recommendation for the stock
                const aiRecommendation = await fetch(`https://ai-recommendations-production.up.railway.app/api/recommendations/${encodeURIComponent(crypto.name)}`)
                    .then(response => {
                        if (!response.ok) throw new Error('Failed to fetch AI recommendation');
                        return response.json();
                    })
                    .then(data => data.recommendation || "No recommendation available")
                    .catch(err => {
                        console.error(`Error fetching AI recommendation for ${crypto.name}:`, err);
                        return "No recommendation available";
                    });

                return{
                    key: crypto.key,
                    name: crypto.name,
                    id: cryptoMapping[crypto.name] || crypto.name,
                    price: crypto.price,
                    quantity: crypto.quantity,
                    currentPrice,
                    profitLoss,
                    historicalData,
                    aiRecommendation
                };
            }catch (error){
                console.log(`Error fetching live details for ${crypto.name}`,error.message);
                return{
                    key: crypto.key,
                    name: crypto.name,
                    id: cryptoMapping[crypto.name] || crypto.name,
                    price: crypto.price,
                    quantity: crypto.quantity,
                    currentPrice: "N/A",
                    profitLoss: 0,
                    historicalData: [],
                    aiRecommendation: "No data available"
                };
            }
        })
    );

    res.render('home', {username, LiveStock, LiveCrypto}); //render home.ejs and pass username to ejs
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

// Google login route
router.post("/google-login", async (req, res) => {
    const token = req.body.token; // Get the token from the client
    try {
      // Verify the token using the Google OAuth client
      const ticket = await client.verifyIdToken({
        idToken: token,
        audience: 'YOUR_CORRECT_CLIENT_ID', // Replace with your client ID
      });
      const payload = ticket.getPayload();
  
      // Handle user info
      console.log("User info from token:", payload);
  
      req.session.username = payload.email; // Store email in session
      res.redirect("/home");
    } catch (error) {
      console.error("Google login error:", error);
      res.status(500).send("Failed to log in with Google.");
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
    try{
        const stocks = await fetchStocks();
        res.render('buystock', {stocks}); // pass stock data to the buystock.ejs view
    } catch (error) {
        res.status(500).send("Failed to fetch stock data.");
    }
});

// Route for displaying stock details
router.get('/stock/details/:ticker', async (req, res) => {
    const ticker = req.params.ticker; // Get the stock ticker from the URL
    try {
        const stockDetails = await fetchStockDetails(ticker); // Fetch details based on the ticker
        res.render('stockDetails', { stockDetails }); // Render the details page with the data
    } catch (error) {
        console.error(error);
        res.status(500).send('Error retrieving stock details.');
    }
});


// stock buy confirm get route
router.get('/stock_confirm/:ticker',async (req,res)=>{
    try{
        const ticker = req.params.ticker;
        const stockDetails = await fetchStockDetails(ticker);
        const companyId = req.session.companyId;
        const auth = getAuth();
        const user = auth.currentUser;
    
        const companyRef = ref(db, `users/${user.uid}/companies/${companyId}`);
        const companySnapshot = await get(companyRef);
        if (!companySnapshot.exists()){
            return res.status(401).send("Company not found")
        }
        const companyData = companySnapshot.val();
        const balance = companyData.wallet ? companyData.wallet.balance : 0;
        res.render("stock_confirm",{stockDetails, balance});
    }catch (error){
        console.error("Error retrieving stock confirmation data",error);
        res.status(500).send("An error occurred while processing your request");
    }

});

// stock buy confirm post route
router.post('/stock_confirm',async (req,res)=>{
    const username = req.session.username;
    const auth = getAuth();
    const user = auth.currentUser;
    const companyId = req.session.companyId;
    const {ticker, price,quantity} = req.body;
    const priceNum = parseFloat(price);
    const quantNum = parseFloat(quantity);
    const MAX_STOCKS = 10; //max amount of different stocks per person

    try{
        if (isNaN(priceNum) || priceNum <= 0) {
            return res.status(400).send("Invalid price.");
        }
        const totalCost = priceNum * quantNum;

        const WalletRef = ref(db, `users/${user.uid}/companies/${companyId}/wallet`); 

        //get current balance
        const snapshot = await get(WalletRef);
        if (!snapshot.exists()){
            return res.status(404).send("Wallet not found.");
        }

        const currentBal = snapshot.val().balance;

        //update balance
        const newBal = currentBal - totalCost;

        await update(WalletRef, {
            balance: newBal //update balance
        });

        const stockRef = ref(db, `users/${user.uid}/companies/${companyId}/stocks`);
        const stockSnapshot = await get(stockRef);

        if (stockSnapshot.exists() && Object.keys(stockSnapshot.val()).length >= MAX_STOCKS) {
            return res.status(400).send(`Cannot add more than ${MAX_STOCKS} stocks.`);
        }

        const newStock = push(stockRef,{
                name: ticker,
                price: priceNum,
                quantity: quantNum,
                date: new Date().toISOString()
        });
        console.log(`Stock purchase successful for ${ticker}: ${quantNum} at $${priceNum}, wallet balance : $${newBal}`);
        res.redirect('/home'); //redirect to home
    }catch (error){
        console.log("Error buying stock",error);
        res.status(500).send("Failed to buy stock");
    }
});

// crypto invest route
router.get('/buycrypto',async (req,res)=>{
    try {
        const cryptoData = await fetchCryptoList()
        res.render('buycrypto', { cryptoData }); // pass crypto data to buycrypto.ejs view
    } catch (error) {
        res.status(500).send("Error retrieving crypto data");
    }
});

router.get('/crypto/details/:id', async (req, res) => {
    const cryptoId = req.params.id; // Get the crypto ID from the URL
    try {
      const cryptoDetails = await fetchCryptoDetails(cryptoId); // Fetch details based on the ID
      res.render('cryptoDetails', { cryptoDetails }); // Render the details page with the data
    } catch (error) {
      res.status(500).send('Error retrieving crypto details');
    }
});

// crypto buy confirm route
router.get('/crypto_confirm/:id',async (req,res)=>{
    try{
        const cryptoId = req.params.id;
        const companyId = req.session.companyId;
        const cryptoDetails = await fetchCryptoDetails(cryptoId);
        const auth = getAuth();
        const user = auth.currentUser;
    
        const companyRef = ref(db, `users/${user.uid}/companies/${companyId}`);
        const companySnapshot = await get(companyRef);
        if (!companySnapshot.exists()){
            return res.status(401).send("Company not found")
        }
        const companyData = companySnapshot.val();
        const balance = companyData.wallet ? companyData.wallet.balance : 0;

        res.render("crypto_confirm",{cryptoDetails, balance});
    }catch (error){
        console.error("Error retrieving stock confirmation data",error);
        res.status(500).send("An error occurred while processing your request");
    }
});

// crypto buy confirm post route
router.post('/crypto_confirm',async (req,res)=>{
    const username = req.session.username;
    const auth = getAuth();
    const user = auth.currentUser;
    const companyId = req.session.companyId;
    const {ticker, price,quantity} = req.body;
    const priceNum = parseFloat(price);
    const quantNum = parseFloat(quantity);
    const MAX_CRYPTOS = 3; //max amount of different cryptos per person

    try{
        if (isNaN(priceNum) || priceNum <= 0) {
            return res.status(400).send("Invalid price.");
        }
        const totalCost = priceNum * quantNum;

        const WalletRef = ref(db, `users/${user.uid}/companies/${companyId}/wallet`); 

        //get current balance
        const snapshot = await get(WalletRef);
        if (!snapshot.exists()){
            return res.status(404).send("Wallet not found.");
        }

        const currentBal = snapshot.val().balance;

        //update balance
        const newBal = currentBal - totalCost;

        await update(WalletRef, {
            balance: newBal //update balance
        });

        const cryptoRef = ref(db, `users/${user.uid}/companies/${companyId}/cryptos`);
        const cryptoSnapshot = await get(cryptoRef);

        if (cryptoSnapshot.exists() && Object.keys(cryptoSnapshot.val()).length >= MAX_CRYPTOS){
            return res.status(400).send(`Cannot add more than ${MAX_CRYPTOS} cryptos.`);
        }

        const newCrypto = push(cryptoRef,{
            name: ticker,
            price: priceNum,
            quantity: quantNum,
            date: new Date().toISOString()
        });
        console.log(`Crypto purchase successful for ${ticker}: ${quantNum} at $${priceNum},wallet balance: $${newBal}`);
        res.redirect('/home');
    }catch (error){
        console.log("Error buying crypto",error);
        res.status(500).send("Failed to buy crypto");
    }
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

router.get('/sell_stock/:name', async (req, res) => {
    const name = req.params.name;
    const companyId = req.session.companyId;
    const auth = getAuth();
    const user = auth.currentUser;

    try {
        const stockData = await fetchStockDetails(name);
        if (!stockData) {
            return res.status(404).send("Stock not found.");
        }

        const companyRef = ref(db, `users/${user.uid}/companies/${companyId}`);
        const companySnapshot = await get(companyRef);
        if (!companySnapshot.exists()) {
            return res.status(401).send("Company not found");
        }

        const companyData = companySnapshot.val();
        const balance = companyData.wallet ? companyData.wallet.balance : 0;

        const stockRef = ref(db, `users/${user.uid}/companies/${companyId}/stocks`);
        const stockSnapshot = await get(stockRef);

        const stockDB = stockSnapshot.val();
        console.log('stockDB:', stockDB);

        // Check if stockDB is a valid object and contains the expected data
        if (!stockDB || typeof stockDB !== 'object') {
            return res.status(404).send("No stock data found in your portfolio.");
        }

        const stockEntry = Object.entries(stockDB).find(([id, stock]) => stock.name === name || stock.ticker === name);

        if (!stockEntry) {
            return res.status(404).send(`Stock ${name} not found in your portfolio.`);
        }

        const [stockid, stockDetails] = stockEntry;  // Destructure to get stock id and stock details

        // Render the sell_stock view, passing necessary data
        res.render('sell_stock', { stockData: stockDetails, balance, stockName: name, stockid });

    } catch (error) {
        console.error("Error fetching stock details", error);
        res.status(500).send("Error processing request");
    }
});

// sell stock post route
router.post('/sell_stock',async (req,res)=> {
    const username = req.session.username;
    const auth = getAuth();
    const user = auth.currentUser;
    const companyId = req.session.companyId;
    const {stockid, price,quantity} = req.body;
    const priceNum = parseFloat(price);
    const quantNum = parseFloat(quantity);

    try{
        if (isNaN(priceNum) || priceNum <= 0) {
            return res.status(400).send("Invalid price.");
        }
        if (isNaN(quantNum) || quantNum <= 0) {
            return res.status(400).send("Invalid quantity.");
        }
        const totalCost = priceNum * quantNum;

        const stockRef = ref(db, `users/${user.uid}/companies/${companyId}/stocks/${stockid}`);
        const stockSnapshot = await get(stockRef);
        const stockData = stockSnapshot.val();
        const stockQuantity = stockData.quantity;

        if (quantNum > stockQuantity){
            return res.status(400).send(`You don't have enough ${stockData.name} stock to sell.`);
        }

        const updatedQuantity = stockQuantity - quantNum;

        if (updatedQuantity === 0){
            await remove(stockRef);
        }else{
            await update(stockRef, {quantity: updatedQuantity});
        }

        const WalletRef = ref(db, `users/${user.uid}/companies/${companyId}/wallet`); 

        //get current balance
        const snapshot = await get(WalletRef);
        if (!snapshot.exists()){
            return res.status(404).send("Wallet not found.");
        }

        const currentBal = snapshot.val().balance;

        //update balance
        const newBal = currentBal + totalCost;

        await update(WalletRef, {
            balance: newBal //update balance
        });

        res.redirect('/home');

    }catch (error){
        console.log("Error selling stock",error);
        res.status(500).send("Failed to sell stock");
    }
});

router.get('/sell_crypto/:name', async (req, res) => {
    const name = req.params.name;
    const companyId = req.session.companyId;
    const auth = getAuth();
    const user = auth.currentUser;

    try { 
        const cryptoMapping = {
        "Bitcoin":"bitcoin",
        "Ethereum":"ethereum",
        "XRP":"ripple",
        "USDT":"tether",
        "Solana":"solana",
        "BNB":"binancecoin",
        "Dogecoin":"dogecoin",
        "Cardano":"cardano",
        "USDC":"usd-coin",
        "Lido Staked Ether":"staked-ether",
        "Avalanche":"avalanche-2",
        "Tron":"tron",
        "Shiba Inu":"shiba-inu",
        "Toncoin":"the-open-network",
        "Stellar":"stellar",
        "Chainlink":"chainlink",
        "Wrapped stETH":"wrapped-steth",
        "Polkadot":"polkadot",
        "Hyperliquid":"hyperliquid",
        "Wrapped Bitcoin":"wrapped-bitcoin"
        };
        const mappedName = Object.keys(cryptoMapping).find(key => cryptoMapping[key] === name);

        const cryptoData = await fetchCryptoDetails(name);
        if (!cryptoData) {
            return res.status(404).send("Crypto not found.");
        }

        const companyRef = ref(db, `users/${user.uid}/companies/${companyId}`);
        const companySnapshot = await get(companyRef);
        if (!companySnapshot.exists()) {
            return res.status(401).send("Company not found");
        }

        const companyData = companySnapshot.val();
        const balance = companyData.wallet ? companyData.wallet.balance : 0;

        const cryptoRef = ref(db, `users/${user.uid}/companies/${companyId}/cryptos`);
        const cryptoSnapshot = await get(cryptoRef);
        const cryptoDB = cryptoSnapshot.val();

        if (!cryptoDB || typeof cryptoDB !== 'object') {
            return res.status(404).send("No crypto data found in your portfolio.");
        }

        const cryptoEntry = Object.entries(cryptoDB).find(([id, crypto]) => crypto.name === mappedName || id === name);

        if (!cryptoEntry) {
            return res.status(404).send(`Crypto ${name} not found in your portfolio.`);
        }

        const [cryptoid, cryptoDetails] = cryptoEntry; 
        console.log('cryptoDetails with quantity:', cryptoDetails);
 

        res.render('sell_crypto', {
            cryptoData,
            cryptoDetails,      
            balance,  
            cryptoName: name,
            cryptoid
        });

    } catch (error) {
        console.error("Error fetching crypto details", error);
        res.status(500).send("Error processing request");
    }
});


router.post('/sell_crypto', async (req, res) => {
    const username = req.session.username;
    const auth = getAuth();
    const user = auth.currentUser;
    const companyId = req.session.companyId;
    const { cryptoid, price, quantity } = req.body;
    const priceNum = parseFloat(price);
    const quantNum = parseFloat(quantity);

    try {
        if (isNaN(priceNum) || priceNum <= 0) {
            return res.status(400).send("Invalid price.");
        }
        if (isNaN(quantNum) || quantNum <= 0) {
            return res.status(400).send("Invalid quantity.");
        }

        const totalCost = priceNum * quantNum;

        const cryptoRef = ref(db, `users/${user.uid}/companies/${companyId}/cryptos/${cryptoid}`);
        const cryptoSnapshot = await get(cryptoRef);
        const cryptoData = cryptoSnapshot.val();

        if (!cryptoData) {
            return res.status(404).send("Crypto not found in portfolio.");
        }

        const cryptoQuantity = cryptoData.quantity;

        if (quantNum > cryptoQuantity) {
            return res.status(400).send(`You don't have enough ${cryptoData.name} crypto to sell.`);
        }

        const updatedQuantity = cryptoQuantity - quantNum;

        if (updatedQuantity === 0) {
            await remove(cryptoRef);  
        } else {
            await update(cryptoRef, { quantity: updatedQuantity });  
        }

        const walletRef = ref(db, `users/${user.uid}/companies/${companyId}/wallet`);
        const walletSnapshot = await get(walletRef);
        const walletData = walletSnapshot.val();

        const currentBalance = walletData.balance;
        const newBalance = currentBalance + totalCost;

        await update(walletRef, { balance: newBalance });

        res.redirect('/home'); 

    } catch (error) {
        console.log("Error selling crypto", error);
        res.status(500).send("Failed to sell crypto");
    }
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

//help form post route
router.post('/help_form',async (req,res)=>{
    const {email,message} = req.body;

    if(!email || !message){
        return res.status(400).send("All fields are required");
    }
    try{
        const auth = getAuth();
        const user = auth.currentUser;

        if(!user){
            return res.status(400).send("Unauthorised. Please log in.");
        }
        
        const queryRef = ref(db,'queries/');
        await push(queryRef,{
            email,
            message,
            userId: user.uid,
            timestamp: Date.now()
        });

        console.log("Query submitted to db");
        res.status(200).send("Query successfully submitted!");
    } catch(error){
        console.error("Error sending query:",error);
    }
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

            const stockRef = ref(db, `users/${user.uid}/companies/${companyId}/stocks`);
            const stockSnapshot = await get(stockRef);
            const stockAmount = stockSnapshot.exists() ? Object.keys(stockSnapshot.val()).length : 0;

            const cryptoRef = ref(db,`users/${user.uid}/companies/${companyId}/cryptos`);
            const cryptoSnapshot = await get(cryptoRef);
            const cryptoAmount = cryptoSnapshot.exists() ? Object.keys(cryptoSnapshot.val()).length : 0;

            res.render('settings', {username, balance, companyName,stockAmount,cryptoAmount}); //renders setting.ejs and passes username to ejs
        }else{
            console.log("Company not found in db");
            res.redirect('select_company');
        }
    } catch (error){
        console.log("Company not found in db");
        res.status(500).send("Error fetching company data")
    }

});

// review route
router.get('/review',async (req,res)=>{
    try{
        const auth = getAuth();
        const user = auth.currentUser;
        if (!user){
            return res.redirect('/');
        }
        const reviewRef = ref(db,'reviews/');
        const reviewSnapshot = await get(reviewRef);
        
        let reviews = [];
        if(reviewSnapshot.exists()){
            const reviewData = reviewSnapshot.val();
            reviews = Object.values(reviewData);
        }

        res.render('review',{reviews}); //renders review.ejs
    }catch (error){
        console.log("Error fetching reviews", error);
        res.status(404).send("Error fetching reviews.");
    }
});

//write review route
router.get('/write_review', async (req,res)=>{
    res.render('write_review'); //renders write_review.ejs
});

//write review post route
router.post('/write_review', async (req,res)=>{
    const {email,review,rate} = req.body;

    if(!review){
        return res.status(400).send("Review is required");
    }
    try{
        const auth = getAuth();
        const user = auth.currentUser;

        if(!user){
            return res.status(400).send("Unauthorised. Please log in.");
        }

        const sanitisedEmail =  email || "Anonymous";
        const sanitisedRating  = rate || "N/A";
        
        const reviewRef = ref(db,'reviews/');
        await push(reviewRef,{
            email: sanitisedEmail,
            review,
            rating: sanitisedRating
        });

        console.log("Review submitted to db");
        res.status(200).send("Review successfully submitted!");
    } catch(error){
        console.error("Error sending review:",error);
    }
});

export default router;
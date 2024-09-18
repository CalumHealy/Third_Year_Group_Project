import express from "express";
import firebase from "firebase";
import bodyParser from "bodyParser";
const app = express();

app.use(bodyParser.urlencoded({extended: true}));
app.set('view engine','ejs');
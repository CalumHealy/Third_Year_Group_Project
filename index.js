import express from "express";
import firebase from "firebase/app";
import bodyParser from "body-parser";

const app = express();

app.use(bodyParser.urlencoded({extended: true}));
app.set('view engine','ejs');
import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";


// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDvqgl44qpLEm3W3voylOanoFSh1HByCtg",
  authDomain: "actweb-account-tester.firebaseapp.com",
  projectId: "actweb-account-tester",
  storageBucket: "actweb-account-tester.appspot.com",
  messagingSenderId: "26391487421",
  appId: "1:26391487421:web:79e9b9a9be7032e35d68d4",
  databaseURL: "https://actweb-account-tester.europe-west1.firebasedatabase.app"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

const database = getDatabase(app);

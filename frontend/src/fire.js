import {firebaseConfig} from "./firebase.config";
import firebase from "firebase";
import {setBeats} from "./store/Actions";
import {store} from './store/DataStore'
export function initFirebase() {
    if (!firebase.apps.length) firebase.initializeApp(firebaseConfig)
    listenForHeartBeats()
}

function getFirestoreRef() {
    const db = firebase.firestore();
    return db.collection('users')
        .doc('test')    // this should be unique device Id
        .collection('heartbeats')
}

function listenForHeartBeats() {
    const collection = getFirestoreRef()
    collection.orderBy('timestamp', 'desc')
        .onSnapshot(documents => {
            const beats = []
            documents.forEach(document => {
                beats.push(document.data())
            })
            store.dispatch(setBeats(beats))
        })
}
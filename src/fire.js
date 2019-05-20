import {firebaseConfig} from "./firebase.config";
import firebase from "firebase";
import {setAttendance, setBeats, setPresence} from "./store/Actions";
import {store} from './store/DataStore'

const heartbeats = 'heartbeats'
const presence = 'presence'

export function initFirebase() {
    if (!firebase.apps.length) firebase.initializeApp(firebaseConfig)
    listenForHeartBeats()
    listenForPresence()
}

function getFirestoreRef(node) {
    const db = firebase.firestore();
    return db.collection('users')
        .doc(store.getState().deviceId)    // this should be unique device Id
        .collection(node)
}

let pulseListener
function listenForHeartBeats() {
    const collection = getFirestoreRef(heartbeats)
    if (pulseListener) pulseListener()
    pulseListener = collection.orderBy('timestamp', 'desc')
        .onSnapshot(documents => {
            const beats = []
            documents.forEach(document => {
                beats.push(document.data())
            })
            store.dispatch(setBeats(beats))
        })
}

export function deleteHeartBeatLogs() {
    const collection = getFirestoreRef(heartbeats)
    collection.get()
        .then((snapshot)=>{
            snapshot.docs.forEach(doc=>{
                doc.ref.delete()
            })
        })
}

export function deleteAttendanceLogs() {
    const collection = getFirestoreRef(presence)
    collection.get()
        .then((snapshot)=>{
            snapshot.docs.forEach(doc=>{
                doc.ref.delete()
            })
        })
}


let presenceListener;
function listenForPresence() {
    const collection = getFirestoreRef(presence)
    if (presenceListener) presenceListener()
    presenceListener = collection.orderBy('timestamp', 'desc')
        .onSnapshot(documents => {
            const attendance = []
            documents.forEach(document=>{
                attendance.push(document.data())
            })
            console.log(attendance)
            store.dispatch(setAttendance(attendance))
        })

    firebase.database().ref(store.getState().deviceId).off()
    firebase.database().ref(store.getState().deviceId).on('value', snapshot=>{
        console.log('Presence', snapshot.val())
        store.dispatch(setPresence(snapshot.val()))
    })
}
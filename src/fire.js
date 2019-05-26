import {firebaseConfig} from "./firebase.config";
import firebase from "firebase";
import {setAttendance, setBeats, setDevices, setPresence, setQuotaExceeded} from "./store/Actions";
import {store} from './store/DataStore'

const heartbeats = 'heartbeats'
const presence = 'presence'

export function initFirebase() {
    if (!firebase.apps.length) firebase.initializeApp(firebaseConfig)
    //listenForHeartBeats()
    //listenForPresence()
    getDevices()
}

function getFirestoreRef(node) {
    const db = firebase.firestore();
    return db.collection('users')
        .doc(store.getState().deviceId)    // this should be unique device Id
        .collection(node)
}

function getDevices(){
    const db = firebase.firestore();
    db.collection('users')
        .get()
        .then(snapshot=>{
            console.log('Total Devices: ' + snapshot.size)
            const devices = []
            snapshot.forEach(doc=>{
                devices.push(doc.id)
            })
            store.dispatch(setDevices(devices))
        })
        .catch(err=>{
            if (err.toString().indexOf("Quota exceeded")!==-1){
                console.error("Quota Exceeded")
                store.dispatch(setQuotaExceeded(true))
            }
        })
}

let pulseListener
export function listenForHeartBeats() {
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
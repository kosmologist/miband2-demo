import {createStore} from "redux";
import reducers from './Reducer'


const initialState = {
    beats : [],
    deviceId:'',
    devices:[],
    quotaExceeded:false
}

export const store = createStore(reducers,initialState)